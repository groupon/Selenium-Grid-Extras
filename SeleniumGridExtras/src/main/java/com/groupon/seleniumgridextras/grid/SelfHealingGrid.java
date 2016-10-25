package com.groupon.seleniumgridextras.grid;

import com.google.gson.JsonObject;

import com.groupon.seleniumgridextras.PortChecker;
import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.GridNode;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

import java.util.List;

import org.apache.log4j.Logger;

public class SelfHealingGrid extends GridStarter {
  private static Logger logger = Logger.getLogger(SelfHealingGrid.class);

  public static void checkStatus(int gridExtrasPort, Config config) {
    if (portOccupied(gridExtrasPort)) {
      logger.info("Already running on port " + gridExtrasPort + " with pid");
      healNodesIfNeeded(config);
      System.exit(0);
    } else {
      logger.info("GridExtras is not running will boot normally");
    }

  }


  private static void healNodesIfNeeded(Config config) {
    logger.info("Checking if all nodes are running");
    for (GridNode node : config.getNodes()) {
      int port = node.getConfiguration() != null ? node.getConfiguration().getPort() : node.getPort();
      if (portOccupied(port)) {
        logger.debug("Node on port " + port + " is running");
      } else {
        logger.debug("Node on port " + port + " is NOT running, attempting to start");

        Boolean isWindows = RuntimeConfig.getOS().isWindows();
        logger.debug(isWindows);
        String logFile = node.getLoadedFromFile().replace("json", "log");
        logger.debug(logFile);
        String configFile = node.getLoadedFromFile();
        logger.debug(configFile);
        List<String> startCommand = getNodeStartCommand(configFile, isWindows, config);
        logger.debug(startCommand);
        List<String> backgroundCommand = getBackgroundStartCommandForNode(startCommand,logFile, isWindows);
        logger.debug(backgroundCommand);

        logger.debug(startOneNode(backgroundCommand));

      }
    }
  }



  private static Boolean portOccupied(int port) {
    JsonObject foo = PortChecker.getParsedPortInfo(port);
    if (foo.has("pid")) {
      return true;
    }
    return false;
  }


}
