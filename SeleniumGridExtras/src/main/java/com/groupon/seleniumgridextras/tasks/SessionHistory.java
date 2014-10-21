package com.groupon.seleniumgridextras.tasks;

import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.loggers.SessionHistoryLog;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import org.apache.log4j.Logger;

import java.util.Map;

public class SessionHistory extends ExecuteOSTask {
    private static Logger logger = Logger.getLogger(SessionHistory.class);

    public SessionHistory() {
        setEndpoint(TaskDescriptions.Endpoints.SESSION_HISTORY);
        setDescription(TaskDescriptions.Description.SESSION_HISTORY);
        JsonObject params = new JsonObject();

        params.addProperty(JsonCodec.WebDriver.Grid.SESSION_ID, "Session used to identify current session");
        params.addProperty(JsonCodec.WebDriver.Grid.HOST, "Host on which the session is kept");
        params.addProperty(JsonCodec.WebDriver.Grid.ACTION, "(" + JsonCodec.SessionLogging.NEW + "/" + JsonCodec.SessionLogging.GET + ") - Notify if the request is to register a new session or retrieve a log. Defaults to '" + JsonCodec.SessionLogging.GET + "'");


        addResponseDescription(JsonCodec.WebDriver.Grid.LOGS, "An array with session history per node");


        setAcceptedParams(params);
        setRequestType("GET");
        setResponseType("json");
        setClassname(this.getClass().getCanonicalName().toString());
        setCssClass("btn-info");


    }


    @Override
    public JsonObject execute() {
        getJsonResponse().addNestedMapValues(JsonCodec.WebDriver.Grid.LOGS, SessionHistoryLog.getTodaysHistoryAsMap());
        return getJsonResponse().getJson();
    }

    @Override
    public JsonObject execute(String param) {
        return execute();
    }

    @Override
    public JsonObject execute(Map<String, String> parameters) {
        if (parameters.containsKey(JsonCodec.WebDriver.Grid.ACTION) && parameters.get(JsonCodec.WebDriver.Grid.ACTION).equals(JsonCodec.SessionLogging.NEW)) {
            if (parameters.containsKey(JsonCodec.WebDriver.Grid.HOST) && parameters.containsKey(JsonCodec.WebDriver.Grid.SESSION_ID)) {
                SessionHistoryLog.newSession(parameters.get(JsonCodec.WebDriver.Grid.HOST), parameters);
                getJsonResponse().addKeyValues(JsonCodec.OUT, parameters.toString());
            } else {
                getJsonResponse().addKeyValues(JsonCodec.ERROR, JsonCodec.WebDriver.Grid.HOST + " and " + JsonCodec.WebDriver.Grid.SESSION_ID + " params are required when starting new session");
            }

            return getJsonResponse().getJson();
        } else {
            return execute();
        }
    }
}
