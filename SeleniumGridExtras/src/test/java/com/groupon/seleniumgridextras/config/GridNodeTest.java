package com.groupon.seleniumgridextras.config;


import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import com.groupon.seleniumgridextras.config.capabilities.Capability;
import com.groupon.seleniumgridextras.config.capabilities.Firefox;
import com.groupon.seleniumgridextras.utilities.json.JsonParserWrapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;


public class GridNodeTest {

  private final String fileaname = "grid_node_test_write_to_file.json";
  private GridNode node;
  private Map expectedConfiguration;
  private List<Capability> expectedCapabilities;
  private Capability expectedFirefoxCapability;

  @Before
  public void setUp() throws Exception {

    expectedConfiguration = new HashMap();
    expectedConfiguration
        .put("proxy", "com.groupon.seleniumgridextras.grid.proxies.SetupTeardownProxy");
    expectedConfiguration.put("maxSession", 3);
    expectedConfiguration.put("port", 5555);
    expectedConfiguration.put("register", true);
    expectedConfiguration.put("registerCycle", 5000);
//    expectedConfiguration.put("browserTimeout", 120);
//    expectedConfiguration.put("timeout", 120);
    expectedConfiguration.put("hubPort", 4444);
    expectedConfiguration.put("hubHost", "google.com");
    expectedConfiguration.put("unregisterIfStillDownAfter", 10000);
    expectedConfiguration.put("nodeStatusCheckTimeout", 10000);
    expectedConfiguration.put("downPollingLimit", 0);

    expectedCapabilities = new LinkedList<Capability>();
    expectedFirefoxCapability = new Firefox();
    expectedFirefoxCapability.put("version", "12");
    expectedCapabilities.add(expectedFirefoxCapability);

    node = new GridNode(false);
    node.getConfiguration().setHubHost("google.com");
    node.getConfiguration().setHubPort(4444);
    node.getConfiguration().setPort(5555);

    Capability toFileCap = new Firefox();
    toFileCap.put("version", "12");
    node.getCapabilities().add(toFileCap);


    node.writeToFile(fileaname);


  }

  @After
  public void tearDown() throws Exception {

    File f = new File(fileaname);

    if (f.exists()) {
      f.delete();
    }

  }

  @Test
  public void testCreateNodeFromFile() throws Exception {
    boolean isSelenium3 = false;
    GridNode nodeFromFile = GridNode.loadFromFile(fileaname, isSelenium3);

    assertEquals(1, nodeFromFile.getCapabilities().size());
    assertEquals(expectedCapabilities, nodeFromFile.getCapabilities());

    //Find lowest common denominator of comparison which is a HasMap, convert everything into that andd run test
    Map expected = new Gson().fromJson(new Gson().toJson(expectedConfiguration), HashMap.class);
    Map
        actual =
        new Gson().fromJson(new Gson().toJson(nodeFromFile.getConfiguration()), HashMap.class);

    assertEquals(expected, actual);
  }


  @Test
  public void testGetCapabilities() throws Exception {

    LinkedList<Capability> expected = new LinkedList<Capability>();
    expected.add(expectedFirefoxCapability);
    assertEquals(expected, node.getCapabilities());

  }

  @Test
  public void testGetConfiguration() throws Exception {
    assertEquals(expectedConfiguration.get("port"), node.getConfiguration().getPort());
    assertEquals(expectedConfiguration.get("hubPort"), node.getConfiguration().getHubPort());
    assertEquals(expectedConfiguration.get("hubHost"), node.getConfiguration().getHubHost());
  }

  @Test
  public void testCapabilitiesProperlyWrittenToFile() throws Exception {
    Map actual = getMapFromConfigFile(fileaname);

    List actualCapabilities = (ArrayList) actual.get("capabilities");
    assertEquals(1, actualCapabilities.size());

    Map actualCapability = JsonParserWrapper.toHashMap(actualCapabilities.get(0).toString());

    actualCapability = GridNode.doubleToIntConverter(actualCapability);

    Map expectedFirefox = new HashMap();
    expectedFirefox.put("browserName", "firefox");
    expectedFirefox.put("maxInstances", 3);
    expectedFirefox.put("seleniumProtocol", "WebDriver");
    expectedFirefox.put("version", 12); //Grumble Grumble, this should be a string not an int

    assertEquals(expectedFirefox, actualCapability);
  }

  @Test
  public void testConfigurationProperlyWrittenToFile() throws Exception {
    Map actual = getMapFromConfigFile(fileaname);

    Map actualConfiguration = GridNode.linkedTreeMapToHashMap((LinkedTreeMap) actual.get("configuration"));
    actualConfiguration = GridNode.doubleToIntConverter(actualConfiguration);
    assertEquals(expectedConfiguration, actualConfiguration);

  }

  private String assertFileExistsAndRead(String filenameToUse) {
    File f = new File(filenameToUse);
    assertTrue(f.exists());

    String stringFromFile = GridNode.readConfigFile(filenameToUse);
    assertNotEquals("", stringFromFile);
    return stringFromFile;
  }

  private Map getMapFromConfigFile(String filenameToUse) {
    String nodeConfigString = assertFileExistsAndRead(filenameToUse);
    return JsonParserWrapper.toHashMap(nodeConfigString);
  }


}
