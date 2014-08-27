package com.groupon.seleniumgridextras.videorecording;

import org.apache.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class VideoRecordingThreadPool {

  private static Logger logger = Logger.getLogger(VideoRecordingThreadPool.class);
  protected static ExecutorService cachedPool;
  protected static Map<String, Future<String>> futures = new HashMap<String, Future<String>>();


  public static void startVideoRecording(String host, String sessionName)
      throws URISyntaxException {

    if (cachedPool == null) {
      initializeThreadPool();
    }

    VideoRecorderCallable aCallable = new VideoRecorderCallable(new URI(host), "1");

    Future callableFuture =  cachedPool.submit(aCallable);
    futures.put(sessionName, callableFuture);
  }


  protected static void initializeThreadPool() {
    logger.info("Initializing a new thread pool");
    cachedPool = Executors.newCachedThreadPool();
  }


}
