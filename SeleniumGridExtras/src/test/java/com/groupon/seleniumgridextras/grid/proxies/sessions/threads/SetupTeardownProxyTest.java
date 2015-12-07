package com.groupon.seleniumgridextras.grid.proxies.sessions.threads;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.DefaultConfig;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.config.capabilities.BrowserType;
import com.groupon.seleniumgridextras.grid.proxies.SetupTeardownProxy;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.grid.internal.ExternalSessionKey;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.internal.TestSlot;
import org.openqa.selenium.remote.CapabilityType;

import java.io.File;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.Future;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class SetupTeardownProxyTest {

    private TestSession mockTestSession;

    @Before
    public void setUp() throws Exception {
        // Delete the session log directory if it previously existed so that these tests don't depend on what's in the file system.
        File sessionLogDir = DefaultConfig.SESSION_LOG_DIRECTORY;
        if (sessionLogDir.exists()) {
            FileUtils.deleteDirectory(sessionLogDir);
        }

        RuntimeConfig.setConfigFile("setup_teardown_proxy_test.json");
        Config config = new Config(true);
        config.writeToDisk(RuntimeConfig.getConfigFile());


        // Load default configs.
        RuntimeConfig.load();

        // Mock a TestSession so we don't need a full-blown Hub & Nodes running to test the session logging!
        final String externalKey = UUID.randomUUID().toString();
        final String internalKey = UUID.randomUUID().toString();

        mockTestSession = mock(TestSession.class);
        when (mockTestSession.getExternalKey()).thenReturn(new ExternalSessionKey(externalKey));
        when (mockTestSession.getInternalKey()).thenReturn(internalKey);
        when (mockTestSession.getRequestedCapabilities()).thenReturn(
            ImmutableMap.<String, Object>of(CapabilityType.BROWSER_NAME, BrowserType.CHROME)
        );

        TestSlot mockTestSlot = mock(TestSlot.class);
        when (mockTestSlot.getRemoteURL()).thenReturn(new URL("http://localhost:5555"));

        when (mockTestSession.getSlot()).thenReturn(mockTestSlot);

    }

    @After
    public void tearDown() throws Exception {
        File config = new File(RuntimeConfig.getConfigFile());
        config.delete();
        new File(RuntimeConfig.getConfigFile() + ".example").delete();
    }

    @Test
    public void testLogSessionWithSessionHistoryEnabled() throws Exception {
        Assert.assertNotNull(RuntimeConfig.getConfig());
        // Turn on session history
        RuntimeConfig.getConfig().setEnableSessionHistory("1");

        Future<String> future = SetupTeardownProxy.logNewSessionHistoryAsync(mockTestSession);
        Assert.assertNotNull(future);

        // Wait for the async task to complete logging.
        String result = future.get();
        Assert.assertTrue(!Strings.isNullOrEmpty(result));

        File sessionLogDir = DefaultConfig.SESSION_LOG_DIRECTORY;
        Assert.assertTrue("Expect the session log directory to exist!", sessionLogDir.exists());
        Assert.assertTrue("Expect the session log directory to be a directory!", sessionLogDir.isDirectory());
        Assert.assertTrue("Expect the session log directory to have a file!", sessionLogDir.list().length > 0);
    }

    @Test
    public void testLogSessionWithSessionHistoryDisabled() {
        Assert.assertNotNull(RuntimeConfig.getConfig());
        // Turn off session history
        RuntimeConfig.getConfig().setEnableSessionHistory("0");

        // The Future returned is null when session history is disabled.
        Future<String> future = SetupTeardownProxy.logNewSessionHistoryAsync(mockTestSession);
        Assert.assertNull(future);

        File sessionLogDir = DefaultConfig.SESSION_LOG_DIRECTORY;
        Assert.assertFalse("Expect the session log directory to NOT exist when disabled!", sessionLogDir.exists());
    }

}
