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
  private ConfigFileReader parsedConfig;
  private Map expectedHashMap;

  @Before
  public void setUp() throws Exception {

    RuntimeConfig.setConfigFile(configFile);
    Config config = new Config(true);
    config.writeToDisk(RuntimeConfig.getConfigFile());

    parsedConfig = new ConfigFileReader(configFile);

    Map expectedNodeFiles = new HashMap();
    expectedNodeFiles.put("node_config_files", new LinkedList());
    expectedNodeFiles.put("hub_config_files", new LinkedList());

    expectedHashMap = new HashMap();
    expectedHashMap.put("theConfigMap", expectedNodeFiles);
  }

  @After
  public void tearDown() throws Exception {

    File f = new File(configFile);

    if (f.exists()) {
      f.delete();
    }

  }

  @Test
  public void testWriteToDisk() throws Exception {
    Map h = parsedConfig.toHashMap();
    h.put("test", "This is a test");
    expectedHashMap.put("test", "This is a test");

    parsedConfig.overwriteExistingConfig(h);
    assertEquals(expectedHashMap, parsedConfig.toHashMap());
  }

  @Test
  public void testFullDefaultConfig() throws Exception {
    assertEquals(true, parsedConfig.hasContent());
    assertEquals(expectedHashMap, parsedConfig.toHashMap());
  }

  @Test
  public void testEmptyConfig() throws Exception {
    ConfigFileReader c = new ConfigFileReader("foo.json");
    assertEquals(false, c.hasContent());
    assertEquals(new HashMap(), c.toHashMap());
  }
}
