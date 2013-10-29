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
import com.groupon.seleniumgridextras.config.capabilities.Capability;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class FirstTimeRunConfig {


  public static Config customiseConfig(Config defaultConfig) {
    System.out.println(
        "\n\n\n\nWe noticed this is a first time running, we will ask some configuration settings\n\n");

    setWebDriverVersion(defaultConfig);
    setDefaultService(defaultConfig);

    String hubHost = getGridHubHost();
    String hubPort = getGridHubPort();

    List<Capability> caps = getDesiredCapabilities();

    configureNodes(caps, hubHost, hubPort, defaultConfig);

    setGridHubAutostart(defaultConfig);
    setGridNodeAutostart(defaultConfig);


    System.out
        .println(
            "Thank you, your answers were recorded to '" + RuntimeConfig.getConfigFile() + "'");
    System.out.println("You can modify this file directly to tweak more options");
    return defaultConfig;
  }

  private static List<GridNode> configureNodes(List<Capability> capabilities, String hubHost, String hubPort, Config defaultConfig){
    List<GridNode> nodes = new LinkedList<GridNode>();
    int nodePort = 5555;

    for(Capability cap : capabilities){
      GridNode node = new GridNode();

      node.getCapabilities().add(cap);
      node.getConfiguration().setHost(RuntimeConfig.getCurrentHostIP());
      node.getConfiguration().setHubHost(hubHost);
      node.getConfiguration().setHubPort(Integer.parseInt(hubPort));
      node.getConfiguration().setPort(nodePort);

      String configFileName = "node_" + nodePort + ".json";

      node.writeToFile(configFileName);
      defaultConfig.addNodeConfigFile(configFileName);
      RuntimeConfig.addNode(node);

      nodePort++;
    }

    return nodes;
  }


  private static List<Capability> getDesiredCapabilities() {
    List<Capability> chosenCapabilities = new LinkedList<Capability>();

    String platform = askQuestion(
        "What is node Platform? (WINDOWS|XP|VISTA|MAC|LINUX|UNIX|ANDROID)",
        guessPlatform());

    for (Capability currentCapability : RuntimeConfig.getAvailableCapabilitiesList()) {
      String
          value =
          askQuestion(
              "Will this node run '" + currentCapability.getBrowserName() + "' (1-yes/0-no)", "0");

      if (value.equals("1")) {
        currentCapability.setPlatform(platform.toUpperCase());
        currentCapability.setBrowserVersion(askQuestion(
            "What version of '" + currentCapability.getBrowserName() + "' is installed?"));

        chosenCapabilities.add(currentCapability);
      }

    }

    return chosenCapabilities;
  }


  private static String guessPlatform() {
    if (OSChecker.isWindows()) {
      return "WINDOWS";
    } else if (OSChecker.isMac()) {
      return "MAC";
    } else {
      return "LINUX";
    }
  }


  private static void setGridHubAutostart(Config defaultConfig) {
    String value = askQuestion("Do you want Grid Hub to be auto started? (1-yes/0-no)", "0");
    defaultConfig.setAutoStartHub(value);
  }

  private static void setGridNodeAutostart(Config defaultConfig) {
    String value = askQuestion("Do you want Grid NodeConfig to be auto started? (1-yes/0-no)", "1");
    defaultConfig.setAutoStartNode(value);
  }

  private static void setWebDriverVersion(Config defaultConfig) {
    System.out.println(defaultConfig);
    String
        newVersion =
        askQuestion("What version of webdriver JAR should we use?",
                    DefaultConfig.getWebDriverDefaultVersion());
    defaultConfig.getWebdriver().setVersion(newVersion);
  }

  private static String getGridHubHost() {
    String host = askQuestion("What is the HOST for the Selenium Grid Hub?", "127.0.0.1");
    return host;
  }

  private static String getGridHubPort() {
    String port = askQuestion("What is the PORT for the Selenium Grid Hub?", "4444");
    return port;
  }

  private static void setDefaultService(Config defaultConfig) {
    String role = askQuestion("What is the default Role of this computer? (hub|node)", "node");
    defaultConfig.setDefaultRole(role);
  }

  private static String askQuestion(String question, String defaultValue) {

    System.out.println("\n\n" + question);
    System.out.println("Default Value: " + defaultValue);

    String answer = readLine();

    if (answer.equals("")) {
      answer = defaultValue;
    }

    System.out.println("'" + answer + "' was set as your value\n\n");

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
      System.out.println("IO error trying to read your input.");
      System.exit(1);
    }

    return line;
  }

}
