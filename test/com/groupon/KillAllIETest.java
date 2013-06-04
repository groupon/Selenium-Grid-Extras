package com.groupon;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA. User: dima Date: 6/4/13 Time: 4:36 PM To change this template use
 * File | Settings | File Templates.
 */
public class KillAllIETest {
  private ExecuteOSTask task;
  private String windowsCommand;
  private String linuxCommnad;
  private String macCommand;

  @Before
  public void setUp() throws Exception {
    task = new KillAllIE();
    windowsCommand = "taskkill -F -IM iexplore.exe";
  }

  @Test
  public void testGetDescription() throws Exception {

    assertEquals("Executes os level kill command on all instance of Internet Explorer", task.getDescription());
  }

  @Test
  public void testGetEndpoint() throws Exception {
    assertEquals("/kill_ie", task.getEndpoint());
  }

  @Test
  public void testgetWindowsCommand() throws Exception {
    assertEquals(windowsCommand, task.getWindowsCommand());
  }

  @Test(expected=RuntimeException.class)
  public void testgetMacCommand() throws Exception {
    task.getMacCommand();
  }

  @Test(expected=RuntimeException.class)
  public void testgetLinuxCommand() throws Exception {
    task.getLinuxCommand();
  }
}
