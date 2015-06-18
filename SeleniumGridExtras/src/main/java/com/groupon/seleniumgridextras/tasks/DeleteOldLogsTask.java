package com.groupon.seleniumgridextras.tasks;


import com.google.common.base.Throwables;
import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.TimeStampUtility;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DeleteOldLogsTask extends ExecuteOSTask {
    private static Logger logger = Logger.getLogger(DeleteOldLogsTask.class);

    public DeleteOldLogsTask() {
        setEndpoint(TaskDescriptions.Endpoints.LOG_DELETE);
        setDescription(TaskDescriptions.Description.LOG_DELETE);
        setRequestType("GET");
        setResponseType("json");
        setClassname(this.getClass().getCanonicalName().toString());
        addResponseDescription(JsonCodec.LOGS_DELETED, "List of logs deleted");
        setEnabledInGui(true);
    }

    @Override
    public JsonObject execute() {

        List<String> listOfFilesAsString = new LinkedList<String>();

        for (File f : deleteOldLogs(
                RuntimeConfig.getConfig().getLogMaximumSize(),
                RuntimeConfig.getConfig().getLogMaximumAge(),
                RuntimeConfig.getConfig().getLogsDirectory())) {
            listOfFilesAsString.add(f.getAbsolutePath());
        }


        getJsonResponse().addKeyValues(JsonCodec.LOGS_DELETED, listOfFilesAsString);
        return getJsonResponse().getJson();
    }

    @Override
    public JsonObject execute(Map<String, String> parameter) {
        return execute();
    }

    @Override
    public JsonObject execute(String version) {
        return execute();
    }


    @Override
    public boolean initialize() {
        try {
            List<File> deletedFiles = deleteOldLogs(
                    RuntimeConfig.getConfig().getLogMaximumSize(),
                    RuntimeConfig.getConfig().getLogMaximumAge(),
                    RuntimeConfig.getConfig().getLogsDirectory()
            );

            for (File log : deletedFiles) {
                logger.info(String.format("Deleted log %s", log.getAbsolutePath()));
            }
        } catch (Exception error) {
            printInitilizedFailure();
            logger.error(error);
            return false;
        }

        printInitilizedSuccessAndRegisterWithAPI();
        return true;

    }

    protected List<File> deleteOldLogs(long bytesLimit, long msLimit, File logDir) {
        logger.info(String.format("Deleting all Logs bigger than %s bytes from %s", bytesLimit, logDir.getAbsolutePath()));
        List<File> filesToDelete = new LinkedList<File>();

        File[] files = logDir.listFiles();


        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().contains(".log") && files[i].length() > bytesLimit) {
                filesToDelete.add(files[i]);
            }
        }

        for (int i = 0; i < files.length; i++) {
            File currentFile = files[i];
            long msRange = TimeStampUtility.timestampInMs() - currentFile.lastModified();

            if (currentFile.getName().contains(".log") && msRange > msLimit) {
                filesToDelete.add(files[i]);
            }
        }

        for (File currentFile : filesToDelete) {
            logger.info(String.format(
                    "Deleting %s because files age %s was over the limit of %s",
                    currentFile.getAbsolutePath(),
                    currentFile.lastModified(),
                    bytesLimit
            ));

            try {
                if(currentFile.exists()){
                    currentFile.delete();
                } else {
                    logger.info(String.format("Attempting to delete %s but file no longer exists",
                            currentFile.getAbsolutePath()));
                }

            } catch (Exception e) {
                logger.warn(String.format(
                        "Error deleting log file %s, error: %s, \n %s",
                        currentFile.getAbsolutePath(),
                        e.getMessage(),
                        Throwables.getStackTraceAsString(e)
                )
                );
            }

        }


        return filesToDelete;
    }
}
