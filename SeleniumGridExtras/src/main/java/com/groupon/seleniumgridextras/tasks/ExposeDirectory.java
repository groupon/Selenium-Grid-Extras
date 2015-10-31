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
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ExposeDirectory extends ExecuteOSTask {

  public File sharedDir;
  private static Logger logger = Logger.getLogger(ExposeDirectory.class);

  public ExposeDirectory() {
    setEndpoint(TaskDescriptions.Endpoints.DIR);
    setDescription(
        TaskDescriptions.Description.DIR);
    setRequestType(TaskDescriptions.HTTP.GET);
    setResponseType(TaskDescriptions.HTTP.JSON);
    setClassname(this.getClass().getCanonicalName());
    setCssClass(TaskDescriptions.UI.BTN_SUCCESS);
    setButtonText(TaskDescriptions.UI.ButtonText.DIR);
    setEnabledInGui(true);

    addResponseDescription(JsonCodec.FILES, "Array list of files in the shared directory");
  }


  @Override
  public JsonObject execute() {
    File[] files = sharedDir.listFiles();
    List<String> filesToString = new LinkedList<>();

    for (File f : files) {
      filesToString.add(f.toString());
    }
    getJsonResponse().addKeyValues(JsonCodec.FILES, filesToString);
    return getJsonResponse().getJson();
  }

  public File getExposedDirectory() {
    return sharedDir;
  }

  private void createDir() {
    File dir = sharedDir;
    dir.mkdir();
  }

  public boolean cleanUpExposedDirectory() {
    try {
      FileUtils.deleteDirectory(sharedDir);
      createDir();
      return true;

    } catch (IOException error) {
      logger.error("Attempt to delete " + RuntimeConfig.getConfig().getSharedDirectory() + " FAILED!!!");
      return false;
    }
  }


  @Override
  public boolean initialize() {

    try {
      sharedDir = new File(RuntimeConfig.getConfig().getSharedDirectory());

      if (sharedDir.exists()) {
        cleanUpExposedDirectory();
      } else {
        createDir();
      }

    } catch (NullPointerException error) {
      printInitilizedFailure();
      logger.error("  'expose_directory' variable was not set in the config " + error);
      return false;
    }

    printInitilizedSuccessAndRegisterWithAPI();
    return true;

  }
}
