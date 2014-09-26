package com.groupon.seleniumgridextras.config;


import com.groupon.seleniumgridextras.utilities.json.JsonCodec;

import java.util.HashMap;

public class Hub extends HashMap<String, String> implements GridRole {

  @Override
  public String getPort() {
    return this.get(JsonCodec.WebDriver.Grid.PORT);
  }

  public void setPort(String port) {
    this.put(JsonCodec.WebDriver.Grid.PORT, port);
  }

  @Override
  public String getHost() {
    return this.get(JsonCodec.WebDriver.Grid.HOST);
  }

  public void setHost(String host) {
    this.put(JsonCodec.WebDriver.Grid.HOST,  host);
  }

  public String getRole() {
    return this.get(JsonCodec.WebDriver.Grid.ROLE);
  }

  public void setRole(String role) {
    this.put(JsonCodec.WebDriver.Grid.ROLE, role);
  }

  public String getServlets() {
    return this.get(JsonCodec.WebDriver.Grid.SERVLETS);
  }

  public void setServlets(String servlets) {
    this.put(JsonCodec.WebDriver.Grid.SERVLETS,  servlets);
  }

  @Override
  public String getStartCommand() {
    return "-role " + getRole() + " -port " + getPort() + " -servlets " + getServlets();
  }
}

