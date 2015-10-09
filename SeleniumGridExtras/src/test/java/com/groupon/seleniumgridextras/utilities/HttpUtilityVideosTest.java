package com.groupon.seleniumgridextras.utilities;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.DefaultConfig;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

import java.io.File;
import java.net.URI;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

// This class is ignored until we find a more consistent place to download videos from
@Ignore
public class HttpUtilityVideosTest {

    public static final String VIDEO_RECORDER_TEST_JSON = "video_recorder_test.json";

    final private String videosUrl = "http://192.168.168.144:3000/download_video/";
    final private String video1Filename = "e985bb1c-bb92-4b16-8e76-62e039efbbc0.mp4";
    final private String video2Filename = "f041511c-367d-4772-a82a-7b48ab69615c.mp4";
    final private String video3Filename = "94a74a04-1579-4475-b65d-926b905f0a40.mp4";

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
        delete(new File(DefaultConfig.VIDEO_OUTPUT_DIRECTORY, video1Filename));
        delete(new File(DefaultConfig.VIDEO_OUTPUT_DIRECTORY, video2Filename));
        delete(new File(DefaultConfig.VIDEO_OUTPUT_DIRECTORY, video3Filename));
        delete(new File(RuntimeConfig.getConfigFile()));
        delete(new File(VIDEO_RECORDER_TEST_JSON + ".example"));
    }

    private void delete(File f) {
        if (f.exists()) {
            f.delete();
        }
    }

    @Test
    public void testGetVideoFromUri() throws Exception {
        File local1 = HttpUtility.downloadVideoFromUri(new URI(videosUrl + video1Filename));
        assertTrue(local1.exists());
    }

    @Test
    public void testGetVideoFromUriAndDeleteOldMovies() throws Exception {
        File video1 = HttpUtility.downloadVideoFromUri(new URI(videosUrl + video1Filename));
        File video2 = HttpUtility.downloadVideoFromUri(new URI(videosUrl + video2Filename));
        assertTrue(video1.exists());
        assertTrue(video2.exists());

        File video3 = HttpUtility.downloadVideoFromUri(new URI(videosUrl + video3Filename));

        // Only one file has been removed because the number of files is checked before the download 
        assertFalse(video1.exists());
        assertTrue(video2.exists());
        assertTrue(video3.exists());
    }

}
