package com.groupon.seleniumgridextras.config.capabilities;

public class InternetExplorer extends Capability {

  public InternetExplorer() {
    this.put("maxInstances", 1);
    this.put("seleniumProtocol", "WebDriver");
    setBrowser(getWDStyleName());
  }

}
