package com.groupon.seleniumgridextras.daemons;

import com.groupon.seleniumgridextras.config.RuntimeConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OsXDaemonTest {

  OsXDaemon daemon;

  private final String daemonName = "com.groupon.seleniumgridextras.Bar.plist";
  private final String daemonPath = RuntimeConfig.getOS().getUserHome() + "/Library/LaunchAgents/" + daemonName;

  private final String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                                     + "<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n"
                                     + "<plist version=\"1.0\">\n"
                                     + "  <dict>\n"
                                     + "     <key>KeepAlive</key>\n"
                                     + "       <true />\n"
                                     + "     <key>Label</key>\n"
                                     + "     <string>com.groupon.seleniumgridextras.Bar.plist</string>\n"
                                     + "     <key>RunAtLoad</key>\n"
                                     + "       <true/>      \n"
                                     + "     <key>RootDirectory</key>\n"
                                     + "       <string>/tmp</string>\n"
                                     + "     <key>WorkingDirectory</key>\n"
                                     + "       <string>/tmp</string>\n"
                                     + "     <key>ProgramArguments</key>\n"
                                     + "       <array>\n"
                                     + "         <string>bar</string>\n"
                                     + "         <string>-jar</string>\n"
                                     + "         <string>foo.jar</string>\n"
                                     + "       </array>\n"
                                     + "     <key>StandardErrorPath</key>\n"
                                     + "       <string>foo/seleniung_grid_extras_err.log</string>\n"
                                     + "     <key>StandardOutPath</key>\n"
                                     + "       <string>foo/seleniung_grid_extras_out.log</string>\n"
                                     + "     <key>StartInterval</key>\n"
                                     + "       <integer>3000</integer>\n"
                                     + "  </dict>\n"
                                     + "</plist>";

  @Before
  public void setUp() throws Exception {
    if (getDaemonPath().exists()){
      getDaemonPath().delete();
    }

    daemon = new OsXDaemon();
    daemon.setLogDirectory("foo");
    daemon.setJavaExecutable("bar");
    daemon.setJarPath("foo.jar");
    daemon.setDaemonName("Bar");
    daemon.setWorkingDirectory("/tmp");
    daemon.setCheckInterval(50);
  }

  @After
  public void tearDown() throws Exception {
     if (getDaemonPath().exists()){
       getDaemonPath().delete();
     }
  }

  private File getDaemonPath(){
    return new File(daemonPath);
  }

  @Test
  public void testInstallUninstallDaemon() throws Exception{
    assertFalse(getDaemonPath().exists());
    daemon.installDaemon();
    assertTrue(getDaemonPath().exists());
    daemon.uninstallDaemon();
    assertFalse(getDaemonPath().exists());
  }

  @Test
  public void testSetDaemonName() throws Exception {
    assertEquals(daemonName, daemon.getDaemonName());
  }

  @Test
  public void testGetCheckInterval() throws Exception {
    assertEquals((50 * 60), daemon.getCheckInterval());
  }

  @Test
  public void testGetXml() throws Exception {
    assertEquals(expectedXml, daemon.getXml());
  }

  @Test
  public void testGetInitDExecutablePath() throws Exception {
    assertEquals(RuntimeConfig.getOS().getUserHome() + "/Library/LaunchAgents/" + daemonName,
                 daemon.getInitDExecutablePath());
  }
}
