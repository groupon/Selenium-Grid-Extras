package com.groupon.seleniumgridextras.grid;

import com.google.gson.JsonObject;

import com.groupon.seleniumgridextras.ExecuteCommand;
import com.groupon.seleniumgridextras.JsonResponseBuilder;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class GridStarter {

  private static Logger logger = Logger.getLogger(GridStarter.class);

  public static String getOsSpecificHubStartCommand(Boolean windows) {

    StringBuilder command = new StringBuilder();
    command.append("java -cp ");
    command.append(getGridExtrasJarFilePath());

    String jarPath = RuntimeConfig.getOS().getPathSeparator() + getCurrentWebDriverJarPath() + " ";
    String
        logCommand =
        " -log log" + RuntimeConfig.getOS().getFileSeparator() + "grid_hub.log";

    command.append(jarPath);
    command.append(" org.openqa.grid.selenium.GridLauncher ");
    command.append(RuntimeConfig.getConfig().getHub().getStartCommand());
    command.append(logCommand);

    return String.valueOf(command);
  }


  public static JsonObject startAllNodes(JsonResponseBuilder jsonResponseBuilder){
    for (String command : getStartCommandsForNodes(RuntimeConfig.getOS().isWindows())) {
      try {

        JsonObject startResponse = startOneNode(command);

        if (!startResponse.get("exit_code").toString().equals("0")) {
          jsonResponseBuilder
              .addKeyValues("error", "Error running " + startResponse.get("error").toString());
        }
      } catch (Exception e) {
        jsonResponseBuilder
            .addKeyValues("error", "Error running " + command);
        jsonResponseBuilder
            .addKeyValues("error", e.toString());

        e.printStackTrace();
      }

    }

    return jsonResponseBuilder.getJson();
  }

  public static JsonObject startOneNode(String command){
    return ExecuteCommand.execRuntime(command, false);
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
        "log" + RuntimeConfig.getOS().getFileSeparator() + logFile;

    command = command + " -log " + logFileFullPath;

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
           + RuntimeConfig.getOS().getPathSeparator() + getCurrentWebDriverJarPath()
           + " org.openqa.grid.selenium.GridLauncher -role node -nodeConfig "
           + configFile;
  }

  protected static String getIEDriverExecutionPathParam() {
    if (RuntimeConfig.getOS().isWindows()) {
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
    return RuntimeConfig.getSeleniumGridExtrasJarFile().getAbsolutePath();
  }

  protected static String getCurrentWebDriverJarPath() {
    return getWebdriverHome() + RuntimeConfig.getOS().getFileSeparator() + getWebdriverVersion() + ".jar";
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
      logger.fatal("Could not write default config file, exit with error " + error.toString());
      System.exit(1);
    }
  }


}
