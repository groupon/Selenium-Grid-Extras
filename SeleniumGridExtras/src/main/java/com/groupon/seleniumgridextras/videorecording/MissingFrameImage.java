package com.groupon.seleniumgridextras.videorecording;

import com.groupon.seleniumgridextras.SeleniumGridExtras;
import com.groupon.seleniumgridextras.utilities.ImageUtils;

import org.apache.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA. User: dima Date: 8/26/14 Time: 5:20 PM To change this template use
 * File | Settings | File Templates.
 */
public class MissingFrameImage {
  private static Logger logger = Logger.getLogger(MissingFrameImage.class);
  final protected static File missingFrameImage = new File(SeleniumGridExtras.class.getClassLoader().getResource("frame_missing_image.png")
                                                               .getFile());

  public static BufferedImage getMissingFrameAsBufferedImage() throws IOException {
    return ImageUtils.readImage(missingFrameImage);
  }

  public static String getMissingFrame(){
    try {
      return ImageUtils.encodeToString(getMissingFrameAsBufferedImage(), "PNG");
    } catch (IOException e) {
      logger.warn("Could not read the missingFrameIMage " + missingFrameImage.getAbsolutePath());
      logger.warn(e);
      e.printStackTrace();
      return "";
    }


  }

}
