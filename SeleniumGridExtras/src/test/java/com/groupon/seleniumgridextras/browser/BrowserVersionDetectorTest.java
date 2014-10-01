package com.groupon.seleniumgridextras.browser;

import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.GridNode;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.utilities.FileIOUtility;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class BrowserVersionDetectorTest {

  protected File gridExtrasJson = new File("browser_detect_test.json");
  protected File nodeConfigJson = new File("test_grid_node.json");


  @Before
  public void setUp() throws Exception {
    writeNodeToFile(nodeConfigJson);

    GridNode node = new GridNode();
    node.setLoadedFromFile(nodeConfigJson.getAbsolutePath());

    RuntimeConfig.setConfigFile(gridExtrasJson.getAbsolutePath());
    Config config = new Config(true);

    config.addNode(node, nodeConfigJson.getName());

    config.writeToDisk(RuntimeConfig.getConfigFile());
    RuntimeConfig.load();
  }

  @After
  public void tearDown() throws Exception {
//    gridExtrasJson.delete();
//    new File(RuntimeConfig.getConfigFile() + ".example").delete();
//    nodeConfigJson.delete();
  }


  @Test
  public void  testFoo() throws Exception{

    System.out.println(JsonCodec.OUT);
    BrowserVersionDetector foo = new BrowserVersionDetector(RuntimeConfig.getConfig().getNodes());
    foo.setChromeDriverPath(new File("/private/tmp/webdriver/chromedriver/chromedriver_2.10_32bit"));
    foo.setIeDriverPath(new File("/private/tmp/webdriver/iedriver/IEDriverServer.exe"));
    foo.setJarPath(new File("/private/tmp/webdriver/2.43.1.jar"));

//    WebDriver driver = new FirefoxDriver();

    foo.updateVersions();

//    GridNode node = RuntimeConfig.getConfig().getNodes().get(0);
//
//    System.out.println(node.getCapabilities().get(0));
//    System.out.println(node.getCapabilities().get(0).getBrowserVersion());



  }

  public void writeNodeToFile(File path) throws IOException {
    FileIOUtility.writeToFile(path, getCapability());
  }

  public String getCapability(){
    return "{\n"
           + "  \"capabilities\": [\n"
           + "    {\n"
           + "      \"platform\": \"MAC\",\n"
           + "      \"seleniumProtocol\": \"WebDriver\",\n"
           + "      \"browserName\": \"chrome\",\n"
           + "      \"version\": 31,\n"
           + "      \"maxInstances\": 3\n"
           + "    },\n"
           + "    {\n"
           + "      \"platform\": \"MAC\",\n"
           + "      \"seleniumProtocol\": \"WebDriver\",\n"
           + "      \"browserName\": \"firefox\",\n"
           + "      \"maxInstances\": 3\n"
           + "    },\n"
           + "    {\n"
           + "      \"platform\": \"MAC\",\n"
           + "      \"seleniumProtocol\": \"WebDriver\",\n"
           + "      \"browserName\": \"internet explorer\",\n"
           + "      \"maxInstances\": 1\n"
           + "    }\n"
           + "  ],\n"
           + "  \"configuration\": {\n"
           + "    \"proxy\": \"com.groupon.seleniumgridextras.grid.proxies.SetupTeardownProxy\",\n"
           + "    \"maxSession\": 3,\n"
           + "    \"port\": 5555,\n"
           + "    \"register\": true,\n"
           + "    \"unregisterIfStillDownAfter\": 10000,\n"
           + "    \"hubPort\": 4444,\n"
           + "    \"hubHost\": \"127.0.0.1\",\n"
           + "    \"nodeStatusCheckTimeout\": 10000,\n"
           + "    \"downPollingLimit\": 0\n"
           + "  }\n"
           + "}";
  }

}
