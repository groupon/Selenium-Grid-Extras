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

  private static JSONObject defaultConfig;
  private static JSONArray enabledPlugins;
  private static JSONArray disabledPlugins;
  private static JSONArray setupTask;
  private static JSONArray teardownTask;
  private static JSONObject gridConfig;
  private static JSONObject webdriverConfig;


  public static String toJsonString(){
    return getDefaultConfig().toJSONString();
  }

  public static JSONObject getDefaultConfig() {
    defaultConfig = new JSONObject();
    enabledPlugins = new JSONArray();
    disabledPlugins = new JSONArray();
    setupTask = new JSONArray();
    teardownTask = new JSONArray();
    gridConfig = new JSONObject();
    webdriverConfig = new JSONObject();


    loadDisabledPlugins();
    loadEnabledPlugins();
    loadSetupConfig();
    loadTeardownConfig();
    loadGridConfig();
    loadSharedDir();
    loadWebDriverInfo();
    loadConfigVersion();

    return defaultConfig;
  }

  public static void loadConfigVersion(){
    defaultConfig.put("config_version", "1.0");
  }

  private static void loadSetupConfig() {
    setupTask.add("com.groupon.seleniumgridextras.tasks.KillAllIE");
    setupTask.add("com.groupon.seleniumgridextras.tasks.MoveMouse");
    defaultConfig.put("setup", setupTask);
  }

  private static void loadTeardownConfig() {
    teardownTask.add("com.groupon.seleniumgridextras.tasks.KillAllIE");
    teardownTask.add("com.groupon.seleniumgridextras.tasks.MoveMouse");
    defaultConfig.put("teardown", teardownTask);
  }


  private static void loadWebDriverInfo() {
    webdriverConfig.put("directory", "webdriver");
    webdriverConfig.put("version", "2.33.0");
    defaultConfig.put("webdriver", webdriverConfig);
  }

  private static void loadEnabledPlugins() {
    enabledPlugins.add("com.groupon.seleniumgridextras.tasks.DownloadWebdriver");
    enabledPlugins.add("com.groupon.seleniumgridextras.tasks.UpgradeWebdriver");
    enabledPlugins.add("com.groupon.seleniumgridextras.tasks.Setup");
    enabledPlugins.add("com.groupon.seleniumgridextras.tasks.Teardown");
    enabledPlugins.add("com.groupon.seleniumgridextras.tasks.MoveMouse");
    enabledPlugins.add("com.groupon.seleniumgridextras.tasks.RebootNode");
    enabledPlugins.add("com.groupon.seleniumgridextras.tasks.KillAllIE");
    enabledPlugins.add("com.groupon.seleniumgridextras.tasks.KillAllFirefox");
    enabledPlugins.add("com.groupon.seleniumgridextras.tasks.KillAllChrome");
    enabledPlugins.add("com.groupon.seleniumgridextras.tasks.GetProcesses");
    enabledPlugins.add("com.groupon.seleniumgridextras.tasks.KillPid");
    enabledPlugins.add("com.groupon.seleniumgridextras.tasks.Netstat");
    enabledPlugins.add("com.groupon.seleniumgridextras.tasks.Screenshot");
    enabledPlugins.add("com.groupon.seleniumgridextras.tasks.ExposeDirectory");
    enabledPlugins.add("com.groupon.seleniumgridextras.tasks.StartGrid");
    enabledPlugins.add("com.groupon.seleniumgridextras.tasks.GetInfoForPort");
    enabledPlugins.add("com.groupon.seleniumgridextras.tasks.GridStatus");
    enabledPlugins.add("com.groupon.seleniumgridextras.tasks.KillAllByName");
    enabledPlugins.add("com.groupon.seleniumgridextras.tasks.StopGrid");
    enabledPlugins.add("com.groupon.seleniumgridextras.tasks.GetConfig");

    defaultConfig.put("activated_modules", enabledPlugins);
  }

  private static void loadDisabledPlugins() {
    disabledPlugins.add("com.groupon.GetFile");
    defaultConfig.put("disabled_modules", disabledPlugins);
  }

  private static void loadGridConfig() {
    gridConfig.put("default_role", "hub");
    gridConfig.put("auto_start_hub", "0");
    gridConfig.put("auto_start_node", "1");
    gridConfig.put("hub", getGridHubConfig());
    gridConfig.put("node", getGridNodeConfig());

    defaultConfig.put("grid", gridConfig);
  }

  private static JSONObject getGridHubConfig() {
    JSONObject gridHubConfig = new JSONObject();
    gridHubConfig.put("-role", "hub");
    gridHubConfig.put("-servlets", "com.groupon.seleniumgridextras.grid.servlets.SeleniumGridExtrasServlet");
    gridHubConfig.put("-port", "4444");

    String hostIp = RuntimeConfig.getCurrentHostIP();
    if (!hostIp.equals("")){
      gridHubConfig.put("-host", hostIp);
    }


    return gridHubConfig;
  }

  private static JSONObject getGridNodeConfig() {
    JSONObject gridNodeConfig = new JSONObject();

    gridNodeConfig.put("-role", "wd");
    gridNodeConfig.put("-hub", "http://localhost:4444");
    gridNodeConfig.put("-port", "4445");
    gridNodeConfig.put("-nodeTimeout", "240");
    gridNodeConfig.put("-maxSession", "1");

    String hostIp = RuntimeConfig.getCurrentHostIP();
    if (!hostIp.equals("")){
      gridNodeConfig.put("-host", hostIp);
    }

    gridNodeConfig.put("-proxy", "com.groupon.seleniumgridextras.grid.proxies.SetupTeardownProxy");

    return gridNodeConfig;
  }

  private static void loadSharedDir() {
    defaultConfig.put("expose_directory", "shared");
  }


}
