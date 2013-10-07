package com.groupon.seleniumgridextras.config;

import com.google.gson.annotations.SerializedName;

public class Hub implements GridRole {

  @SerializedName("-port")
  private String port;
  @SerializedName("-host")
  private String host;
  @SerializedName("-role")
  private String role;
  @SerializedName("-servlets")
  private String servlets;

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
    return "-role " + role + " -port " + port + " -host " + host + " -servlets " + servlets;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getServlets() {
    return servlets;
  }

  public void setServlets(String servlets) {
    this.servlets = servlets;
  }
}

