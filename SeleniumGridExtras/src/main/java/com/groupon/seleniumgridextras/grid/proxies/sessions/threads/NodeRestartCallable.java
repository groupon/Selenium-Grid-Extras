package com.groupon.seleniumgridextras.grid.proxies.sessions.threads;

import com.google.common.base.Throwables;
import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.grid.proxies.SetupTeardownProxy;
import com.groupon.seleniumgridextras.tasks.GridStatus;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import com.groupon.seleniumgridextras.utilities.json.JsonParserWrapper;
import com.groupon.seleniumgridextras.utilities.threads.CommonThreadPool;
import com.groupon.seleniumgridextras.utilities.threads.RemoteGridExtrasAsyncCallable;
import org.apache.log4j.Logger;
import org.openqa.grid.common.exception.RemoteNotReachableException;
import org.openqa.grid.common.exception.RemoteUnregisterException;
import org.openqa.grid.internal.TestSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;


public class NodeRestartCallable implements Callable {

    public static final int TIME_FOR_PROXY_TO_FREEUP = 2000;
    public static final int SECONDS_TIMEOUT = 14400;
    private static Logger logger = Logger.getLogger(NodeRestartCallable.class);

    protected SetupTeardownProxy proxy;
    protected TestSession session;

    public NodeRestartCallable(SetupTeardownProxy proxy, TestSession session) {
        this.proxy = proxy;
        this.session = session;
    }


    @Override
    public String call() throws Exception {
        try {
            //Giving the proxy a couple of seconds to recover post session. This also gives us opportunity to check if the new build is trying to pick it up
            logger.info(String.format("Giving %s proxy %s ms to free up", this.proxy.getId(), TIME_FOR_PROXY_TO_FREEUP));
            Thread.sleep(TIME_FOR_PROXY_TO_FREEUP);
        } catch (InterruptedException e) {
            logger.error(Throwables.getStackTraceAsString(e));
        }

        if (this.proxy.isBusy()) {
            waitForProxyToFreeUp();
        }

        stopGridNode();
        NodeRestartCallable.rebootGridExtrasNode(proxy.getRemoteHost().getHost());

        logger.info(String.format("Proxy restart command sent for %s", proxy.getId()));
        return "Done";
    }

    public void waitForProxyToFreeUp() {
        try {
            for (int i = 0; i < SECONDS_TIMEOUT; i++) {
                if (proxy.isBusy()) {
                    Thread.sleep(1000);
                    logger.debug(String.format("Still waiting for node %s for %s seconds", proxy.getId(), i));
                } else {
                    logger.info(String.format("Proxy %s is no longer busy", proxy.getId()));
                    return;
                }
            }
        } catch (InterruptedException e) {
            logger.error(Throwables.getStackTraceAsString(e));
        }

        logger.error(String.format("Proxy %s did not finish test after %s timeout", proxy.getId(), SECONDS_TIMEOUT));

    }


    public static void rebootGridExtrasNode(String host) {
        logger.info("Asking SeleniumGridExtras to reboot node" + host);
        Future<String> f = CommonThreadPool.startCallable(
                new RemoteGridExtrasAsyncCallable(
                        host,
                        RuntimeConfig.getGridExtrasPort(),
                        TaskDescriptions.Endpoints.REBOOT,
                        new HashMap<String, String>()));
        try {
            logger.debug(f.get());
        } catch (Exception e) {
            logger.error(String.format("Error rebooting node %s, \n %s", host, Throwables.getStackTraceAsString(e)));
        }

    }

    public void stopGridNode() {

        logger.info(String.format("Asking proxy %s to stop gracefully", proxy.getId()));

        Map<String, String> params = new HashMap<String, String>();
        params.put(JsonCodec.WebDriver.Grid.PORT, String.valueOf(proxy.getRemoteHost().getPort()));

        Future<String> f = CommonThreadPool.startCallable(
                new RemoteGridExtrasAsyncCallable(
                        proxy.getRemoteHost().getHost(),
                        RuntimeConfig.getGridExtrasPort(),
                        TaskDescriptions.Endpoints.STOP_GRID,
                        params));

        try {
            logger.debug(f.get());
            unregister();
        } catch (Exception e) {
            logger.error(String.format("Error stopping proxy %s", proxy.getId()), e);
        }
    }

    public void unregister() {
    	boolean unregisterDuringReboot = true;
    	
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
        
        if (!response.equals("")) {
        	Map status = JsonParserWrapper.toHashMap(response);
        	if (status != null && status.containsKey(GridStatus.BOOLEAN_IF_NODE_WILL_UNREGISTER_DURING_REBOOT)) {
				Boolean unregisterNodeDuringReboot = (Boolean) status.get(GridStatus.BOOLEAN_IF_NODE_WILL_UNREGISTER_DURING_REBOOT);
				unregisterDuringReboot = unregisterNodeDuringReboot;
        	}
        }
        
        if (unregisterDuringReboot) {
        	proxy.addNewEvent(new RemoteUnregisterException(String.format("Taking proxy %s offline", proxy.getId())));
        } else {
        	proxy.addNewEvent(new RemoteNotReachableException(String.format("Taking proxy %s down", proxy.getId())));
        }
    }

    public static boolean timeToReboot(String nodeHost, String proxyId) {
        Future<String> f = CommonThreadPool.startCallable(
                new RemoteGridExtrasAsyncCallable(
                        nodeHost,
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
                            proxyId),
                    e);

            return false;
        }


        if (response.equals("")) {
            String error = "Something went wrong when asking for status from " + proxyId;
            logger.error(error);
            return false;
        }

        Map status = JsonParserWrapper.toHashMap(response);

        if (status == null) {
            String message = String.format("Problem communicating with %s, will not attempt to reboot", nodeHost);
            logger.warn(message);
            return false;
        }


        int recordedSessions;
        if (status.containsKey(GridStatus.DEPRECATED_STARTED_SESSIONS_KEY)) {
            logger.warn(GridStatus.DEPRECATION_WARNING + " for node " + proxyId);
            recordedSessions = ((Double) status.get(GridStatus.DEPRECATED_STARTED_SESSIONS_KEY)).intValue();
        } else {
            recordedSessions = ((List) status.get(JsonCodec.WebDriver.Grid.RECORDED_SESSIONS)).size();
        }


        int sessionLimit = ((Double) status.get(JsonCodec.WebDriver.Grid.NODE_SESSIONS_LIMIT)).intValue();

        if (sessionLimit == 0) {
            String message = String.format("Node %s with proxy %s is set to never reboot, skipping this step",
                    nodeHost, proxyId);
            logger.info(message);
            return false;
        }

        if (recordedSessions >= sessionLimit) {
            String message = String.format("Node %s has executed has executed %s sessions, the limit is %s so it is time to reboot it",
                    nodeHost, recordedSessions, sessionLimit);

            logger.info(message);
            return true;
        }

        return false;
    }

}
