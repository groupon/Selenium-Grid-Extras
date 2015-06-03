package com.groupon.seleniumgridextras.tasks;


import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import org.apache.log4j.Logger;

public class DeleteOldLogs extends ExecuteOSTask {
    private static Logger logger = Logger.getLogger(DeleteOldLogs.class);

    public DeleteOldLogs(){
        setEndpoint(TaskDescriptions.Endpoints.LOG_DELETE);
        setDescription(TaskDescriptions.Description.LOG_DELETE);
        JsonObject params = new JsonObject();
        setAcceptedParams(params);
        setRequestType("GET");
        setResponseType("json");
        setClassname(this.getClass().getCanonicalName().toString());
        setEnabledInGui(true);
    }

    @Override
    public String getWindowsCommand() {
        return getWindowsCommand("");
    }

    @Override
    public String getWindowsCommand(String param) {
        String path = String.format("forfiles -p \"%s\" -s -m *.* /D -%s /C \"cmd /c del @path\""
            , RuntimeConfig.getConfig().getLogsDirectory().getAbsolutePath()
            , RuntimeConfig.getConfig().getLogCleaningDays());
        logDeleteLogs();
        return path;
    }

    @Override
    public String getMacCommand() {
        logger.info("Not implemented yet !");
//        logDeleteLogs();
        return getMacCommand("");
    }

    @Override
    public String getMacCommand(String param) {
//        logDeleteLogs();
        return "";
    }

    protected void logDeleteLogs() {
        logger.info(String.format("Deleting all Logs older than %s Days", RuntimeConfig.getConfig().getLogCleaningDays()));
    }
}
