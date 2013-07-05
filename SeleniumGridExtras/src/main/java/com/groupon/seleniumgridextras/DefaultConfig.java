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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class DefaultConfig {

  private static JsonObject defaultConfig;
  private static JsonArray enabledPlugins;
  private static JsonArray disabledPlugins;
  private static JsonArray setupTask;
  private static JsonArray teardownTask;
  private static JsonObject gridConfig;
  private static JsonObject webdriverConfig;


  public static String toJsonString(){
    return getDefaultConfig().toString();
  }

  public static JsonObject getDefaultConfig() {
    defaultConfig = new JsonObject();
    enabledPlugins = new JsonArray();
    disabledPlugins = new JsonArray();
    setupTask = new JsonArray();
    teardownTask = new JsonArray();
    gridConfig = new JsonObject();
    webdriverConfig = new JsonObject();


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
    defaultConfig.addProperty("config_version", "1.0");
  }

  private static void loadSetupConfig() {
    setupTask.add(new JsonPrimitive("com.groupon.seleniumgridextras.tasks.KillAllIE"));
    setupTask.add(new JsonPrimitive("com.groupon.seleniumgridextras.tasks.MoveMouse"));
    defaultConfig.add("setup", setupTask);
  }

  private static void loadTeardownConfig() {
    teardownTask.add(new JsonPrimitive("com.groupon.seleniumgridextras.tasks.KillAllIE"));
    teardownTask.add(new JsonPrimitive("com.groupon.seleniumgridextras.tasks.MoveMouse"));
    defaultConfig.add("teardown", teardownTask);
  }


  private static void loadWebDriverInfo() {
    webdriverConfig.add("directory", new JsonPrimitive("webdriver"));
    webdriverConfig.addProperty("version", "2.33.0");
    defaultConfig.add("webdriver", webdriverConfig);
  }

  private static void loadEnabledPlugins() {
    enabledPlugins.add(new JsonPrimitive("com.groupon.seleniumgridextras.tasks.DownloadWebdriver"));
    enabledPlugins.add(new JsonPrimitive("com.groupon.seleniumgridextras.tasks.UpgradeWebdriver"));
    enabledPlugins.add(new JsonPrimitive("com.groupon.seleniumgridextras.tasks.Setup"));
    enabledPlugins.add(new JsonPrimitive("com.groupon.seleniumgridextras.tasks.Teardown"));
    enabledPlugins.add(new JsonPrimitive("com.groupon.seleniumgridextras.tasks.MoveMouse"));
    enabledPlugins.add(new JsonPrimitive("com.groupon.seleniumgridextras.tasks.RebootNode"));
    enabledPlugins.add(new JsonPrimitive("com.groupon.seleniumgridextras.tasks.KillAllIE"));
    enabledPlugins.add(new JsonPrimitive("com.groupon.seleniumgridextras.tasks.KillAllFirefox"));
    enabledPlugins.add(new JsonPrimitive("com.groupon.seleniumgridextras.tasks.KillAllChrome"));
    enabledPlugins.add(new JsonPrimitive("com.groupon.seleniumgridextras.tasks.GetProcesses"));
    enabledPlugins.add(new JsonPrimitive("com.groupon.seleniumgridextras.tasks.KillPid"));
    enabledPlugins.add(new JsonPrimitive("com.groupon.seleniumgridextras.tasks.Netstat"));
    enabledPlugins.add(new JsonPrimitive("com.groupon.seleniumgridextras.tasks.Screenshot"));
    enabledPlugins.add(new JsonPrimitive("com.groupon.seleniumgridextras.tasks.ExposeDirectory"));
    enabledPlugins.add(new JsonPrimitive("com.groupon.seleniumgridextras.tasks.StartGrid"));
    enabledPlugins.add(new JsonPrimitive("com.groupon.seleniumgridextras.tasks.GetInfoForPort"));
    enabledPlugins.add(new JsonPrimitive("com.groupon.seleniumgridextras.tasks.GridStatus"));
    enabledPlugins.add(new JsonPrimitive("com.groupon.seleniumgridextras.tasks.KillAllByName"));
    enabledPlugins.add(new JsonPrimitive("com.groupon.seleniumgridextras.tasks.StopGrid"));
    enabledPlugins.add(new JsonPrimitive("com.groupon.seleniumgridextras.tasks.GetConfig"));

    defaultConfig.add("activated_modules", enabledPlugins);
  }

  private static void loadDisabledPlugins() {
    disabledPlugins.add(new JsonPrimitive("com.groupon.GetFile"));
    defaultConfig.add("disabled_modules", disabledPlugins);
  }

  private static void loadGridConfig() {
    gridConfig.addProperty("default_role", "hub");
    gridConfig.addProperty("auto_start_hub", "0");
    gridConfig.addProperty("auto_start_node", "1");
    gridConfig.add("hub", getGridHubConfig());
    gridConfig.add("node", getGridNodeConfig());

    defaultConfig.add("grid", gridConfig);
  }

  private static JsonObject getGridHubConfig() {
    JsonObject gridHubConfig = new JsonObject();
    gridHubConfig.addProperty("-role", "hub");
    gridHubConfig.addProperty("-servlets", "com.groupon.seleniumgridextras.grid.servlets.SeleniumGridExtrasServlet");
    gridHubConfig.addProperty("-port", "4444");

    String hostIp = RuntimeConfig.getCurrentHostIP();
    if (!hostIp.equals("")){
      gridHubConfig.addProperty("-host", hostIp);
    }
    return gridHubConfig;
  }

  private static JsonObject getGridNodeConfig() {
    JsonObject gridNodeConfig = new JsonObject();

    gridNodeConfig.addProperty("-role", "wd");
    gridNodeConfig.addProperty("-hub", "http://localhost:4444");
    gridNodeConfig.addProperty("-port", "4445");
    gridNodeConfig.addProperty("-nodeTimeout", "240");
    gridNodeConfig.addProperty("-maxSession", "1");

    String hostIp = RuntimeConfig.getCurrentHostIP();
    if (!hostIp.equals("")){
      gridNodeConfig.addProperty("-host", hostIp);
    }

    gridNodeConfig.addProperty("-proxy", "com.groupon.seleniumgridextras.grid.proxies.SetupTeardownProxy");

    return gridNodeConfig;
  }

  private static void loadSharedDir() {
    defaultConfig.addProperty("expose_directory", "shared");
  }


}
