package com.groupon.seleniumgridextras.config;

import com.google.gson.JsonObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


public class RuntimeConfigTest {

  private JsonObject foo;

  @Before
  public void setUp() throws Exception {
    RuntimeConfig.clearConfig();
    RuntimeConfig.setConfigFile("merge_configs_test.json");
  }

  @After
  public void tearDown() throws Exception {
    File config = new File(RuntimeConfig.getConfigFile());
    config.delete();
  }

  @Test
  public void testLoadDefaultsOnly() throws Exception {
    assertNull(RuntimeConfig.getConfig());

    RuntimeConfig.loadDefaults();
    assertNotNull(RuntimeConfig.getConfig());
    assertNotNull(RuntimeConfig.getConfig().getWebdriver().getVersion());
  }

  @Test
  public void testLoadWithOverwrites() throws Exception {
    assertNull(RuntimeConfig.getConfig());
  }
}
