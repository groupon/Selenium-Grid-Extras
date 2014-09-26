package com.groupon.seleniumgridextras.browser;

import com.groupon.seleniumgridextras.config.GridNode;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.config.capabilities.Capability;

import java.io.File;
import java.util.List;

public class BrowserVersionDetector {

  protected File jarPath;
  protected List<GridNode> nodesFromConfigFile;


  public BrowserVersionDetector(List<GridNode> nodes) {
    jarPath = new File(getCurrentWebDriverJarPath());
    nodesFromConfigFile = nodes;
  }

  public void updateVersions(){

    System.out.println(
        nodesFromConfigFile.get(0).getCapabilities().get(0).getClass().getCanonicalName());
    System.out.println(jarPath.getAbsolutePath());


  }

  protected String getCurrentWebDriverJarPath() {
    return getWebdriverHome() + RuntimeConfig.getOS().getFileSeparator() + getWebdriverVersion()
           + ".jar";
  }

  protected String getWebdriverHome() {
    return RuntimeConfig.getConfig().getWebdriver().getDirectory();
  }

  protected String getWebdriverVersion() {
    return RuntimeConfig.getConfig().getWebdriver().getVersion();
  }
}
