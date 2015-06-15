package com.groupon.seleniumgridextras.grid.proxies.sessions.threads;

import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.grid.proxies.SetupTeardownProxy;
import com.groupon.seleniumgridextras.tasks.GridStatus;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import com.groupon.seleniumgridextras.utilities.json.JsonParserWrapper;
import com.groupon.seleniumgridextras.utilities.threads.CommonThreadPool;
import com.groupon.seleniumgridextras.utilities.threads.RemoteGridExtrasAsyncCallable;
import org.apache.log4j.Logger;
import org.openqa.grid.internal.TestSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;


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

        Map status = null;
        try {


            if (this.proxy.isBusy() || this.proxy.getRegistry().getNewSessionRequestCount() != 0) {
                String message = String.format("Proxy %s is currently %s busy and has there are %s items in the queue. Will not attempt to reboot node until the grid is free",
                        this.proxy.getId(),
                        (this.proxy.isBusy() ? "" : "not"),
                        this.proxy.getRegistry().getNewSessionRequestCount());

                logger.info(message);
                return message;
            }

            Future<String> f = CommonThreadPool.startCallable(
                    new RemoteGridExtrasAsyncCallable(
                            proxy.getRemoteHost().getHost(),
                            RuntimeConfig.getGridExtrasPort(),
                            TaskDescriptions.Endpoints.GRID_STATUS,
                            new HashMap<String, String>()));

            String response = "";
            try {
                response = f.get();
                logger.debug(response);
            } catch (Exception e) {
                logger.error(
                        String.format(
                                "Error getting the %s endpoint for proxy %s ",
                                TaskDescriptions.Endpoints.GRID_STATUS,
                                proxy.getId()),
                        e);
            }


            if (response.equals("")){
                String error = "Something went wrong when asking for status from " + proxy.getId();
                logger.error(error);
                return error;
            }

            status = JsonParserWrapper.toHashMap(response);

            if (status == null) {
                String message = String.format("Problem communicating with %s, will not attempt to reboot", proxy.getRemoteHost().getHost());
                logger.warn(message);
                return message;
            }


            int recordedSessions;
            if (status.containsKey(GridStatus.DEPRECATED_STARTED_SESSIONS_KEY)) {
                logger.warn(GridStatus.DEPRECATION_WARNING + " for node " + this.proxy.getId());
                recordedSessions = ((Double) status.get(GridStatus.DEPRECATED_STARTED_SESSIONS_KEY)).intValue();
            } else {
                recordedSessions = ((List) status.get(JsonCodec.WebDriver.Grid.RECORDED_SESSIONS)).size();
            }


            int sessionLimit = ((Double) status.get(JsonCodec.WebDriver.Grid.NODE_SESSIONS_LIMIT)).intValue();

            if (sessionLimit == 0) {
                String message = String.format("Node %s with proxy %s is set to never reboot, skipping this step",
                        proxy.getRemoteHost().getHost(), proxy.getId());
                logger.info(message);
                return message;
            }

            if (recordedSessions >= sessionLimit) {
                String message = String.format("Node %s has executed has executed %s sessions, the limit is %s so it is time to reboot it",
                        proxy.getRemoteHost().getHost(), recordedSessions, sessionLimit);

                logger.info(message);
                proxy.setAvailable(false);
                proxy.setRestarting(false);

                String host = proxy.getRemoteHost().getHost(); //Grabbing host before un-register call, just in case it gets set to null
                proxy.stopGridNode();
                SetupTeardownProxy.rebootGridExtrasNode(host);


                return message;
            }
        } catch (Exception e) {
            //Capture any unexpected exception and print it to log. Currently the hub will completely ignore any
            //Exception, this way at least we can try to debug it.
            String message = String.format("Something didn't go right when trying to reboot node %s : %s. \nReply from node:\n%s",
                    proxy.getRemoteHost().getHost(), e.getMessage(), "" + status);
            logger.error(message, e);
            return message;
        }

        return ""; //Should never get to this point
    }


}
