package com.groupon.seleniumgridextras.tasks;


import com.google.common.base.Throwables;
import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.VideoHttpExecutor;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import com.groupon.seleniumgridextras.utilities.json.JsonParserWrapper;
import com.groupon.seleniumgridextras.utilities.threads.video.VideoRecordingThreadPool;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class VideoRecorder extends ExecuteOSTask {
  public File videoOutputDir = RuntimeConfig.getConfig().getVideoRecording().getOutputDir();
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

    addResponseDescription(JsonCodec.Video.AVAILABLE_VIDEOS,
        "List of videos on the hard driver");

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

    if (parameter.isEmpty()) {
      return execute();
    } else if ((parameter.containsKey(JsonCodec.Video.ACTION)) && (parameter.get(JsonCodec.Video.ACTION).equals(JsonCodec.Video.STOP_ALL))) {
      VideoRecordingThreadPool.stopAndFinalizeAllVideos();
      getJsonResponse().addKeyValues(JsonCodec.OUT, "Calling stop all videos command");
    } else if (!parameter.containsKey(JsonCodec.Video.SESSION)) {
      return execute();
    } else {
      String session = parameter.get(JsonCodec.Video.SESSION);
      String action = parameter.get(JsonCodec.Video.ACTION);
      String userDescription = parameter.get(JsonCodec.Video.DESCRIPTION);

      getJsonResponse().addKeyValues(JsonCodec.Video.SESSION, "" + session);
      getJsonResponse().addKeyValues(JsonCodec.Video.ACTION, "" + action);
      getJsonResponse().addKeyValues(JsonCodec.Video.DESCRIPTION, "" + userDescription);

      if (action.equals(JsonCodec.Video.START)) { //Once again, a kingdom for a string switch
        startVideoRecording(session);
      } else if (action.equals(JsonCodec.Video.STOP)) {
        stopVideoRecording(session);
      } else if (action.equals(JsonCodec.Video.HEARTBEAT)) {
        updateLastAction(session, userDescription);
      } else {
        String error = String.format(
            "Unrecognized action: %s, for session: %s, on host: %s",
            action,
            session,
            RuntimeConfig.getHostIp());

        logger.warn(error);
        getJsonResponse().addKeyValues(JsonCodec.ERROR, error);

      }
    }

    addExistingAndCurrentVideosToResponse();
    return getJsonResponse().getJson();
  }


  @Override
  public JsonObject execute() {
    addExistingAndCurrentVideosToResponse();
    getJsonResponse().addKeyValues(JsonCodec.ERROR,
        "Cannot call this endpoint without required parameters: session and action");
    return getJsonResponse().getJson();
  }

  protected void updateLastAction(String session, String userDescription) {
    VideoRecordingThreadPool.addNewDescriptionToLowerThird(session, userDescription);
    getJsonResponse().addKeyValues(JsonCodec.OUT, "Updating lower 3rd description with " + userDescription);
  }

  protected void startVideoRecording(String session) {
    try {
      Future<String> f = VideoRecordingThreadPool.startVideoRecording(session,
          RuntimeConfig.getConfig().getVideoRecording().getIdleTimeout());

      String message = String.format(
          "Starting video recording for session: %s, on host: %s, done: %s, cancelled: %s",
          session,
          RuntimeConfig.getHostIp(),
          f.isDone(),
          f.isCancelled());
      logger.info(message);
      getJsonResponse().addKeyValues(JsonCodec.OUT, message);
    } catch (Exception e) {
      String error = String.format(
          "Error starting video recording for session: %s, error: %s, \n%s",
          session,
          e.getMessage(),
          Throwables.getStackTraceAsString(e));

      logger.error(error);
      getJsonResponse().addKeyValues(JsonCodec.ERROR, error);
    }
  }

  protected void stopVideoRecording(String session) {
    Future<String> f = VideoRecordingThreadPool.stopVideoRecording(session);


    try {
      String output = f.get();

      VideoRecordingThreadPool.removeSession(session);

      getJsonResponse().addKeyValues(
          JsonCodec.OUT,
          String.format(
              "Stopping video done: %s, cancelled: %s, output: %s",
              f.isDone(),
              f.isCancelled(),
              output));
    } catch (Exception e) {
      String error = String.format("Error stopping video for session: %s, host: %s, IP: %s, \n%s",
          session,
          RuntimeConfig.getOS().getHostName(),
          RuntimeConfig.getHostIp(),
          Throwables.getStackTraceAsString(e));

      logger.error(error);
      getJsonResponse().addKeyValues(JsonCodec.ERROR, error);
    }
  }


  protected void addExistingAndCurrentVideosToResponse() {
    List<String> videosInMidRecording = VideoRecordingThreadPool.getAllVideos();

    getJsonResponse()
    .addKeyValues(JsonCodec.Video.CURRENT_VIDEOS, videosInMidRecording);


    Map<String, Map<String, Object>> filesReadyForDownload = new HashMap<String, Map<String, Object>>();
    try {
      for (File f : RuntimeConfig.getConfig().getVideoRecording().getOutputDir().listFiles()) {

        if (!f.getName().contains(".mp4")){
          continue;
        }

        String sessionId = FilenameUtils.removeExtension(f.getName());
        if (!videosInMidRecording.contains(sessionId)) {
          Map<String, Object> videoInfo = new HashMap<String, Object>();
          videoInfo.put(JsonCodec.Video.SESSION, sessionId);
          videoInfo.put(JsonCodec.Video.VIDEO_DOWNLOAD_URL, buildUrlToVideo(f.getName()));
          videoInfo.put(JsonCodec.Video.VIEDO_SIZE, f.length());
          videoInfo.put(JsonCodec.Video.LAST_MODIFIED, f.lastModified());
          videoInfo.put(JsonCodec.Video.VIDEO_ABSOLUTE_PATH, f.getAbsolutePath());

          filesReadyForDownload.put(sessionId, videoInfo);
        }
      }

      getJsonResponse().addKeyValues(
          JsonCodec.Video.AVAILABLE_VIDEOS,
          JsonParserWrapper.toJsonObject(filesReadyForDownload));
    } catch (Exception e) {
      String error = String.format(
          "Error occurred while collecting while trying to collect downloadable video files %s\n%s",
          e.getMessage(),
          Throwables.getStackTraceAsString(e));
      logger.error(error, e);

      getJsonResponse().addKeyValues(JsonCodec.ERROR, error);
    }
  }

  protected String buildUrlToVideo(String fileName) {
    try {
      URIBuilder uriBuilder = new URIBuilder();
      uriBuilder.setScheme("http");
      uriBuilder.setHost(RuntimeConfig.getHostIp());
      uriBuilder.setPort(RuntimeConfig.getGridExtrasPort());
      uriBuilder.setPath(VideoHttpExecutor.GET_VIDEO_FILE_ENDPOINT + "/" + fileName);

      return uriBuilder.build().toString();
    } catch (Exception e) {
      String error = String.format("Error building direct URL for session %s, %s\n%s",
          e.getMessage(),
          Throwables.getStackTraceAsString(e));
      logger.error(error, e);
      getJsonResponse().addKeyValues(JsonCodec.ERROR, error);
      return "";
    }
  }
  
  private void createDir() {
    File dir = videoOutputDir;
    dir.mkdir();
  }

  @Override
  public boolean initialize() {
    try {
      if (!videoOutputDir.exists()) {
        createDir();
      }
    } catch (NullPointerException error) {
      printInitilizedFailure();
      logger.error(" 'VIDEO_OUTPUT_DIR' variable was not set in the config " + error);
      return false;
    }
    printInitilizedSuccessAndRegisterWithAPI();
    return true;
  }

}
