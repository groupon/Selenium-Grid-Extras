package com.groupon.seleniumgridextras.config.capabilities;

public class Firefox extends Capability {

  @Override
  public String getWebDriverClass() {
    return "org.openqa.selenium.firefox.FirefoxDriver";
  }
}
