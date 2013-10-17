package com.groupon.seleniumgridextras.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.StringMap;

import com.groupon.seleniumgridextras.config.driver.IEDriver;
import com.groupon.seleniumgridextras.config.driver.WebDriver;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

  public static final String ACTIVATE_MODULES = "ACTIVATE_MODULES";
  public static final String DISABLED_MODULES = "DISABLED_MODULES";
  public static final String SETUP = "SETUP";
  public static final String TEAR_DOWN = "TEAR_DOWN";
  public static final String GRID = "GRID";
  public static final String WEBDRIVER = "WEBDRIVER";
  public static final String IEDRIVER = "IEDRIVER";
  public static final String EXPOSE_DIRECTORY = "EXPOSE_DIRECTORY";

  public static final String AUTO_START_NODE = "auto_start_node";
  public static final String AUTO_START_HUB = "auto_start_hub";
  public static final String DEFAULT_ROLE = "default_role";
  public static final String NODE_CONFIG = "node_config";
  public static final String HUB_CONFIG = "hub_config";


  protected Map theConfigMap;

  public Config() {
    theConfigMap = new HashMap();
    theConfigMap.put(ACTIVATE_MODULES, new ArrayList<String>());
    theConfigMap.put(DISABLED_MODULES, new ArrayList<String>());
    theConfigMap.put(SETUP, new ArrayList<String>());
    theConfigMap.put(TEAR_DOWN, new ArrayList<String>());

    theConfigMap.put(GRID, new StringMap());
    theConfigMap.put(WEBDRIVER, new WebDriver());
    theConfigMap.put(IEDRIVER, new IEDriver());

    theConfigMap.put(HUB_CONFIG, new Hub());
    theConfigMap.put(NODE_CONFIG, new NodeConfig());


  }


  public List<String> getActivatedModules() {
    return (List<String>) theConfigMap.get(ACTIVATE_MODULES);
  }

  public List<String> getDisabledModules() {
    return (List<String>) theConfigMap.get(DISABLED_MODULES);
  }

  public String getExposedDirectory() {
    return (String) theConfigMap.get(EXPOSE_DIRECTORY);
  }

  public List<String> getSetup() {
    return (List<String>) theConfigMap.get(SETUP);
  }

  public List<String> getTeardown() {
    return (List<String>) theConfigMap.get(TEAR_DOWN);
  }

  public StringMap getGrid() {
    return (StringMap) theConfigMap.get(GRID);
  }

  public void setIEdriver() {
//    this.put(IEDRIVERz)
  }

  public IEDriver getIEdriver() {
    try {
      return (IEDriver) theConfigMap.get(IEDRIVER);
    } catch (ClassCastException e) {
      StringMap
          stringMapFromGoogleWhoCantUseHashMapOnNestedObjects =
          (StringMap) theConfigMap.get(IEDRIVER);
      IEDriver ieDriver = new IEDriver();

      ieDriver.putAll(stringMapFromGoogleWhoCantUseHashMapOnNestedObjects);

      theConfigMap.put(IEDRIVER, ieDriver);

      return ieDriver;
    }
  }

  public WebDriver getWebdriver() {
    try {
      return (WebDriver) theConfigMap.get(WEBDRIVER);
    } catch (ClassCastException e) {
      StringMap
          stringMapFromGoogleWhoCantUseHashMapOnNestedObjects =
          (StringMap) theConfigMap.get(WEBDRIVER);
      WebDriver webDriver = new WebDriver();

      webDriver.putAll(stringMapFromGoogleWhoCantUseHashMapOnNestedObjects);

      theConfigMap.put(WEBDRIVER, webDriver);

      return webDriver;
    }
  }


  public void writeToDisk(String file) {
    try {
      File f = new File(file);
      String config = this.toPrettyJsonString();
      FileUtils.writeStringToFile(f, config);
    } catch (Exception error) {
      System.out
          .println("Could not write default config file, exit with error " + error.toString());
      System.exit(1);
    }
  }

  public void addSetupTask(String task) {
    getSetup().add(task);
  }

  public void addTeardownTask(String task) {
    getTeardown().add(task);
  }

  public void addActivatedModules(String module) {
    getActivatedModules().add(module);
  }

  public void addDisabledModule(String module) {
    getDisabledModules().add(module);
  }

  public void setSharedDir(String sharedDir) {
    theConfigMap.put(EXPOSE_DIRECTORY, sharedDir);
  }

  public String toJsonString() {
    return new Gson().toJson(this);
  }

  public String toPrettyJsonString() {
    return new GsonBuilder().setPrettyPrinting().create().toJson(this);
  }

  public boolean checkIfModuleEnabled(String module) {
    return getActivatedModules().contains(module);
  }


  public JsonObject asJsonObject() {
    return (JsonObject) new JsonParser().parse(this.toJsonString());
  }

  public boolean getAutoStartNode() {
    return theConfigMap.get(AUTO_START_HUB).equals("1") ? true : false;
  }

  public boolean getAutoStartHub() {
    return theConfigMap.get(AUTO_START_NODE).equals("1") ? true : false;
  }

  public void setDefaultRole(String defaultRole) {
    theConfigMap.put(DEFAULT_ROLE, defaultRole);
  }

  public void setAutoStartHub(String autoStartHub) {
    theConfigMap.put(AUTO_START_HUB, autoStartHub);
  }

  public void setAutoStartNode(String autoStartNode) {
    theConfigMap.put(AUTO_START_NODE, autoStartNode);
  }

  public Hub getHub() {

    try {
      return (Hub) theConfigMap.get(HUB_CONFIG);
    } catch (ClassCastException e) {
      StringMap
          stringMapFromGoogleWhoCantUseHashMapOnNestedObjects =
          (StringMap) theConfigMap.get(HUB_CONFIG);
      Hub hubConfig = new Hub();

      hubConfig.putAll(stringMapFromGoogleWhoCantUseHashMapOnNestedObjects);

      theConfigMap.put(HUB_CONFIG, hubConfig);

      return hubConfig;
    }
  }

  public void setHub(Hub hub) {
    theConfigMap.put(HUB_CONFIG, hub);
  }


  public NodeConfig getNode() {
    try {
      return (NodeConfig) theConfigMap.get(NODE_CONFIG);
    } catch (ClassCastException e) {
      StringMap
          stringMapFromGoogleWhoCantUseHashMapOnNestedObjects =
          (StringMap) theConfigMap.get(NODE_CONFIG);
      NodeConfig nodeConfig = new NodeConfig();

      nodeConfig.putAll(stringMapFromGoogleWhoCantUseHashMapOnNestedObjects);

      theConfigMap.put(NODE_CONFIG, nodeConfig);

      return nodeConfig;
    }

  }

  public String getDefaultRole() {
    return (String) theConfigMap.get(DEFAULT_ROLE);
  }
}
