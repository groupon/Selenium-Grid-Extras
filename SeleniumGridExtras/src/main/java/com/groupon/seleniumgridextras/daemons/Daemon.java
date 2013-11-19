package com.groupon.seleniumgridextras.daemons;

public interface Daemon {

  public Boolean startDaemon();

  public Boolean stopDaemon();

  public void setExecutable(String executable);

  public void setLogDir(String dir);

  public String getInitDFilePath();

  public void setDaemonName(String name);

  public void setWorkingDir(String dir);

  public void setJava(String java);

}
