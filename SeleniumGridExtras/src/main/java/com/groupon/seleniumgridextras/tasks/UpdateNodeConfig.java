package com.groupon.seleniumgridextras.tasks;

import com.google.gson.JsonObject;

import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.FileIOUtility;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: dima Date: 7/2/14 Time: 10:41 AM To change this template use
 * File | Settings | File Templates.
 */
public class UpdateNodeConfig extends ExecuteOSTask {

  private static Logger logger = Logger.getLogger(UpdateNodeConfig.class);

  public UpdateNodeConfig() {
    setEndpoint(TaskDescriptions.Endpoints.UPDATE_NODE_CONFIG);
    setDescription(TaskDescriptions.Description.UPDATE_NODE_CONFIG);
    JsonObject params = new JsonObject();
    setAcceptedParams(params);
    params.addProperty(JsonCodec.WebDriver.Grid.NODE,
                       "(Required) -  Name of the node who's config needs to be updated");
    params.addProperty(JsonCodec.Config.FILENAME, "(Required) -  Name of the config to be update");
    params.addProperty(JsonCodec.Config.CONTENT, "(Required) -  Base64 encoded string of content");
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass("btn-success");
    setButtonText(TaskDescriptions.UI.ButtonText.UPDATE_NODE_CONFIG);
    setEnabledInGui(true);

    getJsonResponse()
        .addKeyDescriptions(JsonCodec.WebDriver.Grid.NODE, "Node for which config was updated");
    getJsonResponse()
        .addKeyDescriptions(JsonCodec.Config.FILENAME, "Name of the config file updated");

  }

  @Override
  public JsonObject execute() {
    getJsonResponse()
        .addKeyValues(JsonCodec.ERROR, "node, filename, content are required parameters");
    return getJsonResponse().getJson();
  }


  @Override
  public JsonObject execute(Map<String, String> parameter) {

    if (parameter.isEmpty() || !parameter.containsKey(JsonCodec.WebDriver.Grid.NODE) || !parameter
        .containsKey(
            JsonCodec.Config.FILENAME)
        || !parameter.containsKey(JsonCodec.Config.CONTENT)) {
      return execute();
    } else {
      byte[] decodedBytes = Base64.decodeBase64(parameter.get(JsonCodec.Config.CONTENT));
      final String decodedString = new String(decodedBytes);
      logger.info(decodedString);

      final String node = parameter.get(JsonCodec.WebDriver.Grid.NODE);
      final
      File filename =
          new File(
              RuntimeConfig.getConfig().getConfigsDirectory() + RuntimeConfig.getOS()
                  .getFileSeparator() + node + RuntimeConfig
                  .getOS().getFileSeparator() + parameter
                  .get(JsonCodec.Config.FILENAME));

      createNodeDirIfNotExisting(filename);
      logger.info(
          "Update to node '" + node + "' file '" + filename.getAbsolutePath() + " was called");
      logger.debug(decodedString);
      try {
        FileIOUtility.writePrettyJsonToFile(filename, decodedString);
        getJsonResponse().addKeyValues(JsonCodec.WebDriver.Grid.NODE, node);
        getJsonResponse().addKeyValues(JsonCodec.Config.FILENAME, filename.getAbsolutePath());
      } catch (Exception error) {
        logger.warn(error.toString());
        getJsonResponse().addKeyValues(JsonCodec.ERROR, error.toString());
      }

      return getJsonResponse().getJson();
    }
  }

  protected void createNodeDirIfNotExisting(File node) {
    createConfigsDirIfNotExisting(RuntimeConfig.getConfig().getConfigsDirectory());
    if (!node.getParentFile().exists()) {
      logger.info("Node's config dir didn't exist so we will create it");
      node.getParentFile().mkdir();
    }
  }

  protected void createConfigsDirIfNotExisting(File config) {
    if (!config.exists()) {
      logger.info("Configs dir didn't exist so we will create it");
      config.mkdir();
    }
  }

}
