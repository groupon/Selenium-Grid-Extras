package com.groupon.seleniumgridextras.tasks;

import com.google.gson.JsonObject;

import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.utilities.FileIOUtility;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: dima Date: 7/2/14 Time: 10:41 AM To change this template use
 * File | Settings | File Templates.
 */
public class UpdateNodeConfig extends ExecuteOSTask {

  private File central_config_dir = new File("node_configs");
  private static Logger logger = Logger.getLogger(UpdateNodeConfig.class);

  public UpdateNodeConfig() {
    setEndpoint("/update_node_config");
    setDescription("Send the current config to the central location to be stored");
    JsonObject params = new JsonObject();
    setAcceptedParams(params);
    params.addProperty("node", "(Required) -  Name of the node who's config needs to be updated");
    params.addProperty("filename", "(Required) -  Name of the config to be update");
    params.addProperty("content", "(Required) -  Base64 encoded string of content");
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass("btn-success");
    setButtonText("Get Node Config");
    setEnabledInGui(true);

    getJsonResponse().addKeyDescriptions("node", "Node for which config was updated");
    getJsonResponse().addKeyDescriptions("filename", "Name of the config file updated");

  }

  @Override
  public JsonObject execute() {
    getJsonResponse().addKeyValues("error", "node, filename, content are required parameters");
    return getJsonResponse().getJson();
  }


  @Override
  public JsonObject execute(Map<String, String> parameter) {

    if (parameter.isEmpty() || !parameter.containsKey("node") || !parameter.containsKey("filename")
        || !parameter.containsKey("content")) {
      return execute();
    } else {
      byte[] decodedBytes = Base64.decodeBase64(parameter.get("content"));
      final String decodedString = new String(decodedBytes);
      logger.info(decodedString);

      final String node = parameter.get("node");
      final
      File filename =
          new File(
              central_config_dir + RuntimeConfig.getOS().getFileSeparator() + node + RuntimeConfig
                  .getOS().getFileSeparator() + parameter
                  .get("filename"));


      createNodeDirIfNotExisting(filename);
      logger.info(
          "Update to node '" + node + "' file '" + filename.getAbsolutePath() + " was called");
      logger.debug(decodedString);
      try {
        FileIOUtility.writeToFile(filename, decodedString);
        getJsonResponse().addKeyValues("node", node);
        getJsonResponse().addKeyValues("filename", filename.getAbsolutePath());
      } catch (Exception error) {
        logger.warn(error.toString());
        getJsonResponse().addKeyValues("error", error.toString());
      }

      return getJsonResponse().getJson();
    }
  }

  protected void createNodeDirIfNotExisting(File node){
    if (!node.getParentFile().exists()){
      logger.info("Node's config dir didn't exist so we will create it");
      node.getParentFile().mkdir();
    }
  }

}
