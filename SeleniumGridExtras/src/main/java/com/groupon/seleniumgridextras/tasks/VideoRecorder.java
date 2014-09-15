package com.groupon.seleniumgridextras.tasks;


import com.google.gson.JsonObject;

import com.groupon.seleniumgridextras.videorecording.VideoRecordingThreadPool;

import java.util.Map;

public class VideoRecorder extends ExecuteOSTask {

  public VideoRecorder() {
    setEndpoint("/video");
    setDescription("Starts and stops video recording");
    JsonObject params = new JsonObject();
    params.addProperty("session", "(Required) - Session name of the test being recorded");
    params.addProperty("action", "(Required) - Action to perform (start|stop|heartbeat)");
    params.addProperty("description",
                       "Description to appear in lower 3rd of the video");
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());

    addResponseDescription("session", "Session on which the current action was performed");
    addResponseDescription("action", "Action performed on current Video");
    addResponseDescription("description", "Update");

    setEnabledInGui(false);
  }


  @Override
  public JsonObject execute(Map<String, String> parameter) {

    if (!parameter.isEmpty() && parameter.containsKey("session") && parameter
        .containsKey("action")) {

      String session = parameter.get("session");
      String action = parameter.get("action");
      String userDescription = parameter.get("description");

      getJsonResponse().addKeyValues("session", "" + session);
      getJsonResponse().addKeyValues("action", "" + action);
      getJsonResponse().addKeyValues("description", "" + userDescription);

      if (action.equals("start")) {
        VideoRecordingThreadPool.startVideoRecording(session, 120);
        getJsonResponse().addKeyValues("out", "Starting Video Recording");
      } else if (action.equals("stop")) {
        getJsonResponse().addKeyValues("out", "Stoping Video Recording");
        VideoRecordingThreadPool.stopVideoRecording(session);
      } else if (action.equals("heartbeat")) {
        getJsonResponse().addKeyValues("out", "Updating lower 3rd description");

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
