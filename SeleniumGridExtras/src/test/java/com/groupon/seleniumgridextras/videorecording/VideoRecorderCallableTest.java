package com.groupon.seleniumgridextras.videorecording;

import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VideoRecorderCallableTest {

  final private String session = "123456";
  final private File outputDir = new File("video_output", session);

  @Test
  public void testRecordVideo() throws Exception {

    VideoRecorderCallable video = new VideoRecorderCallable(session);

    ExecutorService cachedPool = Executors.newCachedThreadPool();

    try {
      Future<String> future = cachedPool.submit(video);

      video.lastAction("Last action");
      Thread.sleep(2000);
      video.lastAction("Last Last action");
      Thread.sleep(2000);
      video.lastAction("Maybe not");
      Thread.sleep(2000);
      video.lastAction("Who knows");
      video.stop();

      future.get(); //wait for thread to finish

      //We ran recording for 6 seconds but CPU might be slow so check that at least 4 seconds are captured
      assertTrue(outputDir.listFiles().length > 4);

      System.out.println(future.isDone());
    } finally {
      cachedPool.shutdown();
    }
  }

}
