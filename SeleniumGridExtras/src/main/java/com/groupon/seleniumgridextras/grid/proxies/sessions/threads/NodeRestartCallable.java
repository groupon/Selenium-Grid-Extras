package com.groupon.seleniumgridextras.grid.proxies.sessions.threads;

import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.grid.proxies.SetupTeardownProxy;
import org.apache.log4j.Logger;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;

import java.util.concurrent.Callable;


public class NodeRestartCallable implements Callable {

    public static final int TIME_FOR_PROXY_TO_FREEUP = 2000;
    private static Logger logger = Logger.getLogger(NodeRestartCallable.class);

    protected SetupTeardownProxy proxy;
    protected TestSession session;

    public NodeRestartCallable(SetupTeardownProxy proxy, TestSession session) {
        this.proxy = proxy;
        this.session = session;
    }


    @Override
    public String call() throws Exception {
        //TODO: This is a huge improvment in the reboot logic compared to how it used to be. But this method needs clean up done by fresh eyes

        try {
            //Giving the proxy a couple of seconds to recover post session. This also gives us opportunity to check if the new build is trying to pick it up
            logger.info(String.format("Giving %s proxy %s ms to free up", this.proxy.getId(), TIME_FOR_PROXY_TO_FREEUP));
            Thread.sleep(TIME_FOR_PROXY_TO_FREEUP);
        } catch (InterruptedException e) {
            logger.error(e);
        }

        try {



            if (this.proxy.isBusy() || this.proxy.getRegistry().getNewSessionRequestCount() != 0){
                String message = String.format("Proxy %s is currently %s busy and has there are %s items in the queue. Will not attempt to reboot node until the grid is free",
                        this.proxy.getId(),
                        (this.proxy.isBusy() ? "" : "not"),
                        this.proxy.getRegistry().getNewSessionRequestCount());

                logger.info(message);
                return message;
            }

            JsonObject status = proxy.callRemoteGridExtras("grid_status");

            if (status == null) {
                String message = String.format("Problem communicating with %s, will not attempt to reboot", proxy.getRemoteHost().getHost());
                logger.warn(message);
                return message;
            }

            int sessionsStarted = status.get("node_sessions_started").getAsInt();
            int sessionLimit = status.get("node_sessions_limit").getAsInt();

            if (sessionLimit == 0) {
                String message = String.format("Node %s with proxy %s is set to never reboot, skipping this step",
                        proxy.getRemoteHost().getHost(), proxy.getId());
                logger.info(message);
                return message;
            }

            if (sessionsStarted >= sessionLimit) {
                String message = String.format("Node %s has executed has executed %s sessions, the limit is %s so it is time to reboot it",
                        proxy.getRemoteHost().getHost(), sessionsStarted, sessionLimit);

                logger.info(message);
                proxy.setAvailable(false);
                proxy.setRestarting(false);
                proxy.stopGridNode();
                proxy.rebootGridExtrasNode();


                return message;
            }
        } catch (Exception e) {
            //Capture any unexpected exception and print it to log. Currently the hub will completely ignore any
            //Exception, this way at least we can try to debug it.
            String message = String.format("Something didn't go right when trying to reboot node %s : %s",
                    proxy.getRemoteHost().getHost(), e.getMessage());
            logger.error(message, e);
            return message;
        }

        return ""; //Should never get to this point
    }


}
