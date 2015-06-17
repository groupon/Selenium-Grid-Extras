package com.groupon.seleniumgridextras.utilities.shutdownhooks;

import com.groupon.seleniumgridextras.utilities.threads.video.VideoRecordingThreadPool;
import org.apache.log4j.Logger;

public class VideoShutdownHook {

  private static Logger logger = Logger.getLogger(VideoShutdownHook.class);

  public VideoShutdownHook() {
   logger.info("Creating instance of the video shutdown hook");
  }

  public void attachShutDownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        String message = "Application is shutting down, trying to finalize current videos. If this is not allowed to finish, some of the videos will be corrupted!";
        System.out.println(message);
        logger.info(message);
        VideoRecordingThreadPool.stopAndFinalizeAllVideos();
      }
    });
    logger.info("Video Render Shut Down Hook Attached.");
  }
}
