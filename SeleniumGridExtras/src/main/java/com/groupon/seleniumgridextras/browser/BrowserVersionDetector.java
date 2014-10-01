package com.groupon.seleniumgridextras.browser;

import com.groupon.seleniumgridextras.config.GridNode;
import com.groupon.seleniumgridextras.config.capabilities.Capability;

import java.io.File;
import java.util.List;

public class BrowserVersionDetector {

//  TODO: Dima finish this when you have some free time. Good idea but using classloader is a bad ide
//  TODO: Java is not good with dynamic jar loading in way you want. Instead go with a mini grid with 1
//  TODO: node and do all of the stuff via simple HTTP calls, CURL FTW!


  protected File jarPath;
  protected File ieDriverPath;
  protected File chromeDriverPath;
  protected List<GridNode> nodesFromConfigFile;


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
  }


}
