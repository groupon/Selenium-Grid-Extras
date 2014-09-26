package com.groupon.seleniumgridextras.config.remote;

import com.google.gson.Gson;

import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.utilities.FileIOUtility;
import com.groupon.seleniumgridextras.utilities.HttpUtility;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA. User: dima Date: 7/8/14 Time: 3:16 PM To change this template use
 * File | Settings | File Templates.
 */
public class ConfigPuller {

  private File configFile;
  private static Logger logger = Logger.getLogger(ConfigPuller.class);

  public ConfigPuller() {
    this.configFile =
        new File(RuntimeConfig.getConfig().getConfigsDirectory().getAbsolutePath() + RuntimeConfig
            .getOS()
            .getFileSeparator() + RuntimeConfig.getConfig().getCentralConfigFileName());
    logger
        .info("Config file storing central repository URL is " + this.configFile.getAbsolutePath());
  }

  public void updateFromRemote() {
    String url = getCentralUrl(this.configFile);
    logger.info("Central config URL for current node is set to '" + url + "'");

    if (url.equals("")) {
      logger.info("The central config URL is empty, will not download the latest configs.");
    } else {
      try {
        String
            message =
            "Checking central Config repository for " + RuntimeConfig.getOS().getHostName()
            + " node's config from "+ url +" . This may take a while...";
        logger.info(message);
        System.out.println(message);
        downloadRemoteConfigs(new URL(url));
      } catch (MalformedURLException error) {
        String
            message =
            "The URL of central config  '" + url + "' seems to be malformed in the '" + this
                .configFile.getAbsolutePath() + "' file. Will skip remote update";
        System.out.println(message);
        logger.warn(message);
        logger.warn(error);
      }
    }

  }

  protected void downloadRemoteConfigs(URL url) {

    try {
      String rawJson = HttpUtility.getRequestAsString(url);
      logger.debug(rawJson);
      Map remoteConfigs = new Gson().fromJson(rawJson, HashMap.class);
      logger.debug(remoteConfigs);
      if (remoteConfigs.containsKey(JsonCodec.EXIT_CODE)) {
        Integer exitCode = ((Double) remoteConfigs.get(JsonCodec.EXIT_CODE)).intValue();
        logger.info("Remote server responded with this exit code to our request " + exitCode);
        if (exitCode == 0) {
          saveIndividualFiles(remoteConfigs);
        } else {
          logger.info("Remote config request had an error, will skip update from remote source");
          logger.info(remoteConfigs.get(JsonCodec.ERROR));
        }
      }


    } catch (IOException error) {
      System.out.println(
          "Reading config from central repository encountered an error: " + error.getMessage());
      logger.warn("Problem reading the content from remote url " + url);
      logger.warn(error);
    }

  }

  protected void saveIndividualFiles(Map config) {

    for (String filename : (Set<String>) config.keySet()) {
      if (!filename.equals(JsonCodec.EXIT_CODE) && !filename.equals(JsonCodec.OUT) && !filename.equals(JsonCodec.ERROR)) {
        try {
          String fileContents = (String) (((ArrayList) config.get(filename)).get(0));

          FileIOUtility.writePrettyJsonToFile(filename, fileContents);

          String message = "Updated '" + filename + "' from central config repository";
          System.out.println(message);
          logger.info(message);
          logger.debug("Config contents: " + fileContents);

        } catch (IOException error) {
          logger.warn(
              "Writing content of '" + filename + "' to HD encountered an error. Content:\n"
              + config
                  .get(filename));
          logger.warn(error);
        }
      }
    }

  }


  protected String getCentralUrl(File filename) {
    try {
      return FileIOUtility.getAsString(filename) + "get_node_config?node=" + RuntimeConfig.getOS()
          .getHostName();
    } catch (FileNotFoundException error) {
      logger.info("Config file for central repo does not exist " + filename.getAbsolutePath());
      return "";
    }
  }

}
