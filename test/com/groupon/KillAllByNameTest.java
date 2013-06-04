package com.groupon;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA. User: dima Date: 6/4/13 Time: 4:27 PM To change this template use
 * File | Settings | File Templates.
 */
public class KillAllByNameTest {

  private ExecuteOSTask task;
  private String windowsCommand;
  private String linuxCommnad;
  private String macCommand;

  @Before
  public void setUp() throws Exception {
    task = new KillAllByName();
    windowsCommand = "taskkill -F -IM test";
    linuxCommnad = "killall -v -m test";
    macCommand = linuxCommnad;
  }

  @Test
  public void testGetDescription() throws Exception {
    assertEquals("Executes os level kill command on a given PID name", task.getDescription());
  }

  @Test
  public void testGetEndpoint() throws Exception {
    assertEquals("/kill_all_by_name", task.getEndpoint());
  }

  @Test
  public void testgetWindowsCommand() throws Exception {
    assertEquals(windowsCommand, task.getWindowsCommand("test"));
  }

  @Test
  public void testgetMacCommand() throws Exception {
    assertEquals(macCommand, task.getMacCommand("test"));
  }

  @Test
  public void testgetLinuxCommand() throws Exception {
    assertEquals(linuxCommnad, task.getLinuxCommand(""));
  }

  @Test(expected = RuntimeException.class)
  public void testGetLinuxCommandNoParam() throws Exception {
    task.getLinuxCommand();
  }

  @Test(expected = RuntimeException.class)
  public void testGetMacCommandNoParam() throws Exception {
    task.getMacCommand();
  }

  @Test(expected = RuntimeException.class)
  public void testGetWindowsCommandNoParam() throws Exception {
    task.getWindowsCommand();
  }

}
