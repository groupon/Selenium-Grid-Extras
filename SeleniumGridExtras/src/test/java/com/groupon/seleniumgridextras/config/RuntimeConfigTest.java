package com.groupon.seleniumgridextras.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


public class RuntimeConfigTest {

    @Before
    public void setUp() throws Exception {
        RuntimeConfig.clearConfig();
        RuntimeConfig.setConfigFile("merge_configs_test.json");

    }

    @After
    public void tearDown() throws Exception {
        File config = new File(RuntimeConfig.getConfigFile());
        config.delete();
        new File(RuntimeConfig.getConfigFile() + ".example").delete();
    }

    @Test
    public void testLoadDefaultsOnly() throws Exception {
        assertNull(RuntimeConfig.getConfig());

        writeEmptyConfig();
        RuntimeConfig.load();

        assertNotNull(RuntimeConfig.getConfig());
        assertNotNull(RuntimeConfig.getConfig().getWebdriver().getVersion());
        assertEquals(File.separator + "tmp" + File.separator + "webdriver", RuntimeConfig.getConfig().getWebdriver().getDirectory());

        List<String> expectedSetup = new LinkedList<String>();
        expectedSetup.add("com.groupon.seleniumgridextras.tasks.MoveMouse");

        assertEquals(expectedSetup, RuntimeConfig.getConfig().getSetup());

        assertEquals(true, RuntimeConfig.getConfig().getAutoStartNode());
        assertEquals(false, RuntimeConfig.getConfig().getAutoStartHub());

    }

    @Test
    public void testLoadWithOverwrites() throws Exception {
        assertNull(RuntimeConfig.getConfig());

        loadOverwriteConfigs();
        RuntimeConfig.load();

        assertNotNull(RuntimeConfig.getConfig());
        assertEquals("1.1.1.1.1", RuntimeConfig.getConfig().getWebdriver().getVersion());
        assertEquals("some_dir", RuntimeConfig.getConfig().getWebdriver().getDirectory());

        assertEquals("64", RuntimeConfig.getConfig().getChromeDriver().getBit());
        assertEquals("40.0.1.0", RuntimeConfig.getConfig().getChromeDriver().getVersion());

        List<String> expectedSetup = new LinkedList<String>();
        assertEquals(expectedSetup, RuntimeConfig.getConfig().getSetup());

        assertEquals(false, RuntimeConfig.getConfig().getAutoStartNode());
        assertEquals(true, RuntimeConfig.getConfig().getAutoStartHub());


    }

    private void writeEmptyConfig() throws Exception {
        File f = new File(RuntimeConfig.getConfigFile());
        FileUtils.writeStringToFile(f, "{}");
    }

    private void loadOverwriteConfigs() throws Exception {
        JsonObject fullConfig = new JsonObject();
        JsonObject theConfigMap = new JsonObject();
        JsonObject chromedriver = new JsonObject();

        JsonObject webdriver = new JsonObject();
        webdriver.addProperty("directory", "some_dir");
        webdriver.addProperty("version", "1.1.1.1.1");

        chromedriver.addProperty("bit", "64");
        chromedriver.addProperty("version", "40.0.1.0");

        theConfigMap.add("webdriver", webdriver);
        theConfigMap.add("chromedriver", chromedriver);
        theConfigMap.add("setup", new JsonArray());
        theConfigMap.addProperty("auto_start_hub", "1");
        theConfigMap.addProperty("auto_start_node", "0");
        theConfigMap.addProperty("days_until_log_deletion", "7");


        File f = new File(RuntimeConfig.getConfigFile());

        fullConfig.add("theConfigMap", theConfigMap);

        FileUtils.writeStringToFile(f, fullConfig.toString());

    }

}
