package com.groupon.seleniumgridextras.videorecording;


import com.groupon.seleniumgridextras.config.RuntimeConfig;
import org.junit.After;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class VideoRecordingThreadPoolTest {

    final private String session1 = "123456";
    final private String session2 = "654321";
    final private String session3 = "abcdef";

    final private File session1File = new File("video_output", session1 + ".mp4");
    final private File session2File = new File("video_output", session2 + ".mp4");
    final private File session3File = new File("video_output", session3 + ".mp4");

    @After
    public void tearDown() throws Exception {
        delete(session1File);
        delete(session2File);
        delete(session3File);
    }

    private void delete(File f) {
        if (f.exists()) {
            f.delete();
        }
    }

    @Test
    public void testRecordVideos() throws Exception {
        if (!ImageProcessorTest.testIfDimasComputer()){
            return;
        }

        if (RuntimeConfig.getOS().hasGUI()) {
            VideoRecordingThreadPool.startVideoRecording(session1);
            VideoRecordingThreadPool.startVideoRecording(session2);
            VideoRecordingThreadPool.startVideoRecording(session3);

            VideoRecordingThreadPool.getVideo(session3).lastAction("fooooo");

            Thread.sleep(4000);

            VideoRecordingThreadPool.stopVideoRecording(session1);

            Thread.sleep(2000);
            VideoRecordingThreadPool.stopVideoRecording(session2);

            Thread.sleep(1000);
            VideoRecordingThreadPool.stopVideoRecording(session3);

            VideoRecordingThreadPool.waitForThreadToStop(session3);

            VideoRecordingThreadPool.removeSession(session1);
            VideoRecordingThreadPool.removeSession(session2);
            VideoRecordingThreadPool.removeSession(session3);

            assertTrue(session1File.exists());
            assertTrue(session2File.exists());
            assertTrue(session3File.exists());

            assertTrue(session1File.length() > 1000);
            assertTrue(session2File.length() > 1100);
            assertTrue(session3File.length() > 1200);
        }
    }


}
