package com.groupon.seleniumgridextras.config;

import com.google.gson.Gson;

import com.groupon.seleniumgridextras.config.capabilities.Capability;
import com.groupon.seleniumgridextras.config.capabilities.Firefox;

import com.google.gson.internal.StringMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class GridNodeTest {

  private final String fileaname = "grid_node_test_write_to_file.json";
  private GridNode node;
  private Map expectedConfiguration;
  private List<Capability> expectedCapabilities;

  @Before
  public void setUp() throws Exception {
    node = new GridNode();

    expectedConfiguration = new HashMap();
    expectedConfiguration
        .put("proxy", "com.groupon.seleniumgridextras.grid.proxies.SetupTeardownProxy");
    expectedConfiguration.put("maxSession", 1);
    expectedConfiguration.put("port", 5555);
    expectedConfiguration.put("host", "localhost");
    expectedConfiguration.put("register", true);
    expectedConfiguration.put("registerCycle", 2000);
    expectedConfiguration.put("hubPort", 4444);
    expectedConfiguration.put("hubHost", "google.com");

    expectedCapabilities = new LinkedList<Capability>();
    expectedCapabilities.add(new Firefox());


    node.getConfiguration().setHubHost("google.com");
    node.getConfiguration().setHost("localhost");
    node.getConfiguration().setHubPort(4444);
    node.getConfiguration().setPort(5555);

    node.getCapabilities().add(new Firefox());

    node.writeToFile(fileaname);



  }

  @After
  public void tearDown() throws Exception {

//    File f = new File(fileaname);
//
//    if ( f.exists() ){
//      f.delete();
//    }

  }

  @Test
  public void testGetCapabilities() throws Exception {

    LinkedList<Capability> expected = new LinkedList<Capability>();
    expected.add(new Firefox());
    assertEquals(expected, node.getCapabilities());

  }

  @Test
  public void testGetConfiguration() throws Exception {
    assertEquals(expectedConfiguration.get("port"), node.getConfiguration().getPort());
    assertEquals(expectedConfiguration.get("hubPort"), node.getConfiguration().getHubPort());
    assertEquals(expectedConfiguration.get("host"), node.getConfiguration().getHost());
    assertEquals(expectedConfiguration.get("hubHost"), node.getConfiguration().getHubHost());
  }

  @Test
  public void testCapabilitiesProperlyWrittenToFile() throws Exception {
    Map actual = getMapFromConfigFile(fileaname);

    List actualCapabilities = (ArrayList) actual.get("capabilities");
    assertEquals(1, actualCapabilities.size());

    Map actualCapability = getMapFromString(actualCapabilities.get(0).toString());

    actualCapability = doubleToIntConverter(actualCapability);

    Map expectedFirefox = new HashMap();
    expectedFirefox.put("browserName", "*firefox");
    expectedFirefox.put("maxInstances", 1);
    expectedFirefox.put("seleniumProtocol", "Selenium");

    assertEquals(expectedFirefox, actualCapability);
  }

  @Test
  public void testConfigurationProperlyWrittenToFile() throws Exception {
    Map actual = getMapFromConfigFile(fileaname);

    Map actualConfiguration = stringMapToHashMap((StringMap) actual.get("configuration"));
    actualConfiguration = doubleToIntConverter(actualConfiguration);

    assertEquals(expectedConfiguration, actualConfiguration);


  }

  @Test
  public void testGetStartCommand() throws Exception {

  }

  private Map getMapFromString(String input){
    return new Gson().fromJson(input, HashMap.class);
  }

  private Map getMapFromConfigFile(String filenameToUse){
    String nodeConfigString = assertFileExistsAndRead(filenameToUse);
    return new Gson().fromJson(nodeConfigString, HashMap.class);
  }

  private String assertFileExistsAndRead(String filenameToUse) {
    File f = new File(filenameToUse);
    assertTrue(f.exists());

    String stringFromFile = readConfigFile(filenameToUse);
    assertNotEquals("", stringFromFile);
    return stringFromFile;
  }


  //<Grumble Grumble>, google parsing Gson, Grumble
  private Map doubleToIntConverter(Map input) {
    for (Object key : input.keySet()) {

      if (input.get(key) instanceof Double) {
        input.put(key, ((Double) input.get(key)).intValue());
      }
    }

    return input;
  }

  private Map stringMapToHashMap(StringMap input) {
    Map output = new HashMap();
    output.putAll(input);

    return output;
  }

  //</Grubmle>

  private String readConfigFile(String filePath) {
    String returnString = "";
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filePath));
      String line = null;
      while ((line = reader.readLine()) != null) {
        returnString = returnString + line;
      }
    } catch (FileNotFoundException error) {
      System.out.println("File " + filePath + " does not exist, going to use default configs");
    } catch (IOException error) {
      System.out.println("Error reading" + filePath + ". Going with default configs");
    }
    return returnString;
  }

}
