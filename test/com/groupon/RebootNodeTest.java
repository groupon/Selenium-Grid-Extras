package com.groupon;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA. User: dima Date: 6/4/13 Time: 5:15 PM To change this template use
 * File | Settings | File Templates.
 */
public class RebootNodeTest {

  private ExecuteOSTask task;
  private String windowsCommand;

  @Before
  public void setUp() throws Exception {
    task = new RebootNode();
    windowsCommand = "shutdown -r -t 1 -f";
  }

  @Test
  public void testGetDescription() throws Exception {

    assertEquals("Restart the host node", task.getDescription());
  }

  @Test
  public void testGetEndpoint() throws Exception {
    assertEquals("/reboot", task.getEndpoint());
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
