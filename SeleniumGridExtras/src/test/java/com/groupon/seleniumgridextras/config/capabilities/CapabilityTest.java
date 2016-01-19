package com.groupon.seleniumgridextras.config.capabilities;

import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CapabilityTest {


  @Test
  public void getCapabilitiesFromList() throws Exception {
    Capability firefox = Capability.getCapabilityFor("firefox");
    Capability ie = Capability.getCapabilityFor("internet explorer");
    Capability chrome = Capability.getCapabilityFor("chrome");
    Capability safari = Capability.getCapabilityFor("safari");
    Capability android = Capability.getCapabilityFor("android");
    Capability chromium = Capability.getCapabilityFor("chromium");
    Capability browser = Capability.getCapabilityFor("browser");
    Capability iphone = Capability.getCapabilityFor("iPhone");
    Capability ipad = Capability.getCapabilityFor("iPad");
    Capability phantomjs = Capability.getCapabilityFor("phantomjs");

    assertTrue(firefox instanceof Firefox);
    assertTrue(ie instanceof InternetExplorer);
    assertTrue(chrome instanceof Chrome);
    assertTrue(safari instanceof Safari);
    assertTrue(android instanceof Android);
    assertTrue(chromium instanceof Chromium);
    assertTrue(browser instanceof Browser);
    assertTrue(iphone instanceof IPhone);
    assertTrue(ipad instanceof IPad);
    assertTrue(phantomjs instanceof PhantomJs);
  }


  @Test
  public void testGetWDStyleName() throws Exception {
    assertEquals("firefox", Capability.getSupportedCapabilities().get(Firefox.class));
    assertEquals("chrome", Capability.getSupportedCapabilities().get(Chrome.class));
    assertEquals("safari", Capability.getSupportedCapabilities().get(Safari.class));
    assertEquals("internet explorer",
                 Capability.getSupportedCapabilities().get(InternetExplorer.class));
    assertEquals("android", Capability.getSupportedCapabilities().get(Android.class));
    assertEquals("chromium", Capability.getSupportedCapabilities().get(Chromium.class));
    assertEquals("browser", Capability.getSupportedCapabilities().get(Browser.class));
    assertEquals("iPhone", Capability.getSupportedCapabilities().get(IPhone.class));
    assertEquals("iPad", Capability.getSupportedCapabilities().get(IPad.class));
  }

  @Test
  public void testGetCorrectWebDriverClass() throws Exception {

    assertEquals("org.openqa.selenium.firefox.FirefoxDriver",
                 Capability.getCapabilityFor("firefox").getWebDriverClass());
    assertEquals("org.openqa.selenium.chrome.ChromeDriver",
                 Capability.getCapabilityFor("chrome").getWebDriverClass());
    assertEquals("org.openqa.selenium.ie.InternetExplorerDriver",
                 Capability.getCapabilityFor("internet explorer").getWebDriverClass());
    assertEquals("org.openqa.selenium.safari.SafariDriver",
                 Capability.getCapabilityFor("safari").getWebDriverClass());
    assertEquals("io.appium.java_client.android.AndroidDriver",
            Capability.getCapabilityFor("android").getWebDriverClass());
    assertEquals("io.appium.java_client.android.AndroidDriver",
            Capability.getCapabilityFor("chromium").getWebDriverClass());
    assertEquals("io.appium.java_client.android.AndroidDriver",
            Capability.getCapabilityFor("browser").getWebDriverClass());
    assertEquals("io.appium.java_client.ios.IOSDriver",
            Capability.getCapabilityFor("iPhone").getWebDriverClass());
    assertEquals("io.appium.java_client.ios.IOSDriver",
            Capability.getCapabilityFor("iPad").getWebDriverClass());
    assertEquals("org.openqa.selenium.phantomjs.PhantomJSDriver",
            Capability.getCapabilityFor("phantomjs").getWebDriverClass());

  }
}
