package com.groupon.seleniumgridextras.downloader;

import com.groupon.seleniumgridextras.downloader.webdriverreleasemanager.WebDriverReleaseManager;

import org.junit.Before;
import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


public class WebDriverReleaseManagerTest {

  private WebDriverReleaseManager releaseManager;

  @Before
  public void setUp() throws Exception {
      URL webDriverAndIEDriverURL = ClassLoader.getSystemResource("fixtures/selenium_release_manifest.xml");
      URL chromeDriverVersionURL = ClassLoader.getSystemResource("fixtures/selenium_release_version.txt");
      URL marionetteDriverVersionURL = ClassLoader.getSystemResource("fixtures/selenium_release_version.txt");
      releaseManager = new WebDriverReleaseManager(webDriverAndIEDriverURL, chromeDriverVersionURL, marionetteDriverVersionURL);
  }

  @Test
  public void testReleaseCounts() throws Exception {
    assertEquals(3, releaseManager.getWebdriverVersionCount());
    assertEquals(3, releaseManager.getIEDriverVersionCount());
  }

  @Test
  public void testGetLatestVersion() throws Exception {
    assertEquals("2.41.0", releaseManager.getWedriverLatestVersion().getPrettyPrintVersion("."));
    assertEquals("2.41.0", releaseManager.getIeDriverLatestVersion().getPrettyPrintVersion("."));
    assertEquals("2.10", releaseManager.getChromeDriverLatestVersion().getPrettyPrintVersion("."));
  }

  @Test
  public void testGetVersionsFromLiveSource() throws Exception {
    String wdManifest = "http://selenium-release.storage.googleapis.com/";
    String chromeManifest = "http://chromedriver.storage.googleapis.com/LATEST_RELEASE";
    String marionetteManifest = "http://chromedriver.storage.googleapis.com/LATEST_RELEASE";

    WebDriverReleaseManager
        manager =
        new WebDriverReleaseManager(new URL(wdManifest), new URL(chromeManifest), new URL(marionetteManifest));

    assertNotEquals(null, manager.getWedriverLatestVersion().getPrettyPrintVersion("."));
    assertNotEquals(null, manager.getIeDriverLatestVersion().getPrettyPrintVersion("."));
    assertNotEquals(null, manager.getChromeDriverLatestVersion().getPrettyPrintVersion("."));

    assertEquals("selenium-server-standalone", manager.getWedriverLatestVersion().getName());
    assertEquals("IEDriverServer", manager.getIeDriverLatestVersion().getName());
    assertEquals("chromedriver", manager.getChromeDriverLatestVersion().getName());

    assertNotEquals(null, manager.getWedriverLatestVersion().getRelativePath());
    assertNotEquals(null, manager.getIeDriverLatestVersion().getRelativePath());
    assertNotEquals(null, manager.getChromeDriverLatestVersion().getRelativePath());

  }


}
