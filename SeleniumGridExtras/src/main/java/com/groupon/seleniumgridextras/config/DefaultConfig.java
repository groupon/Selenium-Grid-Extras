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

import com.groupon.seleniumgridextras.OSChecker;

public class DefaultConfig {

  private static Config config;

  public static Config getDefaultConfig() {
    config = new Config();

    loadWebDriverInfo();
    loadIEDriverInfo();
    loadDisabledPlugins();
    loadEnabledPlugins();
    loadSetupConfig();
    loadTeardownConfig();
    loadGridConfig();
    loadSharedDir();
    loadConfigVersion();

    return config;
  }

  private static void loadConfigVersion() {
    config.setConfigVersion("1.0");
  }

  private static void loadSetupConfig() {
    config.addSetupTask("com.groupon.seleniumgridextras.tasks.MoveMouse");
  }

  private static void loadTeardownConfig() {
    config.addTeardownTask("com.groupon.seleniumgridextras.tasks.MoveMouse");
  }


  private static void loadWebDriverInfo() {
    String tmpDir;

    if (OSChecker.isWindows()) {
      tmpDir = "\\";
    } else {
      tmpDir = "/tmp/";
    }

    config.getWebdriver().setDirectory(tmpDir + "webdriver");
    config.getWebdriver().setVersion("2.33.0");
  }

  private static void loadIEDriverInfo() {
    String tmpDir;

    if (OSChecker.isWindows()) {
      tmpDir = "\\webdriver\\";
    } else {
      tmpDir = "/tmp/webdriver/";
    }

    config.getIEdriver().setDirectory(tmpDir + "iedriver");
    config.getIEdriver().setVersion("2.33.0");
    config.getIEdriver().setBit("Win32");
  }


  private static void loadEnabledPlugins() {
    config.addEnabledModule("com.groupon.seleniumgridextras.tasks.DownloadWebdriver");
    config.addEnabledModule("com.groupon.seleniumgridextras.tasks.UpgradeWebdriver");
    config.addEnabledModule("com.groupon.seleniumgridextras.tasks.Setup");
    config.addEnabledModule("com.groupon.seleniumgridextras.tasks.Teardown");
    config.addEnabledModule("com.groupon.seleniumgridextras.tasks.MoveMouse");
    config.addEnabledModule("com.groupon.seleniumgridextras.tasks.RebootNode");
    config.addEnabledModule("com.groupon.seleniumgridextras.tasks.KillAllIE");
    config.addEnabledModule("com.groupon.seleniumgridextras.tasks.KillAllFirefox");
    config.addEnabledModule("com.groupon.seleniumgridextras.tasks.KillAllChrome");
    config.addEnabledModule("com.groupon.seleniumgridextras.tasks.GetProcesses");
    config.addEnabledModule("com.groupon.seleniumgridextras.tasks.KillPid");
    config.addEnabledModule("com.groupon.seleniumgridextras.tasks.Netstat");
    config.addEnabledModule("com.groupon.seleniumgridextras.tasks.Screenshot");
    config.addEnabledModule("com.groupon.seleniumgridextras.tasks.ExposeDirectory");
    config.addEnabledModule("com.groupon.seleniumgridextras.tasks.StartGrid");
    config.addEnabledModule("com.groupon.seleniumgridextras.tasks.GetInfoForPort");
    config.addEnabledModule("com.groupon.seleniumgridextras.tasks.GridStatus");
    config.addEnabledModule("com.groupon.seleniumgridextras.tasks.KillAllByName");
    config.addEnabledModule("com.groupon.seleniumgridextras.tasks.StopGrid");
    config.addEnabledModule("com.groupon.seleniumgridextras.tasks.GetConfig");
    config.addEnabledModule("com.groupon.seleniumgridextras.tasks.StopGridExtras");
    config.addEnabledModule("com.groupon.seleniumgridextras.tasks.DownloadIEDriver");
    config.addEnabledModule("com.groupon.seleniumgridextras.tasks.IEProtectedMode");
  }

  private static void loadDisabledPlugins() {
    config.addDisabledModule("com.groupon.GetFile");
  }

  private static void loadGridConfig() {
    config.getGrid().setDefaultRole("hub");
    config.getGrid().setAutoStartHub(0);
    config.getGrid().setAutoStartNode(1);

    setGridHubConfig();
    setGridNodeConfig();

  }

  private static void setGridHubConfig() {
    config.getGrid().getHub().setRole("hub");
    config.getGrid().getHub().setPort("4444");
    config.getGrid().getHub()
        .setServlets("com.groupon.seleniumgridextras.grid.servlets.SeleniumGridExtrasServlet,com.groupon.seleniumgridextras.grid.servlets.ProxyStatusJsonServlet");

    String hostIp = RuntimeConfig.getCurrentHostIP();
    if (!hostIp.equals("")) {
      config.getGrid().getHub().setHost(hostIp);
    }
  }

  private static void setGridNodeConfig() {
    config.getGrid().getNode().setRole("wd");
    config.getGrid().getNode().setPort("4445");
    config.getGrid().getNode().setHub("http://localhost:4444");
    config.getGrid().getNode().setNodeTimeout("240");
    config.getGrid().getNode().setMaxSession(1);

    String hostIp = RuntimeConfig.getCurrentHostIP();
    if (!hostIp.equals("")) {
      config.getGrid().getNode().setHost(hostIp);
    }
    config.getGrid().getNode()
        .setProxy("com.groupon.seleniumgridextras.grid.proxies.SetupTeardownProxy");

    if (OSChecker.isWindows()) {
      config.getGrid().getNode().setIeDriver(config.getIEdriver().getExecutablePath());
    }


  }

  private static void loadSharedDir() {
    config.setSharedDir("shared");
  }


}
