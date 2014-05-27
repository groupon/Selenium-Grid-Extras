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
import com.groupon.seleniumgridextras.config.capabilities.Capability;
import com.groupon.seleniumgridextras.downloader.webdriverreleasemanager.WebDriverReleaseManager;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class FirstTimeRunConfig {

  private static Logger logger = Logger.getLogger(FirstTimeRunConfig.class);

  public static Config customiseConfig(Config defaultConfig) {
    final
    String
        message =
        "We noticed this is a first time running, we will ask some configuration settings";
    logger.info(message);
    System.out.println("\n\n\n\n" + message + "\n\n");

    setDefaultService(defaultConfig);

    String hubHost = getGridHubHost();
    String hubPort = getGridHubPort();

    List<Capability> caps = getCapabilitiesFromUser();

    configureNodes(caps, hubHost, hubPort, defaultConfig);

    setDriverAutoUpdater(defaultConfig);

    final
    String
        thankYouMessage =
        "Thank you, your answers were recorded to '" + RuntimeConfig.getConfigFile() + "'\n\n"
        + "You can modify this file directly to tweak more options";
    logger.info(thankYouMessage);
    System.out.println(thankYouMessage);

    return defaultConfig;
  }

  private static void setDriverAutoUpdater(Config defaultConfig) {
    String
        answer =
        askQuestion(
            "Would you like WebDriver, IEDriver and ChromeDriver to auto update (1-yes/0-no)", "1");

    if (answer.equals("1")) {
      defaultConfig.setAutoUpdateDrivers("1");

      WebDriverReleaseManager manager = RuntimeConfig.getReleaseManager();

      defaultConfig.getWebdriver()
          .setVersion(manager.getWedriverLatestVersion().getPrettyPrintVersion("."));
      defaultConfig.getIEdriver()
          .setVersion(manager.getIeDriverLatestVersion().getPrettyPrintVersion("."));
      defaultConfig.getChromeDriver()
          .setVersion(manager.getChromeDriverLatestVersion().getPrettyPrintVersion("."));


    } else {
      defaultConfig.setAutoUpdateDrivers("0");
      System.out.println(
          "Drivers will not be automatically updated.\n You can change the versions of each driver later in the config");
    }

    System.out
        .println("Current Selenium Driver Version: " + defaultConfig.getWebdriver().getVersion());
    System.out.println("Current IE Driver Version: " + defaultConfig.getIEdriver().getVersion());
    System.out
        .println("Current Chrome Driver Version: " + defaultConfig.getChromeDriver().getVersion());

  }

  private static List<GridNode> configureNodes(List<Capability> capabilities, String hubHost,
                                               String hubPort, Config defaultConfig) {
    List<GridNode> nodes = new LinkedList<GridNode>();
    int nodePort = 5555;

    for (Capability cap : capabilities) {
      GridNode node = new GridNode();

      node.getCapabilities().add(cap);
      node.getConfiguration().setHubHost(hubHost);
      node.getConfiguration().setHubPort(Integer.parseInt(hubPort));
      node.getConfiguration().setPort(nodePort);

      String configFileName = "node_" + nodePort + ".json";

      node.writeToFile(configFileName);
      defaultConfig.addNode(node, configFileName);

      nodePort++;
    }

    return nodes;
  }


  private static List<Capability> getCapabilitiesFromUser() {
    List<Capability> chosenCapabilities = new LinkedList<Capability>();

    String platform = askQuestion(
        "What is node Platform? (WINDOWS|XP|VISTA|MAC|LINUX|UNIX|ANDROID)",
        guessPlatform());

    for (Class currentCapabilityClass : Capability.getSupportedCapabilities().keySet()) {
      String
          value =
          askQuestion(
              "Will this node run '" + currentCapabilityClass.getSimpleName()
              + "' (1-yes/0-no)", "0");

      if (value.equals("1")) {
        Capability capability;
        try {
          capability =
              (Capability) Class.forName(currentCapabilityClass.getCanonicalName()).newInstance();
          capability.setPlatform(platform.toUpperCase());
//          capability.setBrowserVersion(askQuestion(
//              "What version of '" + capability.getBrowserName() + "' is installed?"));

          chosenCapabilities.add(capability);
        } catch (Exception e) {
          logger.warn("Warning: Had an issue creating capability for " + currentCapabilityClass
              .getSimpleName());
          logger.warn(e.toString());
        }
      }

    }

    return chosenCapabilities;
  }


  private static String guessPlatform() {
    if (RuntimeConfig.getOS().isWindows()) {
      return "WINDOWS";
    } else if (RuntimeConfig.getOS().isMac()) {
      return "MAC";
    } else {
      return "LINUX";
    }
  }


  private static void setGridHubAutostart(Config defaultConfig, String value) {
    defaultConfig.setAutoStartHub(value);
  }

  private static void setGridNodeAutostart(Config defaultConfig, String value) {
    defaultConfig.setAutoStartNode(value);
  }

  private static String getGridHubHost() {
    String
        host =
        askQuestion("What is the HOST for the Selenium Grid Hub?",
                    "127.0.0.1");
    return host;
  }


  private static String getGridHubPort() {
    String port = askQuestion("What is the PORT for the Selenium Grid Hub?", "4444");
    return port;
  }

  private static void setDefaultService(Config defaultConfig) {
    String
        role =
        askQuestion(
            "What is the default Role of this computer? (1 - node | 2 - hub | 3 - hub & node) ",
            "1");

    if (role.equals("1")) {
      setGridHubAutostart(defaultConfig, "0");
      setGridNodeAutostart(defaultConfig, "1");
      defaultConfig.setDefaultRole("node");
    } else if (role.equals("2")) {
      setGridHubAutostart(defaultConfig, "1");
      setGridNodeAutostart(defaultConfig, "0");
      defaultConfig.setDefaultRole("hub");
    } else {
      setGridHubAutostart(defaultConfig, "1");
      setGridNodeAutostart(defaultConfig, "1");
      defaultConfig.setDefaultRole("hub");
    }
  }

  private static String askQuestion(String question, String defaultValue) {

    System.out.println("\n\n" + question);
    System.out.println("Default Value: " + defaultValue);

    String answer = readLine();

    if (answer.equals("")) {
      answer = defaultValue;
    }

    final String printOutAswer = "'" + answer + "' was set as your value";
    System.out.println(printOutAswer);
    logger.info(printOutAswer);

    return answer;

  }

  private static String askQuestion(String question) {
    System.out.println("\n\n" + question);
    System.out.println("(No Default Value)");
    String answer = readLine();

    return answer;
  }

  private static String readLine() {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String line = null;

    try {
      line = br.readLine();
    } catch (IOException ioe) {
      logger.fatal("IO error trying to read your input.");
      System.exit(1);
    }

    return line;
  }

}
