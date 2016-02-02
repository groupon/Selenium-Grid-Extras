/**
 * Copyright (c) 2013, Groupon, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * Neither the name of GROUPON nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * Created with IntelliJ IDEA.
 * User: Dima Kovalenko (@dimacus) && Darko Marinov
 * Date: 5/10/13
 * Time: 4:06 PM
 */


package com.groupon.seleniumgridextras.grid.proxies;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.grid.proxies.sessions.threads.AppiumNodeRestartCallable;
import com.groupon.seleniumgridextras.grid.proxies.sessions.threads.NodeRestartCallable;

import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.JsonWireCommandTranslator;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import com.groupon.seleniumgridextras.utilities.json.JsonParserWrapper;
import com.groupon.seleniumgridextras.utilities.threads.CommonThreadPool;
import com.groupon.seleniumgridextras.utilities.threads.RemoteGridExtrasAsyncCallable;
import com.groupon.seleniumgridextras.utilities.threads.SessionHistoryCallable;
import com.groupon.seleniumgridextras.utilities.threads.video.RemoteVideoRecordingControlCallable;
import com.groupon.seleniumgridextras.utilities.threads.video.VideoDownloaderCallable;
import org.apache.log4j.Logger;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.common.exception.RemoteUnregisterException;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.internal.listeners.TestSessionListener;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;
import org.openqa.grid.web.servlet.handler.SeleniumBasedResponse;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class AppiumSetupTeardownProxy extends AbstractProxy implements TestSessionListener {

    private volatile int sessionsToServe;
    private volatile int numberOfSessionsServed;
    private volatile boolean offLine;
    private boolean available = true;
    private boolean restarting = false;
    private List<String> sessionsRecording = new LinkedList<String>();

    public static final String DEVICE_NAME = "deviceName";
    public static final String DEVICE_TYPE = "deviceType";
    public static final String PLATFORM_NAME = "platformName";

    private final List<String> toAmend = new ArrayList<String>();

    private static Logger logger = Logger.getLogger(AppiumSetupTeardownProxy.class);


    public AppiumSetupTeardownProxy(RegistrationRequest request, Registry registry) {
        super(request, registry);
        numberOfSessionsServed = 0;
        sessionsToServe = 100;
        offLine = false;
        toAmend.add(DEVICE_NAME);
        logger.debug(String.format("Attaching node %s", this.getId()));
    }

    @Override
    public TestSession getNewSession(Map<String, Object> requestedCapability) {
        Map<String, Object> requestedCapabilityClone = new HashMap<String, Object>();
        requestedCapabilityClone.putAll(requestedCapability);

        synchronized (this) {
            if (offLine) {
                return null;
            }
            if (isDown() || isRestarting()) {
                return null;
            }

            if(!isAvailable()){
                return null;
            }
            String onHoldEnv = null;
            int sessionSize = 1;
            //check for environment
            logger.debug(String.format("Checking for environment"));
            if(requestedCapabilityClone.containsKey("envType")){
                String envType = (String) requestedCapabilityClone.get("envType");
                String envBackend = (String) requestedCapabilityClone.get("envBackend");
                if(requestedCapabilityClone.containsKey("sessionSize"))
                    sessionSize = Integer.valueOf((String) requestedCapabilityClone.get("sessionSize"));
                onHoldEnv = getAvailableEnvName(envType, sessionSize, envBackend);

                log(String.format("Holding environment: %s", onHoldEnv));
                if(onHoldEnv.equals("noEnvironmentFound")) {
                    return null;
                }
            }

            log("Amending capabilities....");
            // add back missing caps
            for(String cap : toAmend) {
                amendCaps(cap, requestedCapabilityClone);
            }
            addUdid(requestedCapabilityClone);

            //set application location
            //also wait for time period before starting session
            log("Checking for environment specific app....");
            if(onHoldEnv!=null){
                requestedCapabilityClone.put("environmentUsed", onHoldEnv);
                String appType = "";
                if (requestedCapabilityClone.containsKey("platformName"))
                    appType = (String) requestedCapabilityClone.get("appType");

                String platform = "";
                if (requestedCapabilityClone.containsKey("platformName")){
                    platform = requestedCapabilityClone.get("platformName").toString();
                }
                String app = getAppForEnvironment(onHoldEnv, appType, platform);

                if(app!="")
                    requestedCapabilityClone.put("app", app);
                requestedCapabilityClone.put("environmentUsed", onHoldEnv);
                //should probably add the delay here??
                //setEnvironmentToInUse(onHoldEnv, sessionSize);
            }
            //or should the timeout be here when setting the env to in use
            //going to try here to start

            log("Creating new session....");
            TestSession session = super.getNewSession(requestedCapabilityClone);
            if (session != null) {
                requestedCapability = requestedCapabilityClone;
                log("Session created");
                log("DeviceName = "+requestedCapabilityClone.get(DEVICE_NAME));
                numberOfSessionsServed++;
                log(String.format("New session served # %d/%d", numberOfSessionsServed, sessionsToServe));
                //should probably pass the session details to this environment so that it can close it if required
                if(onHoldEnv!=null){
                    setEnvironmentToInUse(onHoldEnv, sessionSize);
                }

                try {

                    String host = session.getSlot().getRemoteURL().getHost();
                    CommonThreadPool.startCallable(new SessionHistoryCallable(session));

                    CommonThreadPool.startCallable(
                            new RemoteGridExtrasAsyncCallable(
                                    host,
                                    RuntimeConfig.getGridExtrasPort(),
                                    TaskDescriptions.Endpoints.SETUP,
                                    new HashMap<String, String>()));

                    startVideoRecording(session);


                } catch (Exception e) {
                    logger.error(e.getMessage());
                    logger.error(String.format("Error communicating with %s, \n%s",
                            session.getSlot().getProxy().getId(), e));
                }

            }else{
                if(onHoldEnv != null) {
                    log(String.format("No session available"));
                    //removeActiveFromEnvironment(onHoldEnv, sessionSize);
                    removeHoldOnEnvironment(onHoldEnv, sessionSize);
                    //session.getRequestedCapabilities().put("environmentUsed", onHoldEnv);
                }
            }
            return session;
        }
    }



    @Override
    public void afterSession(TestSession session) {
        super.afterSession(session);

        //Remove active sessions from environment
        try {
            if (session.getRequestedCapabilities().containsKey("environmentUsed")) {
                int sessionSize = 1;
                if (session.getRequestedCapabilities().containsKey("sessionSize"))
                    sessionSize = Integer.valueOf((String) session.getRequestedCapabilities().get("sessionSize"));
                String envName = (String) session.getRequestedCapabilities().get("environmentUsed");
                removeActiveFromEnvironment(envName, sessionSize);
            }
        }catch(Exception e){
            logger.error("unable to remove environment: "+ e.getMessage());
        }

        // Stop and download video only if the external session has been established
        try {
            if (session.getExternalKey() != null) {
                stopVideoRecording(session);

                CommonThreadPool.startCallable(
                        new VideoDownloaderCallable(
                                session.getExternalKey().getKey(),
                                session.getSlot().getRemoteURL().getHost()));
            }
        }catch(Exception e){
            logger.error("Unable to stop video recording: "+ e.getMessage());
        }

        String response = "";
        int appiumLimit = 0;
        int phoneLimit = 0;




        try {
            Future<String> f = CommonThreadPool.startCallable(
                    new RemoteGridExtrasAsyncCallable(
                            this.getRemoteHost().getHost(),
                            RuntimeConfig.getGridExtrasPort(),
                            TaskDescriptions.Endpoints.GRID_STATUS,
                            new HashMap<String, String>()));

            response = f.get();
            logger.debug(response);
            Map status = JsonParserWrapper.toHashMap(response);

            if (status == null) {
                String message = String.format("Problem communicating with %s, will not attempt to reboot", this.getRemoteHost().getHost());
                logger.warn(message);
            }else{
                appiumLimit = ((Double) status.get(JsonCodec.WebDriver.Grid.APPIUM_SESSIONS_LIMIT)).intValue();
                phoneLimit = ((Double) status.get(JsonCodec.WebDriver.Grid.PHONE_SESSIONS_LIMIT)).intValue();
            }
        } catch (Exception e) {
            logger.error(
                    String.format(
                            "Error getting the %s endpoint for proxy %s ",
                            TaskDescriptions.Endpoints.GRID_STATUS,
                            this.getId()),
                    e);


        }
        logger.debug(String.format("Should I restart phone? (%d/%d)", numberOfSessionsServed, phoneLimit));
        if(phoneLimit>0 && (numberOfSessionsServed % phoneLimit) == 0){
            logger.info(String.format("Restart phone. (%d/%d)", numberOfSessionsServed, phoneLimit));
            if(session.getRequestedCapabilities().containsKey("platformName")) {
                if (session.getRequestedCapabilities().get("platformName").toString().toLowerCase().equals("android")) {
                    String udid = null;
                    if (session.getRequestedCapabilities().containsKey("udid")) {
                        udid = session.getRequestedCapabilities().get("udid").toString();
                    } else {
                        for (DesiredCapabilities caps : this.getOriginalRegistrationRequest().getCapabilities()) {
                            Object capValue = caps.getCapability("udid");
                            if(capValue!=null){
                                udid = capValue.toString();
                                break;
                            }

                        }
                    }
                    if(udid!=null){
                        String deviceCategory = "physical"; //default is real device
                        if (session.getRequestedCapabilities().containsKey("deviceCategory")) {
                            udid = session.getRequestedCapabilities().get("deviceCategory").toString();
                        } else {
                            for (DesiredCapabilities caps : this.getOriginalRegistrationRequest().getCapabilities()) {
                                Object capValue = caps.getCapability("deviceCategory");
                                if(capValue!=null){
                                    deviceCategory = capValue.toString();
                                    break;
                                }

                            }
                        }
                        logger.info("devices is of type " + deviceCategory);
                        try {
                            if(deviceCategory.toLowerCase().equals("physical"))
                                restartPhone(udid);
                            if(deviceCategory.toLowerCase().equals("emulator")||deviceCategory.toLowerCase().equals("simulator"))
                                restartEmulator(udid);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        if(appiumLimit>0 && numberOfSessionsServed >= appiumLimit){
            logger.info(String.format("Restart appiun node. (%d/%d)", numberOfSessionsServed, appiumLimit));
            offLine = true;

            CommonThreadPool.startCallable(new AppiumNodeRestartCallable(this, session));
            numberOfSessionsServed = 0;
        }



        /*if (sessionsToServe > 0 && numberOfSessionsServed >= sessionsToServe) {


        }*/

        CommonThreadPool.startCallable(
                new RemoteGridExtrasAsyncCallable(
                        this.getRemoteHost().getHost(),
                        RuntimeConfig.getGridExtrasPort(),
                        TaskDescriptions.Endpoints.TEARDOWN,
                        new HashMap<String, String>()));

        /*boolean rebooting = false;
        if (NodeRestartCallable.timeToReboot(this.getRemoteHost().getHost(), this.getId())) {
            this.setAvailable(false);
            this.setRestarting(true);

            CommonThreadPool.startCallable(
                    new NodeRestartCallable(
                            this,
                            session));
            rebooting = true;
        }*/

        if (NodeRestartCallable.timeToReboot(this.getRemoteHost().getHost(), this.getId())) {
            this.setAvailable(false);
            this.setRestarting(true);

            CommonThreadPool.startCallable(
                    new NodeRestartCallable(
                            this,
                            session));
        }
    }

    private void removeActiveFromEnvironment(String envName, int sessionSize) {

        /*URIBuilder builder = new URIBuilder();
        builder.setScheme("http");
        builder.setHost(getRegistry().getHub().getUrl().getHost());
        builder.setPort(RuntimeConfig.getGridExtrasPort());
        builder.setPath(TaskDescriptions.Endpoints.ENVIRONMENT_TASKS);
        builder.setParameter("action", "removeActiveFromEnvironment");
        builder.setParameter("sessionSize", String.valueOf(sessionSize));
        builder.setParameter("envName", envName);

        URL url = null;
        try {
            url = builder.build().toURL();
            logger.debug(String.format("Removing environment to being in use: %s", envName));
            JsonObject response = HttpUtility.getRequestAsJsonObject(url);
            response.getAsJsonObject("content").get("envName").getAsString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("action", "removeActiveFromEnvironment");
        params.put("sessionSize", String.valueOf(sessionSize));
        params.put("envName", envName);
        CommonThreadPool.startCallable(
                new RemoteGridExtrasAsyncCallable(
                        getRegistry().getHub().getUrl().getHost(),
                        RuntimeConfig.getGridExtrasPort(),
                        TaskDescriptions.Endpoints.ENVIRONMENT_TASKS,
                        params));
    }

    @Override
    public void afterCommand(TestSession session, HttpServletRequest request,
                             HttpServletResponse response) {
        SeleniumBasedResponse wrappedResponse = (SeleniumBasedResponse) response;
        try {
            JsonObject json = new JsonParser().parse(wrappedResponse.getForwardedContent()).getAsJsonObject();
            int status = json.get("status").getAsInt();
            if (status != 0) {
                String content = json.get("value").getAsString();
                logger.warn(String.format("response status code is not 0", status));
                logger.warn(String.format("status: %d", status));
                logger.warn(String.format("value: %s", content));
            }
        } catch (Exception e) {
            logger.info(String.format(">>>>> Response ex: %s -> %s", e.getClass().getName(), e.getMessage()));
        }
    }



    public void restartPhone(String udid) throws IOException, URISyntaxException {
        //TODO change this to work with iphone

        //Should this method go through the admin then find the node and send the command like this?
        logger.info(String.format("Asking proxy %s to restart device " + udid, this.getId()));

        /*
        // get admin node first
        URIBuilder builder = new URIBuilder();

        // send suicide message
        builder = new URIBuilder();
        builder.setScheme("http");
        builder.setHost(HttpUtility.getRawIp(getRemoteHost().getHost()));
        builder.setPort(RuntimeConfig.getGridExtrasPort());
        builder.setPath(TaskDescriptions.Endpoints.REBOOTPHONE);
        builder.setParameter("udid", udid);

        URL url = builder.build().toURL();
        String res = HttpUtility.getRequestAsString(url);
        logger.info(String.format("Result: %s", res));
        */

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("udid", udid);
        this.available = false;
        setRestarting(true);
        setAvailable(false);
        this.offLine = true;


        CommonThreadPool.startCallable(
                new RemoteGridExtrasAsyncCallable(
                        this.getRemoteHost().getHost(),
                        RuntimeConfig.getGridExtrasPort(),
                        TaskDescriptions.Endpoints.REBOOTPHONE,
                        params));


        //maybe only sleep if response is correct?
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setRestarting(false);
        setAvailable(true);
        offLine = false;

    }

    public void restartEmulator(String udid) throws IOException, URISyntaxException {
        //TODO change this to work with iphone

        //Should this method go through the admin then find the node and send the command like this?
        logger.info(String.format("Asking proxy %s to restart emulator "+ udid, this.getId()));



        HashMap<String, String> params = new HashMap<String, String>();
        params.put("udid", udid);
        this.available = false;
        setRestarting(true);
        setAvailable(false);
        this.offLine = true;


        CommonThreadPool.startCallable(
                new RemoteGridExtrasAsyncCallable(
                        this.getRemoteHost().getHost(),
                        RuntimeConfig.getGridExtrasPort(),
                        TaskDescriptions.Endpoints.REBOOTEMULATOR,
                        params));


        //maybe only sleep if response is correct?
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setRestarting(false);
        setAvailable(true);
        offLine = false;

    }


    private void log(String log){
        logger.info(String.format(log));
    }

    private String getAppForEnvironment(String onHoldEnv, String appType, String platform) {

        /*Map<String, String> params = new HashMap<String, String>();
        params.put("action", "getAppForEnvironment");
        params.put("envName", onHoldEnv);
        params.put("appType", appType);


        String hubUrl = getRegistry().getHub().getUrl().getHost();
        Future<String> f = com.groupon.seleniumgridextras.utilities.threads.CommonThreadPool.startCallable(
                new RemoteGridExtrasAsyncCallable(
                        hubUrl,
                        RuntimeConfig.getGridExtrasPort(),
                        TaskDescriptions.Endpoints.ENVIRONMENT_TASKS,
                        params));

        //how to get the value out of the response?
        */


        String appToFind = "app-"+ platform+"-"+ appType;

        /*URIBuilder builder = new URIBuilder();
        builder.setScheme("http");
        builder.setHost(getRegistry().getHub().getUrl().getHost());
        builder.setPort(RuntimeConfig.getGridExtrasPort());
        builder.setPath(TaskDescriptions.Endpoints.ENVIRONMENT_TASKS);
        builder.setParameter("action", "getAppForEnvironment");
        builder.setParameter("envName", onHoldEnv);
        builder.setParameter("appType", appToFind);
        builder.setParameter("platform", platform);

        URL url = null;
        try {
            url = builder.build().toURL();
            logger.info(String.format("Getting app for environment: %s", onHoldEnv));
            JsonObject response = HttpUtility.getRequestAsJsonObject(url);
            String app = response.getAsJsonObject("content").get("app").getAsString();
            logger.info(String.format("App: %s", app));
            return app;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("action", "getAppForEnvironment");
        params.put("envName", onHoldEnv);
        params.put("appType", appToFind);
        params.put("platform", platform);
        Future<String> f = CommonThreadPool.startCallable(
                new RemoteGridExtrasAsyncCallable(
                        getRegistry().getHub().getUrl().getHost(),
                        RuntimeConfig.getGridExtrasPort(),
                        TaskDescriptions.Endpoints.ENVIRONMENT_TASKS,
                        params));
        String response = "";
        try {
            response = f.get();
            logger.debug(response);
            Map status = JsonParserWrapper.toHashMap(response);

            if (status == null) {
                String message = String.format("Problem communicating with %s, will not attempt to reboot", this.getRemoteHost().getHost());
                logger.warn(message);
            }else{
                return status.get("app").toString();
            }
        } catch (Exception e) {
        }
        return "";
    }

    private void removeHoldOnEnvironment(String onHoldEnv, int sessionSize) {

        /*URIBuilder builder = new URIBuilder();
        builder.setScheme("http");
        builder.setHost(getRegistry().getHub().getUrl().getHost());
        builder.setPort(RuntimeConfig.getGridExtrasPort());
        builder.setPath(TaskDescriptions.Endpoints.ENVIRONMENT_TASKS);
        builder.setParameter("action", "removeHoldOnEnvironment");
        builder.setParameter("sessionSize", String.valueOf(sessionSize));
        builder.setParameter("envName", onHoldEnv);

        URL url = null;
        try {
            url = builder.build().toURL();
            logger.info(String.format("Removing environment from being on hold: %s", onHoldEnv));
            JsonObject response = HttpUtility.getRequestAsJsonObject(url);
            response.getAsJsonObject("content").get("envName").getAsString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("action", "removeHoldOnEnvironment");
        params.put("sessionSize", String.valueOf(sessionSize));
        params.put("envName", onHoldEnv);

        Future<String> f = CommonThreadPool.startCallable(
                new RemoteGridExtrasAsyncCallable(
                        getRegistry().getHub().getUrl().getHost(),
                        RuntimeConfig.getGridExtrasPort(),
                        TaskDescriptions.Endpoints.ENVIRONMENT_TASKS,
                        params));
    }

    private void setEnvironmentToInUse(String onHoldEnv, int sessionSize) {

        /*URIBuilder builder = new URIBuilder();
        builder.setScheme("http");
        builder.setHost(getRegistry().getHub().getUrl().getHost());
        builder.setPort(RuntimeConfig.getGridExtrasPort());
        builder.setPath(TaskDescriptions.Endpoints.ENVIRONMENT_TASKS);
        builder.setParameter("action", "setEnvironmentToInUse");
        builder.setParameter("sessionSize", String.valueOf(sessionSize));
        builder.setParameter("envName", onHoldEnv);

        URL url = null;
        try {
            url = builder.build().toURL();
            logger.info(String.format("Setting environment to being in use: %s", onHoldEnv));
            JsonObject response = HttpUtility.getRequestAsJsonObject(url);
            response.getAsJsonObject("content").get("envName").getAsString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("action", "setEnvironmentToInUse");
        params.put("sessionSize", String.valueOf(sessionSize));
        params.put("envName", onHoldEnv);

        Future<String> f = CommonThreadPool.startCallable(
                new RemoteGridExtrasAsyncCallable(
                        getRegistry().getHub().getUrl().getHost(),
                        RuntimeConfig.getGridExtrasPort(),
                        TaskDescriptions.Endpoints.ENVIRONMENT_TASKS,
                        params));
    }

    private String getAvailableEnvName(String envType, int sessionSize, String envBackend) {
        /*
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http");
        builder.setHost(getRegistry().getHub().getUrl().getHost());
        builder.setPort(RuntimeConfig.getGridExtrasPort());
        builder.setPath(TaskDescriptions.Endpoints.ENVIRONMENT_TASKS);
        builder.setParameter("action", "environmentAvailable");
        builder.setParameter("sessionSize", String.valueOf(sessionSize));
        builder.setParameter("envType", envType);
        builder.setParameter("envBackend",envBackend);

        URL url = null;
        try {
            url = builder.build().toURL();
            logger.debug(String.format("Looking for environment of type: %s", envType));
            logger.debug(String.format("Using url: %s", getRegistry().getHub().getUrl().getHost()));
            logger.debug(String.format("Using port: %s", getRegistry().getHub().getPort()));
            JsonObject response = HttpUtility.getRequestAsJsonObject(url);
            String envToUse = response.getAsJsonObject("content").get("environmentOnHold").getAsString();
            logger.debug(String.format("Environment to use: %s", envToUse));
            return envToUse;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("action", "environmentAvailable");
        params.put("sessionSize", String.valueOf(sessionSize));
        params.put("envType", envType);
        params.put("envBackend",envBackend);
        Future<String> f = CommonThreadPool.startCallable(
                new RemoteGridExtrasAsyncCallable(
                        getRegistry().getHub().getUrl().getHost(),
                        RuntimeConfig.getGridExtrasPort(),
                        TaskDescriptions.Endpoints.ENVIRONMENT_TASKS,
                        params));
        String response = "";
        try {
            response = f.get();
            logger.debug(response);
            Map status = JsonParserWrapper.toHashMap(response);

            if (status == null) {
                String message = String.format("Problem communicating with %s, will not attempt to reboot", this.getRemoteHost().getHost());
                logger.warn(message);
            }else{
                return status.get("environmentOnHold").toString();
            }
        } catch (Exception e) {
        }

        return "noEnvironmentFound";
    }

    private void amendCaps(String capName, Map<String, Object> requestedCapability) {
        logger.debug(String.format("Amending capabilities"));


        for(DesiredCapabilities caps : this.getOriginalRegistrationRequest().getCapabilities()) {
            Object capValue = caps.getCapability(capName);
            if (capValue != null && requestedCapability.get(capName)==null) {
                logger.debug("--------- changing requested capability");
                try {
                    logger.debug("current value " + capName + " to " + requestedCapability.get(capName));
                }catch(Exception e){
                    logger.debug("error with current value");
                }
                logger.debug("setting " + capName + " to " + capValue);
                requestedCapability.put(capName, capValue);
                break;
            }
        }

    }

    //Add udid to the set of requested capabilities so that we can restart the device.
    //do we need this?
    private void addUdid(Map<String, Object> requestedCapability){
        /*try {
            String deviceType = "physical";
            if (requestedCapability.containsKey(DEVICE_TYPE)) {
                deviceType = requestedCapability.get(DEVICE_TYPE).toString();
                logger.debug("device type = " + deviceType);
            }
            if (requestedCapability.containsKey(PLATFORM_NAME) && deviceType.toLowerCase().equals("physical")) {
                logger.debug("setting udid");
                for(DesiredCapabilities caps : this.getOriginalRegistrationRequest().getCapabilities()) {
                    Object capValue = caps.getCapability("udid");
                    if (capValue != null && requestedCapability.get("udid")==null){
                        requestedCapability.put("udid", capValue.toString());
                        break;
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }*/

        /*logger.debug("setting udid");
        for(DesiredCapabilities caps : this.getOriginalRegistrationRequest().getCapabilities()) {
            Object capValue = caps.getCapability("udid");
            if (capValue != null && requestedCapability.get("udid")==null){
                requestedCapability.put("udid", capValue.toString());
                break;
            }
        }
        logger.debug("no udid");*/
    }

    public void unregister() {
        addNewEvent(new RemoteUnregisterException(String.format("Taking proxy %s offline", this.getId())));
    }


    private String inputStreamToString(InputStream is) throws IOException {
        StringBuilder str = new StringBuilder();
        int in;
        while ((in = is.read()) != -1) {
            str.append((char) in);
        }
        is.close();
        return str.toString();
    }

    boolean alreadyRecordingCurrentSession(TestSession session) {
        if ((session.getExternalKey() == null) || !getSessionsRecording().contains(session.getExternalKey().getKey())) {
            return false;
        }

        return true;
    }


    void startVideoRecording(TestSession session) {

        if (alreadyRecordingCurrentSession(session)) {
            return;
        }

        CommonThreadPool.startCallable(
                new RemoteVideoRecordingControlCallable(
                        this,
                        session,
                        JsonCodec.Video.START));
    }

    void stopVideoRecording(TestSession session) {
        Future a = CommonThreadPool.startCallable(
                new RemoteVideoRecordingControlCallable(
                        this,
                        session,
                        JsonCodec.Video.STOP));

        try {
            logger.debug(String.format(
                    "Waiting for stop command to finish for session: %s, output:\n%s",
                    session.getExternalKey().getKey(),
                    a.get()));
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        } catch (ExecutionException e) {
            logger.error(e.getMessage(), e);
        }
    }

    void updateLastCommand(TestSession session, HttpServletRequest request) {

        if (session.getExternalKey() == null) {
            return;
        }

        try {
            String
                    command =
                    new JsonWireCommandTranslator(request.getMethod(), request.getRequestURI(),
                            JsonWireCommandTranslator.getBodyAsString(request))
                            .toString();

            CommonThreadPool.startCallable(
                    new RemoteVideoRecordingControlCallable(
                            this,
                            session,
                            JsonCodec.Video.HEARTBEAT,
                            command));
        } catch (Exception e) {
            logger.error(String.format("Error updating last action for int. key: %s"), e);
        }
    }


    public List<String> getSessionsRecording() {
        return this.sessionsRecording;
    }

    protected boolean isAvailable() {
        return this.available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isRestarting() {
        return this.restarting;
    }

    public void setRestarting(boolean restarting) {
        this.restarting = restarting;
    }

    @Override
    public void beforeCommand(TestSession session, HttpServletRequest request,
                              HttpServletResponse response) {
        updateLastCommand(session, request);
        session.put("lastCommand", request.getMethod() + " - " + request.getPathInfo() + " executed.");
    }
}
