package com.groupon.seleniumgridextras.tasks;


import com.google.gson.JsonObject;

import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import com.groupon.seleniumgridextras.utilities.json.JsonResponseBuilder;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.videorecording.VideoRecordingThreadPool;

import java.util.Map;

public class VideoRecorder extends ExecuteOSTask {

  private static final String SESSION = "session";
  private static final String ACTION = "action";
  private static final String DESCRIPTION = "description";
  private static final String CURRENT_VIDEOS = "current_videos";
  private static final String START = "start";
  private static final String STOP = "stop";
  private static final String HEARTBEAT = "heartbeat";
  private static final String STATUS = "status";
  private static final String STOP_ALL = "stop_all";

  public VideoRecorder() {
    setEndpoint("/video");
    setDescription("Starts and stops video recording");
    JsonObject params = new JsonObject();
    params.addProperty(SESSION, "(Required) - Session name of the test being recorded");
    params.addProperty(ACTION, "(Required) - Action to perform (start|stop|heartbeat|status|stop_all)");
    params.addProperty(DESCRIPTION,
                       "Description to appear in lower 3rd of the video");
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());

    addResponseDescription(SESSION, "Session on which the current action was performed");
    addResponseDescription(ACTION, "Action performed on current Video");
    addResponseDescription(DESCRIPTION, "Update of last action performed by test to be displayed in lower third");
    addResponseDescription(CURRENT_VIDEOS, "List of videos currently being recorded, retrieved with 'status' action");

    setEnabledInGui(false);
  }


  @Override
  public JsonObject execute(Map<String, String> parameter) {

    if (!RuntimeConfig.getConfig().getVideoRecording().getRecordTestVideos()){
      getJsonResponse().addKeyValues(JsonCodec.ERROR, "Video Recording is disabled on this node");
      return getJsonResponse().getJson();
    }


    if (!parameter.isEmpty() && parameter.containsKey(SESSION) && parameter
        .containsKey(ACTION)) {

      String session = parameter.get(SESSION);
      String action = parameter.get(ACTION);
      String userDescription = parameter.get(DESCRIPTION);

      getJsonResponse().addKeyValues(SESSION, "" + session);
      getJsonResponse().addKeyValues(ACTION, "" + action);
      getJsonResponse().addKeyValues(DESCRIPTION, "" + userDescription);

      if (action.equals(START)) {
        VideoRecordingThreadPool.startVideoRecording(session,
                                                     RuntimeConfig.getConfig().getVideoRecording()
                                                         .getIdleTimeout());
        getJsonResponse().addKeyValues(JsonCodec.OUT, "Starting Video Recording");
      } else if (action.equals(STOP)) {
        getJsonResponse().addKeyValues(JsonCodec.OUT, "Stopping Video Recording");
        VideoRecordingThreadPool.stopVideoRecording(session);
      } else if (action.equals(HEARTBEAT)) {
        VideoRecordingThreadPool.addNewDescriptionToLowerThird(session, userDescription);
        getJsonResponse().addKeyValues(JsonCodec.OUT, "Updating lower 3rd description");
      } else if (action.equals(STATUS)){
        getJsonResponse().addKeyValues(CURRENT_VIDEOS, VideoRecordingThreadPool.getAllVideos());
      } else if (action.equals(STOP_ALL)){
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
