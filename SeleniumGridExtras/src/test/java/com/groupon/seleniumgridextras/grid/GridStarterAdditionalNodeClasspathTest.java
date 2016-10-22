package com.groupon.seleniumgridextras.grid;

import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

public class GridStarterAdditionalNodeClasspathTest {

    private static final String CLASSPATH_ITEM_1 = "/opt/selenium/prioritizer.jar";
    private static final String CLASSPATH_ITEM_2 = "/opt/selenium/lib/capabilitymatcher.jar";

    private final String configFileName = "grid_start_additional_node_classpath.json";

    private static final String START_NODE_BAT = "start_node.bat";
    private static final String GRID_NODE_LOG = "grid_node.log";
    private final String logFile = "foo.log";
    private final String windowsBatchFileName = logFile.replace("log", "bat");


    private Config config;
    
    @Before
    public void setUp(){
        RuntimeConfig.setConfigFile(configFileName);
        config = new Config();
        config.addNodeClasspathItem(CLASSPATH_ITEM_1);
        config.addNodeClasspathItem(CLASSPATH_ITEM_2);
        config.getWebdriver().setVersion("3.0.1");
        config.writeToDisk(RuntimeConfig.getConfigFile());
        RuntimeConfig.load();
    }

    @After
    public void tearDown() {

        new File(START_NODE_BAT).delete();
        new File(GRID_NODE_LOG).delete();
        new File(windowsBatchFileName).delete();
        new File(configFileName).delete();
        new File(RuntimeConfig.getConfigFile() + ".example").delete();
    }

    @Test
    public void testAdditionalClasspathItemsArePresent() {
        List<String> command = GridStarter.getWebNodeStartCommand(configFileName, RuntimeConfig.getOS().isWindows(), config);
        StringBuilder sb = new StringBuilder();
        for(String part : command) {
          sb.append(part + " ");
        }
        assertTrue(sb.toString().contains(RuntimeConfig.getOS().getPathSeparator() + CLASSPATH_ITEM_1 + RuntimeConfig.getOS().getPathSeparator()));
        assertTrue(sb.toString().contains(RuntimeConfig.getOS().getPathSeparator() + CLASSPATH_ITEM_2 + RuntimeConfig.getOS().getPathSeparator()));
    }
}
