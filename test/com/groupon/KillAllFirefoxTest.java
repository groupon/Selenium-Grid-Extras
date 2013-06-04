package com.groupon;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA. User: dima Date: 6/4/13 Time: 4:54 PM To change this template use
 * File | Settings | File Templates.
 */
public class KillAllFirefoxTest {

  private ExecuteOSTask task;
  private String windowsCommand;
  private String linuxCommnad;
  private String macCommand;

  @Before
  public void setUp() throws Exception {
    task = new KillAllFirefox();
    windowsCommand = "taskkill -F -IM firefox.exe";
    linuxCommnad = "killall -v -m [Ff]irefox";
    macCommand = linuxCommnad;
  }

  @Test
  public void testGetDescription() throws Exception {
    assertEquals("Executes os level kill command on all instance of Firefox", task.getDescription());
  }

  @Test
  public void testGetEndpoint() throws Exception {
    assertEquals("/kill_firefox", task.getEndpoint());
  }

  @Test
  public void testgetWindowsCommand() throws Exception {
    assertEquals(windowsCommand, task.getWindowsCommand());
  }

  @Test
  public void testgetMacCommand() throws Exception {
    assertEquals(macCommand, task.getMacCommand());
  }

  @Test
  public void testgetLinuxCommand() throws Exception {
    assertEquals(linuxCommnad, task.getLinuxCommand());
  }
}
