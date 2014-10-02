package com.groupon.seleniumgridextras.utilities;

public class ValueConverter {

  public static String bytesToHumanReadable(long bytes, boolean si){
    int unit = si ? 1000 : 1024;
    if (bytes < unit) {
      return bytes + " B";
    }
    int exp = (int) (Math.log(bytes) / Math.log(unit));
    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
  }

  public static String millisecondsToHours(long milliseconds){
    return String.valueOf(milliseconds % 24);
  }

}
