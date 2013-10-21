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

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;


public class RuntimeConfig {

  private static String configFile = "selenium_grid_extras_config.json";
  private static Config config = null;

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

  public static Config load() {
    Map overwriteValues;
    config = DefaultConfig.getDefaultConfig();

    String configString = readConfigFile(configFile);
    //Reason to use the "" instead of checking if the config already exists is because
    //if the file does exist but there is any formatting error, or parsing error, etc..
    //We assume file does not exist and overwrite it with good configs.
    if (configString != "") {
      overwriteValues = new Gson().fromJson(configString, HashMap.class);
    } else {
      // first time runner
      Config userInput = Config.initilizedFromUserInput();
      userInput.writeToDisk(RuntimeConfig.getConfigFile());
      configString = readConfigFile(configFile);
      overwriteValues = new Gson().fromJson(configString, HashMap.class);
    }
    config.overwriteConfig(overwriteValues);

    return config;
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
    } catch (IOException error) {
      System.out.println("Error reading" + filePath + ". Going with default configs");
    }
    return returnString;
  }

  public static Config getConfig() {
    return config;
  }

}
