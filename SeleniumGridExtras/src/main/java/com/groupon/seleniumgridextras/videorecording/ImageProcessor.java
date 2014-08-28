package com.groupon.seleniumgridextras.videorecording;

import com.groupon.seleniumgridextras.utilities.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;


public class ImageProcessor {


  public static BufferedImage addTextCaption(BufferedImage image, String line1, String line2,
                                             String line3, String line4) {

    Graphics g = image.getGraphics();

    final int imageHeight = image.getHeight();
    final int imageWidth = image.getWidth();
    final int borderHeight = 52;
    final int firstLineStartHeight = imageHeight - borderHeight;
    final int textLeftStartPosition = 40;


    g.setColor(
        new Color(129, 182, 64, 128)); //Set the rectangle color to Groupon green and transperent

    g.fillRect(0, firstLineStartHeight, imageWidth,
               borderHeight); //Add rectangle at the very bottom of the screen

    g.setColor(Color.white); //Set text font color
    g.setFont(g.getFont().deriveFont(12f)); //Set text size


    g.drawString(line1, textLeftStartPosition, firstLineStartHeight + 12);
    g.drawString(line2, textLeftStartPosition, firstLineStartHeight + 24);
    g.drawString(line3, textLeftStartPosition, firstLineStartHeight + 36);
    g.drawString(line4, textLeftStartPosition, firstLineStartHeight + 48);
    g.dispose();

    return image;


  }

  public static BufferedImage createTitleFrame(Dimension dimension, int imageType, String line1, String line2,
                                               String line3){
    BufferedImage image = new BufferedImage(dimension.width, dimension.height, imageType);

    Graphics g = image.getGraphics();
    g.setColor(
        new Color(129, 182, 64, 128)); //Set the rectangle color to Groupon green and transperent

    int height = image.getHeight();
    int width  = image.getWidth();
    int firstLineX = ((Double) ( width * 0.1)).intValue();
    int firstLineY = ((Double) ( width * 0.1)).intValue();
    int secondLineY = firstLineY + 20;
    int thirdLineY = secondLineY + 12;

    g.setFont(g.getFont().deriveFont(20f));
    g.drawString(line1, firstLineX, firstLineY );
    g.setFont(g.getFont().deriveFont(12f));
    g.drawString(line2, firstLineX, secondLineY);
    g.drawString(line3, firstLineX, thirdLineY);

    return image;
  }

}
