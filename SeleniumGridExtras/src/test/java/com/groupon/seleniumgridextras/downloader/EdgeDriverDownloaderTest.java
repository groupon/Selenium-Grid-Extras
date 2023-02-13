package com.groupon.seleniumgridextras.downloader;

import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.downloader.webdriverreleasemanager.WebDriverReleaseManager;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class EdgeDriverDownloaderTest {

  private EdgeDriverDownloader downloader;
  private final String downloadDir = "/tmp/download_edge_driver_test";

  @Before
  public void setUp() throws Exception {
    RuntimeConfig.setConfigFile("edge_download_test.json");
    Config config = new Config();

    config.getEdgeDriver().setDirectory(downloadDir);
    config.writeToDisk(RuntimeConfig.getConfigFile());
    RuntimeConfig.load();
    downloader = new EdgeDriverDownloader("86.0.598.0", "32");
  }

  @After
  public void tearDown() throws Exception {
    new File(RuntimeConfig.getConfigFile()).delete();
    new File(RuntimeConfig.getConfigFile() + ".example").delete();
    new File(downloader.getDestinationDir()).delete();
  }

  @Test
  public void testSetSourceURL() throws Exception {
    downloader.setSourceURL("google.com");
    assertEquals("google.com", downloader.getSourceURL());

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
