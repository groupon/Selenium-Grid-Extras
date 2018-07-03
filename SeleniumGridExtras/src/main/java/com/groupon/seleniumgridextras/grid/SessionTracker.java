package com.groupon.seleniumgridextras.grid;

import com.groupon.seleniumgridextras.config.*;

import java.util.LinkedList;
import java.util.List;

public class SessionTracker {

    LinkedList<String> sessionsRecorded = new LinkedList<String>();


    public void startSession(String session) {
        sessionsRecorded.add(session);

        // videos to keep, and sessions to keep track of
        int videosToKeep
            = RuntimeConfig.getConfig().getVideoRecording().getVideosToKeep();
        if (videosToKeep > 0
            && sessionsRecorded.size() > videosToKeep)
        {
            // then forget the first session
            sessionsRecorded.remove();
        }
    }

    public List<String> getSessions() {
        return sessionsRecorded;
    }
}
