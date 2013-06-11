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

package com.groupon;

import org.apache.commons.codec.binary.Base64;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;

public class Screenshot extends ExecuteOSTask {

  @Override
  public String getEndpoint() {
    return "/screenshot";
  }

  @Override
  public String getDescription() {
    return "Take a full OS screen Screen Shot of the node";
  }

  @Override
  public String execute() {

    String filename;
    String encodedImage;

    //Todo: Clean this mess up!!!!
    // This is so ugly it makes me cry

    try {
      Robot robot = new Robot();
      Rectangle captureSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
      BufferedImage screenshot = robot.createScreenCapture(captureSize);
      try {
        String directory = RuntimeConfig.getSharedDirectory();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM_dd_yyyy_h_mm_ss_a");
        String formattedTimestamp = sdf.format(date);
        filename = "screenshot_" + formattedTimestamp + ".png";
        String fullPath = directory + "/" + filename;
        File outputfile = new File(fullPath);
        ImageIO.write(screenshot, "png", outputfile);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(screenshot, "png", baos);
        baos.flush();

        Base64 base = new Base64(false);
        encodedImage = base.encodeToString(baos.toByteArray());
        baos.close();

        encodedImage = java.net.URLEncoder.encode(encodedImage, "ISO-8859-1");

      } catch (IOException e) {
        return JsonWrapper.taskResultToJson(1, "", "Error Saving image to file\n " + e);
      }
      return JsonWrapper.screenshotToJson(encodedImage, filename, "png");
    } catch (AWTException error) {
      return JsonWrapper.taskResultToJson(1, "", "Error with AWT Robot\n" + error);
    }
  }


  @Override
  public Map getResponseDescription() {
    Map response = new HashMap();
    response.put("file_type","Type of file returned (PNG/JPG/GIF)");
    response.put("file", "Name of the file saved on the Node's HD");
    response.put("image", "Base64 URL Encoded (ISO-8859-1) string of the image");
    return response;
  }



  @Override
  public List<String> getDependencies(){
    List<String> localDependencies = new LinkedList<String>();

    localDependencies.add("com.groupon.ExposeDirectory");
    return localDependencies;
  }
}