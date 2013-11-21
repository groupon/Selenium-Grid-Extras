package com.groupon.seleniumgridextras.daemons;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DaemonWrapperTest {

  DaemonWrapper daemon;

  @Before
  public void setUp() throws Exception {
    daemon = new DaemonWrapper();
    daemon.setLogDirectory("foo");
    daemon.setJavaExecutable("bar");
    daemon.setJarPath("foo.jar");
    daemon.setDaemonName("bar");
    daemon.setWorkingDirectory("/tmp");
    daemon.setCheckInterval(50);
  }


  @Test
  public void testSetLogDirectory() throws Exception {
    assertEquals("foo", daemon.getLogDirectory());
  }

  @Test
  public void testSetJavaExecutable() throws Exception {
    assertEquals("bar", daemon.getJavaExecutable());
  }


  @Test
  public void testSetJarPath() throws Exception {
    assertEquals("foo.jar", daemon.getJarPath());
  }

  @Test
  public void testSetDaemonName() throws Exception {
    assertEquals("bar", daemon.getDaemonName());
  }

  @Test
  public void testSetWorkingDirectory() throws Exception {
    assertEquals("/tmp", daemon.getWorkingDirectory());
  }

  @Test
  public void testSetCheckInterval() throws Exception {
    assertEquals(50, daemon.getCheckInterval());
  }

}
