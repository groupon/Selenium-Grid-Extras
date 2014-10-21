package com.groupon.seleniumgridextras.loggers;

import com.google.gson.JsonSyntaxException;
import com.groupon.seleniumgridextras.utilities.FileIOUtility;
import com.groupon.seleniumgridextras.utilities.TimeStampUtility;
import com.groupon.seleniumgridextras.utilities.json.JsonParserWrapper;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NodeSessionHistory {

    protected Date created;
    protected List<Map> sessions;
    protected File outputFile;
    protected long logRotationDuration = 86400000; //24 hours

    private static Logger logger = Logger.getLogger(NodeSessionHistory.class);

    public NodeSessionHistory(File outputFile) {
        this.created = TimeStampUtility.getTimestamp();
        this.sessions = new LinkedList<Map>();
        this.outputFile = outputFile;
        readInExistingHistory();
    }

    public void addNewSession(Map sessionInfo){
        this.sessions.add(sessionInfo);
    }

    public List<Map> getSessions(){
        return sessions;
    }

    public String toJson(){
        return JsonParserWrapper.prettyPrintString(sessions);
    }

    public void backupToFile(){
        try {
            FileIOUtility.writePrettyJsonToFile(outputFile, sessions, false);
        } catch (IOException e) {
            logger.warn("Unable to backup session threads to file for " + outputFile.getAbsolutePath());
            logger.warn(e);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public boolean timeToRotateLog() {
        //Rotate log every 24 hours
        return (TimeStampUtility.getTimestamp().getTime() - this.created.getTime() > logRotationDuration);
    }

    public void setLogRotationDuration(long ms){
        this.logRotationDuration = ms;
    }

    protected void readInExistingHistory(){
        if (this.outputFile.exists()){

            try {
                String fileContents = FileIOUtility.getAsString(this.outputFile);
                this.sessions = JsonParserWrapper.toList(fileContents);
                return;
            } catch (FileNotFoundException e) {
                logger.error(String.format("Error reading previous log file %s", this.outputFile.getAbsolutePath()), e);
            } catch (JsonSyntaxException e){
                logger.error(String.format("File %s seems to have corrupted JSON, will start fresh", this.outputFile.getAbsolutePath()), e);
            } catch (Exception e){
                logger.error("Some unhandled error occurred, will get rid of the previous log file and start fresh", e);
            }

            String fileName = this.outputFile.getAbsolutePath();
            this.outputFile.delete();
            this.outputFile = new File(fileName);
        }
    }

}
