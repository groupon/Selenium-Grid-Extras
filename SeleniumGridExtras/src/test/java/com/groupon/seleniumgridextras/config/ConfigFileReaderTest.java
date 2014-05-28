package com.groupon.seleniumgridextras.config;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class ConfigFileReaderTest {

  private static String configFile = "config_file_reader_test.json";

  @Before
  public void setUp() throws Exception {

    RuntimeConfig.setConfigFile(configFile);
    Config config = new Config(true);
    config.writeToDisk(RuntimeConfig.getConfigFile());

  }

  @After
  public void tearDown() throws Exception {

    File f = new File(configFile);

    if (f.exists()) {
      f.delete();
    }

  }

  @Test
  public void testFullDefaultConfig() {
    ConfigFileReader c = new ConfigFileReader(configFile);
    assertEquals(true, c.hasContent());

    Map expectedNodeFiles = new HashMap();
    expectedNodeFiles.put("node_config_files", new LinkedList());

    Map expected = new HashMap();
    expected.put("theConfigMap", expectedNodeFiles);

    assertEquals(expected, c.toHashMap());

  }

  @Test
  public void testEmptyConfig() {
    ConfigFileReader c = new ConfigFileReader("foo.json");
    assertEquals(false, c.hasContent());
    assertEquals(new HashMap(), c.toHashMap());
  }
}
