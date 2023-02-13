package com.groupon.seleniumgridextras.tasks;


import com.google.gson.JsonObject;

import com.groupon.seleniumgridextras.config.ConfigFileReader;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import com.groupon.seleniumgridextras.Version;
import com.groupon.seleniumgridextras.utilities.VersionCompare;

import org.apache.log4j.Logger;

import java.util.Map;

public class AutoUpgradeDrivers extends ExecuteOSTask {

  private static Logger logger = Logger.getLogger(AutoUpgradeDrivers.class);

  private boolean updateWebDriver = false;
  private boolean updateIEDriver = false;
  private boolean updateChromeDriver = false;
  private boolean updateGeckoDriver = false;
  private boolean updateEdgeDriver = false;

  public AutoUpgradeDrivers() {
    setEndpoint(TaskDescriptions.Endpoints.AUTO_UPGRADE_WEBDRIVER);
    setDescription(
        TaskDescriptions.Description.AUTO_UPGRADE_WEBDRIVER);
    setRequestType(TaskDescriptions.HTTP.GET);
    setResponseType(TaskDescriptions.HTTP.JSON);
    setClassname(this.getClass().getCanonicalName().toString());

    addResponseDescription(JsonCodec.WebDriver.OLD_WEB_DRIVER_JAR, "Old version of WebDriver Jar");
    addResponseDescription(JsonCodec.WebDriver.OLD_CHROME_DRIVER, "Old version of Chrome Driver");
    addResponseDescription(JsonCodec.WebDriver.OLD_GECKO_DRIVER, "Old version of Gecko Driver");
    addResponseDescription(JsonCodec.WebDriver.OLD_IE_DRIVER, "Old version of IE Driver");
    addResponseDescription(JsonCodec.WebDriver.OLD_EDGE_DRIVER, "Old version of Edge Driver");

    addResponseDescription(JsonCodec.WebDriver.NEW_WEB_DRIVER_JAR, "New versions of WebDriver Jar");
    addResponseDescription(JsonCodec.WebDriver.NEW_CHROME_DRIVER, "New version of Chrome Driver");
    addResponseDescription(JsonCodec.WebDriver.NEW_GECKO_DRIVER, "New version of Gecko Driver");
    addResponseDescription(JsonCodec.WebDriver.NEW_IE_DRIVER, "New version of IE Driver");
    addResponseDescription(JsonCodec.WebDriver.NEW_CHROME_DRIVER, "New version of Edge Driver");

    getJsonResponse().addKeyValues(JsonCodec.WebDriver.OLD_WEB_DRIVER_JAR, RuntimeConfig.getConfig().getWebdriver().getVersion());
    getJsonResponse().addKeyValues(JsonCodec.WebDriver.OLD_CHROME_DRIVER, RuntimeConfig.getConfig().getChromeDriver().getVersion());
    getJsonResponse().addKeyValues(JsonCodec.WebDriver.OLD_GECKO_DRIVER, RuntimeConfig.getConfig().getGeckoDriver().getVersion());
    getJsonResponse().addKeyValues(JsonCodec.WebDriver.OLD_IE_DRIVER, RuntimeConfig.getConfig().getIEdriver().getVersion());
    getJsonResponse().addKeyValues(JsonCodec.WebDriver.OLD_EDGE_DRIVER, RuntimeConfig.getConfig().getEdgeDriver().getVersion());

    getJsonResponse().addKeyValues(JsonCodec.WebDriver.NEW_WEB_DRIVER_JAR, RuntimeConfig.getConfig().getWebdriver().getVersion());
    getJsonResponse().addKeyValues(JsonCodec.WebDriver.NEW_CHROME_DRIVER, RuntimeConfig.getConfig().getChromeDriver().getVersion());
    getJsonResponse().addKeyValues(JsonCodec.WebDriver.NEW_GECKO_DRIVER, RuntimeConfig.getConfig().getGeckoDriver().getVersion());
    getJsonResponse().addKeyValues(JsonCodec.WebDriver.NEW_IE_DRIVER, RuntimeConfig.getConfig().getIEdriver().getVersion());
    getJsonResponse().addKeyValues(JsonCodec.WebDriver.NEW_EDGE_DRIVER, RuntimeConfig.getConfig().getEdgeDriver().getVersion());

  }


  @Override
  public JsonObject execute() {
    checkWhoNeedsUpdates();

    String genericUpdate = " needs to be updated to latest version of ";
    ConfigFileReader configOnDisk = new ConfigFileReader(RuntimeConfig.getConfigFile());
    Map configHash = configOnDisk.toHashMap();

    if (updateChromeDriver) {
      String
          newChromeDriverVersion =
          RuntimeConfig.getReleaseManager().getChromeDriverLatestVersion().getPrettyPrintVersion(
              ".");
      logger.info("Chrome Driver " + genericUpdate + " " + newChromeDriverVersion);
      RuntimeConfig.getConfig().getChromeDriver().setVersion(newChromeDriverVersion);

      updateVersionFor(configHash, "chromedriver", newChromeDriverVersion);
      getJsonResponse().addKeyValues(JsonCodec.WebDriver.NEW_CHROME_DRIVER, newChromeDriverVersion);
    }

    if (updateGeckoDriver) {
        String
            newGeckoDriverVersion =
            RuntimeConfig.getReleaseManager().getGeckoDriverLatestVersion().getPrettyPrintVersion(
                ".");
        logger.info("Gecko Driver " + genericUpdate + " " + newGeckoDriverVersion);
        RuntimeConfig.getConfig().getGeckoDriver().setVersion(newGeckoDriverVersion);

        updateVersionFor(configHash, "geckodriver", newGeckoDriverVersion);
        getJsonResponse().addKeyValues(JsonCodec.WebDriver.NEW_GECKO_DRIVER, newGeckoDriverVersion);
      }
    if (updateEdgeDriver) {
      String
              newEdgeDriverVersion =
              RuntimeConfig.getReleaseManager().getEdgeDriverLatestVersion().getPrettyPrintVersion(
                      ".");
      logger.info("Edge Driver " + genericUpdate + " " + newEdgeDriverVersion);
      RuntimeConfig.getConfig().getEdgeDriver().setVersion(newEdgeDriverVersion);

      updateVersionFor(configHash, "msedgedriver", newEdgeDriverVersion);
      getJsonResponse().addKeyValues(JsonCodec.WebDriver.NEW_EDGE_DRIVER, newEdgeDriverVersion);
    }

    if (updateWebDriver) {
      String gridExtrasVersion = Version.getSanitizedVersion();
      String
          newWebDriverVersion =
          RuntimeConfig.getReleaseManager().getWedriverLatestVersion().getPrettyPrintVersion(".");
      if(gridExtrasVersion.startsWith("1.")) {
        if(VersionCompare.versionCompare(newWebDriverVersion, "3.7.1") >= 0) {
          String message = String.format("SeleniumGridExtras 2.X is not compatible with Selenium version 3.7.0 or less.");
          logger.info(message);
          getJsonResponse().addKeyValues(JsonCodec.OUT, message);
          return getJsonResponse().getJson();
        }
      } else if(gridExtrasVersion.startsWith("2.")) {
        if(VersionCompare.versionCompare(newWebDriverVersion, "3.7.1") < 0) {
          String message = String.format("SeleniumGridExtras 2.X is not compatible with Selenium version 3.7.0 or less.");
          logger.info(message);
          getJsonResponse().addKeyValues(JsonCodec.OUT, message);
          return getJsonResponse().getJson();
        }
      }
      logger.info("WebDriver JAR " + genericUpdate + " " + newWebDriverVersion);
      RuntimeConfig.getConfig().getWebdriver().setVersion(newWebDriverVersion);
      updateVersionFor(configHash, "webdriver", newWebDriverVersion);
      getJsonResponse().addKeyValues(JsonCodec.WebDriver.NEW_WEB_DRIVER_JAR, newWebDriverVersion);
    }

    if (updateIEDriver) {
      String
          newIEDriverVersion =
          RuntimeConfig.getReleaseManager().getIeDriverLatestVersion().getPrettyPrintVersion(".");
      logger.info("IE Driver " + genericUpdate + " " + newIEDriverVersion);
      RuntimeConfig.getConfig().getIEdriver().setVersion(newIEDriverVersion);
      updateVersionFor(configHash, "iedriver", newIEDriverVersion);
      getJsonResponse().addKeyValues(JsonCodec.WebDriver.NEW_IE_DRIVER, newIEDriverVersion);
    }

    if (updateChromeDriver || updateIEDriver || updateWebDriver || updateGeckoDriver || updateEdgeDriver) {
      String
          message =
          "Update was detected for one or more versions of the drivers. You may need to restart Grid Extras for new versions to work";

      systemAndLog(message);
      getJsonResponse().addKeyValues(JsonCodec.OUT, message);

      logger.info(configOnDisk.toHashMap());
      configOnDisk.overwriteExistingConfig(configHash);
      logger.info(configOnDisk.toHashMap());

    }

    return getJsonResponse().getJson();
  }

  protected void updateVersionFor(Map inputMap, String driver, String version) {
    ((Map) ((Map) inputMap.get("theConfigMap")).get(driver)).put("version", version);
  }

  @Override
  public boolean initialize() {

    if (RuntimeConfig.getConfig().getAutoUpdateDrivers()) {
      try {
        execute();
      } catch (Exception e) {
        printInitilizedFailure();
        logger.error(e.toString());
        return false;
      }
    }

    printInitilizedSuccessAndRegisterWithAPI();
    return true;
  }

  private void checkWhoNeedsUpdates() {

    int
        currentChromeVersion =
        getComparableVersion(RuntimeConfig.getConfig().getChromeDriver().getVersion());
    int
        newestChromeVersion =
        RuntimeConfig.getReleaseManager().getChromeDriverLatestVersion().getComparableVersion();

    updateChromeDriver = currentChromeVersion < newestChromeVersion;

    int
        currentGeckoVersion =
        getComparableVersion(RuntimeConfig.getConfig().getGeckoDriver().getVersion());
    int
        newestGeckoVersion =
        RuntimeConfig.getReleaseManager().getGeckoDriverLatestVersion().getComparableVersion();

    updateGeckoDriver = currentGeckoVersion < newestGeckoVersion;

    int
        currentIEDriverVersion =
        getComparableVersion(RuntimeConfig.getConfig().getIEdriver().getVersion());
    int
        newestIEDriverVersion =
        RuntimeConfig.getReleaseManager().getIeDriverLatestVersion().getComparableVersion();

    updateIEDriver = currentIEDriverVersion != newestIEDriverVersion;

    int
        currentWebDriverJarVersion =
        getComparableVersion(RuntimeConfig.getConfig().getWebdriver().getVersion());
    int
        newestWebDriverJarVersion =
        RuntimeConfig.getReleaseManager().getWedriverLatestVersion().getComparableVersion();

    updateWebDriver = currentWebDriverJarVersion != newestWebDriverJarVersion;

    int
            currentEdgeVersion =
            getComparableVersion(RuntimeConfig.getConfig().getEdgeDriver().getVersion());
    int
            newestEdgeVersion =
            RuntimeConfig.getReleaseManager().getEdgeDriverLatestVersion().getComparableVersion();

    updateEdgeDriver = currentEdgeVersion < newestEdgeVersion;
  }

  private Integer getComparableVersion(String version) {
    return Integer.valueOf(version.replace(".", ""));
  }


}
