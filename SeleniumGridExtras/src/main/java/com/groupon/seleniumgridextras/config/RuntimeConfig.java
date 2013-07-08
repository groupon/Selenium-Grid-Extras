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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.groupon.seleniumgridextras.OSChecker;
import com.groupon.seleniumgridextras.SeleniumGridExtras;
import com.groupon.seleniumgridextras.WriteDefaultConfigs;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;


public class RuntimeConfig {

  private static String configFile = "selenium_grid_extras_config.json";
  private static Config config;

  public RuntimeConfig() {
    config = new Config();
  }

  public static void setConfig(String file) {
    configFile = file;
  }

  public static String getConfigFile() {
    return configFile;
  }

  public static void loadConfig() {

    String configString = readConfigFile(configFile);

    if (configString != "") {
      config = new Gson().fromJson(configString, Config.class);
    }
  }

  public static List<String> getSetupModules() {
    return config.getSetup();
  }

  public static String getExposedDirectory() {
    return config.getExposeDirectory();
  }

  public static List<String> getTeardownModules() {
    return config.getTeardown();
  }

  public static List<String> getActivatedModules() {
    return config.getActivatedModules();
  }

  public static List<String> getDeactivatedModules() {
    return config.getDisabledModules();
  }

  public static Boolean checkIfModuleEnabled(String module) {
    return getActivatedModules().contains(module);
  }

  public static Config.WebDriver getWebdriverConfig() {
    return config.getWebdriver();
  }

  public static Config.GridInfo getGridConfig() {
    return config.getGrid();
  }

  public static Boolean autoStartHub() {
    return getGridConfig().getAutoStartHub();
  }

  public static Boolean autoStartNode() {
    return getGridConfig().getAutoStartNode();
  }

  public static String getSeleniumGridExtrasJarFile() {
    return SeleniumGridExtras.class.getProtectionDomain().getCodeSource().getLocation().getPath();
  }

  public static String getCurrentHostIP() {
    try {
      InetAddress addr = InetAddress.getLocalHost();
      return addr.getHostAddress();
    } catch (UnknownHostException error) {
      System.out.println(error);
      return "";
    }
  }

  public static String getSeleniungGridExtrasHomePath() {
    String path = getSeleniumGridExtrasJarFile();
    path = path.replaceAll("[\\w-\\d\\.]*\\.jar", "");

    if (OSChecker.isWindows()) {
      path = OSChecker.toWindowsPath(path);
    }

    return path;
  }

  public static String getWebdriverParentDir() {
    return RuntimeConfig.getWebdriverConfig().getDirectory();
  }

  public static String getWebdriverVersion() {
    return RuntimeConfig.getWebdriverConfig().getVersion();
  }

  public static void setWebdriverVersion(String newVersion) {
    getWebdriverConfig().setVersion(newVersion);
  }

  public static void saveConfigToFile() throws IOException {
    String jsonText = new GsonBuilder().setPrettyPrinting().create().toJson(config);
    FileUtils.writeStringToFile(new File(configFile), jsonText);
  }

  public static String getJsonString() {
    return new GsonBuilder().setPrettyPrinting().create().toJson(config);
  }

  private static String readConfigFile(String filePath) {
    String returnString = "";
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filePath));
      String line = null;
      while ((line = reader.readLine()) != null) {
        returnString = returnString + line;
      }
    } catch (FileNotFoundException error) {
      System.out.println("File " + filePath + " does not exist, going to use default configs");
      WriteDefaultConfigs.writeConfig(filePath);
      return readConfigFile(filePath);

    } catch (IOException error) {
      System.out.println("Error reading" + filePath + ". Going with default configs");
      WriteDefaultConfigs.writeConfig(filePath);
      return readConfigFile(filePath);
    }

    return returnString;
  }

}
