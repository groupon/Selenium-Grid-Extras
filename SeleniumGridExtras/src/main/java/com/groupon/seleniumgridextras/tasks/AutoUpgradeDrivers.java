package com.groupon.seleniumgridextras.tasks;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.ConfigFileReader;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.downloader.webdriverreleasemanager.WebDriverRelease;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class AutoUpgradeDrivers extends ExecuteOSTask {

  private static Logger logger = Logger.getLogger(AutoUpgradeDrivers.class);

  private boolean updateWebDriver = false;
  private boolean updateIEDriver = false;
  private boolean updateChromeDriver = false;


  public AutoUpgradeDrivers() {
    setEndpoint("/auto_upgrade_webdriver");
    setDescription(
        "Automatically checks the latest versions of all drivers and upgrades the current config to use them");
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());

    addResponseDescription("old_web_driver_jar", "Old version of WebDriver Jar");
    addResponseDescription("old_chrome_driver", "Old version of Chrome Driver");
    addResponseDescription("old_ie_driver", "Old version of IE Driver");

    addResponseDescription("new_web_driver_jar", "New versions of WebDriver Jar");
    addResponseDescription("new_chrome_driver", "New version of Chrome Driver");
    addResponseDescription("new_ie_driver", "New version of IE Driver");

    getJsonResponse()
        .addKeyValues("old_web_driver_jar", RuntimeConfig.getConfig().getWebdriver().getVersion());
    getJsonResponse().addKeyValues("old_chrome_driver",
                                   RuntimeConfig.getConfig().getChromeDriver().getVersion());
    getJsonResponse()
        .addKeyValues("old_ie_driver", RuntimeConfig.getConfig().getIEdriver().getVersion());

    getJsonResponse()
        .addKeyValues("new_web_driver_jar", RuntimeConfig.getConfig().getWebdriver().getVersion());
    getJsonResponse().addKeyValues("new_chrome_driver",
                                   RuntimeConfig.getConfig().getChromeDriver().getVersion());
    getJsonResponse()
        .addKeyValues("new_ie_driver", RuntimeConfig.getConfig().getIEdriver().getVersion());

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
    }

    if (updateWebDriver) {
      String
          newWebDriverVersion =
          RuntimeConfig.getReleaseManager().getWedriverLatestVersion().getPrettyPrintVersion(".");
      logger.info("WebDriver JAR " + genericUpdate + " " + newWebDriverVersion);
      RuntimeConfig.getConfig().getWebdriver().setVersion(newWebDriverVersion);
      updateVersionFor(configHash, "webdriver", newWebDriverVersion);
    }

    if (updateIEDriver) {
      String
          newIEDriverVersion =
          RuntimeConfig.getReleaseManager().getIeDriverLatestVersion().getPrettyPrintVersion(".");
      logger.info("IE Driver " + genericUpdate + " " + newIEDriverVersion);
      RuntimeConfig.getConfig().getIEdriver().setVersion(newIEDriverVersion);
      updateVersionFor(configHash, "iedriver", newIEDriverVersion);
    }

    if (updateChromeDriver || updateIEDriver || updateWebDriver) {
      String
          message =
          "Update was detected for one or more versions of the drivers. You may need to restart Grid Extras for new versions to work";

      systemAndLog(message);
      getJsonResponse().addKeyValues("out", message);

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
        currentIEDriverVersion =
        getComparableVersion(RuntimeConfig.getConfig().getIEdriver().getVersion());
    int
        newestIEDriverVersion =
        RuntimeConfig.getReleaseManager().getIeDriverLatestVersion().getComparableVersion();

    updateIEDriver = currentIEDriverVersion < newestIEDriverVersion;

    int
        currentWebDriverJarVersion =
        getComparableVersion(RuntimeConfig.getConfig().getWebdriver().getVersion());
    int
        newestWebDriverJarVersion =
        RuntimeConfig.getReleaseManager().getWedriverLatestVersion().getComparableVersion();

    updateWebDriver = currentWebDriverJarVersion < newestWebDriverJarVersion;

  }

  private Integer getComparableVersion(String version) {
    return Integer.valueOf(version.replace(".", "0"));
  }


}
