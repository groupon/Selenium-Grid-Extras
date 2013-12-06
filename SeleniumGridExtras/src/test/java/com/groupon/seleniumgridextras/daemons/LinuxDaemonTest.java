package com.groupon.seleniumgridextras.daemons;

import org.junit.Test;

import org.junit.Before;

import static org.junit.Assert.assertEquals;


public class LinuxDaemonTest {

  private LinuxDaemon daemon;
  private String expectedCron;
  private String expectedInstallCron;
  private final String expectedOutFile = "/tmp/DaemonName.out";
  private String expectedUninstallCommand;

  @Before
  public void setUp() throws Exception {
    daemon = new LinuxDaemon();
    daemon.setLogDirectory("/tmp");
    daemon.setJavaExecutable("/usr/local/bin/java");
    daemon.setJarPath("dima.jar");
    daemon.setWorkingDirectory("/home/builduser/dima");
    daemon.setCheckInterval(2);
    daemon.setDaemonName("DaemonName");

    expectedCron =
        "0-59/2 * * * * bash -i -c 'cd /home/builduser/dima; /usr/local/bin/java -jar dima.jar 2>&1 >> "+ expectedOutFile +"'";

    expectedInstallCron = "crontab -l | awk \"{print} END {print \\\"" + expectedCron + "\\\"}\" | crontab";

    expectedUninstallCommand = "crontab -l | sed \"/DaemonName.out/d\" | crontab";

  }

  @Test
  public void testCronOutputFile() throws Exception {
    assertEquals(expectedOutFile, daemon.getCronOutputFile().getAbsolutePath());
  }

  @Test
  public void testUninstallCommand() throws Exception {
    assertEquals(expectedUninstallCommand, daemon.getUninstallCommand());
  }

  @Test
  public void testGetCronJob() throws Exception {
    assertEquals(expectedCron, daemon.getCronJob());
  }

  @Test
  public void testGetInstallCommand() throws Exception {
    assertEquals(expectedInstallCron, daemon.getInstallCommand());
  }
}
