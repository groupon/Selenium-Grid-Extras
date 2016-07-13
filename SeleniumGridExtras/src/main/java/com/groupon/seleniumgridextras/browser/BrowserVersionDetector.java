package com.groupon.seleniumgridextras.browser;

import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.ExecuteCommand;
import com.groupon.seleniumgridextras.config.GridNode;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.config.capabilities.Capability;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import java.io.File;
import java.util.List;

public class BrowserVersionDetector {

//  TODO: Dima finish this when you have some free time. Good idea but using classloader is a bad ide
//  TODO: Java is not good with dynamic jar loading in way you want. Instead go with a mini grid with 1
//  TODO: node and do all of the stuff via simple HTTP calls, CURL FTW!

  private static
  org.apache.log4j.Logger
      logger = org.apache.log4j.Logger.getLogger(BrowserVersionDetector.class);

  protected File jarPath;
  protected File ieDriverPath;
  protected File chromeDriverPath;
  protected File geckoDriverPath;
  protected List<GridNode> nodesFromConfigFile;

  public static final String[] chromeMacVersionCommand = {"/Applications/Google Chrome.app/Contents/MacOS/Google Chrome", "--version"};
  
  public BrowserVersionDetector(List<GridNode> nodes) {
    nodesFromConfigFile = nodes;
  }

  public void setJarPath(File jarPath) {
    this.jarPath = jarPath;
  }

  public void setIeDriverPath(File ieDriverPath) {
    this.ieDriverPath = ieDriverPath;
  }

  public void setChromeDriverPath(File chromeDriverPath) {
    this.chromeDriverPath = chromeDriverPath;
  }

  public void setGeckoDriverPath(File geckoDriverPath) {
    this.geckoDriverPath = geckoDriverPath;
  }

  public List<GridNode> updateVersions() {
    setDriverPathsInSystemProperty();
    loadWebdriverJarToClassPath();

    for (GridNode node : this.nodesFromConfigFile) {
      checkNodeCapabilities(node);
    }

    return this.nodesFromConfigFile;
  }


  protected void checkNodeCapabilities(GridNode node) {
    for (Capability cap : node.getCapabilities()) {
      checkCapability(cap);
    }
  }

  protected void checkCapability(Capability cap) {

  }

  protected void loadWebdriverJarToClassPath() {

//    System.out.println(URLClassLoader.);
//    try {
//      URLClassLoader
//          child =
//          new URLClassLoader(new URL[]{this.jarPath.toURL()}, this.getClass().getClassLoader());
//    } catch (MalformedURLException e) {
//      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//    }
//    try {
//      Object driver =  Class.forName("org.openqa.selenium.firefox.FirefoxDriver").newInstance();
//
//
//    } catch (InstantiationException e) {
//      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//    } catch (IllegalAccessException e) {
//      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//    } catch (ClassNotFoundException e) {
//      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//    }
//    Method method = classToLoad.getDeclaredMethod ("myMethod");
//    Object instance = classToLoad.newInstance ();
//    Object result = method.invoke (instance);
  }

  protected void setDriverPathsInSystemProperty() {
    System.setProperty("webdriver.ie.driver", this.ieDriverPath.getAbsolutePath());
    System.setProperty("webdriver.chrome.driver", this.chromeDriverPath.getAbsolutePath());
    System.setProperty("webdriver.gecko.driver", this.geckoDriverPath.getAbsolutePath());
  }
  
  /**
   * 
   * @param browserName firefox, chrome, or internetexplorer
   * @return Browser version installed of browserName
   */
  public static String guessBrowserVersion(String browserName) {
    if (browserName.equalsIgnoreCase("firefox")) {
      return getFirefoxVersion();
    } else if (browserName.equalsIgnoreCase("chrome")) {
      return getChromeVersion();
    } else if (browserName.equalsIgnoreCase("internetexplorer")) {
      return getIEVersion();
    } else if (browserName.equalsIgnoreCase("internet explorer")) {
      return getIEVersion();
    } else {
      return "";
    }
  }
  
  /**
   * 
   * @return version of IE installed
   */
  private static String getIEVersion() {
    String regLocation = "Software\\Microsoft\\Internet Explorer";
    String version = "";

    if (RuntimeConfig.getOS().isWindows()) {
      try {
        version = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, regLocation, "svcVersion");
      } catch (Exception e) {
        logger.warn("Getting IE version from " + regLocation + "\\svcVersion failed.");
        logger.warn(e.getMessage());
        logger.warn("Trying " + regLocation + " next.");
      }
      if (version == "") { // svcVersion didn't exist. Try Version instead (maybe IE8 ?).
        try {
          version = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, regLocation, "version");
        } catch (Exception e) {
          logger.warn("Getting IE version from " + regLocation + " failed.");
          logger.warn(e.getMessage());
        }
      }
      if (version != "") {
        version = version.trim();
        version = version.substring(0, version.indexOf('.'));
      }
      return version;
    }
    return version;
  }

  /**
   * 
   * @return version of Firefox installed
   */
  private static String getFirefoxVersion() {
    String version = "";
    if (RuntimeConfig.getOS().isWindows()) {
      String[] cmd = new String[4];
      cmd[0] = "cmd";
      cmd[1] = "/C";
      File f = new File("C:/Program Files (x86)");
      if (f.exists()) {
        cmd[2] = "C:/Program Files (x86)/Mozilla Firefox/firefox.exe";
      } else {
        cmd[2] = "C:/Program Files/Mozilla Firefox/firefox.exe";
      }

      cmd[3] = "--version|more";
      try {
        JsonObject object = ExecuteCommand.execRuntime(cmd, true);
        version = object.get("out").getAsJsonArray().get(0).getAsString().trim().replaceAll("[^\\d.]", ""); // Removes "Mozilla Firefox"
        version = version.substring(0, version.indexOf('.'));
      } catch (Exception e) {
        // If ExecuteCommand.execRuntime fails, still return "";
        logger.warn(e.getMessage());
      }
      return version;
    } else if (RuntimeConfig.getOS().isMac()) {
      String[] cmd = {"/Applications/Firefox.app/Contents/MacOS/firefox", "--version"};
      try {
        JsonObject object = ExecuteCommand.execRuntime(cmd, true);
        version = object.get("out").getAsString().trim().replaceAll("[^\\d.]", ""); // Removes "Mozilla Firefox"
        version = version.substring(0, version.indexOf('.'));
      } catch (Exception e) {
        // If ExecuteCommand.execRuntime fails, still return "";
        logger.warn(e.getMessage());
      }
      return version;
    }
    return version;
  }

  /**
   * 
   * @return version of Chrome installed
   */
  private static String getChromeVersion() {
    String regLocation32Bit = "Software\\Google\\Chrome\\BLBeacon";
    String regLocation64Bit = "Software\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Google Chrome";
    String version = "";
    if (RuntimeConfig.getOS().isWindows()) {
      try {
        version = Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, regLocation32Bit, "version");
      } catch (Exception e) {
        logger.warn("Getting chrome version from " + regLocation32Bit + " failed.");
        logger.warn(e.getMessage());
        logger.warn("Trying " + regLocation64Bit + " next.");
      }
      if (version == "") { // If 1st location didn't exist, try 2nd location.
        try {
          version = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, regLocation64Bit, "version");
        } catch (Exception e) {
          logger.warn("Getting chrome version from " + regLocation64Bit + " failed.");
          logger.warn(e.getMessage());
        }
      }
      version = version.substring(0, version.indexOf('.'));
      return version.trim();
    } else if (RuntimeConfig.getOS().isMac()) {
      try {
        JsonObject object = ExecuteCommand.execRuntime(chromeMacVersionCommand, true);
        version = object.get("out").getAsString().trim().replaceAll("[^\\d.]", ""); // Removes "Google Chrome"
        version = version.substring(0, version.indexOf('.'));
      } catch (Exception e) {
        // If ExecuteCommand.execRuntime fails, still return "";
        logger.warn(e.getMessage());
      }
      return version;
    }
    return version;
  }
}
