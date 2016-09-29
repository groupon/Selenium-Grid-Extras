package com.groupon.seleniumgridextras.downloader.webdriverreleasemanager;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WebDriverReleaseTest {


  @Test
  public void testSeleniumStandAlone239() throws Exception {
    String name = "2.39/selenium-server-standalone-2.39.0.jar";
    WebDriverRelease release = new WebDriverRelease(name);

    assertEquals("selenium-server-standalone", release.getName());
    assertEquals(2, release.getMajorVersion());
    assertEquals(39, release.getMinorVersion());
    assertEquals(0, release.getPatchVersion());

    assertEquals(name, release.getRelativePath());


  }

  @Test
  public void testSeleniumStandAlone2411() throws Exception {
    String name = "2.41/selenium-server-standalone-2.41.1.jar";
    WebDriverRelease release = new WebDriverRelease(name);

    assertEquals("selenium-server-standalone", release.getName());
    assertEquals(2, release.getMajorVersion());
    assertEquals(41, release.getMinorVersion());
    assertEquals(1, release.getPatchVersion());

    assertEquals(name, release.getRelativePath());


  }

  @Test
  public void testIEDriver32Bit() throws Exception {
    String name = "2.39/IEDriverServer_Win32_2.39.0.zip";
    WebDriverRelease release = new WebDriverRelease(name);


    assertEquals(2, release.getMajorVersion());
    assertEquals(39, release.getMinorVersion());
    assertEquals(0, release.getPatchVersion());

    assertEquals("IEDriverServer", release.getName());
    assertEquals(name, release.getRelativePath());

  }

  @Test
  public void testIEDriver64Bit() throws Exception {
    String name = "2.40/IEDriverServer_x64_2.40.0.zip";
    WebDriverRelease release = new WebDriverRelease(name);


    assertEquals(0, release.getMajorVersion());
    assertEquals(0, release.getMinorVersion());
    assertEquals(0, release.getPatchVersion());

    assertEquals(null, release.getName());
    assertEquals(null, release.getRelativePath());

  }

  @Test
  public void testPrettyPrintVersion() throws Exception{
    WebDriverRelease ieDriverRelease = new WebDriverRelease("2.39/IEDriverServer_Win32_2.39.0.zip");
    WebDriverRelease wdDriverRelease = new WebDriverRelease("2.41/selenium-server-standalone-2.41.1.jar");

    assertEquals("2.39.0", ieDriverRelease.getPrettyPrintVersion("."));
    assertEquals("2.41.1", wdDriverRelease.getPrettyPrintVersion("."));
  }

  @Test
  public void testComparableVersion() throws Exception{
    WebDriverRelease ieDriverRelease = new WebDriverRelease("2.39/IEDriverServer_Win32_2.39.0.zip");
    WebDriverRelease wdDriverRelease = new WebDriverRelease("2.41/selenium-server-standalone-2.41.1.jar");

    assertEquals(203900, ieDriverRelease.getComparableVersion());
    assertEquals(204101, wdDriverRelease.getComparableVersion());

  }

  @Test
  public void testChromeDriverRelease() throws Exception{
    WebDriverRelease chromeRelease = new ChromeDriverRelease("2.10");

    assertEquals("2.10", chromeRelease.getPrettyPrintVersion("."));
    assertEquals("chromedriver", chromeRelease.getName());
    assertEquals("index.html?path=2.10/", chromeRelease.getRelativePath());

  }

  @Test
  public void testMarionetteDriverRelease() throws Exception{
    WebDriverRelease marionetteRelease = new GeckoDriverRelease("0.10.0");

    assertEquals("0.10.0", marionetteRelease.getPrettyPrintVersion("."));
    assertEquals("geckodriver", marionetteRelease.getName());

  }
}
