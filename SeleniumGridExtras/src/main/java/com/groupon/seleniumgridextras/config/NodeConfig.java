package com.groupon.seleniumgridextras.config;

import com.google.gson.annotations.SerializedName;

public class NodeConfig implements GridRole {

  @SerializedName("-port")
  private String port;
  @SerializedName("-hub")
  private String hub;
  @SerializedName("-host")
  private String host;
  @SerializedName("-role")
  private String role;
  @SerializedName("-nodeTimeout")
  private String nodeTimeout;
  @SerializedName("-maxSession")
  private int maxSession;
  @SerializedName("-proxy")
  private String proxy;
  @SerializedName("-Dwebdriver.ie.driver")
  private String ieDriver;

  public void setIeDriver(String ieDriverPath){
    this.ieDriver = ieDriverPath;
  }

  @Override
  public String getPort() {
    return port;
  }

  public void setPort(String port) {
    this.port = port;
  }

  @Override
  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  @Override
  public String getStartCommand() {
    String command = "-role " + role + " -port " + port + " -host " + host + " -hub " + hub
                     + " -nodeTimeout " + nodeTimeout + " -maxSession " + maxSession + " -proxy " + proxy;

    if(ieDriver != null){
      command = command + " -Dwebdriver.ie.driver=" + ieDriver;
    }

    return command;
  }

  public String getHub() {
    return hub;
  }

  public void setHub(String hub) {
    this.hub = hub;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getNodeTimeout() {
    return nodeTimeout;
  }

  public void setNodeTimeout(String nodeTimeout) {
    this.nodeTimeout = nodeTimeout;
  }

  public int getMaxSession() {
    return maxSession;
  }

  public void setMaxSession(int maxSession) {
    this.maxSession = maxSession;
  }

  public String getProxy() {
    return proxy;
  }

  public void setProxy(String proxy) {
    this.proxy = proxy;
  }
}
