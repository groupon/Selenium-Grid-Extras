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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DefaultConfig {

  private JSONObject defaultConfig = new JSONObject();
  private JSONArray enabledPlugins = new JSONArray();
  private JSONArray disabledPlugins = new JSONArray();
  private JSONArray setupTask = new JSONArray();
  private JSONArray teardownTask = new JSONArray();
  private JSONObject gridConfig = new JSONObject();
  private JSONObject webdriverConfig = new JSONObject();


  public DefaultConfig() {
    loadDisabledPlugins();
    loadEnabledPlugins();
    loadSetupConfig();
    loadTeardownConfig();
    loadGridConfig();
    loadSharedDir();
    loadWebDriverInfo();
  }

  public String toString() {
    return defaultConfig.toJSONString();
  }

  private void loadSetupConfig() {
    setupTask.add("com.groupon.seleniumgridextras.KillAllIE");
    setupTask.add("com.groupon.seleniumgridextras.MoveMouse");
    defaultConfig.put("setup", setupTask);
  }

  private void loadTeardownConfig() {
    teardownTask.add("com.groupon.seleniumgridextras.KillAllIE");
    defaultConfig.put("teardown", teardownTask);
  }


  private void loadWebDriverInfo() {
    webdriverConfig.put("directory", "webdriver");
    webdriverConfig.put("version", "2.33.0");
    defaultConfig.put("webdriver", webdriverConfig);
  }

  private void loadEnabledPlugins() {
    enabledPlugins.add("com.groupon.seleniumgridextras.DownloadWebdriver");
    enabledPlugins.add("com.groupon.seleniumgridextras.UpgradeWebdriver");
    enabledPlugins.add("com.groupon.seleniumgridextras.Setup");
    enabledPlugins.add("com.groupon.seleniumgridextras.Teardown");
    enabledPlugins.add("com.groupon.seleniumgridextras.MoveMouse");
    enabledPlugins.add("com.groupon.seleniumgridextras.RebootNode");
    enabledPlugins.add("com.groupon.seleniumgridextras.KillAllIE");
    enabledPlugins.add("com.groupon.seleniumgridextras.KillAllFirefox");
    enabledPlugins.add("com.groupon.seleniumgridextras.KillAllChrome");
    enabledPlugins.add("com.groupon.seleniumgridextras.GetProcesses");
    enabledPlugins.add("com.groupon.seleniumgridextras.KillPid");
    enabledPlugins.add("com.groupon.seleniumgridextras.Netstat");
    enabledPlugins.add("com.groupon.seleniumgridextras.Screenshot");
    enabledPlugins.add("com.groupon.seleniumgridextras.ExposeDirectory");
    enabledPlugins.add("com.groupon.seleniumgridextras.StartGrid");
    enabledPlugins.add("com.groupon.seleniumgridextras.GetInfoForPort");
    enabledPlugins.add("com.groupon.seleniumgridextras.GridStatus");
    enabledPlugins.add("com.groupon.seleniumgridextras.KillAllByName");
    enabledPlugins.add("com.groupon.seleniumgridextras.StopGrid");
    enabledPlugins.add("com.groupon.seleniumgridextras.GetConfig");

    defaultConfig.put("activated_modules", enabledPlugins);
  }

  private void loadDisabledPlugins() {
    disabledPlugins.add("com.groupon.GetFile");
    defaultConfig.put("disabled_modules", disabledPlugins);
  }

  private void loadGridConfig() {
    gridConfig.put("default_role", "hub");
    gridConfig.put("hub", getGridHubConfig());
    gridConfig.put("node", getGridNodeConfig());

    defaultConfig.put("grid", gridConfig);
  }

  private JSONObject getGridHubConfig() {
    JSONObject gridHubConfig = new JSONObject();
    gridHubConfig.put("-role", "hub");
    gridHubConfig.put("-servlets", "com.groupon.seleniumgridextras.SeleniumGridExtrasServlet");
    gridHubConfig.put("-port", "4444");
    return gridHubConfig;
  }

  private JSONObject getGridNodeConfig() {
    JSONObject gridNodeConfig = new JSONObject();

    gridNodeConfig.put("-host", "http://127.0.0.1");
    gridNodeConfig.put("-role", "wd");
    gridNodeConfig.put("-hub", "http://localhost:4444");
    gridNodeConfig.put("-port", "5555");
    gridNodeConfig.put("-host", "http://127.0.0.1");

    return gridNodeConfig;
  }

  private void loadSharedDir() {
    defaultConfig.put("expose_directory", "shared");
  }


}
