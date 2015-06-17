package com.groupon.seleniumgridextras.utilities;

import java.util.concurrent.TimeUnit;

public class ValueConverter {

    public static String bytesToHumanReadable(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }


    public static long daysToMilliseconds(int days) {
        return TimeUnit.DAYS.toMillis(days);
    }

    public static long millisecondsToDays(long milliseconds) {
        return millisecondsToHours(milliseconds) / 24;
    }

    public static long millisecondsToHours(long milliseconds) {
        return millisecondsToMinutes(milliseconds) / 60;
    }

    public static long millisecondsToSeconds(long milliseconds) {
        return TimeUnit.MILLISECONDS.toSeconds(milliseconds);
    }

    public static long millisecondsToMinutes(long milliseconds) {
        return TimeUnit.MILLISECONDS.toMinutes(milliseconds);
    }


}
