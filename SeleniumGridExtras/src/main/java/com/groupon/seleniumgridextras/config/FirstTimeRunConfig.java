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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FirstTimeRunConfig {


  public static Config customiseConfig(Config defaultConfig) {
    System.out.println(
        "\n\n\n\nWe noticed this is a first time running, we will ask some configuration settings\n\n");

    setWebDriverVersion(defaultConfig);
    setDefaultService(defaultConfig);
    setGridHubUrl(defaultConfig);
    setGridHubAutostart(defaultConfig);
    setGridNodeAutostart(defaultConfig);

    System.out
        .println("Thank you, your answers were recorded to '" + RuntimeConfig.getConfigFile() + "'");
    System.out.println("You can modify this file directly to tweak more options");
    return defaultConfig;
  }

  private static void setGridHubAutostart(Config defaultConfig) {
    String value = askQuestion("Do you want Grid Hub to be auto started? (1-yes/0-no)", "0");
    defaultConfig.getGrid().setAutoStartHub(Integer.valueOf(value));
  }

  private static void setGridNodeAutostart(Config defaultConfig) {
    String value = askQuestion("Do you want Grid Node to be auto started? (1-yes/0-no)", "1");
    defaultConfig.getGrid().setAutoStartNode(Integer.valueOf(value));
  }

  private static void setWebDriverVersion(Config defaultConfig) {
    String newVersion = askQuestion("What version of webdriver JAR should we use?", defaultConfig.getWebdriver().getVersion());
    defaultConfig.getWebdriver().setVersion(newVersion);
  }

  private static void setGridHubUrl(Config defaultConfig) {
    String url = askQuestion("What is the url for the Selenium Grid Hub?", "http://localhost:4444");
    defaultConfig.getGrid().getNode().setHub(url);
  }

  private static void setDefaultService(Config defaultConfig) {
    String role = askQuestion("What is the default Role of this computer? (hub|node)", "node");
    defaultConfig.getGrid().setDefaultRole(role);
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
