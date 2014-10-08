package com.groupon.seleniumgridextras.tasks;

import com.google.gson.JsonObject;

import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.FileIOUtility;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//TODO: Add some tests for this class

public class GetNodeConfig extends ExecuteOSTask {

  private static Logger logger = Logger.getLogger(GetNodeConfig.class);

  public GetNodeConfig() {
    setEndpoint(TaskDescriptions.Endpoints.GET_NODE_CONFIG);
    setDescription(TaskDescriptions.Description.GET_NODE_CONFIG);
    JsonObject params = new JsonObject();
    setAcceptedParams(params);
    params.addProperty(JsonCodec.WebDriver.Grid.NODE, "(Required) -  Computer name of desired node.");
    setRequestType(TaskDescriptions.HTTP.GET);
    setResponseType(TaskDescriptions.HTTP.JSON);
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass(TaskDescriptions.UI.BTN_SUCCESS);
    setButtonText(TaskDescriptions.UI.ButtonText.GET_NODE_CONFIG);
    setEnabledInGui(true);
  }


  @Override
  public JsonObject execute(String node) {

    File
        node_specific_config_dir =
        new File(
            RuntimeConfig.getConfig().getConfigsDirectory().getAbsoluteFile() + RuntimeConfig
                .getOS().getFileSeparator() + node);

    if (node_specific_config_dir.exists()) {
      logger.info("Found " + node_specific_config_dir.getAbsolutePath());

      for (File file : getAllJsonConfigs(node_specific_config_dir)) {
        try {
          addFileContentsToResponse(file.getName(), FileIOUtility.getAsString(file));
        } catch (Exception error) {
          getJsonResponse().addKeyValues(JsonCodec.ERROR, error.toString());
          logger.warn(error.toString());
        }
      }
    } else {
      String
          error =
          "Config directory for '" + node + "' node does not exist in " + node_specific_config_dir
              .getAbsolutePath();
      getJsonResponse().addKeyValues(JsonCodec.ERROR, error);
      logger.info(error);
    }

    return getJsonResponse().getJson();
  }

  protected void addFileContentsToResponse(String file, String contents) {
    addResponseDescription(file, "Config file");
    getJsonResponse().addKeyValues(file, contents);
  }

  @Override
  public JsonObject execute() {
    getJsonResponse().addKeyValues(JsonCodec.ERROR, "Cannot call this end point without 'node' parameter");
    return getJsonResponse().getJson();
  }


  @Override
  public JsonObject execute(Map<String, String> parameter) {

    if (parameter.isEmpty() || !parameter.containsKey(JsonCodec.WebDriver.Grid.NODE) ) {
      return execute();
    } else if (!configDirExist()) {
      getJsonResponse().addKeyValues(JsonCodec.ERROR, "This node does not contain the following directory: "
                                              + RuntimeConfig.getConfig().getConfigsDirectory().getName());
      return getJsonResponse().getJson();
    } else {
      return execute(parameter.get(JsonCodec.WebDriver.Grid.NODE).toString());
    }
  }

  protected boolean configDirExist() {

    if (RuntimeConfig.getConfig().getConfigsDirectory().exists()) {
      logger.info(
          RuntimeConfig.getConfig().getConfigsDirectory().getAbsolutePath() + " directory exists");
      return true;
    } else {
      logger.info(RuntimeConfig.getConfig().getConfigsDirectory().getAbsolutePath()
                  + " directory does not exist");
      return false;
    }

  }

  protected List<File> getAllJsonConfigs(File config_dir) {
    List<File> files = new LinkedList<File>();

    for (File f : config_dir.listFiles()) {
      if (FilenameUtils.getExtension(f.getName()).equals("json")) {
        logger.info("Found config " + f.getAbsolutePath());
        files.add(f);
      }

    }

    return files;

  }

}
