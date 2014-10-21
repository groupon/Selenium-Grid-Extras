package com.groupon.seleniumgridextras.grid.proxies.sessions.threads;

import com.groupon.seleniumgridextras.config.DefaultConfig;
import com.groupon.seleniumgridextras.loggers.SessionHistoryLog;
import com.groupon.seleniumgridextras.utilities.TimeStampUtility;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import org.apache.log4j.Logger;
import org.openqa.grid.internal.TestSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class SessionHistoryCallable implements Callable {

    public static final int SECONDS_TO_WAIT_FOR_EXTERNAL_KEY = 120;
    protected TestSession session;
    private static Logger logger = Logger.getLogger(SessionHistoryCallable.class);

    public SessionHistoryCallable(TestSession session) {
        this.session = session;
    }

    @Override
    public String call() throws Exception {
        Map<String, Object> sessionDetails = new HashMap<String, Object>();
        try {


            sessionDetails.put(JsonCodec.WebDriver.Grid.INTERNAL_KEY, this.session.getInternalKey());
            sessionDetails.put(JsonCodec.WebDriver.Grid.EXTERNAL_KEY, JsonCodec.WebDriver.Grid.NOT_YET_ASSIGNED);
            sessionDetails.put(JsonCodec.WebDriver.Grid.HOST, session.getSlot().getRemoteURL().getHost());
            sessionDetails.put(JsonCodec.WebDriver.Grid.PORT, String.valueOf(session.getSlot().getRemoteURL().getPort()));
            sessionDetails.put(JsonCodec.TIMESTAMP, TimeStampUtility.getTimestampAsString());
            sessionDetails.put(JsonCodec.WebDriver.Grid.REQUESTED_CAPABILITIES, this.session.getRequestedCapabilities());

            logger.debug(sessionDetails);

            logger.info(String.format("Waiting for internal key %s, to get an external key", this.session.getInternalKey()));
            for (int i = 0; i < SECONDS_TO_WAIT_FOR_EXTERNAL_KEY; i++) {
                if (this.session.getExternalKey() != null) {
                    sessionDetails.put(JsonCodec.WebDriver.Grid.EXTERNAL_KEY, this.session.getExternalKey().getKey());
                    logger.info(String.format("Associating internal key %s to external key %s",
                            this.session.getExternalKey(), this.session.getExternalKey().getKey()));
                    break;
                }

                Thread.sleep(1000);

            }

            if (this.session.getExternalKey() == null) {
                logger.error(String.format("Session %s, did not get an external key after %s seconds.\nMore info: %s",
                        this.session.getInternalKey(), SECONDS_TO_WAIT_FOR_EXTERNAL_KEY, sessionDetails));
            }


            SessionHistoryLog.setOutputDir(DefaultConfig.SESSION_LOG_DIRECTORY);
            SessionHistoryLog.newSession(this.session.getSlot().getRemoteURL().getHost(), sessionDetails);
        } catch (Exception e) {
            logger.error("Something went wrong when gathering information for new session threads", e);
        }
        return sessionDetails.toString();
    }

}