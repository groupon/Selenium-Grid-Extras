package com.groupon.seleniumgridextras.config;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class Hub extends HashMap<String, String> implements GridRole {

  public static final String PORT = "port";
  public static final String HOST = "host";
  public static final String ROLE = "role";
  public static final String SERVLETS = "servlets";

  @Override
  public String getPort() {
    return this.get(PORT);
  }

  public void setPort(String port) {
    this.put(PORT, port);
  }

  @Override
  public String getHost() {
    return this.get(HOST);
  }

  public void setHost(String host) {
    this.put(HOST,  host);
  }

  public String getRole() {
    return this.get(ROLE);
  }

  public void setRole(String role) {
    this.put(ROLE, role);
  }

  public String getServlets() {
    return this.get(SERVLETS);
  }

  public void setServlets(String servlets) {
    this.put(SERVLETS,  servlets);
  }

  @Override
  public String getStartCommand() {
    return "-role " + getRole() + " -port " + getPort() + " -host " + getHost() + " -servlets " + getServlets();
  }
}

