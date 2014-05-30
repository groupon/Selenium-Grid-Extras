package com.groupon.seleniumgridextras.tasks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.groupon.seleniumgridextras.config.RuntimeConfig;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA. User: dima Date: 5/30/14 Time: 11:03 AM To change this template use
 * File | Settings | File Templates.
 */
public class SessionCounterStartAndStopSessionTest {

  private ExecuteOSTask startSession = new SessionCounterStartSession();
  private ExecuteOSTask stopSession = new SessionCounterStopSession();

  @Test
  public void testStartSessionCounter() throws Exception {
    startSession.execute();
    JsonObject out = startSession.execute();

    assertEquals("This node has started 2 sessions", ((JsonArray)out.get("out")).get(0).getAsString());
    assertEquals(2, RuntimeConfig.getTestSessionTracker().getSessionsStarted());

  }

  @Test
  public void testStopSessionCounter() throws Exception {
    stopSession.execute();
    JsonObject out = stopSession.execute();

    assertEquals("This node has ended 2 sessions", ((JsonArray)out.get("out")).get(0).getAsString());

    assertEquals(2, RuntimeConfig.getTestSessionTracker().getSessionsEnded());

  }



}
