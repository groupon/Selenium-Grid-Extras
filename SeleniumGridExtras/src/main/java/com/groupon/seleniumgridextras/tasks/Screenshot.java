/**
 * Copyright (c) 2013, Groupon, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * Neither the name of GROUPON nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * Created with IntelliJ IDEA.
 * User: Dima Kovalenko (@dimacus) && Darko Marinov
 * Date: 5/10/13
 * Time: 4:06 PM
 */

package com.groupon.seleniumgridextras.tasks;

import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.RuntimeConfig;
import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Screenshot extends ExecuteOSTask {

  public Screenshot() {
    setEndpoint("/screenshot");
    setDescription("Take a full OS screen Screen Shot of the node");
    Map<String, String> params = new HashMap();
    params.put("width", "width");
    params.put("height", "height");
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass("btn-info");
    setButtonText("screenshot");
    setEnabledInGui(true);

    addResponseDescription("file_type", "Type of file returned (PNG/JPG/GIF)");
    addResponseDescription("file", "Name of the file saved on the Node's HD");
    addResponseDescription("image", "Base64 URL Encoded (ISO-8859-1) string of the image");

  }

  @Override
  public JsonObject execute() {
    return execute(new HashMap<String, String>());
  }

  @Override
  public JsonObject execute(Map<String, String> parameter) {

    int width = parameter.containsKey("width") ? Integer.parseInt(parameter.get("width")) : 0;
    int height = parameter.containsKey("height") ? Integer.parseInt(parameter.get("height")) : 0;

    return createScreenshot(width, height);
  }

  private JsonObject createScreenshot(int width, int height) {
    String filename;
    String encodedImage;
    try {
      BufferedImage screenshot = takeScreenshot();
      if(width > 0 || height >0){
        screenshot = createResizedCopy(screenshot, width, height, true);
      }
      try {
        filename = writeImageToDisk(screenshot);

        ByteArrayOutputStream baos = writeImageToStream(screenshot);

        encodedImage = encodeStreamToBase64(baos);

        encodedImage = java.net.URLEncoder.encode(encodedImage, "ISO-8859-1");

      } catch (IOException e) {
        getJsonResponse().addKeyValues("error", "Error Saving image to file\n " + e);
        return getJsonResponse().getJson();
      }
      getJsonResponse().addKeyValues("file_type", "PNG");
      getJsonResponse().addKeyValues("file",
          RuntimeConfig.getExposedDirectory() + "/" + filename);
      getJsonResponse().addKeyValues("image", encodedImage);
      return getJsonResponse().getJson();
    } catch (AWTException error) {
      getJsonResponse().addKeyValues("error", "Error with AWT Robot\n" + error);
      return getJsonResponse().getJson();
    }
  }

  private String encodeStreamToBase64(ByteArrayOutputStream byteArrayOutputStream) throws IOException {
    String encodedImage;
    Base64 base = new Base64(false);
    encodedImage = base.encodeToString(byteArrayOutputStream.toByteArray());
    byteArrayOutputStream.close();
    return encodedImage;
  }

  private ByteArrayOutputStream writeImageToStream(BufferedImage screenshot) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(screenshot, "png", baos);
    baos.flush();
    return baos;
  }

  private String writeImageToDisk(BufferedImage screenshot) throws IOException {
    String filename;
    String directory = RuntimeConfig.getExposedDirectory();
    filename = createTimestampFilename();
    String fullPath = directory + "/" + filename;
    File outputFile = new File(fullPath);
    outputFile.mkdirs();
    ImageIO.write(screenshot, "png", outputFile);
    return filename;
  }

  private String createTimestampFilename() {
    String filename;Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("MM_dd_yyyy_h_mm_ss_a");
    String formattedTimestamp = sdf.format(date);
    filename = "screenshot_" + formattedTimestamp + ".png";
    return filename;
  }

  private BufferedImage takeScreenshot() throws AWTException {
    Robot robot = new Robot();
    Rectangle captureSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    return robot.createScreenCapture(captureSize);
  }

  private BufferedImage createResizedCopy(Image originalImage,
                                          int scaledWidth, int scaledHeight,
                                          boolean preserveAlpha) {
    int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
    BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
    Graphics2D g = scaledBI.createGraphics();
    if (preserveAlpha) {
      g.setComposite(AlphaComposite.Src);
    }
    g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
    g.dispose();
    return scaledBI;
  }

  @Override
  public List<String> getDependencies() {
    List<String> localDependencies = new LinkedList<String>();

    localDependencies.add("com.groupon.seleniumgridextras.tasks.ExposeDirectory");
    return localDependencies;
  }
}