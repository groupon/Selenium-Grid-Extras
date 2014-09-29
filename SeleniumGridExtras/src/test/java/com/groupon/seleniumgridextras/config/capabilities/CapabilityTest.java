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

    assertTrue(firefox instanceof Firefox);
    assertTrue(ie instanceof InternetExplorer);
    assertTrue(chrome instanceof Chrome);

    assertTrue(safari instanceof Safari);
  }


  @Test
  public void testGetWDStyleName() throws Exception {
    assertEquals("firefox", Capability.getSupportedCapabilities().get(Firefox.class));
    assertEquals("chrome", Capability.getSupportedCapabilities().get(Chrome.class));
    assertEquals("safari", Capability.getSupportedCapabilities().get(Safari.class));
    assertEquals("internet explorer",
                 Capability.getSupportedCapabilities().get(InternetExplorer.class));
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
  }
}
