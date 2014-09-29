package com.groupon.seleniumgridextras.config.capabilities;

public class Chrome extends Capability {

  @Override
  public String getWebDriverClass() {
    return "org.openqa.selenium.chrome.ChromeDriver";
  }
}
