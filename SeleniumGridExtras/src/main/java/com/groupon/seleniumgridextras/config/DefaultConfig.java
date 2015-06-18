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


import com.groupon.seleniumgridextras.loggers.SessionHistoryLog;
import com.groupon.seleniumgridextras.tasks.*;
import com.groupon.seleniumgridextras.utilities.ValueConverter;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;

import java.io.File;

public class DefaultConfig {

    public static final String VIDEO_OUTPUT_DIRECTORY = "video_output";
    public static final String REBOOT_AFTER_THIS_MANY_SESSIONS = "10";
    public static final String DEFAULT_HUB_PORT = "4444";
    public static final String DEFAULT_SHARED_DIRECTORY = "shared";

    public static final String LOG_MAXIMUM_SIZE = "20000000";
    public static final long LOG_MAX_AGE = ValueConverter.daysToMilliseconds(10);

    public static final String BOOTSTRAP_CSS = "/bootstrap.3.2.0.min.css";
    public static final String BOOTSTRAP_JS = "/bootstrap.3.2.0.min.js";
    public static final String JQUERY_JS = "/jquery.1.11.1.min.js";
    public static final String BOOTSTRAP_TEMPLATE = "/jumbotron-narrow.css";

    public static final
    String
            JQUERY_URL =
            "https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js";
    public static final
    String
            BOOTSTRAP_CSS_URL =
            "https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css";
    public static final
    String
            BOOTSTRAP_TEMPLATE_URL =
            "http://getbootstrap.com/examples/jumbotron-narrow/jumbotron-narrow.css";
    public static final
    String
            BOOTSTRAP_URL =
            "https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js";


    public static final String HEAD_PARTIAL_HTML = "/header_partial.html";
    public static final String TOP_BAR_PARTIAL_HTML = "/nav_bar_partial.html";
    public static final String FOOTER_PARTIAL_HTML = "/footer_partial.html";
    public static final int HTTP_REQUEST_TIMEOUT = 60000;
    public static final int CONFIG_PULLER_HTTP_TIMEOUT = 5000;
    public static final File SESSION_LOG_DIRECTORY = new File("log", "session_logs");
    public static final int VIDEOS_TO_KEEP = 40;
    public static final String DEFAULT_GRID_EXTRAS_RELEASE_URL = "https://api.github.com/repos/groupon/Selenium-Grid-Extras/releases";
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
        setAutoUpdateDrivers(JsonCodec.TRUE_INT);
        setRebootAfterSessionCount(REBOOT_AFTER_THIS_MANY_SESSIONS);
        loadDefaultVideoRecordingOptions();
        loadHTTPOptions();
        loadHtmlRenderOptions();
        loadGridExtrasReleaseUrl();

        loadLogConfig();

        loadSessionLogDir();

        return config;
    }

    public static void loadGridExtrasReleaseUrl() {
        config.setGridExtrasReleaseUrl(DEFAULT_GRID_EXTRAS_RELEASE_URL);
        config.setGridExtrasAutoUpdate(false);
    }

    public static void loadSessionLogDir() {
        SessionHistoryLog.setOutputDir(SESSION_LOG_DIRECTORY);
    }

    public static void loadHtmlRenderOptions() {
        //Set the locally packed source files first
        config.getHtmlRender().setMainCss(BOOTSTRAP_CSS);

        config.getHtmlRender().setMainJs(BOOTSTRAP_JS);

        config.getHtmlRender().setJquery(JQUERY_JS);

        config.getHtmlRender().setTemplateJs(BOOTSTRAP_TEMPLATE);

        //Set fallback sources
        config.getHtmlRender()
                .setJqueryFallBack(JQUERY_URL);

        config.getHtmlRender().setFallBackCss(
                BOOTSTRAP_CSS_URL);

        config.getHtmlRender().setTemplateJsFallback(
                BOOTSTRAP_TEMPLATE_URL);

        config.getHtmlRender()
                .setMainJsFallBack(BOOTSTRAP_URL);

        //Local sources for grid extras
        //Set the locally packed source files first
        config.getHtmlRender().setHtmlHeadFile(HEAD_PARTIAL_HTML);

        config.getHtmlRender().setHtmlNavBar(TOP_BAR_PARTIAL_HTML);

        config.getHtmlRender().setHtmlFooter(FOOTER_PARTIAL_HTML);
    }

    public static void loadHTTPOptions() {
        config.setHttpRequestTimeout(HTTP_REQUEST_TIMEOUT);
        config.setConfigPullerHttpTimeout(CONFIG_PULLER_HTTP_TIMEOUT);
    }


    private static void loadDefaultVideoRecordingOptions() {
        config.getVideoRecording().setRecordTestVideos(true);
        config.getVideoRecording().setFrameRate(5, 1);
        config.getVideoRecording().setOutputDimensions(1024, 768);
        config.getVideoRecording().setVideosToKeep(VIDEOS_TO_KEEP);
        config.getVideoRecording().setOutputDir(VIDEO_OUTPUT_DIRECTORY);
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
    }

    private static void loadTeardownConfig() {
        config.addTeardownTask(MoveMouse.class.getCanonicalName());
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
        config.getIEdriver().setBit(JsonCodec.WebDriver.Downloader.WIN32);
    }

    private static void loadChromeDriverInfo() {
        String tmpDir;

        tmpDir = config.getWebdriver().getDirectory() + RuntimeConfig.getOS().getFileSeparator();

        config.getChromeDriver().setDirectory(tmpDir + "chromedriver");
        config.getChromeDriver().setVersion(getChromeDriverDefaultVersion());
        config.getChromeDriver().setBit(JsonCodec.WebDriver.Downloader.BIT_32);
    }

    private static void loadLogConfig() {
        config.setLogMaximumSize(LOG_MAXIMUM_SIZE);
        config.setLogMaximumAge(LOG_MAX_AGE);
    }

    private static void loadEnabledPlugins() {

        config.addActivatedModules(DeleteOldLogsTask.class.getCanonicalName());
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
        config.addActivatedModules(IEMixedContent.class.getCanonicalName());
        config.addActivatedModules(SystemInfo.class.getCanonicalName());
        config.addActivatedModules(GetNodeConfig.class.getCanonicalName());
        config.addActivatedModules(UpdateNodeConfig.class.getCanonicalName());

        config.addActivatedModules(AutoUpgradeDrivers.class.getCanonicalName());
        config.addActivatedModules(DownloadWebdriver.class.getCanonicalName());
        config.addActivatedModules(DownloadIEDriver.class.getCanonicalName());
        config.addActivatedModules(DownloadChromeDriver.class.getCanonicalName());
        config.addActivatedModules(SessionHistory.class.getCanonicalName());

        config.addActivatedModules(UpgradeGridExtrasTask.class.getCanonicalName());
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
        config.getHub().setPort(DEFAULT_HUB_PORT);
        config.getHub()
                .setServlets("com.groupon.seleniumgridextras.grid.servlets.ProxyStatusJsonServlet");

        String hostIp = RuntimeConfig.getHostIp();
        if (hostIp != null) {
            config.getHub().setHost(hostIp);
        }
    }

    private static void loadSharedDir() {
        config.setSharedDir(DEFAULT_SHARED_DIRECTORY);
    }

    public static void setRebootAfterSessionCount(String sessionCount) {
        config.setRebootAfterSessions(sessionCount);
    }


}
