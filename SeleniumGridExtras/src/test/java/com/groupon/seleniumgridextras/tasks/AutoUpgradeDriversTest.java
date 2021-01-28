package com.groupon.seleniumgridextras.tasks;

import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.DefaultConfig;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AutoUpgradeDriversTest {

    public AutoUpgradeDrivers task;
    private String downloadDir = "/tmp/update_driver_test";

    @Before
    public void setUp() throws Exception {
        RuntimeConfig.setConfigFile("update_driver_test.json");
        Config config = DefaultConfig.getDefaultConfig();
        config.getChromeDriver().setDirectory(downloadDir);
        config.writeToDisk(RuntimeConfig.getConfigFile());
        RuntimeConfig.load();
        task = new AutoUpgradeDrivers();
    }

    @Test
    public void testAutoUpdate() throws Exception {
        task.execute();
    }
}