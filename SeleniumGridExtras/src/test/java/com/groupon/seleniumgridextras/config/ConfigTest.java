package com.groupon.seleniumgridextras.config;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;


public class ConfigTest {

  Config config;
  String filename = "config_test.json";

  @Before
  public void setUp() throws Exception {
    config = DefaultConfig.getDefaultConfig();
  }

  @After
  public void tearDown() throws Exception {
    File f = new File(filename);

    if (f.exists()){
      f.delete();
    }
  }

  @Test
  public void testGetWebdriver() throws Exception {

    config.writeToDisk(filename);


  }
}
