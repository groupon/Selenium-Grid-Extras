package com.groupon.seleniumgridextras.config.capabilities;

import com.google.gson.GsonBuilder;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashMap;

public abstract class Capability extends HashMap {

  public Capability(){
    this.put("maxInstances", 1);
    this.put("seleniumProtocol", "Selenium");
  }

  public void setBrowserVersion(String version){
    this.put("version", version);
  }

  public void setPlatform(String platform){
    this.put("platform", platform);
  }

  protected void setBrowser(String browser){
    this.put("browserName", browser);
  };

  public String getBrowserName() {
    return this.getClass().getSimpleName();
  }


}
