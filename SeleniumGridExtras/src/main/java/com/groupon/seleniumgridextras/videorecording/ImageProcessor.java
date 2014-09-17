package com.groupon.seleniumgridextras.videorecording;

import com.groupon.seleniumgridextras.config.RuntimeConfig;

import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;


public class ImageProcessor {

  private static Logger logger = Logger.getLogger(ImageProcessor.class);

  public static BufferedImage addTextCaption(BufferedImage image, String line1, String line2,
                                             String line3, String line4) {
    try {
      Graphics g = image.getGraphics();

      final int imageHeight = image.getHeight();
      final int imageWidth = image.getWidth();
      final int borderHeight = 52;
      final int firstLineStartHeight = imageHeight - borderHeight;
      final int textLeftStartPosition = 40;

      Color backgroundColor;
      Color fontColor;
      if (RuntimeConfig.getConfig() != null) {
        backgroundColor =
            RuntimeConfig.getConfig().getVideoRecording().getLowerThirdBackgroundColor();
        fontColor = RuntimeConfig.getConfig().getVideoRecording().getLowerThirdFontColor();
      } else {
        backgroundColor = new Color(0, 0, 0, 200);
        fontColor = new Color(255, 255, 255, 255);
      }

      g.setColor(backgroundColor);

      //Add rectangle at the very bottom of the screen
      g.fillRect(0, firstLineStartHeight, imageWidth, borderHeight);

      g.setColor(fontColor);
      g.setFont(g.getFont().deriveFont(12f)); //Set text size

      g.drawString("" + line1, textLeftStartPosition, firstLineStartHeight + 12);
      g.drawString("" + line2, textLeftStartPosition, firstLineStartHeight + 24);
      g.drawString("" + line3, textLeftStartPosition, firstLineStartHeight + 36);
      g.drawString("" + line4, textLeftStartPosition, firstLineStartHeight + 48);
      g.dispose();

    } catch (Exception e) {
      logger.error("Input lines where: " + line1 + " - " + line2 + " - " + line3 + " - " + line4);
      logger.error("Problem with adding caption to screenshot");
      logger.error(e);
      e.printStackTrace();
    }

    return image;


  }

  public static BufferedImage createTitleFrame(Dimension dimension, int imageType, String line1,
                                               String line2,
                                               String line3) {
    BufferedImage image = new BufferedImage(dimension.width, dimension.height, imageType);

    Graphics g = image.getGraphics();
    g.setColor(
        RuntimeConfig.getConfig().getVideoRecording().getTitleFrameFontColor());

    int height = image.getHeight();
    int width = image.getWidth();
    int firstLineX = ((Double) (width * 0.1)).intValue();
    int firstLineY = ((Double) (width * 0.1)).intValue();
    int secondLineY = firstLineY + 20;
    int thirdLineY = secondLineY + 12;

    g.setFont(g.getFont().deriveFont(20f));
    g.drawString("" + line1, firstLineX, firstLineY);
    g.setFont(g.getFont().deriveFont(12f));
    g.drawString("" + line2, firstLineX, secondLineY);
    g.drawString("" + line3, firstLineX, thirdLineY);

    return image;
  }

}
