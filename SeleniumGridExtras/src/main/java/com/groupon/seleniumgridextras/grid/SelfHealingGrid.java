package com.groupon.seleniumgridextras.grid;

import com.google.gson.JsonObject;

import com.groupon.seleniumgridextras.PortChecker;
import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.GridNode;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

public class SelfHealingGrid extends GridStarter {


  public static void checkStatus(int gridExtrasPort, Config config) {
    if (portOccupied(gridExtrasPort)) {
      System.out.println("Already running on port " + gridExtrasPort + " with pid");
      healNodesIfNeeded(config);
      System.exit(0);
    } else {
      System.out.println("GridExtras is not running will boot normally");
    }

  }


  private static void healNodesIfNeeded(Config config) {
    System.out.println("Checking if all nodes are running");
    for (GridNode node : config.getNodes()) {
      int port = node.getConfiguration().getPort();
      if (portOccupied(port)) {
        System.out.println("Node on port " + port + " is running");
      } else {
        System.out.println("Node on port " + port + " is NOT running, attempting to start");

        Boolean isWindows = RuntimeConfig.getOS().isWindows();
        String logFile = node.getLoadedFromFile().replace("json", "log");
        String configFile = node.getLoadedFromFile();
        String startCommand = getNodeStartCommand(configFile, isWindows);
        String backgroundCommand = getBackgroundStartCommandForNode(startCommand,logFile, isWindows);

        startOneNode(backgroundCommand);

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
