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

import com.groupon.seleniumgridextras.JsonResponseBuilder;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.utilities.ScreenshotUtility;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Screenshot extends ExecuteOSTask {

  private static final String WIDTH = "width";
  private static final String HEIGHT = "height";
  private static final String KEEP = "keep";
  private static final String FILE_TYPE = "file_type";
  private static final String FILE = "file";
  private static final String IMAGE = "image";
  private static final String HOSTNAME = "hostname";
  private static final String IP = "ip";
  private static final String TIMESTAMP = "timestamp";
  private static final String PNG = "png";
  private static Logger logger = Logger.getLogger(Screenshot.class);

  public Screenshot() {
    setEndpoint("/screenshot");
    setDescription("Take a full OS screen Screen Shot of the node");
    JsonObject params = new JsonObject();
    params.addProperty(WIDTH, WIDTH);
    params.addProperty(HEIGHT, HEIGHT);
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass("btn-info");
    setButtonText("screenshot");
    setEnabledInGui(true);

    addResponseDescription(FILE_TYPE, "Type of file returned (PNG/JPG/GIF)");
    addResponseDescription(FILE, "Name of the file saved on the NodeConfig's HD");
    addResponseDescription(IMAGE, "Base64 URL Encoded (ISO-8859-1) string of the image");
    addResponseDescription(HOSTNAME, "Human readable machine name");
    addResponseDescription(IP, "IP Address of current machine");
    addResponseDescription(TIMESTAMP, "Timestamp of the screenshot");

  }

  @Override
  public JsonObject execute() {
    return execute(new HashMap<String, String>());
  }

  @Override
  public JsonObject execute(Map<String, String> parameter) {

    int width = parameter.containsKey(WIDTH) ? Integer.parseInt(parameter.get(WIDTH)) : 0;
    int height = parameter.containsKey(HEIGHT) ? Integer.parseInt(parameter.get(HEIGHT)) : 0;
    boolean
        keepFile =
        parameter.containsKey(KEEP) ? Boolean.parseBoolean(parameter.get(KEEP)) : true;
    return createScreenshot(width, height, keepFile);
  }

  private JsonObject createScreenshot(int width, int height, boolean keepFile) {
    String filename;
    String encodedImage;
    try {
      BufferedImage screenshot = ScreenshotUtility.getResizedScreenshot(width, height);
      try {
        if (keepFile) {
          filename = writeImageToDisk(screenshot);
        } else {
          filename = "not_saved";
        }
        ByteArrayOutputStream baos = writeImageToStream(screenshot);

        encodedImage = encodeStreamToBase64(baos);

        encodedImage = java.net.URLEncoder.encode(encodedImage, "ISO-8859-1");

      } catch (IOException e) {
        getJsonResponse().addKeyValues(JsonResponseBuilder.ERROR, "Error Saving image to file\n " + e);
        return getJsonResponse().getJson();
      }
      getJsonResponse().addKeyValues(FILE_TYPE, "PNG");
      getJsonResponse().addKeyValues(FILE,
                                     RuntimeConfig.getConfig().getSharedDirectory() + RuntimeConfig
                                         .getOS().getFileSeparator() + filename);
      getJsonResponse().addKeyValues(IMAGE, encodedImage);

      getJsonResponse().addKeyValues(HOSTNAME, RuntimeConfig.getOS().getHostName());
      getJsonResponse().addKeyValues(IP, RuntimeConfig.getOS().getHostIp());
      Date newTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
      getJsonResponse().addKeyValues(TIMESTAMP, newTimestamp.toString());

      return getJsonResponse().getJson();
    } catch (AWTException error) {
      getJsonResponse().addKeyValues(JsonResponseBuilder.ERROR, "Error with AWT Robot\n" + error);
      return getJsonResponse().getJson();
    }
  }

  private String encodeStreamToBase64(ByteArrayOutputStream byteArrayOutputStream)
      throws IOException {
    String encodedImage;
    Base64 base = new Base64(false);
    encodedImage = base.encodeToString(byteArrayOutputStream.toByteArray());
    byteArrayOutputStream.close();
    return encodedImage;
  }

  private ByteArrayOutputStream writeImageToStream(BufferedImage screenshot) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(screenshot, PNG, baos);
    baos.flush();
    return baos;
  }

  private String writeImageToDisk(BufferedImage screenshot) throws IOException {
    String filename;
    String directory = RuntimeConfig.getConfig().getSharedDirectory();
    filename = createTimestampFilename();
    String fullPath = directory + RuntimeConfig.getOS().getFileSeparator() + filename;
    File outputFile = new File(fullPath);
    outputFile.mkdirs();
    ImageIO.write(screenshot, PNG, outputFile);
    return filename;
  }

  private String createTimestampFilename() {
    String filename;
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("MM_dd_yyyy_h_mm_ss_a");
    String formattedTimestamp = sdf.format(date);
    filename = "screenshot_" + formattedTimestamp + "." + PNG;
    return filename;
  }

  @Override
  public List<String> getDependencies() {
    List<String> localDependencies = new LinkedList<String>();

    localDependencies.add("com.groupon.seleniumgridextras.tasks.ExposeDirectory");
    return localDependencies;
  }

  @Override
  public boolean initialize() {

    try {
      logger.debug("Starting the AWT service");
      this.execute();
      printInitilizedSuccessAndRegisterWithAPI();
      return true;
    } catch (NullPointerException error) {
      printInitilizedFailure();
      logger.error(error);
      return false;
    }
  }

}