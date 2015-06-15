package com.groupon.seleniumgridextras.config.remote;

import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.utilities.FileIOUtility;
import com.groupon.seleniumgridextras.utilities.HttpUtility;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class ConfigPusher {

  private static Logger logger = Logger.getLogger(ConfigPusher.class);


  protected String host;
  protected Map<String, String> params;
  protected List<String> configFiles;
  protected final String nodeName = RuntimeConfig.getOS().getHostName();

  public ConfigPusher() {
    this.params = new HashMap<String, String>();
    this.configFiles = new LinkedList<String>();
  }

  public void addConfigFile(String file) {
    logger.info("Adding " + file + " to send queue");
    this.configFiles.add(file);

  }

  public void setHubHost(String host) {
    this.host = host;
  }

  public Map<String, Integer> sendAllConfigsToHub() {
    final Map<String, String> filesToSend = getConfigFiles();
    Map<String, Integer> responses = new HashMap<String, Integer>();

    for (String file : filesToSend.keySet()) {

      String content = filesToSend.get(file);
      logger.info("Sending '" + file +"'");
      logger.debug("File content is: " + content);


      try {
        responses.put(file, HttpUtility.getRequest(buildUrl(file, content)).getResponseCode());
      } catch (URISyntaxException error) {
        logger.warn("Error building send url for file " + file);
        logger.warn("Config will not be sent");
        logger.warn("Content: " + filesToSend.get(file));
        logger.warn(error);
        responses.put(file, 999);
      } catch (IOException error){
        logger.warn("Error reading content");
        logger.warn("Config will not be sent");
        logger.warn("Content: " + filesToSend.get(file));
        logger.warn(error);
        responses.put(file, 999);
      }
    }

    return responses;

  }


  protected Map<String, String> getConfigFiles() {
    Map<String, String> readFiles = new HashMap<String, String>();

    for (String file : this.configFiles) {
      try {
        readFiles.put(file, toBase64(FileIOUtility.getAsString(file)));
      } catch (FileNotFoundException error) {
        logger.error("File " + file + " was not found, it will not be pushed to HUB");
      }
    }

    return readFiles;
  }


  protected URI buildUrl(String filename, String content) throws URISyntaxException {
    return new URIBuilder()
        .setScheme("http")
        .setHost(host)
        .setPort(RuntimeConfig.getGridExtrasPort())
        .setPath("/update_node_config")
        .addParameter("node", this.nodeName)
        .addParameter("filename", filename)
        .addParameter("content", content)
        .build();
  }

  protected String toBase64(String value) {
    return new String(Base64.encodeBase64(value.getBytes()));
  }

}
