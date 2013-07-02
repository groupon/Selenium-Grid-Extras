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

package com.groupon.seleniumgridextras;

import com.groupon.seleniumgridextras.grid.GridWrapper;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FileDownloader {


  public static Map<String, String> downloadWebdriverVersion(String version) {

    Map<String, String> returnStatus = new HashMap<String, String>();
    returnStatus.put("error", "");
    returnStatus.put("out", "");
    returnStatus.put("file", "");
    returnStatus.put("source_url", "");

    String webdriverDir = GridWrapper.getWebdriverHome();
    System.out.println("Downloading Driver to " + webdriverDir);
    createWebdriverDir(webdriverDir);

    try {
      URL url = new URL(getUrl(version));
      System.out.println("Source URL: " + url);
      String jarFile = version + ".jar";
      System.out.println("Target file: " + jarFile);
      File destination = new File(GridWrapper.getWebdriverHome() + "/" + jarFile);

      if (destination.exists()) {
        System.out.println("File already exists, will not download");
        returnStatus.put("file", jarFile);
        returnStatus.put("out", "File already exist, no need to download again.");
        return returnStatus;
      } else {
        System.out.println("File does not exist, will download");
        FileUtils.copyURLToFile(url, destination);
        System.out.println("Download complete from " + url);
        returnStatus.put("file", jarFile);
        returnStatus.put("source_url", url.toString());
        return returnStatus;
      }


    } catch (Exception error) {
      returnStatus.put("error", error.toString());
      return returnStatus;
    }
  }


  private static void createWebdriverDir(String dirString) {
    File dir = new File(dirString);

    if (dir.exists()) {
      System.out.println(dirString + " already exists");
      //Do nothing, it's already there
    } else {
      System.out.println(dirString + " does not yet exist. Creating it");
      dir.mkdir();
    }
  }

  private static String getUrl(String version) {
    String
        fullUrl =
        "http://selenium.googlecode.com/files/selenium-server-standalone-" + version + ".jar";
    System.out.println("Will download from " + fullUrl);
    return fullUrl;
  }

}
