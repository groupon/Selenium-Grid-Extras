package com.groupon.seleniumgridextras.tasks;

import com.google.gson.JsonObject;

import com.groupon.seleniumgridextras.JsonResponseBuilder;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA. User: dima Date: 5/30/14 Time: 10:32 AM To change this template use
 * File | Settings | File Templates.
 */
public class SessionCounterStartSession extends ExecuteOSTask {

  private static Logger logger = Logger.getLogger(SessionCounterStartSession.class);

  @Override
  public JsonObject execute(String param) {

    RuntimeConfig.getTestSessionTracker().startSession();

    String message = "This node has started " + RuntimeConfig.getTestSessionTracker().getSessionsStarted() + " sessions";
    logger.info( message );
    getJsonResponse().addKeyValues(JsonResponseBuilder.OUT, message);


    return getJsonResponse().getJson();
  }

}
