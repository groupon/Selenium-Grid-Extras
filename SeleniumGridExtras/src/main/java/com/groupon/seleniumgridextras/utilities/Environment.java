package com.groupon.seleniumgridextras.utilities;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.json.JsonFileReader;
import com.groupon.seleniumgridextras.utilities.threads.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by jfarrier on 22/04/2015.
 */


public class Environment {

    //should have a session on hold where we have checked for a session but not yet allocated.

    public String envName;
    public String envType;
    public String envBackend;

    public int maxSessions = 1;
    public int onHoldSessions = 0;
    public int activeSessions = 0;
    public int delayStart = 0;

    public Map<String, String> apps;



    public String hubUrl;
    public int hubPort;

    public Calendar lastSessionServed;

    private static Logger logger = Logger.getLogger(Environment.class);

    public Environment(File file) {
        JsonObject config = null;
        try {
            config = JsonFileReader.getJsonObject(file);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        envName = config.get("envName").getAsString();
        envType = config.get("envType").getAsString();
        envBackend = config.get("envBackend").getAsString();

        maxSessions = config.get("maxSessions").getAsInt();
        delayStart = config.get("delayStart").getAsInt();

        apps = new HashMap<String, String>();
        for (Map.Entry<String,JsonElement> entry : config.entrySet()) {
            if(entry.getKey().startsWith("app-"))
                apps.put(entry.getKey(), entry.getValue().getAsString());
        }

        Map<String, String> params = new HashMap<String, String>();

        params.put("action", "add");
        params.put("envName", envName);
        params.put("envType", envType);
        params.put("envBackend", envBackend);
        params.put("maxSessions", String.valueOf(maxSessions));
        params.put("delayStart", String.valueOf(delayStart));
        for(Map.Entry<String,String> entry : apps.entrySet()) {
            params.put(entry.getKey(), entry.getValue());
        }
        /*params.put("app", app);
        params.put("iosApp", iosApp);
        params.put("androidApp", androidApp);*/

        hubUrl = config.get("hubUrl").getAsString();
        hubPort = config.get("hubPort").getAsInt();
        Future<String> f = com.groupon.seleniumgridextras.utilities.threads.CommonThreadPool.startCallable(
                new RemoteGridExtrasAsyncCallable(
                        hubUrl,
                        RuntimeConfig.getGridExtrasPort(),
                        TaskDescriptions.Endpoints.ENVIRONMENT_TASKS,
                        params));



    }

    public Environment(String envName, String envType, String envBackend, int maxSessions, int delayStart, Map<String, String> apps) {
        this.envName = envName;
        this.envType = envType;
        this.envBackend = envBackend;
        this.maxSessions = maxSessions;
        this.delayStart = delayStart;
        this.apps = apps;
        lastSessionServed = Calendar.getInstance();
    }
}
