package com.groupon.seleniumgridextras.config;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import com.groupon.seleniumgridextras.config.capabilities.Capability;
import com.groupon.seleniumgridextras.utilities.json.JsonParserWrapper;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class GridNode {

  private LinkedList<Capability> capabilities;
  private GridNodeConfiguration configuration;
  private String loadedFromFile;

  // Selenium 3 has values at top level, not in "configuration"
  private String proxy;
  private Integer maxSession;
  private Integer port;
  private Boolean register;
  private Integer unregisterIfStillDownAfter;
  private Integer hubPort;
  private String hubHost;
  private String host;
  private String url;
  private Integer registerCycle;
  private Integer nodeStatusCheckTimeout;
  private String appiumStartCommand;

  //Only test the node status 1 time, since the limit checker is
  //Since DefaultRemoteProxy.java does this check failedPollingTries >= downPollingLimit
  private Integer downPollingLimit;

  private static Logger logger = Logger.getLogger(GridNode.class);

  // Used for FirstTimeRunConfig
  public GridNode(boolean isSelenium3) {
    capabilities = new LinkedList<Capability>();
    if(!isSelenium3) { // This won't work for beta1, beta2, or beta3.
      configuration = new GridNodeConfiguration();
    } else {
      proxy = "com.groupon.seleniumgridextras.grid.proxies.SetupTeardownProxy";
      maxSession = 3;
      register = true;
      unregisterIfStillDownAfter = 10000;
      registerCycle = 5000;
      nodeStatusCheckTimeout = 10000;
      downPollingLimit = 0;
    }
  }

  private GridNode(LinkedList<Capability> caps, GridNodeConfiguration config, int hubPort, String hubHost, int nodePort) {
    capabilities = caps;
    if(config != null) { // If config is not null, this is Selenium 2 
      configuration = config;
      setHubPort(null);
      setPort(null);
      setMaxSession(null);
      setRegister(null);
      setRegisterCycle(null);
      setNodeStatusCheckTimeout(null);
      setUnregisterIfStillDownAfter(null);
      setDownPollingLimit(null);
    } else { // If config is null then hubPort, hubHost, and nodePort should be set (Selenium 3)
      setHubPort(hubPort);
      setHubHost(hubHost);
      setPort(nodePort);
    }
  }

  public static GridNode loadFromFile(String filename, boolean isSelenium3) {
    String configString = readConfigFile(filename);
    JsonObject topLevelJson = new JsonParser().parse(configString).getAsJsonObject();

    LinkedList<Capability> filteredCapabilities = new LinkedList<Capability>();
    for (JsonElement cap : topLevelJson.getAsJsonArray("capabilities")) {
      Map capHash = JsonParserWrapper.toHashMap(cap.toString());
      if (capHash.containsKey("browserName")) {
        filteredCapabilities.add(Capability.getCapabilityFor((String) capHash.get("browserName"), capHash));
      }
    }
    
    String configFromFile = null;
    GridNodeConfiguration nodeConfiguration = null;
    String hubHost = null;
    int hubPort = 0;
    int nodePort = 0;
    if(isSelenium3) { // This won't work for beta1, beta2, or beta3.
      try {
        hubPort = Integer.parseInt(topLevelJson.get("hubPort").toString());
        hubHost = topLevelJson.get("hubHost").getAsString();
        nodePort = Integer.parseInt(topLevelJson.get("port").toString());
        GridNode node = new GridNode(filteredCapabilities, null, hubPort, hubHost, nodePort);
        node.setMaxSession(Integer.parseInt(topLevelJson.get("maxSession").toString()));
        node.setProxy(topLevelJson.get("proxy").getAsString());
        node.setRegister(topLevelJson.get("register").getAsBoolean());
        node.setRegisterCycle(topLevelJson.get("registerCycle") != null
                ? Integer.parseInt(topLevelJson.get("registerCycle").toString()) : null);
        node.setUnregisterIfStillDownAfter(topLevelJson.get("unregisterIfStillDownAfter") != null
                ? Integer.parseInt(topLevelJson.get("unregisterIfStillDownAfter").toString()) : null);
        node.setNodeStatusCheckTimeout(topLevelJson.get("nodeStatusCheckTimeout") != null
                ? Integer.parseInt(topLevelJson.get("nodeStatusCheckTimeout").toString()) : null);
        node.setDownPollingLimit(topLevelJson.get("downPollingLimit") != null
                ? Integer.parseInt(topLevelJson.get("downPollingLimit").toString()) : null);
        node.setHost(topLevelJson.get("host") != null ? topLevelJson.get("host").getAsString() : null);
        node.setUrl(topLevelJson.get("url") != null ? topLevelJson.get("url").getAsString() : null);
        node.setAppiumStartCommand(topLevelJson.get("appiumStartCommand") != null
                ? topLevelJson.get("appiumStartCommand").getAsString() : null);
        node.setLoadedFromFile(filename);
        node.writeToFile(filename);

        return node;
      } catch(Exception e) { // Going from Selenium 2 to Selenium 3 - Re-write config file and return node
        configFromFile = topLevelJson.getAsJsonObject("configuration").toString();
        nodeConfiguration =
            new Gson().fromJson(configFromFile, GridNodeConfiguration.class);
        
        GridNode node = new GridNode(true);
        node.setCapabilities(filteredCapabilities);
        node.setHubPort(nodeConfiguration.getHubPort());
        node.setHubHost(nodeConfiguration.getHubHost());
        node.setPort(nodeConfiguration.getPort());
        node.setMaxSession(nodeConfiguration.getMaxSession());
        node.setProxy(nodeConfiguration.getProxy());
        node.setRegister(nodeConfiguration.getRegister());
        try {
            // If register cycle is not configured, an exception is thrown when converting value to int
            node.setRegisterCycle(nodeConfiguration.getRegisterCycle());
        } catch (NullPointerException exc) {
            node.setRegisterCycle(null);
        }
        node.setUnregisterIfStillDownAfter(nodeConfiguration.getUnregisterIfStillDownAfter());
        node.setNodeStatusCheckTimeout(nodeConfiguration.getNodeStatusCheckTimeout());
        node.setDownPollingLimit(nodeConfiguration.getDownPollingLimit());
        node.setHost(nodeConfiguration.getHost());
        node.setUrl(nodeConfiguration.getUrl());
        node.setAppiumStartCommand(nodeConfiguration.getAppiumStartCommand());
        node.setLoadedFromFile(filename);
        node.writeToFile(filename);

        nodeConfiguration = null; // Should be null for Selenium 3
        
        return node;
      }
    } else { // Selenium 2
      try {
        configFromFile = topLevelJson.getAsJsonObject("configuration").toString();
        nodeConfiguration =
            new Gson().fromJson(configFromFile, GridNodeConfiguration.class);
      } catch(Exception e) { // Maybe moving from Selenium 3 to Selenium 2
        GridNode node = new GridNode(false);
        node.setCapabilities(filteredCapabilities);
        node.getConfiguration().setHubHost(topLevelJson.get("hubHost").getAsString());
        node.getConfiguration().setHubPort(Integer.parseInt(topLevelJson.get("hubPort").toString()));
        node.getConfiguration().setPort(Integer.parseInt(topLevelJson.get("port").toString()));
        
        node.getConfiguration().setMaxSession(Integer.parseInt(topLevelJson.get("maxSession").toString()));
        node.getConfiguration().setProxy(topLevelJson.get("proxy").getAsString());
        node.getConfiguration().setRegister(topLevelJson.get("register").getAsBoolean());
        if (topLevelJson.get("registerCycle") != null) {
            node.getConfiguration().setRegisterCycle(Integer.parseInt(topLevelJson.get("registerCycle").toString()));
        }
        node.getConfiguration().setUnregisterIfStillDownAfter(topLevelJson.get("unregisterIfStillDownAfter") != null
                ? Integer.parseInt(topLevelJson.get("unregisterIfStillDownAfter").toString()) : null);
        node.getConfiguration().setNodeStatusCheckTimeout(topLevelJson.get("nodeStatusCheckTimeout") != null
                ? Integer.parseInt(topLevelJson.get("nodeStatusCheckTimeout").toString()) : null);
        node.getConfiguration().setDownPollingLimit(topLevelJson.get("downPollingLimit") != null
                ? Integer.parseInt(topLevelJson.get("downPollingLimit").toString()) : null);
        node.getConfiguration().setHost(topLevelJson.get("host") != null
                ? topLevelJson.get("host").getAsString() : null);
        node.getConfiguration().setUrl(topLevelJson.get("url") != null ? topLevelJson.get("url").getAsString() : null);
        node.getConfiguration().setAppiumStartCommand(topLevelJson.get("appiumStartCommand") != null
                ? topLevelJson.get("appiumStartCommand").getAsString() : null);
        node.setLoadedFromFile(filename);
        node.writeToFile(filename);
        
        return node;
      }
    }

    // Only called if going from Selenium 3 to Selenium 3, or Selenium 2 to Selenium 2
    GridNode node = new GridNode(filteredCapabilities, nodeConfiguration, hubPort, hubHost, nodePort);
    node.setLoadedFromFile(filename);

    return node;
  }

  public String getLoadedFromFile() {
    return this.loadedFromFile;
  }

  public void setLoadedFromFile(String file) {
    this.loadedFromFile = file;
  }

  // Selenium 3 requires these at the root
  public int getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public int getHubPort() {
    return hubPort;
  }

  public void setHubPort(Integer hubPort) {
    this.hubPort = hubPort;
  }

  public String getHubHost() {
    return hubHost;
  }

  public void setHubHost(String hubHost) {
    this.hubHost = hubHost;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }
  
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public int getMaxSession() {
    return this.maxSession;
  }

  public void setMaxSession(Integer maxSession) {
    this.maxSession = maxSession;
  }

  public boolean getRegister() {
    return register;
  }
  
  public void setRegister(Boolean register) {
    this.register = register;
  }

  public int getRegisterCycle() {
    return registerCycle;
  }
  
  public void setRegisterCycle(Integer registerCycle) {
    this.registerCycle = registerCycle;
  }

  public String getProxy() {
    return proxy;
  }

  public void setProxy(String proxy) {
    this.proxy = proxy;
  }

  public int getNodeStatusCheckTimeout() {
    return nodeStatusCheckTimeout;
  }
  
  public void setNodeStatusCheckTimeout(Integer nodeStatusCheckTimeout) {
    this.nodeStatusCheckTimeout = nodeStatusCheckTimeout;
  }
  
  public int getUnregisterIfStillDownAfter() {
    return unregisterIfStillDownAfter;
  }
  
  public void setUnregisterIfStillDownAfter(Integer unregisterIfStillDownAfter) {
    this.unregisterIfStillDownAfter = unregisterIfStillDownAfter;
  }

  public void setDownPollingLimit(Integer downPollingLimit) {
    this.downPollingLimit = downPollingLimit;
  }
  
  public String getAppiumStartCommand() {
    return appiumStartCommand;
  }

  public void setAppiumStartCommand(String appiumStartCommand) {
    this.appiumStartCommand = appiumStartCommand;
  }


  public LinkedList<Capability> getCapabilities() {
    return capabilities;
  }

  public void setCapabilities(LinkedList<Capability> caps) {
    this.capabilities = caps;
  }

  public GridNodeConfiguration getConfiguration() {
    return configuration;
  }

  public boolean isAppiumNode() {
    return getLoadedFromFile().startsWith("appium");
  }

  public void writeToFile(String filename) {

    try {
      File f = new File(filename);
      String config = this.toPrettyJsonString();
      FileUtils.writeStringToFile(f, config);
    } catch (Exception e) {
      logger.fatal("Could not write node config for '" + filename + "' with following error");
      logger.fatal(e.toString());
      System.exit(1);
    }


  }

  private String toPrettyJsonString() {
    return JsonParserWrapper.prettyPrintString(this);
  }


  protected static String readConfigFile(String filePath) {
    String returnString = "";
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(filePath));
      String line = null;
      while ((line = reader.readLine()) != null) {
        returnString = returnString + line;
      }
    } catch (FileNotFoundException error) {
      String e = String.format(
          "Error loading config from %s, %s, Will have to exit. \n%s",
          filePath,
          error.getMessage(),
          Throwables.getStackTraceAsString(error));
      System.out.println(e);
      logger.error(e);

      System.exit(1);
    } catch (IOException error) {
      String e = String.format(
          "Error loading config from %s, %s, Will have to exit. \n%s",
          filePath,
          error.getMessage(),
          Throwables.getStackTraceAsString(error));
      System.out.println(e);
      logger.error(e);

      System.exit(1);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ex) {
          System.out.println("Error closing buffered reader");
          logger.warn("Error closing buffered reader");
        }
      }
    }
    return returnString;
  }


  //<Grumble Grumble>, google parsing Gson, Grumble
  protected static Map doubleToIntConverter(Map input) {
    for (Object key : input.keySet()) {

      if (input.get(key) instanceof Double) {
        input.put(key, ((Double) input.get(key)).intValue());
      }
    }

    return input;
  }

  public static Map linkedTreeMapToHashMap(LinkedTreeMap input) {
    Map output = new HashMap();
    output.putAll(input);

    return output;
  }

  //</Grubmle>


  public class GridNodeConfiguration {

    private String proxy = "com.groupon.seleniumgridextras.grid.proxies.SetupTeardownProxy";
    private int maxSession = 3;
    private int port;
    private boolean register = true;
    private int unregisterIfStillDownAfter = 10000;
    private int hubPort;
    private String hubHost;
    private String host;
    private String url;
    private Integer registerCycle = 5000;
    private int nodeStatusCheckTimeout = 10000;
    private String appiumStartCommand;

    //Only test the node status 1 time, since the limit checker is
    //Since DefaultRemoteProxy.java does this check failedPollingTries >= downPollingLimit
    private int downPollingLimit = 0;


    //java -jar 2.41.0.jar -role node -hub http://192.168.168.17:4444 -maxSession 3 -register true -unregisterIfStillDownAfter 20000 -browserTimeout 120 -timeout 120 -port

    public int getMaxSession() {
      return this.maxSession;
    }

    public void setMaxSession(int maxSession) {
      this.maxSession = maxSession;
    }

    public int getPort() {
      return port;
    }

    public void setPort(int port) {
      this.port = port;
    }

    public int getHubPort() {
      return hubPort;
    }

    public void setHubPort(int hubPort) {
      this.hubPort = hubPort;
    }

    public String getHubHost() {
      return hubHost;
    }

    public void setHubHost(String hubHost) {
      this.hubHost = hubHost;
    }

    public String getHost() {
      return host;
    }

    public void setHost(String host) {
      this.host = host;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public String getProxy() {
      return proxy;
    }

    public void setProxy(String proxy) {
      this.proxy = proxy;
    }

    public boolean getRegister() {
      return register;
    }
    
    public void setRegister(boolean register) {
      this.register = register;
    }
    
    public int getUnregisterIfStillDownAfter() {
      return unregisterIfStillDownAfter;
    }
    
    public void setUnregisterIfStillDownAfter(int unregisterIfStillDownAfter) {
      this.unregisterIfStillDownAfter = unregisterIfStillDownAfter;
    }
    
    public int getNodeStatusCheckTimeout() {
      return nodeStatusCheckTimeout;
    }
    
    public void setNodeStatusCheckTimeout(int nodeStatusCheckTimeout) {
      this.nodeStatusCheckTimeout = nodeStatusCheckTimeout;
    }
    
    public int getRegisterCycle() {
      return registerCycle.intValue();
    }

    public void setRegisterCycle(int registerCycle) {
      this.registerCycle = new Integer(registerCycle);
    }
    
    public int getDownPollingLimit() {
      return downPollingLimit;
    }
    
    public void setDownPollingLimit(int downPollingLimit) {
      this.downPollingLimit = downPollingLimit;
    }

    public String getAppiumStartCommand() {
      return appiumStartCommand;
    }

    public void setAppiumStartCommand(String appiumStartCommand) {
      this.appiumStartCommand = appiumStartCommand;
    }
  }

}


