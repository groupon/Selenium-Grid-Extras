package com.groupon.seleniumgridextras.videorecording;

/**
 * Created with IntelliJ IDEA. User: dima Date: 8/27/14 Time: 10:19 AM To change this template use
 * File | Settings | File Templates.
 */

import com.groupon.seleniumgridextras.SeleniumGridExtras;
import com.groupon.seleniumgridextras.utilities.ImageUtils;

import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import static org.junit.Assert.assertEquals;

public class MissingFrameImageTest {

  @Test
  public void testGetMissingImageString() throws Exception {
    File
        imageFile =
        new File(SeleniumGridExtras.class.getClassLoader().getResource("frame_missing_image.png")
                     .getFile());

    assertEquals(ImageUtils.encodeToString(ImageIO.read(imageFile), "PNG"),
                 MissingFrameImage.getMissingFrame());


  }

}
