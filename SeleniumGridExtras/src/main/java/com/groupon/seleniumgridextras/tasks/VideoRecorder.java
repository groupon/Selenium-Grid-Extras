package com.groupon.seleniumgridextras.tasks;


import com.google.gson.JsonObject;

import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.videorecording.VideoRecordingThreadPool;

import java.util.Map;

public class VideoRecorder extends ExecuteOSTask {

  public VideoRecorder() {
    setEndpoint("/video");
    setDescription("Starts and stops video recording");
    JsonObject params = new JsonObject();
    params.addProperty("session", "(Required) - Session name of the test being recorded");
    params.addProperty("action", "(Required) - Action to perform (start|stop|heartbeat|status|stop_all)");
    params.addProperty("description",
                       "Description to appear in lower 3rd of the video");
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());

    addResponseDescription("session", "Session on which the current action was performed");
    addResponseDescription("action", "Action performed on current Video");
    addResponseDescription("description", "Update of last action performed by test to be displayed in lower third");
    addResponseDescription("current_videos", "List of videos currently being recorded, retrieved with 'status' action");

    setEnabledInGui(false);
  }


  @Override
  public JsonObject execute(Map<String, String> parameter) {

    if (!RuntimeConfig.getConfig().getVideoRecording().getRecordTestVideos()){
      getJsonResponse().addKeyValues("error", "Video Recording is disabled on this node");
      return getJsonResponse().getJson();
    }


    if (!parameter.isEmpty() && parameter.containsKey("session") && parameter
        .containsKey("action")) {

      String session = parameter.get("session");
      String action = parameter.get("action");
      String userDescription = parameter.get("description");

      getJsonResponse().addKeyValues("session", "" + session);
      getJsonResponse().addKeyValues("action", "" + action);
      getJsonResponse().addKeyValues("description", "" + userDescription);

      if (action.equals("start")) {
        VideoRecordingThreadPool.startVideoRecording(session,
                                                     RuntimeConfig.getConfig().getVideoRecording()
                                                         .getIdleTimeout());
        getJsonResponse().addKeyValues("out", "Starting Video Recording");
      } else if (action.equals("stop")) {
        getJsonResponse().addKeyValues("out", "Stopping Video Recording");
        VideoRecordingThreadPool.stopVideoRecording(session);
      } else if (action.equals("heartbeat")) {
        VideoRecordingThreadPool.addNewDescriptionToLowerThird(session, userDescription);
        getJsonResponse().addKeyValues("out", "Updating lower 3rd description");
      } else if (action.equals("status")){
        getJsonResponse().addKeyValues("current_videos", VideoRecordingThreadPool.getAllVideos());
      } else if (action.equals("stop_all")){
        VideoRecordingThreadPool.stopAndFinalizeAllVideos();
        getJsonResponse().addKeyValues("out", "Calling stop all videos command");
      } else {
        getJsonResponse().addKeyValues("error", "Unrecognized action '" + action
                                                + "', only acceptable actions are start, stop, heartbeat");

      }

      return getJsonResponse().getJson();

    } else {
      return execute();
    }


  }


  @Override
  public JsonObject execute() {
    getJsonResponse().addKeyValues("error",
                                   "Cannot call this endpoint without required parameters: session & action");
    return getJsonResponse().getJson();
  }


}
