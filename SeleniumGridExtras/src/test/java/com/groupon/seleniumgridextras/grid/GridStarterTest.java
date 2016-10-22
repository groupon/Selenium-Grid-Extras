package com.groupon.seleniumgridextras.grid;

import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.GridNode;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GridStarterTest {

    private final String gridStartTestJson = "grid_start_test.json";
    private static final String START_HUB_BAT = "start_hub.bat";
    private static final String GRID_HUB_LOG = "grid_hub.log";
    private static final String TEST_COMMAND = "command is here";
    private final String nodeOneConfig = "node1.json";
    private final String nodeTwoConfig = "node2.json";
    private final String nodeAppiumConfig = "appium_node.json";

    private final String logFile = "foo.log";
    private final String command = "command";
    private final String windowsBatchFileName = logFile.replace("log", "bat");
    private final
    String
            expectedCommand =
            command + " -log log" + RuntimeConfig.getOS().getFileSeparator() + logFile + " ";

    private final String appiumLogFile = "appium_foo.log";
    private final String windowsAppiumBatchFileName = appiumLogFile.replace("log", "bat");
    String
            expectedAppiumCommand =
            command + " --log " + System.getProperty("user.dir") + RuntimeConfig.getOS().getFileSeparator()
                    + "log" + RuntimeConfig.getOS().getFileSeparator() + appiumLogFile + " ";


    //COMPILED WITH USE OF http://gskinner.com/RegExr/ Use it, it will make your life simpler
    private final
    Pattern
            startHubCommandPattern =
            Pattern.compile(
                    "(java\\s*-cp)\\s*\"([/\\\\\\w-]*)([:;])([/\\\\\\w-^]*)(1.1.1.jar)\"\\s*(org.openqa.grid.selenium.GridLauncher)\\s*-role\\s(\\w*)\\s-port\\s(\\d{4})\\s-host\\s([\\d\\.]*)\\s-servlets\\s([\\w\\.]*),([\\w\\.]*)");


    @Before
    public void setUp() throws Exception {
        RuntimeConfig.setConfigFile(gridStartTestJson);
        Config config = new Config();
        config.getWebdriver().setVersion("1.1.1");

        GridNode node1 = new GridNode(false);
        GridNode node2 = new GridNode(false);
        GridNode nodeAppium = new GridNode(false);
        nodeAppium.getConfiguration().setAppiumStartCommand("appium");
        nodeAppium.getConfiguration().setPort(4723);

        node1.writeToFile(nodeOneConfig);
        node2.writeToFile(nodeTwoConfig);
        nodeAppium.writeToFile(nodeAppiumConfig);

        config.addNodeConfigFile(nodeOneConfig);
        config.addNodeConfigFile(nodeTwoConfig);
        config.addNodeConfigFile(nodeAppiumConfig);

        config.writeToDisk(RuntimeConfig.getConfigFile());

        RuntimeConfig.load();
    }

    @After
    public void tearDown() throws Exception {
        new File(START_HUB_BAT).delete();
        new File(GRID_HUB_LOG).delete();
        new File(nodeOneConfig).delete();
        new File(nodeTwoConfig).delete();
        new File(nodeAppiumConfig).delete();
        new File(windowsBatchFileName).delete();
        new File(windowsAppiumBatchFileName).delete();
        new File(gridStartTestJson).delete();
        new File(RuntimeConfig.getConfigFile() + ".example").delete();

    }


    @Test
    public void testGetNodeStartCommand() throws Exception {

        List<String> startCommand = GridStarter.getNodeStartCommand(nodeOneConfig, false, RuntimeConfig.getConfig());
        StringBuilder sb1 = new StringBuilder();
        for(String part : startCommand) {
          sb1.append(part + " ");
        }
        assertTrue(sb1.toString().contains("org.openqa.grid.selenium.GridLauncher"));
        assertTrue(sb1.toString().contains("-role node"));
        assertTrue(sb1.toString().contains("-nodeConfig " + nodeOneConfig));

        startCommand = GridStarter.getNodeStartCommand(nodeAppiumConfig, false, RuntimeConfig.getConfig());
        StringBuilder sb2 = new StringBuilder();
        for(String part : startCommand) {
          sb2.append(part + " ");
        }
        assertTrue(sb2.toString().contains("appium -p 4723"));
        assertTrue(sb2.toString().contains("--nodeconfig " + System.getProperty("user.dir")
                + RuntimeConfig.getOS().getFileSeparator() + nodeAppiumConfig));

        startCommand = GridStarter.getNodeStartCommand(nodeAppiumConfig, true, RuntimeConfig.getConfig());
        StringBuilder sb3 = new StringBuilder();
        for(String part : startCommand) {
          sb3.append(part + " ");
        }
        assertTrue(sb3.toString().contains("appium -p 4723"));
        assertTrue(sb3.toString().contains("--nodeconfig " + System.getProperty("user.dir")
                + RuntimeConfig.getOS().getFileSeparator() + nodeAppiumConfig));
    }

    @Test
    public void testGetBackgroundStartCommandForNode() throws Exception {
        List<String> cmd = new ArrayList<String>();
        cmd.add(command);
        List<String> backgroundStartCmdNonWindows = GridStarter.getBackgroundStartCommandForNode(cmd, logFile, false);

        assertEquals(expectedCommand, convertCommandToString(backgroundStartCmdNonWindows));

        cmd.clear();
        cmd.add(command);
        List<String> backgroundStartCmdWindows = GridStarter.getBackgroundStartCommandForNode(cmd, logFile, true);
        assertEquals("start /MIN " + windowsBatchFileName + " ", convertCommandToString(backgroundStartCmdWindows));

        cmd.clear();
        cmd.add(command);        
        assertEquals(expectedCommand, readFile(windowsBatchFileName));

        cmd.clear();
        cmd.add(command);
        List<String> backgroundStartCmdAppiumNonWindows = GridStarter.getBackgroundStartCommandForNode(cmd, appiumLogFile, false);
        assertEquals(expectedAppiumCommand, convertCommandToString(backgroundStartCmdAppiumNonWindows));
        
        cmd.clear();
        cmd.add(command);
        List<String> backgroundStartCmdAppiumWindows = GridStarter.getBackgroundStartCommandForNode(cmd, appiumLogFile, true);
        assertEquals("start /MIN " + windowsAppiumBatchFileName + " ", convertCommandToString(backgroundStartCmdAppiumWindows));

        cmd.clear();
        cmd.add(command);
        assertEquals(expectedAppiumCommand, readFile(windowsAppiumBatchFileName));

    }

    private String convertCommandToString(List<String> command) {
      StringBuilder sb = new StringBuilder();
      
      for(String part : command) {
        sb.append(part + " ");
      }
      
      return sb.toString();
    }

    //  @Test
//  public void testGetOsSpecificHubStartCommandForLinux() throws Exception {
//    String
//        expecteWdDir =
//        RuntimeConfig.getOS().getFileSeparator() + "tmp" + RuntimeConfig.getOS().getFileSeparator()
//        + "webdriver"
//        + RuntimeConfig.getOS().getFileSeparator();
//    Matcher
//        matcher =
//        startHubCommandPattern.matcher(GridStarter.getOsSpecificHubStartCommand(false));
//
//    assertTrue(matcher.find()); //Make sure the matchers are met
//    assertEquals(11, matcher.groupCount()); //We have 11 total matches
//    assertEquals("java  -cp", matcher.group(1)); //start with java command
//    assertEquals(RuntimeConfig.getOS().getPathSeparator(),
//                 matcher.group(3)); //OS specific class delimeter
//    assertEquals(expecteWdDir, matcher.group(4)); //Location of the WD jar file
//    assertEquals("1.1.1.jar", matcher.group(5)); //name of jar file
//    assertEquals("org.openqa.grid.selenium.GridLauncher",
//                 matcher.group(6)); //Calling the Grid launcher class
//    assertEquals("hub", matcher.group(7)); //check role of the start command
//    assertEquals("4444", matcher.group(8)); //Check port used
//    assertEquals(RuntimeConfig.getOS().getHostIp(), matcher.group(9)); //Host name
//    assertEquals("com.groupon.seleniumgridextras.grid.servlets.SeleniumGridExtrasServlet",
//                 matcher.group(10)); //Using the servlete to pretty print html
//    assertEquals("com.groupon.seleniumgridextras.grid.servlets.ProxyStatusJsonServlet",
//                 matcher.group(11)); //JSON current status proxy
//
//
//  }
//
    @Test
    public void testBuildBackgroundStartCommand() throws Exception {
        assertEquals(TEST_COMMAND, GridStarter.buildBackgroundStartCommand(TEST_COMMAND, false));

        assertEquals("start " + START_HUB_BAT,
                GridStarter.buildBackgroundStartCommand(TEST_COMMAND, true));

        assertEquals(TEST_COMMAND, readFile(START_HUB_BAT));

    }

    @Test
    public void testEdgeDriverDString() throws Exception {
        Config config = new Config();
        assertEquals("-Dwebdriver.edge.driver=\"C:\\Program Files (x86)\\Microsoft Web Driver\\MicrosoftWebDriver.exe\"",
                GridStarter.getEdgeDriverExecutionPathParam(config));
    }

    @Test
    public void testChromeDriverDString() throws Exception {
        Config config = new Config();
        config.getChromeDriver().setDirectory("/tmp/webdriver/chromedriver");
        if (RuntimeConfig.getOS().isWindows()) {
            assertTrue(GridStarter.getChromeDriverExecutionPathParam(config).contains("-Dwebdriver.chrome.driver=\"\\tmp\\webdriver\\chromedriver\\chromedriver_"));
        } else {
            assertTrue(GridStarter.getChromeDriverExecutionPathParam(config).contains("-Dwebdriver.chrome.driver=\"/tmp/webdriver/chromedriver/chromedriver_"));
        }
    }


    private String readFile(String filePath) {
        String returnString = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line = null;
            while ((line = reader.readLine()) != null) {
                returnString = returnString + line;
            }
        } catch (FileNotFoundException error) {
            System.out.println("File " + filePath + " does not exist, going to use default configs");
        } catch (IOException error) {
            System.out.println("Error reading" + filePath + ". Going with default configs");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    System.out.println("Error closing buffered reader");
                }
            }
        }
        return returnString;
    }

}
