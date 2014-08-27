package com.groupon.seleniumgridextras.videorecording;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageProcessor {


  public static BufferedImage addTextCaption(BufferedImage image, String line1, String line2, String line3, String line4){

      Graphics g = image.getGraphics();

      g.setColor(new Color(129, 182, 64, 128)); //Set the rectangle color to Groupon green and transperent
      g.fillRect(0, 700, 1024, 52); //Add rectangle at the very bottom of the screen



      g.setColor(Color.white); //Set text font color
      g.setFont(g.getFont().deriveFont(12f)); //Set text size

      g.drawString(line1, 10, 714);
      g.drawString(line2, 10, 726);
      g.drawString(line3, 10, 738);
      g.drawString(line4, 10, 750);
      g.dispose();

    return image;


  }

}
