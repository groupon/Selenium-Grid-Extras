package com.groupon.seleniumgridextras.config.capabilities;

import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CapabilityTest {


  @Test
  public void getCapabilitiesFromList() throws Exception {
    Capability firefox = Capability.getCapabilityFor( "firefox" );
    Capability ie = Capability.getCapabilityFor( "internet explorer" );
    Capability chrome = Capability.getCapabilityFor( "chrome" );

    assertTrue(firefox instanceof Firefox);
    assertTrue(ie instanceof InternetExplorer);
    assertTrue(chrome instanceof Chrome);
  }



  @Test
  public void testGetWDStyleName() throws Exception {
    assertEquals("firefox", Capability.getSupportedCapabilities().get(Firefox.class));
    assertEquals("chrome", Capability.getSupportedCapabilities().get(Chrome.class));
    assertEquals("internet explorer",
                 Capability.getSupportedCapabilities().get(InternetExplorer.class));
  }
}
