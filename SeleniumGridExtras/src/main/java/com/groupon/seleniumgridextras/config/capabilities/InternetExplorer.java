package com.groupon.seleniumgridextras.config.capabilities;

public class InternetExplorer extends Capability {

  @Override
  public String getWebDriverClass() {
    return "org.openqa.selenium.ie.InternetExplorerDriver";
  }

  public InternetExplorer() {
    this.put("maxInstances", 1);
    this.put("seleniumProtocol", "WebDriver");
    setBrowser(getWDStyleName());
  }

}
