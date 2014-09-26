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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.groupon.seleniumgridextras.ExecuteCommand;
import com.groupon.seleniumgridextras.browser.BrowserVersionDetector;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.config.remote.ConfigPuller;
import com.groupon.seleniumgridextras.grid.GridStarter;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.Map;

public class StartGrid extends ExecuteOSTask {

  private static final String ATTEMPTING_TO_START_GRID_NODES = "Attempting to start Grid Nodes";
  private static final String ATTEMPTING_TO_START_GRID_HUB = "Attempting to start Grid Hub";
  private static final
  String CANT_LAUNCH_ERROR = "Something didn't go right in launching service";
  private static final
  String UPDATING_BROWSER_VERSIONS = "Updating browser capabilities, this may take some time";
  private static Logger logger = Logger.getLogger(StartGrid.class);

  public StartGrid() {
    waitToFinishTask = false;

    setEndpoint(TaskDescriptions.Endpoints.START_GRID);
    setDescription(TaskDescriptions.Description.START_GRID);
    JsonObject params = new JsonObject();
    params.addProperty(JsonCodec.WebDriver.Grid.ROLE, "hub|node - defaults to 'default_role' param in config file");
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass("btn-info");
    setButtonText(TaskDescriptions.UI.ButtonText.START_GRID);
    setEnabledInGui(false);
  }

  @Override
  public JsonObject execute() {
    return execute(RuntimeConfig.getConfig().getDefaultRole());
  }

  @Override
  public JsonObject execute(String role) {
    try {
      if (role.equals("hub")) {
        return startHub();
      } else {
        return startNodes();
      }

    } catch (Exception error) {
      getJsonResponse().addKeyValues(JsonCodec.ERROR, error.toString());
      return getJsonResponse().getJson();
    }
  }

  @Override
  public JsonObject execute(Map<String, String> parameter) {
    if (parameter.isEmpty() || !parameter.containsKey(JsonCodec.WebDriver.Grid.ROLE)) {
      return execute();
    } else {
      return execute(parameter.get(JsonCodec.WebDriver.Grid.ROLE).toString());
    }
  }

  private JsonObject startNodes() {
    File configsDirectory = RuntimeConfig.getConfig().getConfigsDirectory();
    if (configsDirectory.exists()) {
      new ConfigPuller().updateFromRemote();
    }

    System.out.println(UPDATING_BROWSER_VERSIONS);
    logger.info(UPDATING_BROWSER_VERSIONS);
    new BrowserVersionDetector(RuntimeConfig.getConfig().getNodes()).updateVersions();

    System.out.println(ATTEMPTING_TO_START_GRID_NODES);
    logger.info(ATTEMPTING_TO_START_GRID_NODES);
    return GridStarter.startAllNodes(getJsonResponse());
  }

  private JsonObject startHub() {
    System.out.println(ATTEMPTING_TO_START_GRID_HUB);

    JsonObject serviceStartResponse = ExecuteCommand.execRuntime(
        GridStarter.getOsSpecificHubStartCommand(RuntimeConfig.getOS().isWindows())
        , false);

    logger.info(serviceStartResponse);

    if (serviceStartResponse.get(JsonCodec.EXIT_CODE).toString().equals("0")) {
      getJsonResponse().addKeyValues(JsonCodec.OUT,
                                     "Service start command sent, might take as long as 10 seconds to spin up");
    } else {
      logger.error(CANT_LAUNCH_ERROR);
      logger.error(serviceStartResponse);
      getJsonResponse().addKeyValues(JsonCodec.ERROR, (JsonArray) serviceStartResponse.get(JsonCodec.ERROR));
    }

    return getJsonResponse().getJson();

  }


}
