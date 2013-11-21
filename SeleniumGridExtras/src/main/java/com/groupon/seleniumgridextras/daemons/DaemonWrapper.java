package com.groupon.seleniumgridextras.daemons;

import com.groupon.seleniumgridextras.config.RuntimeConfig;

import java.util.HashMap;

public class DaemonWrapper extends HashMap<String, String> implements DaemonInterface {

  protected DaemonInterface daemon;

  protected String workingDir;
  protected String javaExecutable;
  protected String logDirectory;
  protected String jarPath;
  protected String daemonName;
  protected int interval;

//  return RuntimeConfig.getSeleniungGridExtrasHomePath();
//
//  return System.getProperty("java.home") + "/java";
//  return RuntimeConfig.getSeleniumGridExtrasJarFile().getAbsolutePath();
//  return RuntimeConfig.getSeleniungGridExtrasHomePath() + RuntimeConfig.getConfig().getSharedDirectory();

  public DaemonWrapper(){

  }

  @Override
  public void installDaemon() {
    daemon.installDaemon();
  }

  @Override
  public void uninstallDaemon() {
    daemon.uninstallDaemon();
  }


  @Override
  public void setLogDirectory(String path) {
    this.logDirectory = path;
  }

  @Override
  public void setJavaExecutable(String path) {
    this.javaExecutable = path;
  }

  protected String getJavaExecutable() {
    return this.javaExecutable;
  }

  @Override
  public void setJarPath(String path) {
    this.jarPath = path;
  }

  @Override
  public void setDaemonName(String name) {
    this.daemonName = name;
  }

  @Override
  public void setWorkingDirectory(String path) {
    this.workingDir = path;
  }

  @Override
  public void setCheckInterval(int minutes) {
    this.interval = minutes;
  }

  protected int getCheckInterval(){
    return this.interval;
  }

  protected String getJarPath() {
    return this.jarPath;
  }


  protected String getDaemonName() {
    return daemonName;
  }

  protected String getWorkingDirectory() {
    return this.workingDir;
  }

  protected String getLogDirectory() {
    return this.logDirectory;
  }
}
