package com.groupon.seleniumgridextras.videorecording;

import com.groupon.seleniumgridextras.utilities.ImageUtils;
import com.groupon.seleniumgridextras.utilities.ScreenshotUtility;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA. User: dima Date: 8/28/14 Time: 10:57 AM To change this template use
 * File | Settings | File Templates.
 */
public class SaveProcessedScreenshot implements Callable{

  protected File output;
  protected Dimension dimension;
  protected String line1;
  protected String line2;
  protected String line3;
  protected String line4;

  public SaveProcessedScreenshot(String outputFile, Dimension dimension, String line1, String line2,
                                 String line3,
                                 String line4) {
    this.output = new File(outputFile);
    this.dimension = dimension;
    this.line1 = line1;
    this.line2 = line2;
    this.line3 = line3;
    this.line4 = line4;

  }

  @Override
  public Object call() throws Exception {
    BufferedImage screenshot = ScreenshotUtility.getResizedScreenshot(dimension.width, dimension.height);
    screenshot = ImageProcessor.addTextCaption(screenshot, line1, line2, line3, line4);
    ImageUtils.saveImage(output, screenshot);
    return "done";
  }




}
