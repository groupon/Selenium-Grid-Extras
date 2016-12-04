package com.groupon.seleniumgridextras.downloader;

import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GeckoDriverDownloaderTest {

  private GeckoDriverDownloader downloader;
  private final String downloadDir = "/tmp/download_gecko_driver_test";
  private File testDir = new File("gecko_downloader_test");
  private final String VERSION = "0.10.0";
  
  @Before
  public void setUp() throws Exception {
    RuntimeConfig.setConfigFile("gecko_download_test.json");
    Config config = new Config();

    config.getGeckoDriver().setDirectory(downloadDir);
    config.writeToDisk(RuntimeConfig.getConfigFile());
    RuntimeConfig.load();

    testDir.mkdir();

    downloader = new GeckoDriverDownloader(VERSION);
    downloader.setDestinationDir(testDir.getAbsolutePath());
  }

  @After
  public void tearDown() throws Exception {
    new File(RuntimeConfig.getConfigFile()).delete();
    new File(RuntimeConfig.getConfigFile() + ".example").delete();
    String EXPECTED_FILENAME = "geckodriver";
    String EXPECTED_FILENAME_COMPRESSED = "geckodriver_" + VERSION + ".tar";
    if(RuntimeConfig.getOS().isWindows()) {
      EXPECTED_FILENAME = "geckodriver_" + VERSION + ".exe";
      EXPECTED_FILENAME_COMPRESSED = "geckodriver_" + VERSION + ".zip";
    } else {
      String EXPECTED_FILENAME_COMPRESSED2 = "geckodriver_" + VERSION + ".tar.gz";
      new File(testDir, EXPECTED_FILENAME_COMPRESSED2).delete();
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
    assertEquals("macos", downloader.getMacName());
    assertEquals("win64", downloader.getWindowsName());
    assertEquals("linux64", downloader.getLinuxName());
  }
  
  @Test
  public void testDownload() throws Exception {
    String EXPECTED_FILENAME = "geckodriver";
    if(RuntimeConfig.getOS().isWindows()) {
      EXPECTED_FILENAME = "geckodriver" + ".exe";
    }
    File expectedFile = new File(testDir, EXPECTED_FILENAME);

    assertEquals("Download failed.", true, downloader.download());
    assertEquals("Expected File missing.", true, expectedFile.exists());

    assertTrue(expectedFile.length() > (1024000)*2);
    assertTrue(expectedFile.length() < (1024000)*4);
  }

}
