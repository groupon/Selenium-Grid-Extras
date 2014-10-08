package com.groupon.seleniumgridextras.videorecording;

import com.groupon.seleniumgridextras.config.RuntimeConfig;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertTrue;

public class VideoRecorderCallableTest {

    final private String session = "123456";
    final private File output = new File("video_output", session + ".mp4");

    @After
    public void tearDown() throws Exception {
        if (output.exists()) {
            output.delete();
        }
    }

    @Test
    public void testRecordVideo() throws Exception {
        if (RuntimeConfig.getOS().hasGUI()) {
            VideoRecorderCallable video = new VideoRecorderCallable(session, 60);

            ExecutorService cachedPool = Executors.newCachedThreadPool();

            try {
                Future<String> future = cachedPool.submit(video);

                video.lastAction("Last action");
                Thread.sleep(1000);
                video.lastAction("Maybe not");
                Thread.sleep(1000);
                video.stop();

                future.get(); //wait for thread to finish

                assertTrue(output.exists());

            } finally {
                cachedPool.shutdown();
            }
        }

    }

    @Test
    public void testTimeout() throws Exception {
        if (RuntimeConfig.getOS().hasGUI()) {
            VideoRecorderCallable video = new VideoRecorderCallable(session, 2);

            ExecutorService cachedPool = Executors.newCachedThreadPool();

            try {
                Future<String> future = cachedPool.submit(video);
                Thread.sleep(6000);
                assertTrue(future.isDone());
            } finally {
                cachedPool.shutdown();
            }
        }
    }

    @Test
    public void testTimeoutExtended() throws Exception {
        if (RuntimeConfig.getOS().hasGUI()) {
            VideoRecorderCallable video = new VideoRecorderCallable(session, 2);

            ExecutorService cachedPool = Executors.newCachedThreadPool();

            try {
                Future<String> future = cachedPool.submit(video);
                video.lastAction("action");
                assertTrue(!future.isDone());
                Thread.sleep(1000);
                video.lastAction("action");
                assertTrue(!future.isDone());
                Thread.sleep(1000);
                video.lastAction("action");
                assertTrue(!future.isDone());
                Thread.sleep(1000);
                video.lastAction("action");
                assertTrue(!future.isDone());
                Thread.sleep(1000);
                video.lastAction("action");
                assertTrue(!future.isDone());
                Thread.sleep(3000);
                assertTrue(future.isDone());
            } finally {
                cachedPool.shutdown();
            }
        }
    }

}
