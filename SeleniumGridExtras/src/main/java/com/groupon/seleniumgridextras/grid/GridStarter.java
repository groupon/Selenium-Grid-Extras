package com.groupon.seleniumgridextras.grid;


import com.google.gson.JsonObject;

import com.groupon.seleniumgridextras.ExecuteCommand;
import com.groupon.seleniumgridextras.OSChecker;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

import org.apache.commons.io.FileUtils;

import java.io.File;

public class GridStarter {


  public static JsonObject startHub() {
    Boolean isWindows = OSChecker.isWindows();
    String backgroundCommand = buildBackgroundStartCommand(getOsSpecificHubStartCommand(isWindows), isWindows);
    return ExecuteCommand.execRuntime(backgroundCommand);
  }


  public static JsonObject startNodes() {
    return null;
  }

  protected static String buildBackgroundStartCommand(String command, Boolean windows) {
    String backgroundCommand;
    final String gridLogFile = "grid_hub.log";
    final String batchFile = "start_hub.bat";

    if (windows) {
      writeBatchFile(batchFile, command);
      backgroundCommand =
          "powershell.exe /c \"Start-Process " + batchFile + "\" | Out-File " + gridLogFile;
    } else {
      backgroundCommand = command + " & 2>&1 > " + gridLogFile;
    }

    return backgroundCommand;
  }

  protected static String getOsSpecificHubStartCommand(Boolean windows) {
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
