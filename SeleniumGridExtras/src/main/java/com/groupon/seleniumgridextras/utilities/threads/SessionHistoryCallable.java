package com.groupon.seleniumgridextras.utilities.threads;

import com.google.common.base.Throwables;
import com.groupon.seleniumgridextras.config.DefaultConfig;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.loggers.SessionHistoryLog;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.HttpUtility;
import com.groupon.seleniumgridextras.utilities.TimeStampUtility;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.openqa.grid.internal.TestSession;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class SessionHistoryCallable implements Callable<String> {

    public static final int SECONDS_TO_WAIT_FOR_EXTERNAL_KEY = 120;
    public static final String SOMETHING_WENT_WRONG_WHEN_GATHERING_INFORMATION_FOR_NEW_SESSION_THREADS = "Something went wrong when gathering information for new session threads";
    public static final String SOMETHING_WENT_WRONG_WHEN_NOTIFYING_NODE_OF_NEW_SESSION = "Something went wrong when notifying node of new session";
    protected TestSession session;
    private static Logger logger = Logger.getLogger(SessionHistoryCallable.class);

    public SessionHistoryCallable(TestSession session) {
        this.session = session;
    }

    @Override
    public String call() throws Exception {
        logger.info(String.format("Waiting for internal key %s, to get an external key", getSession().getInternalKey()));
        for (int i = 0; i < SECONDS_TO_WAIT_FOR_EXTERNAL_KEY; i++) {
            if (getSession().getExternalKey() != null) {
                logger.info(String.format("Associating internal key %s to external key %s",
                        getSession().getExternalKey(), getSession().getExternalKey().getKey()));
                break;
            }

            Thread.sleep(1000);

        }

        return String.format("Hub: %s, \nNode: %s", notifyHubGridExtrasOfNewSession(), notifyNodeGridExtrasOfNewSession());
    }

    protected String notifyNodeGridExtrasOfNewSession() {
        try {
            URIBuilder uri = new URIBuilder();
            uri.setScheme("http");
            uri.setHost(getSession().getSlot().getRemoteURL().getHost());
            uri.setPort(RuntimeConfig.getGridExtrasPort());
            uri.setPath(TaskDescriptions.Endpoints.GRID_STATUS);
            if (getSession().getExternalKey() != null) {
                uri.addParameter(JsonCodec.WebDriver.Grid.NEW_SESSION_PARAM, getSession().getExternalKey().getKey());
            } else {
                String message = String.format("Session %s, did not get an external key after %s seconds.",
                        getSession().getInternalKey(), SECONDS_TO_WAIT_FOR_EXTERNAL_KEY);
                uri.addParameter(JsonCodec.WebDriver.Grid.NEW_SESSION_PARAM, message);
            }

            URI finalUri = uri.build();
            logger.info(String.format("Notifying Remote Grid Extras node of new session with %s", finalUri));

            return HttpUtility.getRequestAsString(finalUri);
        } catch (Exception e) {
            String error = String.format("%s\n%s",
                    SOMETHING_WENT_WRONG_WHEN_NOTIFYING_NODE_OF_NEW_SESSION,
                    Throwables.getStackTraceAsString(e));
            logger.error(error);
            return error ;
        }
    }

    protected String notifyHubGridExtrasOfNewSession() {
        Map<String, Object> sessionDetails = new HashMap<String, Object>();
        try {
            sessionDetails.put(JsonCodec.WebDriver.Grid.INTERNAL_KEY, getSession().getInternalKey());
            sessionDetails.put(JsonCodec.WebDriver.Grid.EXTERNAL_KEY, JsonCodec.WebDriver.Grid.NOT_YET_ASSIGNED);
            sessionDetails.put(JsonCodec.WebDriver.Grid.HOST, getSession().getSlot().getRemoteURL().getHost());
            sessionDetails.put(JsonCodec.WebDriver.Grid.PORT, String.valueOf(getSession().getSlot().getRemoteURL().getPort()));
            sessionDetails.put(JsonCodec.TIMESTAMP, TimeStampUtility.getTimestampAsString());
//            sessionDetails.put(JsonCodec.WebDriver.Grid.REQUESTED_CAPABILITIES, getSession().getRequestedCapabilities());


            if (getSession().getExternalKey() != null) {
                sessionDetails.put(JsonCodec.WebDriver.Grid.EXTERNAL_KEY, getSession().getExternalKey().getKey());
            } else {
                String message = String.format("Session %s, did not get an external key after %s seconds.\nMore info: %s",
                        getSession().getInternalKey(), SECONDS_TO_WAIT_FOR_EXTERNAL_KEY, sessionDetails);

                sessionDetails.put(JsonCodec.WebDriver.Grid.EXTERNAL_KEY, message);
                logger.error(message);
            }

            logger.debug(sessionDetails);

            SessionHistoryLog.setOutputDir(DefaultConfig.SESSION_LOG_DIRECTORY);
            SessionHistoryLog.newSession(getSession().getSlot().getRemoteURL().getHost(), sessionDetails);
            return sessionDetails.toString();
        } catch (Exception e) {
            logger.error(SOMETHING_WENT_WRONG_WHEN_GATHERING_INFORMATION_FOR_NEW_SESSION_THREADS, e);
            return SOMETHING_WENT_WRONG_WHEN_GATHERING_INFORMATION_FOR_NEW_SESSION_THREADS;
        }
    }

    protected TestSession getSession() {
        return this.session;
    }

}