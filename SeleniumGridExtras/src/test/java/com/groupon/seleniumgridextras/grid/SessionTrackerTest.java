package com.groupon.seleniumgridextras.grid;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA. User: dima Date: 5/30/14 Time: 10:45 AM To change this template use
 * File | Settings | File Templates.
 */
public class SessionTrackerTest {


  @Test
  public void testStartAndEndSessions() throws Exception{

    SessionTracker s = new SessionTracker();

    s.startSession();
    s.startSession();
    s.startSession();

    s.stopSession();
    s.stopSession();


    assertEquals(3, s.getSessionsStarted());
    assertEquals(2, s.getSessionsEnded());


  }

}
