package com.groupon.seleniumgridextras.downloader.webdriverreleasemanager;


import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.groupon.seleniumgridextras.config.DefaultConfig;
import com.groupon.seleniumgridextras.downloader.GitHubDownloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class WebDriverReleaseManager {

  private static final String WEBDRIVER_JAR = "webdriver-jar";
  private static final String IE_DRIVER = "ie-driver";
  private static final String CHROME_DRIVER = "chrome-driver";
  private static final String GECKO_DRIVER = "gecko-driver";
  private WebDriverRelease latestWebdriverVersion;
  private WebDriverRelease latestIEDriverVersion;
  private WebDriverRelease latestChromeDriverVersion;
  private WebDriverRelease latestGeckoDriverVersion;

  private Document parsedXml;
  private static Logger logger = Logger.getLogger(WebDriverReleaseManager.class);


  private Map<String, List<WebDriverRelease>> allProducts;

  private void initialize() {
    allProducts = new HashMap<String, List<WebDriverRelease>>();
    allProducts.put(WEBDRIVER_JAR, new LinkedList<WebDriverRelease>());
    allProducts.put(IE_DRIVER, new LinkedList<WebDriverRelease>());
    allProducts.put(CHROME_DRIVER, new LinkedList<WebDriverRelease>());
    allProducts.put(GECKO_DRIVER, new LinkedList<WebDriverRelease>());
  }

  public WebDriverReleaseManager(URL webDriverAndIEDriverURL, URL chromeDriverVersionURL, URL geckoDriverVersionURL)
      throws DocumentException {

    logger.info("Checking the latest version of WebDriver, IEDriver, ChromeDriver and GeckoDriver from "
                       + webDriverAndIEDriverURL.toExternalForm() + " and " + chromeDriverVersionURL + " and " + geckoDriverVersionURL
        .toExternalForm());
    initialize();

    SAXReader reader = new SAXReader();
    parsedXml = reader.read(webDriverAndIEDriverURL);
    loadWebDriverAndIEDriverVersions(parsedXml);
    loadChromeDriverVersionFromURL(chromeDriverVersionURL);
    loadGeckoDriverVersionFromURL(geckoDriverVersionURL);
  }

  public int getWebdriverVersionCount() {
    return allProducts.get(WEBDRIVER_JAR).size();
  }

  public int getIEDriverVersionCount() {
    return allProducts.get(IE_DRIVER).size();
  }

  public WebDriverRelease getWedriverLatestVersion() {
    if (this.latestWebdriverVersion == null) {
      this.latestWebdriverVersion = findLatestRelease(allProducts.get(WEBDRIVER_JAR));
    }

    return this.latestWebdriverVersion;
  }

  public WebDriverRelease getIeDriverLatestVersion() {

    if (this.latestIEDriverVersion == null) {
      this.latestIEDriverVersion = findLatestRelease(allProducts.get(IE_DRIVER));
    }

    return this.latestIEDriverVersion;
  }

  public WebDriverRelease getChromeDriverLatestVersion() {
    return this.latestChromeDriverVersion;
  }

  public WebDriverRelease getGeckoDriverLatestVersion() {
	return this.latestGeckoDriverVersion;
  }

  /*
   * Choose the greatest major version.
   * If major version is the same, choose the greatest comparable version.
   */
  private WebDriverRelease findLatestRelease(List<WebDriverRelease> list) {

    WebDriverRelease highestVersion = null;

    for (WebDriverRelease r : list) {

      if (highestVersion == null) {
        highestVersion = r;
      } else if (r.getMajorVersion() > highestVersion.getMajorVersion()) {
        highestVersion = r;
      } else if (r.getComparableVersion() > highestVersion.getComparableVersion()) {
        highestVersion = r;
      }
    }

    return highestVersion;
  }

  public void loadChromeDriverVersionFromURL(URL url) {
    InputStream in = null;
    try {
      in = url.openStream();
      loadChromeDriverVersion(IOUtils.toString(in));
    } catch (IOException e) {
      logger.error("Something went wrong when trying to get latest chrome driver version");
      logger.error(e.toString());
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    } finally {
      IOUtils.closeQuietly(in);
    }
  }

  public void loadChromeDriverVersion(String version) {
    this.latestChromeDriverVersion = new ChromeDriverRelease(version);
  }

  public void loadGeckoDriverVersionFromURL(URL url) {
    GitHubDownloader downloader = new GitHubDownloader(url.toString());

    String latestVersion = DefaultConfig.getGeckoDriverDefaultVersion();
    try {
      List<Map<String, String>> downloadableAssets = downloader.getAllDownloadableAssets();
      String latestName = (String) downloadableAssets.get(0).keySet().toArray()[0];
      latestVersion = latestName.substring(latestName.indexOf("-")+1, latestName.lastIndexOf("-"));
	} catch (Exception e) {
      logger.error(e);
	}
    loadGeckoDriverVersion(latestVersion);
  }

  public void loadGeckoDriverVersion(String version) {
    this.latestGeckoDriverVersion = new GeckoDriverRelease(version);
  }

  public void loadWebDriverAndIEDriverVersions(Document xml) {
    Element root = xml.getRootElement();
    for (Iterator i = root.elementIterator("Contents"); i.hasNext(); ) {
      Element node = (Element) i.next();

      WebDriverRelease release = new WebDriverRelease(node.elementText("Key"));

      if (release.getName() == null) {

      } else if (release.getName().equals("selenium-server-standalone")) {
        allProducts.get(WEBDRIVER_JAR).add(release);
      } else {
        allProducts.get(IE_DRIVER).add(release);
      }
    }
  }

}
