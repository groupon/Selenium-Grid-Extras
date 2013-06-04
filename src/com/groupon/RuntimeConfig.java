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

package com.groupon;

import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class RuntimeConfig {

  public static Map config;

  public static void loadConfig(String configFile) {

    String configString = readConfigFile(configFile);

    Map parsedConfig = new HashMap();
    if (configString != "") {
      parseJson(configString, parsedConfig);
    }

    updateConfig(parsedConfig);

    printActivatedModules();
    printDeactivatedModules();
    printSetupModules();
    printTeardownModules();
  }


  public static List<String> getSetupModules() {
    return (List<String>) config.get("setup");
  }

  public static void printSetupModules() {
    System.out.println("=== Modules to run before each test session ===");
    for (Object o : getSetupModules()) {
      System.out.println(o);
    }
  }

  public static List<String> getTeardownModules() {
    return (List<String>) config.get("teardown");
  }

  public static void printTeardownModules() {
    System.out.println("=== Modules to run after each test session ===");
    for (Object o : getTeardownModules()) {
      System.out.println(o);
    }
  }


  public static List<String> getActivatedModules() {
    return (List<String>) config.get("activated_modules");
  }

  public static List<String> getDeactivatedModules() {
    return (List<String>) config.get("deactivated_modules");
  }


  public static void printDeactivatedModules() {
    System.out.println("=== Modules which are deactivated ===");
    for (Object o : getDeactivatedModules()) {
      System.out.println(o);
    }
  }

  public static void printActivatedModules() {
    System.out.println("=== Activated Modules ===");
    for (Object o : getActivatedModules()) {
      System.out.println(o);
    }
  }


  private static void updateConfig(Map configHash) {

    if (configHash.isEmpty()) {
      //Do nothing, the file didn't read anything in
    } else {
      config = configHash;
    }

  }

  private static void parseJson(String inputString, Map returnHash) {

    JSONParser parser = new JSONParser();
    ContainerFactory containerFactory = new ContainerFactory() {
      public List creatArrayContainer() {
        return new LinkedList();
      }

      public Map createObjectContainer() {
        return new LinkedHashMap();
      }

    };

    try {
      Map json = (Map) parser.parse(inputString, containerFactory);
      Iterator iter = json.entrySet().iterator();
      while (iter.hasNext()) {
        Map.Entry entry = (Map.Entry) iter.next();
        returnHash.put(entry.getKey(), entry.getValue());
      }

    } catch (ParseException error) {
      System.out.println("position: " + error.getPosition());
      System.out.println(error);
    }
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
      System.out.println("File" + filePath + " does not exist, going to use default configs");
    } catch (IOException error) {
      System.out.println("Error reading" + filePath + ". Going with default configs");
    }

    return returnString;
  }

}
