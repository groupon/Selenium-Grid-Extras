package com.groupon.seleniumgridextras.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;

import com.groupon.seleniumgridextras.config.driver.IEDriver;
import com.groupon.seleniumgridextras.config.driver.WebDriver;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Config {

  private String config_version;
  private List<String> activated_modules;
  private List<String> disabled_modules;
  private String expose_directory;
  private List<String> setup;
  private List<String> teardown;
  private GridInfo grid;
  private WebDriver webdriver = new WebDriver();
  private IEDriver iedriver = new IEDriver();

  public Config() {
    activated_modules = new ArrayList<String>();
    disabled_modules = new ArrayList<String>();
    setup = new ArrayList<String>();
    teardown = new ArrayList<String>();
    grid = new GridInfo();
    webdriver = new WebDriver();
  }

  public List<String> getActivatedModules() {
    return activated_modules;
  }

  public List<String> getDisabledModules() {
    return disabled_modules;
  }

  public String getExposeDirectory() {
    return expose_directory;
  }

  public List<String> getSetup() {
    return setup;
  }

  public List<String> getTeardown() {
    return teardown;
  }

  public GridInfo getGrid() {
    return grid;
  }

  public IEDriver getIEdriver() {
    return iedriver;
  }

  public WebDriver getWebdriver() {
    return webdriver;
  }

  public String getConfigVersion() {
    return config_version;
  }

  public void writeToDisk(String file) {
    try {
      File f = new File(file);
      String config = this.toPrettyJsonString();
      FileUtils.writeStringToFile(f, config);
    } catch (Exception error) {
      System.out
          .println("Could not write default config file, exit with error " + error.toString());
      System.exit(1);
    }
  }

  public void setConfigVersion(String config_version) {
    this.config_version = config_version;
  }

  public void addSetupTask(String task) {
    setup.add(task);
  }

  public void addTeardownTask(String task) {
    teardown.add(task);
  }

  public void addEnabledModule(String module) {
    activated_modules.add(module);
  }

  public void addDisabledModule(String module) {
    disabled_modules.add(module);
  }

  public void setSharedDir(String sharedDir) {
    expose_directory = sharedDir;
  }

  public String toJsonString() {
    return new Gson().toJson(this);
  }

  public String toPrettyJsonString() {
    return new GsonBuilder().setPrettyPrinting().create().toJson(this);
  }

  public boolean checkIfModuleEnabled(String module) {
    return activated_modules.contains(module);
  }

  public String getExposedDirectory() {
    return expose_directory;
  }



  public JsonObject asJsonObject() {
    return (JsonObject) new JsonParser().parse(this.toJsonString());
  }

}
