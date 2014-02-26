package com.groupon.seleniumgridextras.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.StringMap;

import com.groupon.seleniumgridextras.config.driver.ChromeDriver;
import com.groupon.seleniumgridextras.config.driver.DriverInfo;
import com.groupon.seleniumgridextras.config.driver.IEDriver;
import com.groupon.seleniumgridextras.config.driver.WebDriver;
import com.groupon.seleniumgridextras.daemons.DaemonWrapper;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Config {

  public static final String ACTIVATE_MODULES = "active_modules";
  public static final String DISABLED_MODULES = "disabled_modules";
  public static final String SETUP = "setup";
  public static final String TEAR_DOWN = "tear_down";
  public static final String GRID = "grid";
  public static final String WEBDRIVER = "webdriver";
  public static final String IEDRIVER = "iedriver";
  public static final String CHROME_DRIVER = "chromedriver";
  public static final String SHARED_DIR = "expose_directory";

  public static final String AUTO_START_NODE = "auto_start_node";
  public static final String AUTO_START_HUB = "auto_start_hub";
  public static final String DEFAULT_ROLE = "default_role";
  public static final String HUB_CONFIG = "hub_config";
  public static final String NODE_CONFIG_FILES = "node_config_files";

  public static final String GRID_JVM_OPTIONS = "grid_jvm_options";
  public static final String GRID_EXTRAS_JVM_OPTIONS = "grid_extras_jvm_options";

  public static final String GRID_EXTRAS_DAEMON = "daemon";

  private static Logger logger = Logger.getLogger(Config.class);


  protected Map theConfigMap;
  protected List<GridNode> gridNodeList;

  public Config() {
    theConfigMap = new HashMap();
    gridNodeList = new LinkedList<GridNode>();
    getConfigMap().put(NODE_CONFIG_FILES, new LinkedList<String>());
    initialize();
  }

  public Config(Boolean emptyConfig) {
    theConfigMap = new HashMap();
    gridNodeList = new LinkedList<GridNode>();
    getConfigMap().put(NODE_CONFIG_FILES, new LinkedList<String>());
    if (!emptyConfig) {
      initialize();
    }

  }

  public List<String> getNodeConfigFiles() {
    return (List<String>) getConfigMap().get(NODE_CONFIG_FILES);
  }

  public List<GridNode> getNodes() {
    return this.gridNodeList;
  }

  public void addNode(GridNode node, String filename) {
    getNodes().add(node);
    addNodeConfigFile(filename);
  }

  public void loadNodeClasses() {
    for (String filename : getNodeConfigFiles()) {
      GridNode node = GridNode.loadFromFile(filename);
      getNodes().add(node);
    }
  }

  public DaemonWrapper getDaemon() {
    return (DaemonWrapper) getConfigMap().get(GRID_EXTRAS_DAEMON);
  }


  private void initialize() {
    getConfigMap().put(ACTIVATE_MODULES, new ArrayList<String>());
    getConfigMap().put(DISABLED_MODULES, new ArrayList<String>());
    getConfigMap().put(SETUP, new ArrayList<String>());
    getConfigMap().put(TEAR_DOWN, new ArrayList<String>());

    initializeGridDaemon();

    getConfigMap().put(GRID, new StringMap());
    initializeWebdriver();
    initializeIEDriver();
    initializeChromeDriver();

    getConfigMap().put(NODE_CONFIG_FILES, new LinkedList<String>());

    initializeHubConfig();

    getConfigMap().put(GRID_JVM_OPTIONS, new HashMap<String, Object>());
    getConfigMap().put(GRID_EXTRAS_JVM_OPTIONS, new HashMap<String, Object>());

  }

  public void initializeGridDaemon() {
    getConfigMap().put(GRID_EXTRAS_DAEMON, DaemonWrapper.getNewInstance());
  }

  private void initializeIEDriver() {
    getConfigMap().put(IEDRIVER, new IEDriver());
  }

  private void initializeChromeDriver() {
    getConfigMap().put(CHROME_DRIVER, new ChromeDriver());
  }

  public void addNodeConfigFile(String filename) {
    LinkedList<String> files = (LinkedList<String>) getConfigMap().get(NODE_CONFIG_FILES);
    files.add(filename);
  }


  private void initializeHubConfig() {
    getConfigMap().put(HUB_CONFIG, new Hub());
  }

  private void initializeWebdriver() {
    getConfigMap().put(WEBDRIVER, new WebDriver());
  }

  private Map getConfigMap() {
    return theConfigMap;
  }


  public static Config initilizedFromUserInput() {
    Config config = new Config(true);
    config.initializeWebdriver();
    config.initializeHubConfig();
    config.initializeIEDriver();
    config.initializeChromeDriver();

    return FirstTimeRunConfig.customiseConfig(config);
  }

  public void overwriteConfig(Map overwrites) {
    if (overwrites.containsKey("theConfigMap")) {
      HashMapMerger.overwriteMergeStrategy(getConfigMap(),
                                           (Map<String, Object>) overwrites.get("theConfigMap"));
    }
  }


  public List<String> getActivatedModules() {
    return (List<String>) getConfigMap().get(ACTIVATE_MODULES);
  }

  public List<String> getDisabledModules() {
    return (List<String>) getConfigMap().get(DISABLED_MODULES);
  }

  public String getSharedDirectory() {
    return (String) getConfigMap().get(SHARED_DIR);
  }

  public List<String> getSetup() {
    return (List<String>) getConfigMap().get(SETUP);
  }

  public List<String> getTeardown() {
    return (List<String>) getConfigMap().get(TEAR_DOWN);
  }

  public StringMap getGrid() {
    return (StringMap) getConfigMap().get(GRID);
  }

  public DriverInfo getIEdriver() {
    try {
      return (IEDriver) getConfigMap().get(IEDRIVER);
    } catch (ClassCastException e) {
      StringMap
          stringMapFromGoogleWhoCantUseHashMapOnNestedObjects =
          (StringMap) getConfigMap().get(IEDRIVER);
      IEDriver ieDriver = new IEDriver();

      ieDriver.putAll(stringMapFromGoogleWhoCantUseHashMapOnNestedObjects);

      getConfigMap().put(IEDRIVER, ieDriver);

      return ieDriver;
    }
  }

  public DriverInfo getChromeDriver() {
    try {
      return (ChromeDriver) getConfigMap().get(CHROME_DRIVER);
    } catch (ClassCastException e) {
      StringMap
          stringMapFromGoogleWhoCantUseHashMapOnNestedObjects =
          (StringMap) getConfigMap().get(CHROME_DRIVER);
      DriverInfo chromeDriver = new ChromeDriver();

      chromeDriver.putAll(stringMapFromGoogleWhoCantUseHashMapOnNestedObjects);

      getConfigMap().put(CHROME_DRIVER, chromeDriver);

      return chromeDriver;
    }
  }


  public WebDriver getWebdriver() {
    try {
      return (WebDriver) getConfigMap().get(WEBDRIVER);
    } catch (ClassCastException e) {
      StringMap
          stringMapFromGoogleWhoCantUseHashMapOnNestedObjects =
          (StringMap) getConfigMap().get(WEBDRIVER);
      WebDriver webDriver = new WebDriver();

      webDriver.putAll(stringMapFromGoogleWhoCantUseHashMapOnNestedObjects);

      getConfigMap().put(WEBDRIVER, webDriver);

      return webDriver;
    }
  }


  public void writeToDisk(String file) {
    try {
      File f = new File(file);
      Map temp = new HashMap();
      temp.put("theConfigMap", getConfigMap());

      String config = toPrettyJsonString(temp);
      FileUtils.writeStringToFile(f, config);
    } catch (Exception error) {
      logger.fatal("Could not write default config file, exit with error " + error.toString());
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
    getConfigMap().put(SHARED_DIR, sharedDir);
  }

  public String toJsonString() {
    return new Gson().toJson(this);
  }

  public String toJson() {
    return toPrettyJsonString(this);
  }

  public String toPrettyJsonString(Object object) {
    return new GsonBuilder().setPrettyPrinting().create().toJson(object);
  }

  public boolean checkIfModuleEnabled(String module) {
    return getActivatedModules().contains(module);
  }


  public JsonObject asJsonObject() {
    return (JsonObject) new JsonParser().parse(this.toJsonString());
  }


  public void setDefaultRole(String defaultRole) {
    getConfigMap().put(DEFAULT_ROLE, defaultRole);
  }

  public void setAutoStartHub(String autoStartHub) {
    getConfigMap().put(AUTO_START_HUB, autoStartHub);
  }

  public void setAutoStartNode(String autoStartNode) {
    getConfigMap().put(AUTO_START_NODE, autoStartNode);
  }

  public boolean getAutoStartNode() {
    return getConfigMap().get(AUTO_START_NODE).equals("1") ? true : false;
  }

  public boolean getAutoStartHub() {
    return getConfigMap().get(AUTO_START_HUB).equals("1") ? true : false;
  }

  public String getGridJvmOptions() {
    logger.info(getConfigMap().get(GRID_JVM_OPTIONS));
    return mapToJvmParams((Map<String, Object>) getConfigMap().get(GRID_JVM_OPTIONS));
  }

  public String getGridExtrasJvmOptions() {
    logger.info(getConfigMap().get(GRID_EXTRAS_JVM_OPTIONS));
    return mapToJvmParams((Map<String, Object>) getConfigMap().get(GRID_EXTRAS_JVM_OPTIONS));
  }

  public void addGridJvmOptions(String key, Object value) {
    logger.info(key + " " + value);
    Map<String, Object> params = (Map<String, Object>) getConfigMap().get(GRID_JVM_OPTIONS);
    params.put(key, String.valueOf(value));
  }

  public void addGridExtrasJvmOptions(String key, Object value) {
    logger.info(key + " " + value);
    Map<String, Object> params = (Map<String, Object>) getConfigMap().get(GRID_EXTRAS_JVM_OPTIONS);
    params.put(key, String.valueOf(value));
  }

  protected String mapToJvmParams(Map<String, Object> params) {
    String returnString = "";

    for (String key : params.keySet()) {
      Object value = params.get(key);
      String formattedValue = "";

      if (value instanceof Number) {
        formattedValue = "" + ((Number) value).intValue() + "";
      } else {
        formattedValue = value.toString();
      }

      returnString = returnString + "-D" + key + "=" + formattedValue + " ";
    }

    return returnString;
  }


  public Hub getHub() {

    try {
      return (Hub) getConfigMap().get(HUB_CONFIG);
    } catch (ClassCastException e) {
      StringMap
          stringMapFromGoogleWhoCantUseHashMapOnNestedObjects =
          (StringMap) getConfigMap().get(HUB_CONFIG);
      Hub hubConfig = new Hub();

      hubConfig.putAll(stringMapFromGoogleWhoCantUseHashMapOnNestedObjects);

      getConfigMap().put(HUB_CONFIG, hubConfig);

      return hubConfig;
    }
  }

  public void setHub(Hub hub) {
    getConfigMap().put(HUB_CONFIG, hub);
  }


  public String getDefaultRole() {
    return (String) getConfigMap().get(DEFAULT_ROLE);
  }
}
