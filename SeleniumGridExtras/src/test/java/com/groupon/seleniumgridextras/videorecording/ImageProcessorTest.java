package com.groupon.seleniumgridextras.videorecording;

import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.utilities.ImageUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ImageProcessorTest {

    private static final String IMAGE_PROCESSOR_TEST_JSON = "image_processor_test.json";

    @Before
    public void setUp() throws Exception {
        RuntimeConfig.setConfigFile(IMAGE_PROCESSOR_TEST_JSON);
        Config config = new Config(true);
        config.writeToDisk(RuntimeConfig.getConfigFile());
        RuntimeConfig.load();

        RuntimeConfig.getConfig().getVideoRecording().setTitleFrameFontColor(129, 182, 64, 128);
        RuntimeConfig.getConfig().getVideoRecording().setLowerThirdBackgroundColor(129, 182, 64, 128);

    }

    @After
    public void tearDown() throws Exception {
        File config = new File(IMAGE_PROCESSOR_TEST_JSON);
        config.delete();
        new File(IMAGE_PROCESSOR_TEST_JSON + ".example").delete();
    }


    @Test
    public void testAddTextCaptions() throws Exception {

        if (!testIfDimasComputer()){
            return;
        }

        if (RuntimeConfig.getOS().hasGUI()) {
            BufferedImage processedImage = MissingFrameImage.getMissingFrameAsBufferedImage();

            processedImage = ImageProcessor.addTextCaption(processedImage,
                    "LINE 1: Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                    "LINE 2: Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                    "LINE 3: Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                    "LINE 4: Lorem ipsum dolor sit amet, consectetur adipiscing elit.");

            //Uncomment this line after updating ImageProcessor class, to generate a new expected image
            //ImageUtils.saveImage(new File("SeleniumGridExtras/src/test/resources/fixtures/expected_processed_image.png"), processedImage);
            //ImageUtils.saveImage(new File("SeleniumGridExtras/src/test/resources/fixtures/expected_processed_image_cli.png"), processedImage);

            String actual = ImageUtils.encodeToString(processedImage, "PNG");

            final File
                    expectedFile =
                    new File(ClassLoader.getSystemResource("fixtures/expected_processed_image.png").getFile());

            //So this is a ton of FUN!!! the font that is used by IDE might not match the font used from CLI
            //So doing a backwards hack, will probably fail on windows or linux
            if (ImageUtils.encodeToString(ImageUtils.readImage(expectedFile), "PNG").equals(actual)) {
                assertTrue(true);
            } else {
                final File
                        expectedCLIFile =
                        new File(ClassLoader.getSystemResource("fixtures/expected_processed_image_cli.png").getFile());
                assertEquals(ImageUtils.encodeToString(ImageUtils.readImage(expectedCLIFile), "PNG"), actual);
            }

        }
    }

    @Test
    public void testCreateTitleFrame() throws Exception {
        if (!testIfDimasComputer()){
            return;
        }

        if (RuntimeConfig.getOS().hasGUI()) {
            Dimension size = new Dimension(1024, 768);

            BufferedImage image = ImageProcessor
                    .createTitleFrame(size, BufferedImage.TYPE_3BYTE_BGR, "Line 1", "Line 2", "Line 3");

            // Uncomment this line after updating ImageProcessor class, to generate a new expected image
            //ImageUtils.saveImage(new File("SeleniumGridExtras/src/test/resources/fixtures/expected_title_image.png"), image);
            //ImageUtils.saveImage(new File("SeleniumGridExtras/src/test/resources/fixtures/expected_title_image_cli.png"), image);

            final File
                    expectedFile =
                    new File(ClassLoader.getSystemResource("fixtures/expected_title_image.png").getFile());


            //So this is a ton of FUN!!! the font that is used by IDE might not match the font used from CLI
            //So doing a backwards hack, will probably fail on windows or linux
            if (ImageUtils.encodeToString(ImageUtils.readImage(expectedFile), "PNG").equals(ImageUtils.encodeToString(image, "PNG"))) {
                assertTrue(true);
            } else {
                final File
                        expectedCLIFile =
                        new File(ClassLoader.getSystemResource("fixtures/expected_title_image_cli.png").getFile());

                assertEquals(ImageUtils.encodeToString(ImageUtils.readImage(expectedCLIFile), "PNG"),
                        ImageUtils.encodeToString(image, "PNG"));
            }


        }
    }

    public static boolean testIfDimasComputer(){
        return RuntimeConfig.getOS().getHostName().equals("mbp-3.local");
    }

}
