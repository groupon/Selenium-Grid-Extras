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
package com.groupon.seleniumgridextras.config;


public class DefaultConfig {

  private static Config config;
  private static final String webDriverDefaultVersion = "2.40.0";
  private static final String ieDriverDefaultVersion = "2.40.0";
  private static final String chromeDriverDefaultVersion = "2.9";

  public static Config getDefaultConfig() {
    config = new Config();

    loadWebDriverInfo();
    loadIEDriverInfo();
    loadChromeDriverInfo();
    loadDisabledPlugins();
    loadEnabledPlugins();
    loadSetupConfig();
    loadTeardownConfig();
    loadGridConfig();
    loadSharedDir();
    setAutoUpdateDrivers("1");

    return config;
  }

  public static boolean getAutoUpdateDrivers(){
    return config.getAutoUpdateDrivers();
  }

  public static void setAutoUpdateDrivers(String update){
    config.setAutoUpdateDrivers(update);
  }

  public static String getWebDriverDefaultVersion() {
    return webDriverDefaultVersion;
  }

  public static String getIeDriverDefaultVersion() {
    return ieDriverDefaultVersion;
  }

  public static String getChromeDriverDefaultVersion(){
    return chromeDriverDefaultVersion;
  }


  private static void loadSetupConfig() {
    config.addSetupTask("com.groupon.seleniumgridextras.tasks.MoveMouse");
  }

  private static void loadTeardownConfig() {
    config.addTeardownTask("com.groupon.seleniumgridextras.tasks.MoveMouse");
  }


  private static void loadWebDriverInfo() {
    String tmpDir = RuntimeConfig.getOS().getFileSeparator();

    if (!RuntimeConfig.getOS().isWindows()) {
      tmpDir = tmpDir + "tmp" + RuntimeConfig.getOS().getFileSeparator();
    }

    config.getWebdriver().setDirectory(tmpDir + "webdriver");
    config.getWebdriver().setVersion(DefaultConfig.getWebDriverDefaultVersion());

  }

  private static void loadIEDriverInfo() {
    String tmpDir;

    tmpDir = config.getWebdriver().getDirectory() + RuntimeConfig.getOS().getFileSeparator();

    config.getIEdriver().setDirectory(tmpDir + "iedriver");
    config.getIEdriver().setVersion(getIeDriverDefaultVersion());
    config.getIEdriver().setBit("Win32");
  }

  private static void loadChromeDriverInfo() {
    String tmpDir;

    tmpDir = config.getWebdriver().getDirectory() + RuntimeConfig.getOS().getFileSeparator();

    config.getChromeDriver().setDirectory(tmpDir + "chromedriver");
    config.getChromeDriver().setVersion(getChromeDriverDefaultVersion());
    config.getChromeDriver().setBit("32");
  }


  private static void loadEnabledPlugins() {
    config.addActivatedModules("com.groupon.seleniumgridextras.tasks.DownloadWebdriver");
    config.addActivatedModules("com.groupon.seleniumgridextras.tasks.UpgradeWebdriver");
    config.addActivatedModules("com.groupon.seleniumgridextras.tasks.Setup");
    config.addActivatedModules("com.groupon.seleniumgridextras.tasks.Teardown");
    config.addActivatedModules("com.groupon.seleniumgridextras.tasks.MoveMouse");
    config.addActivatedModules("com.groupon.seleniumgridextras.tasks.RebootNode");
    config.addActivatedModules("com.groupon.seleniumgridextras.tasks.KillAllIE");
    config.addActivatedModules("com.groupon.seleniumgridextras.tasks.KillAllFirefox");
    config.addActivatedModules("com.groupon.seleniumgridextras.tasks.KillAllChrome");
    config.addActivatedModules("com.groupon.seleniumgridextras.tasks.GetProcesses");
    config.addActivatedModules("com.groupon.seleniumgridextras.tasks.KillPid");
    config.addActivatedModules("com.groupon.seleniumgridextras.tasks.Netstat");
    config.addActivatedModules("com.groupon.seleniumgridextras.tasks.Screenshot");
    config.addActivatedModules("com.groupon.seleniumgridextras.tasks.ExposeDirectory");
    config.addActivatedModules("com.groupon.seleniumgridextras.tasks.StartGrid");
    config.addActivatedModules("com.groupon.seleniumgridextras.tasks.GetInfoForPort");
//    config.addActivatedModules("com.groupon.seleniumgridextras.tasks.GridStatus");
    config.addActivatedModules("com.groupon.seleniumgridextras.tasks.KillAllByName");
    config.addActivatedModules("com.groupon.seleniumgridextras.tasks.StopGrid");
    config.addActivatedModules("com.groupon.seleniumgridextras.tasks.GetConfig");
    config.addActivatedModules("com.groupon.seleniumgridextras.tasks.StopGridExtras");
    config.addActivatedModules("com.groupon.seleniumgridextras.tasks.DownloadIEDriver");
    config.addActivatedModules("com.groupon.seleniumgridextras.tasks.DownloadChromeDriver");
    config.addActivatedModules("com.groupon.seleniumgridextras.tasks.IEProtectedMode");
    config.addActivatedModules("com.groupon.seleniumgridextras.tasks.SystemInfo");
  }

  private static void loadDisabledPlugins() {
    config.addDisabledModule("com.groupon.GetFile");
  }

  private static void loadGridConfig() {
    config.setDefaultRole("hub");
    config.setAutoStartHub("0");
    config.setAutoStartNode("1");

    setGridHubConfig();

  }

  private static void setGridHubConfig() {
    config.getHub().setRole("hub");
    config.getHub().setPort("4444");
    config.getHub()
        .setServlets(
            "com.groupon.seleniumgridextras.grid.servlets.SeleniumGridExtrasServlet,com.groupon.seleniumgridextras.grid.servlets.ProxyStatusJsonServlet");

    String hostIp = RuntimeConfig.getOS().getHostIp();
    if (hostIp != null) {
      config.getHub().setHost(hostIp);
    }
  }

  private static void loadSharedDir() {
    config.setSharedDir("shared");
  }


}
