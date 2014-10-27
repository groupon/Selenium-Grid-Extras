package com.groupon.seleniumgridextras.videorecording;

import com.groupon.seleniumgridextras.config.RuntimeConfig;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ImageToVideoConverterTest {

    final private File outputVideo = new File("1.mp4");
    final private
    String
            inputDirt =
            ClassLoader.getSystemResource("fixtures/videoscreenshots").getFile();

    @After
    public void tearDown() throws Exception {
        if (outputVideo.exists()) {
            outputVideo.delete();
        }
    }

    @Test
    public void testVideoConverter() throws Exception {
        if (!ImageProcessorTest.testIfDimasComputer()) {
            return;
        }
        if (RuntimeConfig.getOS().hasGUI()) {
            ImageToVideoConverter
                    converter =
                    new ImageToVideoConverter(inputDirt, "1", "localhost", "Today");

            ExecutorService cachedPool = Executors.newCachedThreadPool();
            try {

                Future<String> future = cachedPool.submit(converter);


                String result = future.get();

                assertEquals("done", result);
                assertEquals(true, outputVideo.exists());
                assertTrue(outputVideo.length() > 20000); //Can't check video frame by frame but know it's
                //roughly 200K when properly formatted

            } finally {
                cachedPool.shutdown();
            }

        }

    }

}
