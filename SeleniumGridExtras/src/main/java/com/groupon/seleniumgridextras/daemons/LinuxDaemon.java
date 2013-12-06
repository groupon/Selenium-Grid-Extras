package com.groupon.seleniumgridextras.daemons;


import com.groupon.seleniumgridextras.ExecuteCommand;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;

public class LinuxDaemon extends DaemonWrapper {

  private static Logger logger = Logger.getLogger(LinuxDaemon.class);
  private final String installDaemonScript = "install_daemon.sh";
  private final String unInstallDaemonScript = "un_install_daemon.sh";

  @Override
  public void installDaemon() {
    logger.info("Install called, will uninstall previous version");
    uninstallDaemon();

    File f = new File(installDaemonScript);

    if (f.exists()){
      logger.info("Deleting existing install script");
    }

    try {
      logger.info("Attemptiong to write " + f.getAbsolutePath());
      FileUtils.writeStringToFile(f.getAbsoluteFile(), getInstallCommand());
      f.setExecutable(true, false);
      f.setReadable(true, false);
    } catch (Exception error){
      logger.fatal("Could not write install script");
      logger.fatal(error.toString());
      System.exit(1);
    }


    logger.info("Installing new version of daemon");
    logger.info(ExecuteCommand.execRuntime("bash -i " + f.getAbsolutePath()));
  }

  @Override
  public void uninstallDaemon() {
    logger.info("Uninstall command called");

    File f = new File(unInstallDaemonScript);

    if (f.exists()){
      logger.info("Deleting existing uninstall script");
    }

    try {
      logger.info("Attemptiong to write " + f.getAbsolutePath());
      FileUtils.writeStringToFile(f.getAbsoluteFile(), getUninstallCommand());
      f.setExecutable(true, false);
      f.setReadable(true, false);
    } catch (Exception error){
      logger.fatal("Could not write uninstall script");
      logger.fatal(error.toString());
      System.exit(1);
    }

    logger.info(ExecuteCommand.execRuntime("bash -i " + f.getAbsolutePath()));
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
    command = command + getJavaExecutable() + " -jar ";
    command = command + getJarPath() + " 2>&1 >> ";
    command = command + getCronOutputFile().getAbsolutePath() + "\'";

    return command;
  }


}
