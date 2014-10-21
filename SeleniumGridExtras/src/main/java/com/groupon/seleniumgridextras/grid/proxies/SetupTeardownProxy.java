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

import com.groupon.seleniumgridextras.grid.proxies.sessions.history.SessionHistoryThreadPool;
import com.groupon.seleniumgridextras.utilities.HttpUtility;
import com.groupon.seleniumgridextras.utilities.JsonWireCommandTranslator;

import org.apache.log4j.Logger;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.common.exception.RemoteUnregisterException;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.internal.listeners.TestSessionListener;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class SetupTeardownProxy extends DefaultRemoteProxy implements TestSessionListener {

    private boolean available = true;
    private boolean restarting = false;
    private List<String> sessionsRecording = new LinkedList<String>();

    private static Logger logger = Logger.getLogger(SetupTeardownProxy.class);


    public SetupTeardownProxy(RegistrationRequest request, Registry registry) {
        super(request, registry);
        writeProxyLog("Attaching a node: " + getHost());
    }

    @Override
    public TestSession getNewSession(Map<String, Object> requestedCapability) {
        synchronized (this) {
            if (timeToReboot() && !this.isBusy()) {
                setAvailable(false);
                setRestarting(false);
//        killBrowserForCurrentSession();
                stopGridNode();
                rebootGridExtrasNode();
            }

            if (isAvailable()) {
                TestSession session = super.getNewSession(requestedCapability);
                if (session == null) {
                    return null;
                } else {
                    SessionHistoryThreadPool.registerNewSession(session);
                    return session;
                }
            } else {
                return null;
            }

        }
    }

    @Override
    public void beforeCommand(TestSession session, HttpServletRequest request,
                              HttpServletResponse response) {

        if (session.getExternalKey() != null) {
            if (!alreadyRecordingCurrentSession(session.getExternalKey().getKey())) {
                startVideoRecording(session.getExternalKey().getKey());
            }

            updateLastCommand(session.getExternalKey().getKey(), request);
        }

        session.put("lastCommand", request.getMethod() + " - " + request.getPathInfo() + " executed.");
    }

    @Override
    public void beforeSession(TestSession session) {
        super.beforeSession(session);
        callRemoteGridExtrasAsync("setup", new HashMap<String, String>());
    }

    @Override
    public void afterSession(TestSession session) {
        super.afterSession(session);
        stopVideoRecording(session.getExternalKey().getKey());
        callRemoteGridExtrasAsync("teardown", new HashMap<String, String>());
    }

    private boolean alreadyRecordingCurrentSession(String session) {
        return this.sessionsRecording.contains(session);
    }


    private void startVideoRecording(String session) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("session", session);
        params.put("action", "start");

        callRemoteGridExtrasAsync("video", params);
        this.sessionsRecording.add(session);
    }

    private void stopVideoRecording(String session) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("session", session);
        params.put("action", "stop");
        callRemoteGridExtrasAsync("video", params);
    }

    private void updateLastCommand(String session, HttpServletRequest request) {
        String
                command =
                new JsonWireCommandTranslator(request.getMethod(), request.getRequestURI(),
                        JsonWireCommandTranslator.getBodyAsString(request))
                        .toString();

        try {
            command = URLEncoder.encode(command, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.warn("Encoding with UTF-8 Failed, falling back to deprecated method");
            command = URLEncoder.encode(command);

        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("session", session);
        params.put("action", "heartbeat");
        params.put("description", command);

        callRemoteGridExtrasAsync("video", params);
    }

    protected void stopGridNode() {
        writeProxyLog("Asking " + getHost() + " to stop grid node politely");
        writeProxyLog(callRemoteGridExtras("stop_grid?port=5555"));
        unregister();
    }

    private void rebootGridExtrasNode() {
        writeProxyLog("Asking SeleniumGridExtras to reboot " + getHost());
        writeProxyLog(callRemoteGridExtras("reboot"));
    }

    private Future<String> callRemoteGridExtrasAsync(String action, Map<String, String> params) {
        Future<String> returnedFuture;
        String parameters = "";

        if (!params.isEmpty()) {
            StringBuilder parameterBuilder = new StringBuilder();
            parameterBuilder.append("?");

            for (String currentParam : params.keySet()) {
                parameterBuilder.append(currentParam + "=" + params.get(currentParam) + "&");
            }

            parameters = parameterBuilder.toString().replaceAll("&$", "");
        }

        try {

            returnedFuture =
                    HttpUtility
                            .makeAsyncGetRequest(new URI("http://" + getHost() + ":3000/" + action + parameters));
        } catch (URISyntaxException e) {
            logger.warn(e);
            return null;
        }

        JsonParser j = new JsonParser();
        logger.info("Async Request is done: " + returnedFuture.isDone());
        return returnedFuture;
    }

    private JsonObject callRemoteGridExtras(String action) {
        String returnedString;

        try {

            returnedString = HttpUtility.getRequestAsString(
                    new URL("http://" + getHost() + ":3000/" + action));

            JsonParser j = new JsonParser();
            logger.debug(returnedString);
            return (JsonObject) j.parse(returnedString);

        } catch (MalformedURLException e) {
            writeProxyLog(e.toString());
            e.printStackTrace();
        } catch (ProtocolException e) {
            writeProxyLog(e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected String getHost() {
        return this.getRemoteHost().getHost();
    }

    protected boolean isAvailable() {
        return this.available;
    }

    protected void setAvailable(boolean available) {
        this.available = available;
    }

    protected boolean timeToReboot() {
        JsonObject status = callRemoteGridExtras("grid_status");

        boolean nodeRunning = status.get("node_running").getAsBoolean();
        int sessionsStarted = status.get("node_sessions_started").getAsInt();
        int sessionLimit = status.get("node_sessions_limit").getAsInt();

        if (!nodeRunning) {
            writeProxyLog("The grid node on " + getHost() + " does not seem to be running");
        } else if (sessionLimit == 0) {
            return false;
        } else if (sessionsStarted >= sessionLimit) {
            System.out
                    .println("Node " + getHost() + " has reached " + sessionsStarted + " of " + sessionLimit
                            + " test session, time to reboot");
        } else {
            return false;

        }
        setAvailable(false);
        return true;
    }

    public void unregister() {
        writeProxyLog("Sending Un register command for " + getHost());
        addNewEvent(new RemoteUnregisterException("Unregistering the node."));
    }

    public boolean isRestarting() {
        return this.restarting;
    }

    public void setRestarting(boolean restarting) {
        this.restarting = restarting;
    }

    private void writeProxyLog(Object logItem) {
        logger.info(logItem.toString());
    }

}
