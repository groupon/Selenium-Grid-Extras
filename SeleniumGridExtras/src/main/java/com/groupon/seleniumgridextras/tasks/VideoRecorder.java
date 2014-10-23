package com.groupon.seleniumgridextras.tasks;


import com.google.gson.JsonObject;

import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import com.groupon.seleniumgridextras.videorecording.VideoRecordingThreadPool;
import org.apache.log4j.Logger;

import java.util.Map;

public class VideoRecorder extends ExecuteOSTask {
    private static Logger logger = Logger.getLogger(VideoRecorder.class);

    public VideoRecorder() {
        setEndpoint(TaskDescriptions.Endpoints.VIDEO);
        setDescription(TaskDescriptions.Description.VIDEO);
        JsonObject params = new JsonObject();
        params.addProperty(JsonCodec.Video.SESSION,
                "(Required) - Session name of the test being recorded");
        params.addProperty(JsonCodec.Video.ACTION,
                "(Required) - Action to perform (start|stop|heartbeat|status|stop_all)");
        params.addProperty(JsonCodec.Video.DESCRIPTION,
                "Description to appear in lower 3rd of the video");
        setAcceptedParams(params);
        setRequestType("GET");
        setResponseType("json");
        setClassname(this.getClass().getCanonicalName().toString());

        addResponseDescription(JsonCodec.Video.SESSION,
                "Session on which the current action was performed");
        addResponseDescription(JsonCodec.Video.ACTION, "Action performed on current Video");
        addResponseDescription(JsonCodec.Video.DESCRIPTION,
                "Update of last action performed by test to be displayed in lower third");
        addResponseDescription(JsonCodec.Video.CURRENT_VIDEOS,
                "List of videos currently being recorded, retrieved with 'status' action");

        setEnabledInGui(false);
    }


    @Override
    public JsonObject execute(Map<String, String> parameter) {

        if (!RuntimeConfig.getConfig().getVideoRecording().getRecordTestVideos()) {
            getJsonResponse().addKeyValues(JsonCodec.ERROR, "Video Recording is disabled on this node");
            return getJsonResponse().getJson();
        } else if (!RuntimeConfig.getOS().hasGUI()) {
            String message = "Video recording is only supported on operating systems with GUI." +
                    " If you are using a daemon/service to start Selenium Grid Extras, " +
                    "make sure the process has access to the user with the DISPLAY privileges. " +
                    "If on a Unix based system, make sure you export the DISPLAY environment variable";

            logger.error(message);
            getJsonResponse().addKeyValues(JsonCodec.ERROR, message);
            return getJsonResponse().getJson();
        }

        if (!parameter.isEmpty() && parameter.containsKey(JsonCodec.Video.SESSION) && parameter
                .containsKey(JsonCodec.Video.ACTION)) {

            String session = parameter.get(JsonCodec.Video.SESSION);
            String action = parameter.get(JsonCodec.Video.ACTION);
            String userDescription = parameter.get(JsonCodec.Video.DESCRIPTION);

            getJsonResponse().addKeyValues(JsonCodec.Video.SESSION, "" + session);
            getJsonResponse().addKeyValues(JsonCodec.Video.ACTION, "" + action);
            getJsonResponse().addKeyValues(JsonCodec.Video.DESCRIPTION, "" + userDescription);

            if (action.equals(JsonCodec.Video.START)) {
                VideoRecordingThreadPool.startVideoRecording(session,
                        RuntimeConfig.getConfig().getVideoRecording()
                                .getIdleTimeout());
                getJsonResponse().addKeyValues(JsonCodec.OUT, "Starting Video Recording");
            } else if (action.equals(JsonCodec.Video.STOP)) {
                getJsonResponse().addKeyValues(JsonCodec.OUT, "Stopping Video Recording");
                VideoRecordingThreadPool.stopVideoRecording(session);
            } else if (action.equals(JsonCodec.Video.HEARTBEAT)) {
                VideoRecordingThreadPool.addNewDescriptionToLowerThird(session, userDescription);
                getJsonResponse().addKeyValues(JsonCodec.OUT, "Updating lower 3rd description");
            } else if (action.equals(JsonCodec.Video.STATUS)) {
                getJsonResponse()
                        .addKeyValues(JsonCodec.Video.CURRENT_VIDEOS, VideoRecordingThreadPool.getAllVideos());
            } else if (action.equals(JsonCodec.Video.STOP_ALL)) {
                VideoRecordingThreadPool.stopAndFinalizeAllVideos();
                getJsonResponse().addKeyValues(JsonCodec.OUT, "Calling stop all videos command");
            } else {
                getJsonResponse().addKeyValues(JsonCodec.ERROR, "Unrecognized action '" + action
                        + "', only acceptable actions are start, stop, heartbeat");

            }

            return getJsonResponse().getJson();

        } else {
            return execute();
        }


    }


    @Override
    public JsonObject execute() {
        getJsonResponse().addKeyValues(JsonCodec.ERROR,
                "Cannot call this endpoint without required parameters: session & action");
        return getJsonResponse().getJson();
    }


}
