package com.groupon.seleniumgridextras.loggers;

import com.google.common.base.Throwables;
import com.groupon.seleniumgridextras.utilities.FileIOUtility;
import com.groupon.seleniumgridextras.utilities.TimeStampUtility;
import com.groupon.seleniumgridextras.utilities.json.JsonParserWrapper;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SessionHistoryLog {

    public static final String NO_HISTORY_FOR_NODE = "[]";
    private static Logger logger = Logger.getLogger(SessionHistoryLog.class);
    private static File outputDir;
    private static Map<String, NodeSessionHistory> history;

    public static void setOutputDir(File dir) {
        outputDir = dir;
    }

    public static File getOutputDir() {
        return outputDir;
    }

    public static void newSession(String node, Map sessionDetails) {
        initialize();

        String logFile = getLogFileForNode(node);
        File outputFile = new File(outputDir, logFile);

        logger.info("Registering new session to file " + outputFile.getAbsolutePath());
        logger.debug(sessionDetails);

        if (!history.containsKey(logFile) || history.get(logFile).timeToRotateLog()) {
            history.put(logFile, new NodeSessionHistory(outputFile));
        }

        history.get(logFile).addNewSession(sessionDetails);
        history.get(logFile).backupToFile();
    }

    protected static void resetMemory() {
        if (history == null) {
            history = new HashMap<String, NodeSessionHistory>();
        } else {
            //delete all reference to objects to make objects ready for garbage collection
            //Doing it this way, just in case the history map is refered to anywhere, and does not get garbage collected
            //This is done in hopes of reducing memory bloat, if a JVM expert is reading this, please fix it!
            //-Love, Dima
            history.clear();

        }
    }

    public static Map<String, List> getTodaysHistoryAsMap() {
        initialize();
        Map<String, List> allHistory = new HashMap<String, List>();
        String todaysTimeStamp = TimeStampUtility.osFriendlyTimestamp();

        for (File currentFile : outputDir.listFiles()) {

            if (currentFile.getName().contains(todaysTimeStamp)) {
                try {
                    String fileContents = FileIOUtility.getAsString(currentFile);
                    String host = new String(currentFile.getName()).replaceAll("_" + todaysTimeStamp + ".log", "");
                    allHistory.put(host, JsonParserWrapper.toList(fileContents));
                } catch (FileNotFoundException e) {
                    logger.error(String.format("A file that existed a minute ago is now missing, %s\n%s\n%s",
                            currentFile.getAbsolutePath(), e.getMessage(), Throwables.getStackTraceAsString(e)));
                }


            }

        }

        return allHistory;
    }

    public static String getTodaysHistoryAsString() {
        initialize();
        return JsonParserWrapper.prettyPrintString(SessionHistoryLog.getTodaysHistoryAsMap());
    }

    protected static String getLogFileForNode(String node) {
        return node + "_" + TimeStampUtility.osFriendlyTimestamp() + ".log";
    }


    protected static void createOutputDir() {
        outputDir.mkdir();
    }

    protected static void initialize() {
        if (!outputDir.exists()) {
            createOutputDir();
        }

        if (history == null) {
            resetMemory();
        }
    }

}
