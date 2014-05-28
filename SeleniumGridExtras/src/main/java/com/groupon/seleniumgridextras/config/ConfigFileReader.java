package com.groupon.seleniumgridextras.config;


import com.google.gson.Gson;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigFileReader {

  private String filePath;
  private String configString = "";
  private Map parsedConfig;


  private static Logger logger = Logger.getLogger(ConfigFileReader.class);

  public ConfigFileReader(String file){
    this.filePath = file;
    readConfigFile();

  }

  public boolean hasContent(){
    if (this.configString.equals("")){
      return false;
    } else {
      return true;
    }
  }

  public Map toHashMap(){
    if (hasContent()){
      return new Gson().fromJson(this.configString, HashMap.class);
    } else {
      return new HashMap();
    }
  }

  protected void readConfigFile(){

    String readString = "";
    try {
      BufferedReader reader = new BufferedReader(new FileReader(this.filePath));
      String line = null;
      while ((line = reader.readLine()) != null) {
        readString = readString + line;
      }
    } catch (FileNotFoundException error) {
      logger.info("File " + this.filePath + " does not exist, going to use default configs");
    } catch (IOException error) {
      logger.info("Error reading" + this.filePath + ". Going with default configs");
    }

    this.configString = readString;

  }



}
