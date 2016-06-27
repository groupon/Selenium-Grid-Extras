package com.groupon.seleniumgridextras.downloader;

import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MarionetteDriverDownloaderTest {

  private MarionetteDriverDownloader downloader;
  private final String downloadDir = "/tmp/download_marionette_driver_test";
  private File testDir = new File("marionette_downloader_test");
  private final String VERSION = "0.8.0";
  
  @Before
  public void setUp() throws Exception {
    RuntimeConfig.setConfigFile("marionette_download_test.json");
    Config config = new Config();

    config.getMarionetteDriver().setDirectory(downloadDir);
    config.writeToDisk(RuntimeConfig.getConfigFile());
    RuntimeConfig.load();

    testDir.mkdir();

    downloader = new MarionetteDriverDownloader(VERSION);
    downloader.setDestinationDir(testDir.getAbsolutePath());
  }

  @After
  public void tearDown() throws Exception {
    new File(RuntimeConfig.getConfigFile()).delete();
    new File(RuntimeConfig.getConfigFile() + ".example").delete();
    String EXPECTED_FILENAME = "marionettedriver_" + VERSION;
    String EXPECTED_FILENAME_COMPRESSED = "marionettedriver_" + VERSION + ".gz";
    if(RuntimeConfig.getOS().isWindows()) {
      EXPECTED_FILENAME = "marionettedriver_" + VERSION + ".exe";
      EXPECTED_FILENAME_COMPRESSED = "marionettedriver_" + VERSION + ".zip";
    }
    new File(testDir, EXPECTED_FILENAME).delete();
    new File(testDir, EXPECTED_FILENAME_COMPRESSED).delete();
    new File(downloader.getDestinationDir()).delete();
  }

  @Test
  public void testSetSourceURL() throws Exception {
    downloader.setSourceURL("google.com");
    assertEquals("google.com", downloader.getSourceURL());
  }

  @Test
  public void testSetDestinationDir() throws Exception {
    assertEquals(testDir.getAbsolutePath(), downloader.getDestinationDir());
    downloader.setDestinationDir("temp");
    assertEquals("temp", downloader.getDestinationDir());
  }

  @Test
  public void testSetVersion() throws Exception {
    downloader.setVersion("5");
    assertEquals("5", downloader.getVersion());
  }

  @Test
  public void testGetOSNames() throws Exception {
    assertEquals("OSX", downloader.getMacName());
    assertEquals("win32", downloader.getWindownsName());
    assertEquals("linux64", downloader.getLinuxName());
  }
  
  @Test
  public void testDownload() throws Exception {
    String EXPECTED_FILENAME = "marionettedriver_" + VERSION;
    if(RuntimeConfig.getOS().isWindows()) {
      EXPECTED_FILENAME = "marionettedriver_" + VERSION + ".exe";
    }
    File expectedFile = new File(testDir, EXPECTED_FILENAME);

    assertEquals(true, downloader.download());
    assertEquals(true, expectedFile.exists());

    assertTrue(expectedFile.length() > (1024000)*2);
    assertTrue(expectedFile.length() < (1024000)*4);
  }

}
