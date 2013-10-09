package com.groupon.seleniumgridextras.config;

public class GridInfo {

  private int auto_start_hub;
  private int auto_start_node;
  private String default_role;
  private NodeConfig node;
  private Hub hub;

  public GridInfo() {
    node = new NodeConfig();
    hub = new Hub();
  }

  public boolean getAutoStartHub() {
    return auto_start_hub == 1 ? true : false;
  }

  public void setAutoStartHub(int autoStartHub) {
    this.auto_start_hub = autoStartHub;
  }

  public boolean getAutoStartNode() {
    return auto_start_node == 1 ? true : false;
  }

  public void setAutoStartNode(int autoStartNode) {
    this.auto_start_node = autoStartNode;
  }

  public String getDefaultRole() {
    return default_role;
  }

  public void setDefaultRole(String defaultRole) {
    this.default_role = defaultRole;
  }

  public NodeConfig getNode() {
    return node;
  }

  public Hub getHub() {
    return hub;
  }

}