package com.groupon.seleniumgridextras.tasks;

import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.sun.net.httpserver.HttpServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;

import static org.junit.Assert.assertEquals;

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
    assertEquals(
        "FOR /F \"usebackq tokens=2\" %i IN (`tasklist /V ^| findstr \"5555.bat\"`) DO taskkill /PID %i",
        task.getWindowsCommand(
            "5555"));
  }

  @Test
  public void testGetLinuxCommand() throws Exception {
    String expected = "kill -9 " + RuntimeConfig.getOS().getCurrentPid();
    assertEquals(expected, task.getLinuxCommand(port));

    final String portToNeverBeFound = "4444444444444444444444444444444";
    assertEquals("", task.getLinuxCommand(portToNeverBeFound));
  }

}
