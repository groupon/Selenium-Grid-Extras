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

import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import com.groupon.seleniumgridextras.utilities.json.JsonResponseBuilder;
import com.groupon.seleniumgridextras.downloader.Downloader;
import com.groupon.seleniumgridextras.downloader.IEDownloader;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.Map;

public class DownloadIEDriver extends ExecuteOSTask {

  private static final String BIT = "bit";
  private static final String WIN32 = "Win32";
  private static final String VERSION = "version";
  private static final String SOURCE_URL = "source_url";
  private static final String FILE_FULL_PATH = "file_full_path";
  private static final String FILE = "file";
  private String bit = WIN32;
  private static Logger logger = Logger.getLogger(DownloadIEDriver.class);

  public DownloadIEDriver() {
    setEndpoint("/download_iedriver");
    setDescription("Downloads a version of IEDriver.exe to local machine");
    JsonObject params = new JsonObject();
    params.addProperty(VERSION, "Version of IEDriver to download, such as 2.33.0");
    params.addProperty(BIT, "Bit version of IEDriver Win32/x64 - (default: Win32)");
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass("btn-success");
    setButtonText("Download IE-Driver");
    setEnabledInGui(true);

    addResponseDescription("root_dir", "Directory to which EXE file was saved to");
    addResponseDescription(FILE, "Relative path to file on the node");
    addResponseDescription(FILE_FULL_PATH, "Full path to file on node");
    addResponseDescription(SOURCE_URL,
                           "Url from which the EXE was downloaded. If file already exists, this will be blank, and download will be skipped");

    this.bit = RuntimeConfig.getConfig().getIEdriver().getBit();
    getJsonResponse()
        .addKeyValues("root_dir", RuntimeConfig.getConfig().getIEdriver().getDirectory());
    getJsonResponse().addKeyValues(SOURCE_URL, "");

  }

  @Override
  public JsonObject execute() {
    return execute(RuntimeConfig.getConfig().getIEdriver().getVersion());
  }

  @Override
  public JsonObject execute(Map<String, String> parameter) {

    if (parameter.isEmpty() || !parameter.containsKey(VERSION)) {
      return execute();
    } else {

      if (parameter.containsKey(BIT)) {
        this.bit = parameter.get(BIT).toString();
      } else {
        this.bit = WIN32;
      }

      return execute(parameter.get(VERSION).toString());
    }
  }

  @Override
  public JsonObject execute(String version) {

    Downloader
        downloader =
        new IEDownloader(version, bit);

    if (!new File(RuntimeConfig.getConfig().getIEdriver().getExecutablePath()).exists()) {
      Boolean downloaded = downloader.download();
      getJsonResponse().addKeyValues(SOURCE_URL, downloader.getSourceURL());

      if (!downloaded) {
        getJsonResponse().addKeyValues(JsonCodec.ERROR, downloader.getErrorMessage());
      }
    } else {
      logger.debug("No need for download");
      getJsonResponse().addKeyValues(JsonCodec.OUT, "File already downloaded, will not download again");
    }

    getJsonResponse()
        .addKeyValues(FILE_FULL_PATH,
                      downloader.getDestinationFileFullPath().getAbsolutePath());

    getJsonResponse()
        .addKeyValues(FILE, downloader.getDestinationFileFullPath().getName());


    return getJsonResponse().getJson();
  }

  @Override
  public boolean initialize() {

    try {
      File ieDriverExe = new File(RuntimeConfig.getConfig().getIEdriver().getExecutablePath());
      File ieDriverHome = new File(RuntimeConfig.getConfig().getIEdriver().getDirectory());

      if (!ieDriverHome.exists()) {
        ieDriverHome.mkdir();
      }

      if (!ieDriverExe.exists()) {
        systemAndLog("Download IE Driver Executable " + RuntimeConfig.getConfig().getIEdriver().getVersion());
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
