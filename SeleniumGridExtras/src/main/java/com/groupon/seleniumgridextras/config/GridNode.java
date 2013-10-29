package com.groupon.seleniumgridextras.config;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import com.groupon.seleniumgridextras.config.capabilities.Capability;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

public class GridNode {

  @SerializedName("capabilities")
  private LinkedList<Capability> capabilities;
  @SerializedName("configuration")
  private GridNodeConfiguration configuration;


  public GridNode(){
    capabilities  = new LinkedList<Capability>();
    configuration = new GridNodeConfiguration();
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

  public String getStartCommand() {
    return "";
  }


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


