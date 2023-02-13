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
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.downloader.EdgeDriverDownloader;
import com.groupon.seleniumgridextras.downloader.Downloader;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Map;

public class DownloadEdgeDriver extends ExecuteOSTask {

  private String bit = JsonCodec.WebDriver.Downloader.BIT_32;
  private static Logger logger = Logger.getLogger(DownloadEdgeDriver.class);

  public DownloadEdgeDriver() {
    setEndpoint(TaskDescriptions.Endpoints.DOWNLOAD_EDGEDRIVER);
    setDescription(TaskDescriptions.Description.DOWNLOAD_EDGEDRIVER);
    JsonObject params = new JsonObject();
    params.addProperty(JsonCodec.WebDriver.Downloader.VERSION,
                       "Version of EdgeDriver to download, such as 2.6");
    params.addProperty(JsonCodec.WebDriver.Downloader.BIT,
                       "Bit Version of EdgeDriver 32/64 - (default: 32)");
    setAcceptedParams(params);
    setRequestType(TaskDescriptions.HTTP.GET);
    setResponseType(TaskDescriptions.HTTP.JSON);
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass(TaskDescriptions.UI.BTN_SUCCESS);
    setButtonText(TaskDescriptions.UI.ButtonText.DOWNLOAD_EDGEDRIVER);
    setEnabledInGui(true);

    addResponseDescription(JsonCodec.WebDriver.Downloader.ROOT_DIR,
                           "Directory to which executable file was saved to");
    addResponseDescription(JsonCodec.WebDriver.Downloader.FILE,
                           "Relative path to file on the node");
    addResponseDescription(JsonCodec.WebDriver.Downloader.FILE_FULL_PATH,
                           "Full path to file on node");
    addResponseDescription(JsonCodec.WebDriver.Downloader.SOURCE_URL,
                           "Url from which the executable was downloaded. If file already exists, this will be blank, and download will be skipped");

    // bit value should be initialized from configuration
    this.bit = RuntimeConfig.getConfig().getEdgeDriver().getBit();

    logger.debug(RuntimeConfig.getConfig());
    getJsonResponse()
        .addKeyValues(JsonCodec.WebDriver.Downloader.ROOT_DIR,
                      RuntimeConfig.getConfig().getEdgeDriver().getDirectory());
    getJsonResponse().addKeyValues(
        JsonCodec.WebDriver.Downloader.SOURCE_URL, "");

  }

  @Override
  public JsonObject execute() {
    return execute(RuntimeConfig.getConfig().getEdgeDriver().getVersion());
  }

  @Override
  public JsonObject execute(Map<String, String> parameter) {

    if (!parameter.isEmpty() && parameter.containsKey(
        JsonCodec.WebDriver.Downloader.VERSION)) {
      if (parameter.containsKey(JsonCodec.WebDriver.Downloader.BIT)) {
        this.bit = parameter.get(JsonCodec.WebDriver.Downloader.BIT).toString();
      } else {
        this.bit = JsonCodec.WebDriver.Downloader.BIT_32;
      }
      return execute(parameter.get(JsonCodec.WebDriver.Downloader.VERSION).toString());
    } else {
      return execute();
    }
  }

  @Override
  public JsonObject execute(String version) {

    Downloader
        downloader =
        new EdgeDriverDownloader(version, this.bit);

    if (!new File(RuntimeConfig.getConfig().getEdgeDriver().getExecutablePath()).exists()) {
      Boolean downloaded = downloader.download();
      getJsonResponse().addKeyValues(
          JsonCodec.WebDriver.Downloader.SOURCE_URL, downloader.getSourceURL());

      if (!downloaded) {
        getJsonResponse().addKeyValues(JsonCodec.ERROR, downloader.getErrorMessage());
      }
    } else {
      logger.debug("No need for download");
      getJsonResponse()
          .addKeyValues(JsonCodec.OUT, "File already downloaded, will not download again");
    }

    getJsonResponse()
        .addKeyValues(JsonCodec.WebDriver.Downloader.FILE_FULL_PATH,
                      downloader.getDestinationFileFullPath().getAbsolutePath());

    getJsonResponse()
        .addKeyValues(JsonCodec.WebDriver.Downloader.FILE,
                      downloader.getDestinationFileFullPath().getName());

    return getJsonResponse().getJson();
  }

  @Override
  public boolean initialize() {

    try {
      File
          EdgeDriverExecutable =
          new File(RuntimeConfig.getConfig().getEdgeDriver().getExecutablePath());
      File EdgeDriverHome = new File(RuntimeConfig.getConfig().getEdgeDriver().getDirectory());

      if (!EdgeDriverHome.exists()) {
        EdgeDriverHome.mkdir();
      }

      if (!EdgeDriverExecutable.exists()) {
        systemAndLog("Downloading Edge Driver " + RuntimeConfig.getConfig().getEdgeDriver()
            .getVersion());
        logger.info(execute().toString());
      }


    } catch (NullPointerException error) {
      printInitilizedFailure();
      logger.error(error);
      return false;
    }

    printInitilizedSuccessAndRegisterWithAPI();
    return true;

  }


}

