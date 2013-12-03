package com.groupon.seleniumgridextras.daemons;

import com.groupon.seleniumgridextras.config.RuntimeConfig;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class WindowsDaemonTest {

  protected WindowsDaemon daemon;
  protected String expectedXml;

  @Before
  public void setUp() throws Exception {

    daemon = new WindowsDaemon();
    daemon.setLogDirectory("foo");
    daemon.setJavaExecutable("bar");
    daemon.setJarPath("foo.jar");
    daemon.setDaemonName("Bar");
    daemon.setWorkingDirectory("/tmp");
    daemon.setCheckInterval(5);

    expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-16\"?>\n"
                  + "<Task version=\"1.2\" xmlns=\"http://schemas.microsoft.com/windows/2004/02/mit/task\">\n"
                  + "  <RegistrationInfo>\n"
                  + "    <Author>" + RuntimeConfig.getOS().getUserName() + "</Author>\n"
                  + "  </RegistrationInfo>\n"
                  + "  <Triggers>\n"
                  + "    <TimeTrigger>\n"
                  + "      <Repetition>\n"
                  + "        <Interval>PT" + daemon.getCheckInterval() + "M</Interval>\n"
                  + "        <StopAtDurationEnd>false</StopAtDurationEnd>\n"
                  + "      </Repetition>\n"
                  + "      <StartBoundary>" + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()) + "</StartBoundary>\n"
                  + "      <Enabled>true</Enabled>\n"
                  + "    </TimeTrigger>\n"
                  + "  </Triggers>\n"
                  + "  <Principals>\n"
                  + "    <Principal id=\"Author\">\n"
                  + "      <UserId>" + RuntimeConfig.getOS().getUserName() + "</UserId>\n"
                  + "      <LogonType>InteractiveToken</LogonType>\n"
                  + "      <RunLevel>HighestAvailable</RunLevel>\n"
                  + "    </Principal>\n"
                  + "  </Principals>\n"
                  + "  <Settings>\n"
                  + "    <MultipleInstancesPolicy>Parallel</MultipleInstancesPolicy>\n"
                  + "    <DisallowStartIfOnBatteries>false</DisallowStartIfOnBatteries>\n"
                  + "    <StopIfGoingOnBatteries>true</StopIfGoingOnBatteries>\n"
                  + "    <AllowHardTerminate>true</AllowHardTerminate>\n"
                  + "    <StartWhenAvailable>true</StartWhenAvailable>\n"
                  + "    <RunOnlyIfNetworkAvailable>false</RunOnlyIfNetworkAvailable>\n"
                  + "    <IdleSettings>\n"
                  + "      <StopOnIdleEnd>true</StopOnIdleEnd>\n"
                  + "      <RestartOnIdle>false</RestartOnIdle>\n"
                  + "    </IdleSettings>\n"
                  + "    <AllowStartOnDemand>true</AllowStartOnDemand>\n"
                  + "    <Enabled>true</Enabled>\n"
                  + "    <Hidden>false</Hidden>\n"
                  + "    <RunOnlyIfIdle>false</RunOnlyIfIdle>\n"
                  + "    <WakeToRun>true</WakeToRun>\n"
                  + "    <ExecutionTimeLimit>P3D</ExecutionTimeLimit>\n"
                  + "    <Priority>7</Priority>\n"
                  + "  </Settings>\n"
                  + "  <Actions Context=\"Author\">\n"
                  + "    <Exec>\n"
                  + "      <Command>\"bar.exe\"</Command>\n"
                  + "      <Arguments>-jar \"foo.jar\"</Arguments>\n"
                  + "      <WorkingDirectory>/tmp</WorkingDirectory>\n"
                  + "    </Exec>\n"
                  + "  </Actions>\n"
                  + "</Task>";
  }

  @Test
  public void testCreateDaemonCommand() throws Exception {
    assertEquals("schtasks /create /tn foo /xml bar", daemon.createDaemonCommand("foo", "bar"));
  }

  @Test
  public void testDeleteDaemonCommand() throws Exception {
    assertEquals("schtasks /delete /F /tn foo", daemon.deleteDaemonCommand("foo"));
  }

  @Test
  public void testGetXml() throws Exception {
    assertEquals(expectedXml, daemon.getXml());
  }
}
