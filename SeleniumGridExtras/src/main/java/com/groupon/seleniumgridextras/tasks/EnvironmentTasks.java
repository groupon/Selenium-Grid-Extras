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

package com.groupon.seleniumgridextras.tasks;

import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.utilities.JsonResponseBuilder;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import com.groupon.seleniumgridextras.utilities.Environment;
import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

public class EnvironmentTasks extends ExecuteOSTask {


    public String envName = null;
    public String envType = null;
    public String envBackend = null;
    public int maxSessions = 1;
    public int activeSessions = 0;
    public int delayStart = 0;
    public String app = "";
    public String iosApp = "";

    public String androidApp = "";

    public String appType = "";

    public int sessionSize = 1;

    public String action = "";

    private static ConcurrentHashMap<String, Environment> environments;
    private static Logger logger = Logger.getLogger(EnvironmentTasks.class);


    public EnvironmentTasks() {
        setEndpoint(TaskDescriptions.Endpoints.ENVIRONMENT_TASKS);
        setDescription(
                TaskDescriptions.Description.ENVIRONMENT_TASKS);
        JsonObject params = new JsonObject();
        setAcceptedParams(params);
        setRequestType("GET");
        setResponseType("json");
        setClassname(this.getClass().getCanonicalName().toString());
        setCssClass("btn-success");
        setButtonText(TaskDescriptions.UI.ButtonText.ENVIRONMENT_TASKS);
        setEnabledInGui(true);


    }




    @Override
    public JsonObject execute(Map<String, String> parameter) {
        if (environments == null) {
            environments = new ConcurrentHashMap();
        }

        loadParameters(parameter);

        logger.info(String.format("Environment action: [%s]", action));
        if (action.equals("add")) {
            add(parameter);
            return JsonResponseBuilder.newResponse(JsonResponseBuilder.ResponseCode.SUCCESS).withProperty("envName", envName).build();
        } else if (action.equals("environmentAvailable")) {
            String environmentOnHold = environmentAvailable();
            return JsonResponseBuilder.newResponse(JsonResponseBuilder.ResponseCode.SUCCESS).withProperty("environmentOnHold", environmentOnHold).build();
        } else if (action.equals("setEnvironmentToInUse")) {
            setEnvironmentToInUse();
            return JsonResponseBuilder.newResponse(JsonResponseBuilder.ResponseCode.SUCCESS).withProperty("envName", envName).build();
        } else if (action.equals("removeHoldOnEnvironment")) {
            removeHoldOnEnvironment();
            return JsonResponseBuilder.newResponse(JsonResponseBuilder.ResponseCode.SUCCESS).withProperty("envName", envName).build();
        } else if (action.equals("removeActiveFromEnvironment")) {
            removeActiveFromEnvironment();
            return JsonResponseBuilder.newResponse(JsonResponseBuilder.ResponseCode.SUCCESS).withProperty("envName", envName).build();
        } else if (action.equals("getAppForEnvironment")) {
            app = getAppForEnvironment(parameter);
            return JsonResponseBuilder.newResponse(JsonResponseBuilder.ResponseCode.SUCCESS).withProperty("app", app).build();
        } else if (action.equals("getEnvironmentDetails")) {
            Environment environment = getEnvironmentDetails();
            return JsonResponseBuilder.newResponse(JsonResponseBuilder.ResponseCode.SUCCESS).withProperty("envName", environment.envName).build();
        } else if (action.equals("getAllEnvironmentDetails")) {
            getAllEnvironmentDetails();
            return JsonResponseBuilder.newResponse(JsonResponseBuilder.ResponseCode.SUCCESS).withProperty("envName", "searching All").build();
        }
        return quickReply(JsonResponseBuilder.ResponseCode.ERROR, "No task found.");
    }

    //TODO this is so ugly - Redo, maybe use our nice tasks, just getting this working for now
    private void loadParameters(Map<String, String> parameter) {
        try {
            action = parameter.get("action").toString();
        }catch(Exception e){action = "";}
        try {
            envName = parameter.get("envName").toString();
        }catch(Exception e){envName = "";}
        try {
            envType = parameter.get("envType").toString();
        }catch(Exception e){envType="";}
        try {
            envBackend = parameter.get("envBackend").toString();
        }catch(Exception e){envBackend="";}
        try {
            maxSessions = Integer.parseInt(parameter.get("maxSessions").toString());
        }catch(Exception e){maxSessions=1;}
        try {
            activeSessions = Integer.parseInt(parameter.get("activeSessions").toString());
        }catch(Exception e){activeSessions=0;}
        try {
            delayStart = Integer.parseInt(parameter.get("delayStart").toString());
        }catch(Exception e){delayStart=0;}
        /*try {
            app = parameter.get("app").toString();
        }catch(Exception e){}
        try {
            iosApp = parameter.get("iosApp").toString();
        }catch(Exception e){}
        try {
            androidApp = parameter.get("androidApp").toString();
        }catch(Exception e){}
        */try {
            appType = parameter.get("appType").toString();
        }catch(Exception e){}
        try {
            sessionSize = Integer.parseInt(parameter.get("sessionSize").toString());
        }catch(Exception e){}

    }

    private Map<String, String> loadAppDetails(Map<String, String> parameter){
        HashMap<String, String> apps = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : parameter.entrySet()) {
            if(entry.getKey().startsWith("app-"))
                apps.put(entry.getKey(), entry.getValue());
        }
        return apps;
    }

    protected JsonObject quickReply(JsonResponseBuilder.ResponseCode responseCode, String message) {
        logger.info(message);
        return JsonResponseBuilder.newResponse(responseCode).withMessage(message).build();
    }

    private Environment getEnvironmentDetails() {
        Environment environment = environments.get(envName);
        logger.info(String.format("Environment Details"));
        logger.info(String.format("Using environment: [%s]", environment.envName));
        logger.info(String.format("Environment type: [%s]", environment.envType));
        logger.info(String.format("Max Sessions: [%s]", environment.maxSessions));
        //logger.info(String.format("App for environment: [%s]", environment.app));
        return environment;
    }

    private String getAllEnvironmentDetails() {
        logger.info(String.format("Number of environments: [%s]", environments.size()));
        for(Environment environment:environments.values()) {
            logger.info(String.format("Environment Details"));
            logger.info(String.format("Using environment: [%s]", environment.envName));
            logger.info(String.format("Environment type: [%s]", environment.envType));
            logger.info(String.format("Max Sessions: [%s]", environment.maxSessions));
            //logger.info(String.format("App for environment: [%s]", environment.app));
        }
        for (Map.Entry<String, Environment> entry : environments.entrySet()) {
            logger.info(String.format("Using key: [%s]", entry.getKey()));
        }

        return "";
    }


    //Should change this to being nested json with a type and a value and search through, this would enable more types
    private String getAppForEnvironment(Map<String, String> parameter) {
        Environment environment = environments.get(envName);
        logger.info(String.format("Getting app for environment"));
        logger.info(String.format("Using environment: [%s]", environment.envName));
        logger.info(String.format("Environment type: [%s]", environment.envType));

        //logger.info(String.format("App: [%s]", environment.app));
        //logger.info(String.format("Android App: [%s]", environment.androidApp));
        //logger.info(String.format("iOS App: [%s]", environment.iosApp));
        logger.info(String.format("App Type: [%s]", appType));

        String appToUse = "";
        /*if(appType.equals("")) {
            appToUse = environment.app;
        }else{
            environment.apps.get(appType);
        }*/
        if(environment.apps.containsKey(appType))
            appToUse = environment.apps.get(appType);
        logger.info(String.format("App for environment: [%s]", appToUse));
        return appToUse;
    }

    private void removeActiveFromEnvironment() {
        Environment environment = environments.get(envName);
        if(sessionSize==-1)
            sessionSize = environment.maxSessions;
        environment.activeSessions = environment.activeSessions-sessionSize;
        logger.info(String.format("Removing active sessions from environment"));
        logger.info(String.format("Using environment: [%s]", environment.envName));
        logger.info(String.format("Environment type: [%s]", environment.envType));
        logger.info(String.format("number of active sessions: [%s]", environment.activeSessions));
        logger.info(String.format("number of onhold sessions: [%s]", environment.onHoldSessions));
        logger.info(String.format("number of max sessions: [%s]", environment.maxSessions));
    }

    private void removeHoldOnEnvironment() {
        Environment environment = environments.get(envName);
        if(sessionSize==-1)
            sessionSize = environment.maxSessions;
        environment.onHoldSessions = environment.onHoldSessions - sessionSize;
        logger.info(String.format("Removing on hold sessions for environment"));
        logger.info(String.format("Using environment: [%s]", environment.envName));
        logger.info(String.format("Environment type: [%s]", environment.envType));
        logger.info(String.format("number of active sessions: [%s]", environment.activeSessions));
        logger.info(String.format("number of onhold sessions: [%s]", environment.onHoldSessions));
        logger.info(String.format("number of max sessions: [%s]", environment.maxSessions));
    }

    private void setEnvironmentToInUse() {
        logger.info(String.format("Finding name"));
        Environment environment = environments.get(envName);
        logger.info(String.format("Found environment"));
        if(sessionSize==-1)
            sessionSize = environment.maxSessions;
        environment.onHoldSessions = environment.onHoldSessions - sessionSize;
        environment.activeSessions = environment.activeSessions + sessionSize;
        Calendar currentTime = Calendar.getInstance();
        logger.info(String.format("This should be removed from here and do the waiting in the node"));
        logger.info(String.format("time for request: [%s]", currentTime.getTimeInMillis()));
        logger.info(String.format("environment time: [%s]", environment.lastSessionServed.getTimeInMillis()));

        //This should be removed from here and do the waiting in the node.

        environment.lastSessionServed.add(Calendar.SECOND, environment.delayStart);
        while(environment.lastSessionServed.compareTo(currentTime)==1){
            try {
                Thread.sleep(1000);
                currentTime = Calendar.getInstance();
                logger.info(String.format("New request time: [%s]", currentTime.getTimeInMillis()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        environment.lastSessionServed = Calendar.getInstance();
        logger.info(String.format("Setting environment to being in use"));
        logger.info(String.format("Using environment: [%s]", environment.envName));
        logger.info(String.format("Environment type: [%s]", environment.envType));
        logger.info(String.format("number of active sessions: [%s]", environment.activeSessions));
        logger.info(String.format("number of onhold sessions: [%s]", environment.onHoldSessions));
        logger.info(String.format("number of max sessions: [%s]", environment.maxSessions));
    }

    //making this use session size as maybe at some stage we have session size of 0 for something :/
    //TODO smarter - distribute tests outs evenly, not just to first environment that matches.  Maybe even make this use  the grid itself.
    //wasn't doing a call back to the grid because
    //1. no idea of session size
    //2. no idea of waiting before starting new sessions
    //3. unable to put a environment on hold, does this matter?
    //4. timeouts? what do we do in this case :/
    //5. getting information out like the app might be difficult
    private String environmentAvailable() {
        logger.info(String.format("Checking for available environment"));
        logger.info(String.format("Number of environments: [%s]", environments.size()));
        logger.info(String.format("Looking for environment with type: [%s]", envType));
        logger.info(String.format("Looking for environment with backend: [%s]", envBackend));
        for(Environment environment:environments.values()){
            if(environment.envType.equals(envType)&&environment.envBackend.equals(envBackend)){
                if(sessionSize==-1){
                    if((environment.activeSessions==0&&environment.onHoldSessions==0)){
                        environment.onHoldSessions = environment.maxSessions;
                        logger.info(String.format("Using environment: [%s]", environment.envName));
                        logger.info(String.format("Environment type: [%s]", environment.envType));
                        logger.info(String.format("number of active sessions: [%s]", environment.activeSessions));
                        logger.info(String.format("number of onhold sessions: [%s]", environment.onHoldSessions));
                        logger.info(String.format("number of max sessions: [%s]", environment.maxSessions));
                        return environment.envName;
                    }
                }else{
                    if(environment.activeSessions+environment.onHoldSessions+sessionSize<=environment.maxSessions){
                        environment.onHoldSessions = environment.onHoldSessions+sessionSize;
                        logger.info(String.format("Using environment: [%s]", environment.envName));
                        logger.info(String.format("Environment type: [%s]", environment.envType));
                        logger.info(String.format("number of active sessions: [%s]", environment.activeSessions));
                        logger.info(String.format("number of onhold sessions: [%s]", environment.onHoldSessions));
                        logger.info(String.format("number of max sessions: [%s]", environment.maxSessions));
                        return environment.envName;
                    }
                }
            }
        }
        logger.info(String.format("No environment found"));
        return "noEnvironmentFound";
    }

    private void add(Map<String, String> parameter) {
        Map<String, String> apps = loadAppDetails(parameter);
        Environment env = new Environment(envName, envType, envBackend, maxSessions, delayStart, apps);
        logger.info(String.format("Adding environment"));
        logger.info(String.format("Using environment: [%s]", envName));
        logger.info(String.format("Environment type: [%s]", envType));
        logger.info(String.format("Max Sessions: [%s]", maxSessions));
        logger.info(String.format("App: [%s]", app));
        logger.info(String.format("Android App: [%s]", androidApp));
        logger.info(String.format("iOS App: [%s]", iosApp));


        environments.put(envName, env);
    }

}
