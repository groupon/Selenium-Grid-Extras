package com.groupon.seleniumgridextras.tasks;

import com.google.gson.JsonObject;


import org.apache.log4j.Logger;


public class CheckInstalledBrowsers  extends ExecuteOSTask{
  private static Logger logger = Logger.getLogger(CheckInstalledBrowsers.class);


  //TODO: not finished, see note in BrowserVersionDetector.java

  public CheckInstalledBrowsers(){
    setEndpoint("/check_browser_versions");
    setDescription("Attempts to open use every browser specified in node's capabilities section, and updates current defined version");
    JsonObject params = new JsonObject();
    params.addProperty("path",
                       "Path to the WebDriver Jar to use when opening the browser");
  }


  @Override
  public JsonObject execute(String path) {



    return getJsonResponse().getJson();
  }


}
