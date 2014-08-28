package com.groupon.seleniumgridextras.utilities;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA. User: dima Date: 8/28/14 Time: 11:17 AM To change this template use
 * File | Settings | File Templates.
 */
public class ScreenshotUtility {

  private static Logger logger = Logger.getLogger(ScreenshotUtility.class);

  public static BufferedImage getFullScreenshot() throws AWTException {
    Robot robot = new Robot();
    Rectangle captureSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    return robot.createScreenCapture(captureSize);
  }

  public static BufferedImage getResizedScreenshot(int width, int height) throws AWTException {

    BufferedImage screenshot = getFullScreenshot();

    if (width > 0 || height > 0) {
      BufferedImage sizedImage = null;
      try {
        sizedImage = Thumbnails.of(getFullScreenshot())
            .size(width, height)
            .asBufferedImage();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return sizedImage;
    } else {
      return screenshot;
    }


  }

}
