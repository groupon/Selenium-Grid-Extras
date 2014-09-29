package com.groupon.seleniumgridextras.config.capabilities;

public class Safari extends Capability {

  @Override
  public String getWebDriverClass() {
    return "org.openqa.selenium.safari.SafariDriver";
  }
}
