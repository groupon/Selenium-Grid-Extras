package com.groupon.seleniumgridextras.tasks;

import com.google.gson.JsonObject;

import com.groupon.seleniumgridextras.config.RuntimeConfig;

import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA. User: dima Date: 5/30/14 Time: 10:33 AM To change this template use
 * File | Settings | File Templates.
 */
public class SessionCounterStopSession extends ExecuteOSTask {

  private static Logger logger = Logger.getLogger(SessionCounterStopSession.class);

  @Override
  public JsonObject execute(String param) {

    RuntimeConfig.getTestSessionTracker().stopSession();

    String
        message =
        "This node has ended " + RuntimeConfig.getTestSessionTracker().getSessionsEnded()
        + " sessions";

    logger.info(message);
    getJsonResponse().addKeyValues("out", message);

    return getJsonResponse().getJson();
  }

}
