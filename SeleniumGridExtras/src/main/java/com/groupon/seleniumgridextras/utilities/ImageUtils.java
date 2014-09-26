package com.groupon.seleniumgridextras.utilities;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtils {

  /**
   * Decode string to image
   * @param imageString The string to decode
   * @return decoded image
   */
  public static BufferedImage decodeToImage(String imageString) {

    BufferedImage image = null;
    byte[] imageByte;
    try {
      BASE64Decoder decoder = new BASE64Decoder();
      imageByte = decoder.decodeBuffer(imageString);
      ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);

      image = ImageIO.read(bis);
      bis.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return image;
  }

  /**
   * Encode image to string
   * @param image The image to encode
   * @param type jpeg, bmp, ...
   * @return encoded string
   */
  public static String encodeToString(BufferedImage image, String type) {
    String imageString = null;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    try {
      ImageIO.write(image, type, bos);
      byte[] imageBytes = bos.toByteArray();

      BASE64Encoder encoder = new BASE64Encoder();
      imageString = encoder.encode(imageBytes);

      bos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return imageString;
  }

  public static BufferedImage readImage(File image) throws IOException {
    return ImageIO.read(image);
  }

  public static void saveImage(File filename, BufferedImage image) throws IOException {
    ImageIO.write(image, "png", filename.getAbsoluteFile());
  }

}