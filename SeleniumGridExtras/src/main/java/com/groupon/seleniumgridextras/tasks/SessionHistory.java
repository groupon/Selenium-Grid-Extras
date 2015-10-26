package com.groupon.seleniumgridextras.tasks;

import com.google.common.base.Throwables;
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

//        params.addProperty(JsonCodec.WebDriver.Grid.SESSION_ID, "Session used to identify current session");
//        params.addProperty(JsonCodec.WebDriver.Grid.HOST, "Host on which the session is kept");


        addResponseDescription(JsonCodec.WebDriver.Grid.LOGS, "An array with session threads per node");


        setAcceptedParams(params);
        setRequestType("GET");
        setResponseType("json");
        setClassname(this.getClass().getCanonicalName());
        setCssClass("btn-info");


    }


    @Override
    public JsonObject execute() {
        try {
            getJsonResponse().addNestedMapValues(JsonCodec.WebDriver.Grid.LOGS, SessionHistoryLog.getTodaysHistoryAsMap());
        } catch (Exception e) {
            String error = String.format("Something went wrong when reading session threads\n%s",
                    Throwables.getStackTraceAsString(e));
           logger.error(error);
            getJsonResponse().addKeyValues(JsonCodec.ERROR, error);
        }

        return getJsonResponse().getJson();
    }

    @Override
    public JsonObject execute(String param) {
        return execute();
    }

    @Override
    public JsonObject execute(Map<String, String> parameters) {
        return execute();
    }
}
