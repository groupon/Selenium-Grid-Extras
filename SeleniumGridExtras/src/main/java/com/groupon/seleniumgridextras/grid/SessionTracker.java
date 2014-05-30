package com.groupon.seleniumgridextras.grid;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: dima Date: 5/30/14 Time: 10:38 AM To change this template use
 * File | Settings | File Templates.
 */
public class SessionTracker {

  List<Date> sessionStarted = new LinkedList<Date>();
  List<Date> sessionEnded = new LinkedList<Date>();


  public void startSession(){
    this.sessionStarted.add(new Date());
  }

  public void stopSession(){
    this.sessionEnded.add(new Date());
  }


  public int getSessionsStarted() {
    return this.sessionStarted.size();
  }

  public int getSessionsEnded() {
    return this.sessionEnded.size();
  }
}
