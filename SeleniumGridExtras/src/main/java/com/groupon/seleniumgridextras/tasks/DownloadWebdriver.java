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


import com.groupon.seleniumgridextras.grid.GridWrapper;
import com.groupon.seleniumgridextras.JsonResponseBuilder;
import com.groupon.seleniumgridextras.RuntimeConfig;
import com.groupon.seleniumgridextras.tasks.ExecuteOSTask;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DownloadWebdriver extends ExecuteOSTask {

  @Override
  public String getEndpoint() {
    return "/download_webdriver";
  }

  @Override
  public String getDescription() {
    return "Downloads a version of WebDriver jar to local machine";
  }

  @Override
  public String execute() {
    return downloadWebdriverVersion(RuntimeConfig.getWebdriverVersion());
  }

  @Override
  public String execute(String version) {
    return downloadWebdriverVersion(version);
  }

  @Override
  public String execute(Map<String, String> parameter) {

    if (parameter.isEmpty() || !parameter.containsKey("version")) {
      return execute();
    } else {
      return execute(parameter.get("version").toString());
    }
  }


  @Override
  public JsonResponseBuilder getJsonResponse() {
    if (jsonResponse == null) {
      jsonResponse = new JsonResponseBuilder();

      jsonResponse.addKeyDescriptions("exit_code", "Record if download was successful or not");
      jsonResponse.addKeyDescriptions("root_dir", "Directory to which JAR file was saved to");
      jsonResponse.addKeyDescriptions("file", "Filename on node's computer");
      jsonResponse.addKeyDescriptions("source_url",
                                      "Url from which the JAR was downloaded. If JAR file already exists, this will be blank, and download will be skipped");
      jsonResponse.addKeyDescriptions("error", "Any Errors that occured");

      jsonResponse.addKeyValues("exit_code", 0);
      jsonResponse.addKeyValues("root_dir", GridWrapper.getWebdriverHome());
    }
    return jsonResponse;
  }

  @Override
  public Map getAcceptedParams() {
    Map<String, String> params = new HashMap();
    params.put("version", "Version of WebDriver to download, such as 2.33.0");
    return params;
  }

  private String downloadWebdriverVersion(String version) {

    String webdriverDir = GridWrapper.getWebdriverHome();
    System.out.println("Downloading Driver to " + webdriverDir);
    createWebdriverDir(webdriverDir);

    try {
      URL url = new URL(getUrl(version));
      System.out.println("Source URL: " + url);
      String jarFile = webdriverDir + "/" + version + ".jar";
      System.out.println("Target file: " + jarFile);
      File destination = new File(jarFile);

      if (destination.exists()) {
        System.out.println("File already exists, will not download");
        getJsonResponse().addKeyValues("file", jarFile);
        getJsonResponse().addKeyValues("out", "File already exist, no need to download again.");
        return getJsonResponse().toString();
      } else {
        System.out.println("File does not exist, will download");
        FileUtils.copyURLToFile(url, destination);
        System.out.println("Download complete from " + url);

        getJsonResponse().addKeyValues("file", jarFile);
        getJsonResponse().addKeyValues("source_url", url.toString());

        return getJsonResponse().toString();
      }


    } catch (MalformedURLException error) {
      getJsonResponse().addKeyValues("error", error.toString());
      return getJsonResponse().toString();
    } catch (IOException error) {
      getJsonResponse().addKeyValues("error", error.toString());
      return getJsonResponse().toString();
    }
  }

  private void createWebdriverDir(String dirString) {
    File dir = new File(dirString);

    if (dir.exists()) {
      System.out.println(dirString + " already exists");
      //Do nothing, it's already there
    } else {
      System.out.println(dirString + " does not yet exist. Creating it");
      dir.mkdir();
    }
  }

  private String getUrl(String version) {
    String
        fullUrl =
        "http://selenium.googlecode.com/files/selenium-server-standalone-" + version + ".jar";
    System.out.println("Will download from " + fullUrl);
    return fullUrl;
  }

  @Override
  public boolean initialize() {

    try {
      File webdriverJar = new File(GridWrapper.getCurrentJarPath());
      File webdriverHome = new File(RuntimeConfig.getWebdriverParentDir());

      if (!webdriverHome.exists()) {
         webdriverHome.mkdir();
      }

      if (!webdriverJar.exists()) {
        downloadWebdriverVersion(GridWrapper.getWebdriverVersion());
      }


    } catch (NullPointerException error) {
      printInitilizedFailure();
      System.out.println(error);
      return false;
    }

    printInitilizedSuccessAndRegisterWithAPI();
    return true;

  }


}
