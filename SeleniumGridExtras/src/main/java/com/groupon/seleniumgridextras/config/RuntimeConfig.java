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

import com.groupon.seleniumgridextras.OS;
import com.groupon.seleniumgridextras.SeleniumGridExtras;
import com.groupon.seleniumgridextras.config.remote.ConfigPuller;
import com.groupon.seleniumgridextras.downloader.webdriverreleasemanager.WebDriverReleaseManager;
import com.groupon.seleniumgridextras.grid.SessionTracker;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;


public class RuntimeConfig {

  public static String configFile = "selenium_grid_extras_config.json";
  private static Config config = null;
  private static OS currentOS = new OS();
  private static Logger logger = Logger.getLogger(RuntimeConfig.class);
  private static WebDriverReleaseManager releaseManager;
  private static SessionTracker sessionTracker;

  public static int getGridExtrasPort() {
    if(getConfig() == null) {
      return 3000;
    } else {
      return getConfig().getGridExtrasPort();
    }
  }

  public static WebDriverReleaseManager getReleaseManager() {
    if (releaseManager == null) {
      releaseManager =
          loadWebDriverReleaseManager("https://selenium-release.storage.googleapis.com/",
                                      "https://chromedriver.storage.googleapis.com/LATEST_RELEASE",
                                      "https://api.github.com/repos/mozilla/geckodriver/releases");
    }

    return releaseManager;
  }


  private static WebDriverReleaseManager loadWebDriverReleaseManager(String webDriverAndIEDriverURL,
                                                                     String chromeDriverUrl,
                                                                     String geckoDriverUrl) {
    try {
      return new WebDriverReleaseManager(new URL(webDriverAndIEDriverURL),
                                         new URL(chromeDriverUrl),
                                         new URL(geckoDriverUrl));
    } catch (MalformedURLException e) {
      logger.error("Seems that " + webDriverAndIEDriverURL + " is malformed");
      logger.error(e.toString());
      e.printStackTrace();
    } catch (DocumentException e) {
      logger.error("Something went wrong loading webdriver versions");
      logger.error(e.toString());
      e.printStackTrace();
    }

    return null;
  }

  public RuntimeConfig() {
    config = new Config();
  }


  protected static void clearConfig() {
    //Use only for tests, don't use for any other reason
    config = null;
  }

  public static String getConfigFile() {
    return configFile;
  }

  public static void setConfigFile(String file) {
    configFile = file;
  }

  public static Config load(boolean UpdateConfigsFromHub) {
    Map overwriteValues;
    config = DefaultConfig.getDefaultConfig();
    logger.debug(config);

    if (UpdateConfigsFromHub) {
      new ConfigPuller().updateFromRemote();
    }

    ConfigFileReader configFileObject = new ConfigFileReader(configFile);

    logger.debug(configFileObject.toHashMap());

    if (!configFileObject.hasContent()) {
      logger.info("Previous config was not found, will ask input from user");
      Config userInput = Config.initilizedFromUserInput();
      logger.debug(userInput);
    }

    // Read the primary config file
    configFileObject.readConfigFile();
    overwriteValues = configFileObject.toHashMap();
    logger.debug(overwriteValues);

    //Overwrite default configs
    config.overwriteConfig(overwriteValues);

    //Load node info from the node classes
    config.loadNodeClasses();
    config.loadHubClasses(); // TODO added for Hub

    //Write out all of the possible examples into an example file
    config.writeToDisk(RuntimeConfig.getConfigFile() + ".example");

    logger.debug(config);
    return config;
  }


  public static Config load() {
    return load(false);
  }

  public static File getSeleniumGridExtrasJarFile() {
    try {
      return new File(
          SeleniumGridExtras.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    } catch (URISyntaxException e) {
      logger.error("Could not get jar file");
      logger.error(e);
      throw new RuntimeException(e);
    }
  }


  public static String getSeleniungGridExtrasHomePath() {
    return FilenameUtils
        .getFullPathNoEndSeparator(getSeleniumGridExtrasJarFile().getAbsolutePath());
  }


  public static Config getConfig() {
    return config;
  }

  public static OS getOS() {
    return currentOS;
  }

  public static String getHostIp() {
    String ip = null;
    if (config != null) {
        if (config.getHostIp() != null) {
            ip = config.getHostIp();
        } else if (config.getDefaultRole().equals("hub") && config.getHubs().size() > 0) {
            ip = config.getHubs().get(0).getConfiguration().getHost();
        } else if (config.getDefaultRole().equals("node") && config.getNodes().size() > 0) {
            ip = config.getNodes().get(0).getConfiguration() != null ? config.getNodes().get(0).getConfiguration().getHost() : config.getNodes().get(0).getHost();
        }
    }
    if (ip == null) {
        ip = currentOS.getHostIp();
    }
    return ip;
  }


  public static SessionTracker getTestSessionTracker() {
    if (sessionTracker == null) {
      sessionTracker = new SessionTracker();
    }

    return sessionTracker;
  }

}
