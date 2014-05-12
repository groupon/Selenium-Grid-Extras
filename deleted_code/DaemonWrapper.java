package com.groupon.seleniumgridextras.daemons;

import com.groupon.seleniumgridextras.config.RuntimeConfig;

import java.util.HashMap;

public class DaemonWrapper extends HashMap<String, String> implements DaemonInterface {

  protected DaemonInterface daemon;

  protected String WORKING_DIRECTORY = "workingDirectory";
  protected String JAVA = "javaExecutable";
  protected String LOG_DIRECTORY = "logDirectory";
  protected String JAR_PATH = "jarPath";
  protected String DAEMON_NAME = "daemonName";
  protected String INTERVAL = "interval";
  private static final String AUTO_INSTALL = "auto_install";


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
    this.put(LOG_DIRECTORY, path);
  }

  @Override
  public void setJavaExecutable(String path) {
    this.put(JAVA, path);
  }

  protected String getJavaExecutable() {
    return this.get(JAVA);
  }

  @Override
  public void setJarPath(String path) {
    this.put(JAR_PATH, path);
  }

  @Override
  public void setDaemonName(String name) {
    this.put(DAEMON_NAME, name);
  }

  @Override
  public void setWorkingDirectory(String path) {
    this.put(WORKING_DIRECTORY,  path);
  }

  @Override
  public void setCheckInterval(int minutes) {
    setCheckInterval(String.valueOf(minutes));
  }

  public void setCheckInterval(String minutes) {
    this.put(INTERVAL, minutes);
  }

  @Override
  public void setAutoInstallDaemon(String trueOrFalse) {
    this.put(AUTO_INSTALL, trueOrFalse);
  }

  @Override
  public boolean getAutoInstallDaemon() {
    if(this.get(AUTO_INSTALL).equals("1")){
      return true;
    } else {
      return false;
    }

  }

  protected int getCheckInterval(){
    return Integer.parseInt(this.get(INTERVAL));
  }

  protected String getJarPath() {
    return this.get(JAR_PATH);
  }


  protected String getDaemonName() {
    return this.get(DAEMON_NAME);
  }

  protected String getWorkingDirectory() {
    return this.get(WORKING_DIRECTORY);
  }

  protected String getLogDirectory() {
    return this.get(LOG_DIRECTORY);
  }

  public static DaemonWrapper getNewInstance() {

    if (RuntimeConfig.getOS().isMac()){
      return new OsXDaemon();
    }  else if (RuntimeConfig.getOS().isWindows()) {
      return new WindowsDaemon();
    } else {
      return new LinuxDaemon();
    }

  }
}
