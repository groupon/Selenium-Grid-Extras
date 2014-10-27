package com.groupon.seleniumgridextras.utilities;


import org.apache.log4j.Logger;

import java.io.IOException;

public class ProcessOutputReader {

    private static Logger logger = Logger.getLogger(ProcessOutputReader.class);
    public static String getStandardOut(Process process) {
        try {
            return StreamUtility.inputStreamToString(process.getInputStream());
        } catch (IOException e) {
            logger.error(String.format("Error reading standard out for %s, %s", process.toString(), e.getMessage()), e);
            return e.getMessage();
        }
    }

    public static String getErrorOut(Process process) {
        try {
            return StreamUtility.inputStreamToString(process.getErrorStream());
        } catch (IOException e) {
            logger.error(String.format("Error reading standard Error for %s, %s", process.toString(), e.getMessage()), e);
            return e.getMessage();
        }
    }
}
