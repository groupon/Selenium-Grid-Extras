package com.groupon.seleniumgridextras.utilities;

import com.groupon.seleniumgridextras.utilities.json.JsonCodec;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

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
        logger.error(e);
        e.printStackTrace();
      }
      return sizedImage;
    } else {
      return screenshot;
    }


  }

  public static String getResizedScreenshotAsBase64String(int width, int height)
      throws AWTException {
    BufferedImage screenshot = getResizedScreenshot(width, height);
    try {
      ByteArrayOutputStream baos = writeImageToStream(screenshot);
      return encodeStreamToBase64(baos);
    } catch (IOException e) {
      logger.error(e);
      e.printStackTrace();
      return "";
    }
  }

  public static String encodeStreamToBase64(ByteArrayOutputStream byteArrayOutputStream) throws IOException {
    String encodedImage;
    Base64 base = new Base64(false);
    encodedImage = base.encodeToString(byteArrayOutputStream.toByteArray());
    byteArrayOutputStream.close();
    return encodedImage;
  }

  public static ByteArrayOutputStream writeImageToStream(BufferedImage screenshot)
      throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(screenshot, JsonCodec.Images.PNG, baos);
    baos.flush();
    return baos;
  }

}
