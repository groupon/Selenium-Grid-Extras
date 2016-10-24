package com.groupon.seleniumgridextras.grid;

import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class GridStarterAdditionalHubClasspathTest {

    private static final String CLASSPATH_ITEM_1 = "/opt/selenium/prioritizer.jar";
    private static final String CLASSPATH_ITEM_2 = "/opt/selenium/lib/capabilitymatcher.jar";

    private final String configFileName = "grid_start_additional_hub_classpath.json";

    private static final String START_HUB_BAT = "start_hub.bat";
    private static final String GRID_HUB_LOG = "grid_hub.log";
    private final String logFile = "foo.log";
    private final String windowsBatchFileName = logFile.replace("log", "bat");

    private Config config;
    
    @Before
    public void setUp(){
        RuntimeConfig.setConfigFile(configFileName);
        config = new Config();
        config.addHubClasspathItem(CLASSPATH_ITEM_1);
        config.addHubClasspathItem(CLASSPATH_ITEM_2);
        config.writeToDisk(RuntimeConfig.getConfigFile());
        RuntimeConfig.load();
    }

    @After
    public void tearDown() {

        new File(START_HUB_BAT).delete();
        new File(GRID_HUB_LOG).delete();
        new File(windowsBatchFileName).delete();
        new File(configFileName).delete();
        new File(RuntimeConfig.getConfigFile() + ".example").delete();
    }

    @Test
    public void testAdditionalClasspathItemsArePresent() {
        String[] command = GridStarter.getOsSpecificHubStartCommand(configFileName, RuntimeConfig.getOS().isWindows());
        StringBuilder sb = new StringBuilder();
        for(String part : command) {
          sb.append(part + " ");
        }
        assertTrue(sb.toString().contains(RuntimeConfig.getOS().getPathSeparator() + CLASSPATH_ITEM_1 + RuntimeConfig.getOS().getPathSeparator()));
        assertTrue(sb.toString().contains(RuntimeConfig.getOS().getPathSeparator() + CLASSPATH_ITEM_2 + RuntimeConfig.getOS().getPathSeparator()));
    }
}
