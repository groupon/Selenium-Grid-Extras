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
import com.groupon.seleniumgridextras.browser.BrowserVersionDetector;
import com.groupon.seleniumgridextras.config.GridNode;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.config.capabilities.Capability;
import com.groupon.seleniumgridextras.config.remote.ConfigPuller;
import com.groupon.seleniumgridextras.config.remote.ConfigPusher;
import com.groupon.seleniumgridextras.grid.GridStarter;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.Map;

public class StartGrid extends ExecuteOSTask {

  private static final String ATTEMPTING_TO_START_GRID_NODES = "Attempting to start Grid Nodes";
  private static final String ATTEMPTING_TO_START_GRID_HUBS = "Attempting to start Grid Hubs";
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

  /**
   * Get latest node config file from hub (if configs directory exists).<br>
   * Update browser version in local node config file.<br>
   * Push updated config file back to hub (if configs directory exists).<br>
   * 
   * @return
   */
  private JsonObject startNodes() {
    File configsDirectory = RuntimeConfig.getConfig().getConfigsDirectory();
    if (!RuntimeConfig.getConfig().getAutoStartHub()) {
      if (configsDirectory.exists()) {
        new ConfigPuller().updateFromRemote();
      }
    }

    // Update browser capabilities and push to remote server
    if (RuntimeConfig.getConfig().getAutoUpdateBrowserVersions()) {
      System.out.println(UPDATING_BROWSER_VERSIONS);
      logger.info(UPDATING_BROWSER_VERSIONS);

      java.util.List<GridNode> nodes = RuntimeConfig.getConfig().getNodes();
      for (GridNode node : nodes) {
        if (node.isAppiumNode()) {
            continue;
        }

        String hubHost = node.getConfiguration().getHubHost();
        java.util.LinkedList<Capability> capabilities = node.getCapabilities();
        for (Capability cap : capabilities) {
          String newVersion = BrowserVersionDetector.guessBrowserVersion(cap.getBrowser());
          if (cap.getBrowserVersion() != newVersion) {
            cap.setBrowserVersion(newVersion);
          }
        }
        node.writeToFile(node.getLoadedFromFile());
        if (!RuntimeConfig.getConfig().getAutoStartHub()) {
          if (configsDirectory.exists()) {
            pushConfigFileToHub(hubHost, node.getLoadedFromFile());
          }
        }
      }
    }

    System.out.println(ATTEMPTING_TO_START_GRID_NODES);
    logger.info(ATTEMPTING_TO_START_GRID_NODES);
    return GridStarter.startAllNodes(getJsonResponse());
  }
  
  private boolean pushConfigFileToHub(String hubHost, String configFile) {
    ConfigPusher pusher = new ConfigPusher();
    pusher.setHubHost(hubHost);
    pusher.addConfigFile(configFile);

    logger.info("Sending config files to " + hubHost);

    logger.info("Open transfer");
    Map<String, Integer> results = pusher.sendAllConfigsToHub();
    logger.info("Checking status of transfered files");
    Boolean failure = false;
    for (String file : results.keySet()) {
      logger.info(file + " - " + results.get(file));
      if (!results.get(file).equals(200)) {
        failure = true;
      }
    }

    if (failure) {
      System.out.println(
          "Not all files were successfully sent to the HUB, please check log for more info");
    } else {
      System.out.println(
          "All files sent to hub, check the 'configs" + RuntimeConfig.getOS().getFileSeparator()
          + RuntimeConfig.getOS().getHostName()
          + "' directory to modify the configs for this node in the future");
    }
    return failure;
  }

  private JsonObject startHub() {
    System.out.println(ATTEMPTING_TO_START_GRID_HUBS);
    return GridStarter.startAllHubs(getJsonResponse());
  }


}
