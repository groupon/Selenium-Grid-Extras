package com.groupon.seleniumgridextras.utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class TimeStampUtility {

    public static Date getTimestamp() {
        return new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
    }

    public static String getTimestampAsString() {
        return getTimestamp().toString();
    }

    public static String osFriendlyTimestamp() {
        return new SimpleDateFormat("dd_MM_yyyy").format(getTimestamp());
    }

    public static long timestampInMs(){
      return getTimestamp().getTime();
    }

}
