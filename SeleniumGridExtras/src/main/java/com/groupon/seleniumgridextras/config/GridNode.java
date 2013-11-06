package com.groupon.seleniumgridextras.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.StringMap;

import com.groupon.seleniumgridextras.config.capabilities.Capability;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GridNode {

  private LinkedList<Capability> capabilities;
  private GridNodeConfiguration configuration;


  public GridNode() {
    capabilities = new LinkedList<Capability>();
    configuration = new GridNodeConfiguration();
  }

  private GridNode(LinkedList<Capability> caps, GridNodeConfiguration config) {
    capabilities = caps;
    configuration = config;
  }

  public static GridNode loadFromFile(String filename) {

    String configString = readConfigFile(filename);
    Map topLevelHash = getMapFromString(configString);

    String configFromFile = topLevelHash.get("configuration").toString();

    GridNodeConfiguration
        nodeConfiguration =
        new Gson().fromJson(configFromFile, GridNodeConfiguration.class);

    List<StringMap> capabilitiesFromFile = (ArrayList<StringMap>) topLevelHash.get("capabilities");

    LinkedList<Capability> filteredCapabilities = new LinkedList<Capability>();

    for (StringMap cap : capabilitiesFromFile){
      if (cap.containsKey("browserName")){
        filteredCapabilities.add(Capability.getCapabilityFor((String) cap.get("browserName")));
      }

    }

    return new GridNode(filteredCapabilities, nodeConfiguration);
  }


  public LinkedList<Capability> getCapabilities() {
    return capabilities;
  }

  public GridNodeConfiguration getConfiguration() {
    return configuration;
  }

  public void writeToFile(String filename) {

    try {
      File f = new File(filename);
      String config = this.toPrettyJsonString();
      FileUtils.writeStringToFile(f, config);
    } catch (Exception e) {
      System.out
          .println("Could not write node config for '" + filename + "' with following error");
      e.printStackTrace();
      System.exit(1);
    }


  }

  private String toPrettyJsonString() {
    return new GsonBuilder().setPrettyPrinting().create().toJson(this);
  }


  protected static String readConfigFile(String filePath) {
    String returnString = "";
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filePath));
      String line = null;
      while ((line = reader.readLine()) != null) {
        returnString = returnString + line;
      }
    } catch (FileNotFoundException error) {
      error.printStackTrace();
      System.exit(1);
    } catch (IOException error) {
      error.printStackTrace();
      System.exit(1);
    }
    return returnString;
  }

  public static Map getMapFromString(String input) {
    return new Gson().fromJson(input, HashMap.class);
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

  public static Map stringMapToHashMap(StringMap input) {
    Map output = new HashMap();
    output.putAll(input);

    return output;
  }

  //</Grubmle>


  public class GridNodeConfiguration {

    private String proxy = "com.groupon.seleniumgridextras.grid.proxies.SetupTeardownProxy";
    private int maxSession = 1;
    private int port;
    private String host;
    private boolean register = true;
    private int registerCycle = 2000;
    private int hubPort;
    private String hubHost;


    public int getPort() {
      return port;
    }

    public void setPort(int port) {
      this.port = port;
    }

    public String getHost() {
      return host;
    }

    public void setHost(String host) {
      this.host = host;
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

  }

}


