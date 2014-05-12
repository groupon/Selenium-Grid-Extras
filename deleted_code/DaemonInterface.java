package com.groupon.seleniumgridextras.daemons;

public interface DaemonInterface {

  public void installDaemon();

  public void uninstallDaemon();

  public void setLogDirectory(String path);

  public void setJavaExecutable(String path);

  public void setWorkingDirectory(String path);

  public void setJarPath(String path);

  public void setDaemonName(String name);

  public void setCheckInterval(int minutes);

  public void setAutoInstallDaemon(String trueOrFalse);

  public boolean getAutoInstallDaemon();

}
