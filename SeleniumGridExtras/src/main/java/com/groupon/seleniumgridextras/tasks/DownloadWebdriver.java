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


import com.groupon.seleniumgridextras.downloader.Downloader;
import com.groupon.seleniumgridextras.downloader.WdDownloader;
import com.groupon.seleniumgridextras.grid.GridWrapper;
import java.io.File;
import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

import java.util.Map;

public class DownloadWebdriver extends ExecuteOSTask {

  public DownloadWebdriver() {
    setEndpoint("/download_webdriver");
    setDescription("Downloads a version of WebDriver jar to local machine");
    JsonObject params = new JsonObject();
    params.addProperty("version", "Version of WebDriver to download, such as 2.33.0");
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass("btn-success");
    setButtonText("Download WebDriver");
    setEnabledInGui(true);

    addResponseDescription("root_dir", "Directory to which JAR file was saved to");
    addResponseDescription("file", "Relative path to file on the node");
    addResponseDescription("file_full_path", "Full path to file on node");
    addResponseDescription("source_url",
                           "Url from which the JAR was downloaded. If JAR file already exists, this will be blank, and download will be skipped");

    getJsonResponse()
        .addKeyValues("file_full_path", RuntimeConfig.getConfig().getWebdriver().getExecutablePath());

    getJsonResponse().addKeyValues("root_dir", GridWrapper.getWebdriverHome());


  }

  @Override
  public JsonObject execute() {
    return execute(RuntimeConfig.getConfig().getWebdriver().getVersion());
  }

  @Override
  public JsonObject execute(String version) {
    Downloader downloader = new WdDownloader(version);

    getJsonResponse().addKeyValues("file", downloader.getDestinationFile());
    getJsonResponse()
        .addKeyValues("file_full_path", downloader.getDestinationFileFullPath().getAbsolutePath());

    if (!downloader.getDestinationFileFullPath().exists()) {
      Boolean downloaded = downloader.download();
      getJsonResponse().addKeyValues("source_url", downloader.getSourceURL());

      if (!downloaded) {
        getJsonResponse().addKeyValues("error", downloader.getErrorMessage());
      }
    } else {
      getJsonResponse().addKeyValues("out", "File already downloaded, will not download again");
    }

    return getJsonResponse().getJson();
  }

  @Override
  public JsonObject execute(Map<String, String> parameter) {

    if (parameter.isEmpty() || !parameter.containsKey("version")) {
      return execute();
    } else {
      return execute(parameter.get("version").toString());
    }
  }


  @Override
  public boolean initialize() {

    try {
      System.out.println(GridWrapper.getCurrentWebDriverJarPath());
      System.out.println(RuntimeConfig.getConfig().getWebdriver().getDirectory());
      File webdriverJar = new File(GridWrapper.getCurrentWebDriverJarPath());
      File webdriverHome = new File(RuntimeConfig.getConfig().getWebdriver().getDirectory());

      if (!webdriverHome.exists()) {
        webdriverHome.mkdir();
      }

      if (!webdriverJar.exists()) {
        System.out.println("no jar");
        execute();
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
