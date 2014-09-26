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


import com.groupon.seleniumgridextras.grid.servlets.ProxyStatusJsonServlet;
import com.groupon.seleniumgridextras.grid.servlets.SeleniumGridExtrasServlet;
import com.groupon.seleniumgridextras.tasks.AutoUpgradeDrivers;
import com.groupon.seleniumgridextras.tasks.DownloadChromeDriver;
import com.groupon.seleniumgridextras.tasks.DownloadIEDriver;
import com.groupon.seleniumgridextras.tasks.DownloadWebdriver;
import com.groupon.seleniumgridextras.tasks.ExposeDirectory;
import com.groupon.seleniumgridextras.tasks.GetConfig;
import com.groupon.seleniumgridextras.tasks.GetFile;
import com.groupon.seleniumgridextras.tasks.GetInfoForPort;
import com.groupon.seleniumgridextras.tasks.GetNodeConfig;
import com.groupon.seleniumgridextras.tasks.GetProcesses;
import com.groupon.seleniumgridextras.tasks.GridStatus;
import com.groupon.seleniumgridextras.tasks.IEProtectedMode;
import com.groupon.seleniumgridextras.tasks.KillAllByName;
import com.groupon.seleniumgridextras.tasks.KillAllChrome;
import com.groupon.seleniumgridextras.tasks.KillAllFirefox;
import com.groupon.seleniumgridextras.tasks.KillAllIE;
import com.groupon.seleniumgridextras.tasks.KillAllSafari;
import com.groupon.seleniumgridextras.tasks.KillPid;
import com.groupon.seleniumgridextras.tasks.MoveMouse;
import com.groupon.seleniumgridextras.tasks.Netstat;
import com.groupon.seleniumgridextras.tasks.RebootNode;
import com.groupon.seleniumgridextras.tasks.Screenshot;
import com.groupon.seleniumgridextras.tasks.SessionCounterStartSession;
import com.groupon.seleniumgridextras.tasks.SessionCounterStopSession;
import com.groupon.seleniumgridextras.tasks.Setup;
import com.groupon.seleniumgridextras.tasks.StartGrid;
import com.groupon.seleniumgridextras.tasks.StopGrid;
import com.groupon.seleniumgridextras.tasks.StopGridExtras;
import com.groupon.seleniumgridextras.tasks.SystemInfo;
import com.groupon.seleniumgridextras.tasks.Teardown;
import com.groupon.seleniumgridextras.tasks.UpdateNodeConfig;
import com.groupon.seleniumgridextras.tasks.VideoRecorder;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;

public class DefaultConfig {

  private static Config config;
  private static final String webDriverDefaultVersion = "2.41.0";
  private static final String ieDriverDefaultVersion = "2.41.0";
  private static final String chromeDriverDefaultVersion = "2.10";

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
    setRebootAfterSessionCount("10");
    loadDefaultVideoRecordingOptions();

    return config;
  }

  private static void loadDefaultVideoRecordingOptions() {
    config.getVideoRecording().setRecordTestVideos(true);
    config.getVideoRecording().setFrameRate(5, 1);
    config.getVideoRecording().setOutputDimensions(1024, 768);
    config.getVideoRecording().setVideosToKeep(10);
    config.getVideoRecording().setOutputDir("video_output");
    config.getVideoRecording().setIdleTimeout(120);

    config.getVideoRecording().setTitleFrameFontColor(129, 182, 64, 128);
    config.getVideoRecording().setLowerThirdBackgroundColor(0, 0, 0, 200);
    config.getVideoRecording().setLowerThirdFontColor(255, 255, 255, 255);

  }

  public static boolean getAutoUpdateDrivers() {
    return config.getAutoUpdateDrivers();
  }

  public static void setAutoUpdateDrivers(String update) {
    config.setAutoUpdateDrivers(update);
  }

  public static String getWebDriverDefaultVersion() {
    return webDriverDefaultVersion;
  }

  public static String getIeDriverDefaultVersion() {
    return ieDriverDefaultVersion;
  }

  public static String getChromeDriverDefaultVersion() {
    return chromeDriverDefaultVersion;
  }


  private static void loadSetupConfig() {

    config.addSetupTask(MoveMouse.class.getCanonicalName());
    config.addSetupTask(SessionCounterStartSession.class.getCanonicalName());
  }

  private static void loadTeardownConfig() {
    config.addTeardownTask(MoveMouse.class.getCanonicalName());
    config.addTeardownTask(SessionCounterStopSession.class.getCanonicalName());
  }


  private static void loadWebDriverInfo() {
    String tmpDir = RuntimeConfig.getOS().getFileSeparator();

    tmpDir = tmpDir + "tmp" + RuntimeConfig.getOS().getFileSeparator();

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

    config.addActivatedModules(Setup.class.getCanonicalName());
    config.addActivatedModules(Teardown.class.getCanonicalName());
    config.addActivatedModules(MoveMouse.class.getCanonicalName());
    config.addActivatedModules(RebootNode.class.getCanonicalName());

    config.addActivatedModules(VideoRecorder.class.getCanonicalName());

    config.addActivatedModules(KillAllIE.class.getCanonicalName());
    config.addActivatedModules(KillAllFirefox.class.getCanonicalName());
    config.addActivatedModules(KillAllChrome.class.getCanonicalName());
    config.addActivatedModules(KillAllSafari.class.getCanonicalName());

    config.addActivatedModules(GetProcesses.class.getCanonicalName());
    config.addActivatedModules(KillPid.class.getCanonicalName());
    config.addActivatedModules(Netstat.class.getCanonicalName());
    config.addActivatedModules(Screenshot.class.getCanonicalName());
    config.addActivatedModules(ExposeDirectory.class.getCanonicalName());
    config.addActivatedModules(StartGrid.class.getCanonicalName());
    config.addActivatedModules(GetInfoForPort.class.getCanonicalName());
    config.addActivatedModules(GridStatus.class.getCanonicalName());
    config.addActivatedModules(KillAllByName.class.getCanonicalName());
    config.addActivatedModules(StopGrid.class.getCanonicalName());
    config.addActivatedModules(GetConfig.class.getCanonicalName());
    config.addActivatedModules(StopGridExtras.class.getCanonicalName());
    config.addActivatedModules(IEProtectedMode.class.getCanonicalName());
    config.addActivatedModules(SystemInfo.class.getCanonicalName());
    config.addActivatedModules(GetNodeConfig.class.getCanonicalName());
    config.addActivatedModules(UpdateNodeConfig.class.getCanonicalName());

    config.addActivatedModules(AutoUpgradeDrivers.class.getCanonicalName());
    config.addActivatedModules(DownloadWebdriver.class.getCanonicalName());
    config.addActivatedModules(DownloadIEDriver.class.getCanonicalName());
    config.addActivatedModules(DownloadChromeDriver.class.getCanonicalName());
  }

  private static void loadDisabledPlugins() {
    config.addDisabledModule(GetFile.class.getCanonicalName());
  }

  private static void loadGridConfig() {
    config.setDefaultRole(JsonCodec.WebDriver.Grid.HUB);
    config.setAutoStartHub(JsonCodec.FALSE_INT);
    config.setAutoStartNode(JsonCodec.TRUE_INT);

    setGridHubConfig();

  }

  private static void setGridHubConfig() {
    config.getHub().setRole(JsonCodec.WebDriver.Grid.HUB);
    config.getHub().setPort("4444");
    config.getHub().setServlets(
        SeleniumGridExtrasServlet.class.getCanonicalName() + "," + ProxyStatusJsonServlet.class
            .getCanonicalName());

    String hostIp = RuntimeConfig.getOS().getHostIp();
    if (hostIp != null) {
      config.getHub().setHost(hostIp);
    }
  }

  private static void loadSharedDir() {
    config.setSharedDir("shared");
  }

  public static void setRebootAfterSessionCount(String sessionCount) {
    config.setRebootAfterSessions(sessionCount);
  }


}
