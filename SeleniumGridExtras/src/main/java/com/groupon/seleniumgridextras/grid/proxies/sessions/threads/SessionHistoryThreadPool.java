package com.groupon.seleniumgridextras.grid.proxies.sessions.threads;

import org.apache.log4j.Logger;
import org.openqa.grid.internal.TestSession;

import java.util.concurrent.*;

public class SessionHistoryThreadPool {

    private static Logger logger = Logger.getLogger(SessionHistoryThreadPool.class);
    protected static ExecutorService cachedPool;

    public static void registerNewSession(TestSession session) {
        if (cachedPool == null) {
            initiateThreadPool();
        }

        logger.info(String.format("New session request started on proxy %s, int. key: %s ",
                session.getSlot().getProxy().getId(), session.getInternalKey()));

        SessionHistoryCallable aCallable = new SessionHistoryCallable(session);

        cachedPool.submit(aCallable);
    }

    private static void initiateThreadPool() {
        logger.info("Initializing a new thread pool for Session History registration");
        cachedPool = Executors.newCachedThreadPool();

    }


}
