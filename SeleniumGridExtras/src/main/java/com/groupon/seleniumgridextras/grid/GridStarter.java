package com.groupon.seleniumgridextras.grid;

import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.ExecuteCommand;
import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.GridNode;
import com.groupon.seleniumgridextras.config.GridNode.GridNodeConfiguration;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import com.groupon.seleniumgridextras.utilities.json.JsonResponseBuilder;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class GridStarter {

  private static Logger logger = Logger.getLogger(GridStarter.class);

  public static String[] getOsSpecificHubStartCommand(String configFile, Boolean windows) {

    List<String> command = new ArrayList<String>();
    command.add(getJavaExe());
    if(RuntimeConfig.getConfig().getGridJvmXOptions() != "") {
      List<String> options = new ArrayList<String>(Arrays.asList(RuntimeConfig.getConfig().getGridJvmXOptions().split(" ")));
      for(String option : options) {
        command.add(option);
      }
    }
    if(RuntimeConfig.getConfig().getGridJvmOptions() != "") {
      List<String> options = new ArrayList<String>(Arrays.asList(RuntimeConfig.getConfig().getGridJvmOptions().split(" ")));
      for(String option : options) {
        command.add(option);
      }
    }
    String cp = getGridExtrasJarFilePath();

    List<String> additionalClassPathItems = RuntimeConfig.getConfig().getAdditionalHubConfig();
    for(String additionalJarPath : additionalClassPathItems) {
      cp += RuntimeConfig.getOS().getPathSeparator() + additionalJarPath;
    }

    String jarPath = RuntimeConfig.getOS().getPathSeparator() + getCurrentWebDriverJarPath(RuntimeConfig.getConfig());
    cp += jarPath;
    command.add("-cp");
    command.add(cp);
    String classPath = getWebdriverVersion(RuntimeConfig.getConfig()).startsWith("3.") ? "org.openqa.grid.selenium.GridLauncherV3" : "org.openqa.grid.selenium.GridLauncher";
    command.add(classPath);
    command.add("-role");
    command.add("hub");

    String logFile = configFile.replace("json", "log");

    command.add("-log");
    command.add("log" + RuntimeConfig.getOS().getFileSeparator() + logFile);
    command.add("-hubConfig");
    command.add(configFile);

    logger.info("Hub Start Command: \n\n" + Arrays.toString(command.toArray(new String[0])));
    return command.toArray(new String[0]);
  }
  
  public static JsonObject startAllNodes(JsonResponseBuilder jsonResponseBuilder) {
    for (List<String> command : getStartCommandsForNodes(
        RuntimeConfig.getOS().isWindows(), 
        RuntimeConfig.getConfig())) {
      logger.info(command);
      try {

        JsonObject startResponse = startOneNode(command);
        logger.info(startResponse);

        if (!startResponse.get(JsonCodec.EXIT_CODE).toString().equals("0")) {
          jsonResponseBuilder
          .addKeyValues(JsonCodec
              .ERROR,
              "Error running " + startResponse.get(JsonCodec.ERROR).toString());
        }
      } catch (Exception e) {
        jsonResponseBuilder
        .addKeyValues(JsonCodec.ERROR, "Error running " + command);
        jsonResponseBuilder
        .addKeyValues(JsonCodec.ERROR, e.toString());

        e.printStackTrace();
      }

    }

    return jsonResponseBuilder.getJson();
  }
  
  public static JsonObject startAllHubs(JsonResponseBuilder jsonResponseBuilder) {
    for (String configFile : RuntimeConfig.getConfig().getHubConfigFiles()) {
      String command[] = getOsSpecificHubStartCommand(configFile, RuntimeConfig.getOS().isWindows());
      logger.info(command);

      try {
        JsonObject startResponse = ExecuteCommand.execRuntime(command, false);
        logger.info(startResponse);

        if (!startResponse.get(JsonCodec.EXIT_CODE).toString().equals("0")) {
          jsonResponseBuilder
          .addKeyValues(JsonCodec.ERROR, "Error running " + startResponse.get(JsonCodec.ERROR).toString());
        }
      } catch (Exception e) {
        jsonResponseBuilder
        .addKeyValues(JsonCodec.ERROR, "Error running " + command);
        jsonResponseBuilder
        .addKeyValues(JsonCodec.ERROR, e.toString());

        e.printStackTrace();
      }
    }
    return jsonResponseBuilder.getJson();
  }

  public static JsonObject startOneNode(List<String> command) {
    logger.info("Hub Start Command: \n\n" + Arrays.toString(command.toArray(new String[0])));
    
    return ExecuteCommand.execRuntime(command.toArray(new String[0]), false);
  }

  public static List<List<String>> getStartCommandsForNodes(Boolean isWindows, Config config) {
    List<List<String>> commands = new LinkedList<List<String>>();
    List<String> configFiles = RuntimeConfig.getConfig().getNodeConfigFiles();

    for (String configFile : configFiles) {

      List<String>
      backgroundCommand =
      getBackgroundStartCommandForNode(getNodeStartCommand(configFile, isWindows, config),
          configFile.replace("json", "log"),
          isWindows);

      commands.add(backgroundCommand);
    }

    logger.info("Node Start Command: \n\n" + String.valueOf(commands));
    return commands;
  }

  protected static List<String> getBackgroundStartCommandForWebNode(List<String> command, String logFile) {
    String logFileFullPath = "log" + RuntimeConfig.getOS().getFileSeparator() + logFile;
    command.add("-log");
    command.add(logFileFullPath);
    return command;
  }

  protected static List<String> getBackgroundStartCommandForAppiumNode(List<String> command, String logFile) {
    String workingDirectory = System.getProperty("user.dir");
    String logFileFullPath = workingDirectory + RuntimeConfig.getOS().getFileSeparator() + "log" +
        RuntimeConfig.getOS().getFileSeparator() + logFile;
    command.add("--log");
    command.add(logFileFullPath);
    return command;
  }

  protected static List<String> getBackgroundStartCommandForNode(List<String> command, String logFile,
      Boolean isWindows) {

    if (logFile.startsWith("appium")) {
      command = getBackgroundStartCommandForAppiumNode(command, logFile);
    } else {
      command = getBackgroundStartCommandForWebNode(command, logFile);
    }

    if (isWindows) {
      String batchFile = logFile.replace("log", "bat");
      StringBuilder sb = new StringBuilder();
      for(String part : command) {
        sb.append(part + " ");
      }
      writeBatchFile(batchFile, sb.toString());
      return new ArrayList<String> ( Arrays.asList ( "cmd", "/C", "start" , "/MIN" , batchFile ) );
    } else {
      return command;
    }

  }

  protected static List<String> getWebNodeStartCommand(String configFile, Boolean windows, Config config) {

    List<String> command = new ArrayList<String>();
    command.add(getJavaExe());
    if(config.getGridJvmXOptions() != "") {
      List<String> options = new ArrayList<String>(Arrays.asList(RuntimeConfig.getConfig().getGridJvmXOptions().split(" ")));
      for(String option : options) {
        command.add(option);
      }
    }
    if(config.getGridJvmOptions() != "") {
      List<String> options = new ArrayList<String>(Arrays.asList(RuntimeConfig.getConfig().getGridJvmOptions().split(" ")));
      for(String option : options) {
        command.add(option);
      }
    }
    if (windows) {
      command.add(getIEDriverExecutionPathParam(config));
      command.add(getEdgeDriverExecutionPathParam(config));
    }

    command.add(getChromeDriverExecutionPathParam(config));
    command.add(getGeckoDriverExecutionPathParam(config));
    command.add("-cp");

    String cp = getGridExtrasJarFilePath();

    List<String> additionalClassPathItems = config.getAdditionalNodeConfig();
    for(String additionalJarPath : additionalClassPathItems) {
      cp += RuntimeConfig.getOS().getPathSeparator() + additionalJarPath;
    }
    cp += RuntimeConfig.getOS().getPathSeparator() + getCurrentWebDriverJarPath(config);
    
    command.add(cp);

    String classPath = getWebdriverVersion(config).startsWith("3.") ? "org.openqa.grid.selenium.GridLauncherV3" : "org.openqa.grid.selenium.GridLauncher";
    command.add(classPath);
    command.add("-role");
    command.add("node");
    
    if (RuntimeConfig.getHostIp() != null && RuntimeConfig.getOS().getHostName() == null) {
      command.add("-host");
      command.add(RuntimeConfig.getHostIp());
    } else if ((RuntimeConfig.getOS().getHostName() != null) && 
        !getWebdriverVersion(config).startsWith("3.")) { // Exception in thread "main" com.beust.jcommander.ParameterException: Unknown option: -friendlyHostName
      command.add("-friendlyHostName");
      command.add(RuntimeConfig.getOS().getHostName());
    }
    
    command.add("-nodeConfig");
    command.add(configFile);

    return command;
  }

  protected static List<String> getAppiumNodeStartCommand(String configFile, Config runtimeConfig) {
    List<String> command = new ArrayList<String>();
    if(!getWebdriverVersion(runtimeConfig).startsWith("3.")) {
      GridNodeConfiguration config = GridNode.loadFromFile(configFile, false).getConfiguration();
      for(String appiumCommand : config.getAppiumStartCommand().split(" ")) {
        command.add(appiumCommand);
      }
      command.add("-p");
      command.add(config.getPort() + "");
    } else {
      GridNode node = GridNode.loadFromFile(configFile, true);
      for(String appiumCommand : node.getAppiumStartCommand().split(" ")) {
        command.add(appiumCommand);
      }
      command.add("-p");
      command.add(node.getPort() + "");
    }

    String workingDirectory = System.getProperty("user.dir");
    String configFileFullPath = workingDirectory + RuntimeConfig.getOS().getFileSeparator() + configFile;
    command.add("--log-timestamp");
    command.add("--nodeconfig");
    command.add(configFileFullPath);

    return command;
  }

  protected static List<String> getNodeStartCommand(String configFile, Boolean windows, Config config) {
    if (configFile.startsWith("appium")) {
      return getAppiumNodeStartCommand(configFile, config);
    } else {
      return getWebNodeStartCommand(configFile, windows, config);
    }
  }

  protected static String getIEDriverExecutionPathParam(Config config) {
    if (RuntimeConfig.getOS().isWindows()) { //TODO: Clean this conditional up and test!!!
      return String.format("-Dwebdriver.ie.driver=%s", config.getIEdriver().getExecutablePath());
    } else {
      return "";
    }
  }

  public static String getEdgeDriverExecutionPathParam(Config config) {
    return String.format("-Dwebdriver.edge.driver=\"%s\"", config.getEdgeDriver().getExecutablePath());
  }

  protected static String getChromeDriverExecutionPathParam(Config config) {
    return String.format("-Dwebdriver.chrome.driver=%s", config.getChromeDriver().getExecutablePath());
  }

  protected static String getGeckoDriverExecutionPathParam(Config config) {
    return String.format("-Dwebdriver.gecko.driver=%s", config.getGeckoDriver().getExecutablePath());
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

  protected static String getCurrentWebDriverJarPath(Config config) {
    return config.getWebdriver().getExecutablePath();
  }

  protected static String getWebdriverVersion(Config config) {
    return config.getWebdriver().getVersion();
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

  private static String getJavaExe() {
    if (RuntimeConfig.getOS().isWindows()) {
      return "java";
    } else {
      String javaHome = System.getProperty("java.home");
      File f = new File(javaHome);
      f = new File(f, "bin");
      f = new File(f, "java");
      return f.getAbsolutePath();
    }
  }
}
