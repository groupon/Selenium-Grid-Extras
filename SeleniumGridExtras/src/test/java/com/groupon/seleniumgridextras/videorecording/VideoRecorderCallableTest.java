package com.groupon.seleniumgridextras.videorecording;

import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.DefaultConfig;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.utilities.threads.video.VideoRecorderCallable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class VideoRecorderCallableTest {

    public static final String VIDEO_RECORDER_TEST_JSON = "video_recorder_test.json";

    final private String session1 = "123456";
    final private String session2 = "654321";
    final private String session3 = "abcdef";

    final private File session1File = new File(DefaultConfig.VIDEO_OUTPUT_DIRECTORY, session1 + ".mp4");
    final private File session2File = new File(DefaultConfig.VIDEO_OUTPUT_DIRECTORY, session2 + ".mp4");
    final private File session3File = new File(DefaultConfig.VIDEO_OUTPUT_DIRECTORY, session3 + ".mp4");

    @Before
    public void setUp() throws Exception {
        RuntimeConfig.setConfigFile(VIDEO_RECORDER_TEST_JSON);
        Config config = DefaultConfig.getDefaultConfig();
        config.getVideoRecording().setVideosToKeep(1);
        config.writeToDisk(RuntimeConfig.getConfigFile());
        RuntimeConfig.load();
    }

    @After
    public void tearDown() throws Exception {
        delete(session1File);
        delete(session2File);
        delete(session3File);
        delete(new File(DefaultConfig.VIDEO_OUTPUT_DIRECTORY));
        delete(new File(RuntimeConfig.getConfigFile()));
        delete(new File(VIDEO_RECORDER_TEST_JSON + ".example"));
    }

    private void delete(File f) {
        if (f.exists()) {
            f.delete();
        }
    }

    @Test
    public void testRecordVideo() throws Exception {
        if (!ImageProcessorTest.testIfDimasComputer()){
            return;
        }

        if (RuntimeConfig.getOS().hasGUI()) {
            VideoRecorderCallable video = new VideoRecorderCallable(session1, 60);

            ExecutorService cachedPool = Executors.newCachedThreadPool();

            try {
                Future<String> future = cachedPool.submit(video);

                video.lastAction("Last action");
                Thread.sleep(1000);
                video.lastAction("Maybe not");
                Thread.sleep(1000);
                video.stop();

                future.get(); //wait for thread to finish

                assertTrue(session1File.exists());

            } finally {
                cachedPool.shutdown();
            }
        }

    }

    @Test
    public void testTimeout() throws Exception {
        if (!ImageProcessorTest.testIfDimasComputer()){
            return;
        }

        if (RuntimeConfig.getOS().hasGUI()) {
            VideoRecorderCallable video = new VideoRecorderCallable(session1, 2);

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
        if (!ImageProcessorTest.testIfDimasComputer()){
            return;
        }

        if (RuntimeConfig.getOS().hasGUI()) {
            VideoRecorderCallable video = new VideoRecorderCallable(session1, 2);

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
                Thread.sleep(4000);
                assertTrue(future.isDone());
            } finally {
                cachedPool.shutdown();
            }
        }
    }

    @Test
    public void testResolutionDivisibleByTwo() throws Exception{
        assertEquals(true, VideoRecorderCallable.isResolutionDivisibleByTwo(new Dimension(1024, 768)));
        assertEquals(false, VideoRecorderCallable.isResolutionDivisibleByTwo(new Dimension(1025, 768)));
        assertEquals(false, VideoRecorderCallable.isResolutionDivisibleByTwo(new Dimension(1024, 769)));
    }

    @Test
    public void testDeleteOldMovies() throws Exception {
        // Create empty files
        File outputDir = new File(DefaultConfig.VIDEO_OUTPUT_DIRECTORY);
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }
        session1File.createNewFile();
        Thread.sleep(100);
        session2File.createNewFile();
        Thread.sleep(100);
        session3File.createNewFile();

        // Delete older files
        VideoRecorderCallable.deleteOldMovies(outputDir);

        // Older files has been removed
        assertFalse(session1File.exists());
        assertFalse(session2File.exists());
        assertTrue(session3File.exists());
    }

    @Test
    public void testRecordVideoAndDeleteOldMovies() throws Exception {
        if (!ImageProcessorTest.testIfDimasComputer()){
            return;
        }

        if (RuntimeConfig.getOS().hasGUI()) {
            ExecutorService cachedPool = Executors.newCachedThreadPool();

            try {
                recordVideo(cachedPool, session1);
                recordVideo(cachedPool, session2);
                assertTrue(session1File.exists());
                assertTrue(session2File.exists());

                recordVideo(cachedPool, session3);

                // Only one file has been removed because the number of files is checked before the record 
                assertFalse(session1File.exists());
                assertTrue(session2File.exists());
                assertTrue(session3File.exists());
            } finally {
                cachedPool.shutdown();
            }
        }
    }

    private void recordVideo(ExecutorService cachedPool, String session) throws InterruptedException, ExecutionException {
        VideoRecorderCallable video = new VideoRecorderCallable(session, 60);
        Future<String> future = cachedPool.submit(video);
        video.stop();
        future.get(); //wait for thread to finish
    }

}
