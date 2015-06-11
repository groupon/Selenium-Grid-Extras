package com.groupon.seleniumgridextras.tasks;

import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.PortChecker;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import com.sun.net.httpserver.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StopGridTest {

    private StopGrid task;
    private HttpServer server;
    private final int port = 9999;


    @Before
    public void setUp() throws Exception {
        task = new StopGrid();
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(null);
        server.start();
    }

    @After
    public void tearDown() throws Exception {
        server.stop(0);
    }

    @Test
    public void testGetWindowsCommand() throws Exception {

        JsonObject portInfo = PortChecker.getParsedPortInfo("5555");

        if (portInfo.has(JsonCodec.OS.PID)) {
            assertEquals("", task.getWindowsCommand());
        } else {
            assertTrue(task.getWindowsCommand().contains("taskkill -F -IM "));
        }
    }

    @Test
    public void testGetLinuxCommand() throws Exception {
        String expected = "kill -9 " + RuntimeConfig.getOS().getCurrentPid();
        assertEquals(expected, task.getLinuxCommand(port));

        final String portToNeverBeFound = "4444444444444444444444444444444";
        assertEquals("", task.getLinuxCommand(portToNeverBeFound));
    }

}
