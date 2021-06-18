package com.groupon.seleniumgridextras.downloader;

import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class MsEdgeDriverDownloaderTest {

  private MsEdgeDriverDownloader downloader;
  private final String downloadDir = "/tmp/download_msedge_driver_test";

  @Before
  public void setUp() throws Exception {
    RuntimeConfig.setConfigFile("msedge_download_test.json");
    Config config = new Config();

    config.getMsEdgeDriver().setDirectory(downloadDir);
    config.writeToDisk(RuntimeConfig.getConfigFile());
    RuntimeConfig.load();


    downloader = new MsEdgeDriverDownloader("76.0.182.22", "32");
  }

  @After
  public void tearDown() throws Exception {
    new File(RuntimeConfig.getConfigFile()).delete();
    new File(RuntimeConfig.getConfigFile() + ".example").delete();
    new File(downloader.getDestinationDir()).delete();
  }

  @Test
  public void testSetSourceURL() throws Exception {
    downloader.setSourceURL("microsoft.com");
    assertEquals("microsoft.com", downloader.getSourceURL());

  }

  @Test
  public void testSetDestinationDir() throws Exception {
    assertEquals(downloadDir, downloader.getDestinationDir());
    downloader.setDestinationDir("temp");
    assertEquals("temp", downloader.getDestinationDir());
  }

  @Test
  public void testSetBitVersion() throws Exception {
      downloader.setBitVersion("64");
      assertEquals("64", downloader.getBitVersion());
  }

  @Test
  public void testSetVersion() throws Exception {
    downloader.setVersion("5");
    assertEquals("5", downloader.getVersion());
  }

  @Test
  public void testGetOSNames() throws Exception {
    assertEquals("mac", downloader.getMacName());
    assertEquals("win", downloader.getWindowsName());
    assertEquals("linux", downloader.getLinuxName());
  }
}
