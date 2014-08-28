package com.groupon.seleniumgridextras.videorecording;


import com.groupon.seleniumgridextras.config.RuntimeConfig;
import org.apache.log4j.Logger;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class VideoRecorderCallable implements Callable {

  private static Logger logger = Logger.getLogger(VideoRecorderCallable.class);
  protected String lastAction;
  protected Date lastActionTimestamp;
  protected boolean recording = true;
  protected URI targetURI;
  protected String sessionId;
  final protected File outputDir = new File("video_output");
  protected File sessionOutputDir;

  ExecutorService imageRetrivalThreadPool = Executors.newCachedThreadPool();

  protected String nodeName;
  protected String lastCommand;


  public VideoRecorderCallable(String sessionID) {
    this.sessionId = sessionID;
    setOutputDirExists(this.sessionId);
  }

  @Override
  public String call() throws Exception {
    //Probably overkill to null these out, but i'm playing it safe until proven otherwise
    this.nodeName =
        "Node: " + RuntimeConfig.getOS().getHostName() + " (" + RuntimeConfig.getOS().getHostIp()
        + ")";
    this.lastCommand = null;

    logger
        .info("Starting video recording for session " + getSessionId() + " to " + outputDir
            .getAbsolutePath());
    int imageFrame = 1;
    try {
      while (isRecording() && getTimeoutNotReached()) {
        SaveProcessedScreenshot
            screenshot =
            new SaveProcessedScreenshot(new File(sessionOutputDir, imageFrame + ".png"),
                                        new Dimension(1024, 768),
                                        "",
                                        this.nodeName,
                                        "Timestamp: " + getTimestamp().toString(),
                                        this.lastAction
            );

        Future<String> future = imageRetrivalThreadPool.submit(screenshot);

        Thread.sleep(1000);
        imageFrame++;
      }
    } finally {
      imageRetrivalThreadPool.shutdown();
    }

    logger.info("Captured " + imageFrame + " frames");

    return getSessionId();
  }


  public void lastAction(String action) {
    this.lastActionTimestamp = getTimestamp();
    this.lastAction = action;
  }

  protected Date getTimestamp() {
    return new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
  }

  public void stop() {
    System.out.println("Stop called");
    this.recording = false;
  }

  protected void setOutputDirExists(String sessionId) {
    if (!outputDir.exists()) {
      System.out.println(
          "Root Video output dir does not exist, creating it here " + outputDir.getAbsolutePath());
      outputDir.mkdir();
    }

    sessionOutputDir = new File(outputDir, sessionId);
    if (!sessionOutputDir.exists()) {
      System.out.println("Creating output dir for session: " + sessionId + " in " + sessionOutputDir
          .getAbsolutePath());
      sessionOutputDir.mkdir();
    }
  }

  protected boolean getTimeoutNotReached() {
    return true;
  }

  protected boolean isRecording() {
    return recording;
  }


  protected String getSessionId() {
    return sessionId;
  }
}
