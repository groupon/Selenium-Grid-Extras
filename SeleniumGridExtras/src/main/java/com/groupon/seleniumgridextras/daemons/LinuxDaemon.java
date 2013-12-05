package com.groupon.seleniumgridextras.daemons;


import com.groupon.seleniumgridextras.ExecuteCommand;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

import org.apache.log4j.Logger;

import java.io.File;

public class LinuxDaemon extends DaemonWrapper {

  private static Logger logger = Logger.getLogger(LinuxDaemon.class);

  @Override
  public void installDaemon() {
    logger.info("Install called, will uninstall previous version");
    uninstallDaemon();
    logger.info("Installing new version of daemon");
    logger.info(ExecuteCommand.execRuntime(getInstallCommand()));
  }

  @Override
  public void uninstallDaemon() {
    logger.info("Uninstall command called");
    logger.info(ExecuteCommand.execRuntime(getUninstallCommand()));
  }

  protected String getUninstallCommand(){
    final String command = "crontab -l | sed \"/" + getCronOutputFile().getName() + "/d\" | crontab";
    logger.info("Uninstall command - " + command);
    return command;
  }

  protected File getCronOutputFile() {
    return new File(getLogDirectory()
           + RuntimeConfig.getOS().getFileSeparator()
           + getDaemonName() + ".out");
  }

  protected String getInstallCommand(){
    final
    String
        command =
        "crontab -l | awk \"{print} END {print \\\"" + getCronJob() + "\\\"}\" | crontab";
    logger.info("Install Command - " + command);
    return command;
  }

  protected String getCronJob() {

    String command = "0-59/" + getCheckInterval() + " * * * *";

    command = command + " bash -i -c 'cd " + getWorkingDirectory() + "; ";
    command = command + getJavaExecutable() + RuntimeConfig.getOS().getFileSeparator() + "java -jar ";
    command = command + getJarPath() + " 2>&1 >> ";
    command = command + getCronOutputFile().getAbsolutePath() + "\'";

    return command;
  }

}
