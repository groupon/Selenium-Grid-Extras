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

package com.groupon.seleniumgridextras;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JsonWrapper {


  public static Map parseJson(String inputString) {
    Map returnHash = new HashMap();

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

    return returnHash;
  }

  public static String fileArrayToJson(File[] inputArray) {
    JSONArray fileList = new JSONArray();

    JSONObject wrapper = new JSONObject();

    for (File f : inputArray) {
      fileList.add(f.toString());
    }

    wrapper.put("files", fileList);

    return wrapper.toString();
  }

  public static String getDefaultConfigs() {
    JSONObject config = new JSONObject();
    JSONArray activeModules = new JSONArray();
    JSONArray setupTask = new JSONArray();
    JSONArray teardownTask = new JSONArray();
    JSONObject webdriverConfig = new JSONObject();
    JSONObject gridConfig = new JSONObject();
    JSONObject gridHubConfig = new JSONObject();
    JSONObject gridNodeConfig = new JSONObject();
    //Webdriver Config
    webdriverConfig.put("directory", "webdriver");
    webdriverConfig.put("version", "2.33.0");
    config.put("webdriver", webdriverConfig);
    //Activated Modules
    activeModules.add("com.groupon.seleniumgridextras.tasks.DownloadWebdriver");
    activeModules.add("com.groupon.seleniumgridextras.tasks.UpgradeWebdriver");
    activeModules.add("com.groupon.seleniumgridextras.tasks.Setup");
    activeModules.add("com.groupon.seleniumgridextras.tasks.Teardown");
    activeModules.add("com.groupon.seleniumgridextras.tasks.MoveMouse");
    activeModules.add("com.groupon.seleniumgridextras.tasks.RebootNode");
    activeModules.add("com.groupon.seleniumgridextras.tasks.KillAllIE");
    activeModules.add("com.groupon.seleniumgridextras.tasks.KillAllFirefox");
    activeModules.add("com.groupon.seleniumgridextras.tasks.KillAllChrome");
    activeModules.add("com.groupon.seleniumgridextras.tasks.GetProcesses");
    activeModules.add("com.groupon.seleniumgridextras.tasks.KillPid");
    activeModules.add("com.groupon.seleniumgridextras.tasks.Netstat");
    activeModules.add("com.groupon.seleniumgridextras.tasks.Screenshot");
    activeModules.add("com.groupon.seleniumgridextras.tasks.ExposeDirectory");
    activeModules.add("com.groupon.seleniumgridextras.tasks.StartGrid");
//    activeModules.add("com.groupon.GetFile");
    activeModules.add("com.groupon.seleniumgridextras.tasks.GetInfoForPort");
    activeModules.add("com.groupon.seleniumgridextras.tasks.GridStatus");
    activeModules.add("com.groupon.seleniumgridextras.tasks.KillAllByName");
    activeModules.add("com.groupon.seleniumgridextras.tasks.StopGrid");
    activeModules.add("com.groupon.seleniumgridextras.tasks.GetConfig");
    config.put("activated_modules", activeModules);
    //Setup Task Modules
    setupTask.add("com.groupon.seleniumgridextras.tasks.KillAllIE");
    setupTask.add("com.groupon.seleniumgridextras.tasks.MoveMouse");
    config.put("setup", setupTask);
    //Teardown Task Modules
    teardownTask.add("com.groupon.seleniumgridextras.tasks.KillAllIE");
    config.put("teardown", teardownTask);

    config.put("expose_directory", "shared");

    gridHubConfig.put("-role", "hub");
    gridHubConfig.put("-servlets", "com.groupon.seleniumgridextras.grid.SeleniumGridExtrasServlet");
    gridHubConfig.put("-port", "4444");

    gridNodeConfig.put("-host", "http://127.0.0.1");
    gridNodeConfig.put("-role", "wd");
    gridNodeConfig.put("-hub", "http://localhost:4444");
    gridNodeConfig.put("-port", "5555");
    gridNodeConfig.put("-host", "http://127.0.0.1");

    gridConfig.put("hub", gridHubConfig);
    gridConfig.put("node", gridNodeConfig);
    gridConfig.put("default_role", "hub");

    config.put("grid", gridConfig);

    return config.toString();
  }


}
