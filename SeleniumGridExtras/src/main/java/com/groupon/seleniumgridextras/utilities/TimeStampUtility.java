package com.groupon.seleniumgridextras.utilities;

import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA. User: dima Date: 9/17/14 Time: 4:40 PM To change this template use
 * File | Settings | File Templates.
 */
public class TimeStampUtility {

  public static Date getTimestamp(){
    return new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
  }

  public static String getTimestampAsString(){
    return getTimestamp().toString();
  }

}
