package com.groupon.seleniumgridextras.grid;


import com.google.gson.JsonObject;

import com.groupon.seleniumgridextras.ExecuteCommand;
import com.groupon.seleniumgridextras.OSChecker;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class GridStarter {

  public static String getOsSpecificHubStartCommand(Boolean windows) {
    String colon = windows ? ";" : ":";

    StringBuilder command = new StringBuilder();
    command.append("java -cp ");
    command.append(getGridExtrasJarFilePath());

    String jarPath = colon + getCurrentWebDriverJarPath() + " ";

    if (windows) {
      jarPath = OSChecker.toWindowsPath(jarPath);
    }

    command.append(jarPath);
    command.append(" org.openqa.grid.selenium.GridLauncher ");
    command.append(RuntimeConfig.getConfig().getHub().getStartCommand());

    return String.valueOf(command);
  }

  public static List<String> getStartCommandsForNodes(Boolean windows) {
    List<String> commands = new LinkedList<String>();

    for (String configFile : RuntimeConfig.getConfig().getNodeConfigFiles()) {

      String
          backgroundCommand =
          getBackgroundStartCommandForNode(getNodeStartCommand(configFile, windows),
                                           configFile.replace("json", "log"),
                                           windows);

      commands.add(backgroundCommand);
    }

    return commands;
  }

  protected static String getBackgroundStartCommandForNode(String command, String logFile,
                                                           Boolean windows) {

    String
        logFileFullPath =
        RuntimeConfig.getConfig().getExposedDirectory() + (windows ? "\\" : "/")
        + logFile;

    if (windows) {
      String batchFile = logFile.replace("log", "bat");
      writeBatchFile(batchFile, command);
      return "start " + batchFile;
    } else {
      return command;
    }

  }

  protected static String getNodeStartCommand(String configFile, Boolean windows) {
    return "java" + getIEDriverExecutionPathParam() +
           getChromeDriverExecutionPathParam() +
           " -cp " + getGridExtrasJarFilePath()
           + (windows ? ";" : ":") + getCurrentWebDriverJarPath()
           + " org.openqa.grid.selenium.GridLauncher -role node -nodeConfig "
           + configFile;
  }

  protected static String getIEDriverExecutionPathParam() {
    if (OSChecker.isWindows()) {
      return " -Dwebdriver.ie.driver=" + RuntimeConfig.getConfig().getIEdriver()
          .getExecutablePath();
    } else {
      return "";
    }
  }

  protected static String getChromeDriverExecutionPathParam() {
    return " -Dwebdriver.chrome.driver=" + RuntimeConfig.getConfig().getChromeDriver()
        .getExecutablePath();
  }

  protected static String buildBackgroundStartCommand(String command, Boolean windows) {
    String backgroundCommand;
    final String gridLogFile = "grid_hub.log";
    final String batchFile = "start_hub.bat";

    if (windows) {
      writeBatchFile(batchFile, command);
      backgroundCommand =
          "start " + batchFile;
    } else {
      backgroundCommand = command;
    }

    return backgroundCommand;
  }

  protected static String getGridExtrasJarFilePath() {
    return RuntimeConfig.getSeleniumGridExtrasJarFile();
  }

  protected static String getCurrentWebDriverJarPath() {
    return getWebdriverHome() + "/" + getWebdriverVersion() + ".jar";
  }

  protected static String getWebdriverVersion() {
    return RuntimeConfig.getConfig().getWebdriver().getVersion();
  }


  protected static String getWebdriverHome() {
    return RuntimeConfig.getConfig().getWebdriver().getDirectory();
  }

  private static void writeBatchFile(String filename, String input) {

    File file = new File(filename);

    try {
      FileUtils.writeStringToFile(file, input);
    } catch (Exception error) {
      System.out
          .println("Could not write default config file, exit with error " + error.toString());

    }
  }


}
