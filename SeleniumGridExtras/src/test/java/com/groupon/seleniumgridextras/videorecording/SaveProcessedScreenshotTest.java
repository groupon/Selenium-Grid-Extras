package com.groupon.seleniumgridextras.videorecording;


import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.utilities.ImageUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SaveProcessedScreenshotTest {

    private File output = new File("screenshot_output_test.png");
    private Dimension dimension = new Dimension(1024, 768);

    @Before
    public void setUp() throws Exception {
        if (output.exists()) {
            output.delete();
        }
    }


    @After
    public void tearDown() throws Exception {
        if (output.exists()) {
            output.delete();
        }
    }

    @Test
    public void testTakeProcessedScreenshot() throws Exception {
        if (!ImageProcessorTest.testIfDimasComputer()) {
            return;
        }

        if (RuntimeConfig.getOS().hasGUI()) {

            SaveProcessedScreenshot
                    imageSaver =
                    new SaveProcessedScreenshot(output, dimension, "line1", "line2", "line3",
                            "line4");

            ExecutorService cachedPool = Executors.newCachedThreadPool();
            try {

                Future<String> future = cachedPool.submit(imageSaver);
                String result = future.get();

                assertEquals("done", result);
                assertTrue(output.exists());
                assertEquals(1024, ImageUtils.readImage(output).getWidth());
            } finally {
                cachedPool.shutdown();
            }
        }

    }


}
