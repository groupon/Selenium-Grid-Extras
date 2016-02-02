package com.groupon.seleniumgridextras.grid.proxies.sessions.threads;

import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.grid.proxies.AbstractProxy;
import com.groupon.seleniumgridextras.tasks.GridStatus;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import com.groupon.seleniumgridextras.utilities.json.JsonParserWrapper;
import com.groupon.seleniumgridextras.utilities.threads.CommonThreadPool;
import com.groupon.seleniumgridextras.utilities.threads.RemoteGridExtrasAsyncCallable;
import org.apache.log4j.Logger;
import org.openqa.grid.common.exception.RemoteUnregisterException;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.internal.TestSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class AppiumNodeRestartCallable implements Callable {

    public static final int TIME_FOR_PROXY_TO_FREEUP = 2000;
    public static final int SECONDS_TIMEOUT = 14400;
    public int timeout = 300;
    private static Logger logger = Logger.getLogger(AppiumNodeRestartCallable.class);

    protected AbstractProxy proxy;
    protected TestSession session;

    public AppiumNodeRestartCallable(AbstractProxy proxy, TestSession session) {
        this.proxy = proxy;
        this.session = session;
    }


    @Override
    public String call() throws Exception {
        logger.info("Running AppiumNodeRestartCallable");
        try {
            //Giving the proxy a couple of seconds to recover post session. This also gives us opportunity to check if the new build is trying to pick it up
            logger.info(String.format("Giving %s proxy %s ms to free up", this.proxy.getId(), TIME_FOR_PROXY_TO_FREEUP));
            Thread.sleep(TIME_FOR_PROXY_TO_FREEUP);
        } catch (InterruptedException e) {
            logger.error(Throwables.getStackTraceAsString(e));
        }

        if (this.proxy.isBusy()) {
            logger.info("Proxy is busy giving it time to free up");
            waitForProxyToFreeUp();
        }


        restartAppiumNode(proxy.getRemoteHost().getHost(), String.valueOf(proxy.getRemoteHost().getPort()));

        logger.info(String.format("Proxy restart command sent for %s", proxy.getId()));
        return "Done";
    }

    private void stopGridNodes() {
        List<RemoteProxy> proxies = getProxiesByIp(proxy.getRemoteHost().toString());
        for (RemoteProxy proxy : proxies) {
            if (proxy instanceof AbstractProxy) {
                ((AbstractProxy) proxy).setAvailable(false);
            } else { // should not be happening, may cause test failures
                proxy.getRegistry().removeIfPresent(proxy);
            }
        }
        // wait for all tasks to finish
        for (RemoteProxy proxy : proxies) {
            Stopwatch stopwatch = Stopwatch.createStarted();
            while (proxy.isBusy() && (stopwatch.elapsed(TimeUnit.SECONDS) < timeout)) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (stopwatch.stop().elapsed(TimeUnit.SECONDS) > timeout) {
                logger.info(String.format("Waiting for proxy %s to finish timeout.", proxy.getId()));
            }
        }

        // restarting os

    }



    private List<RemoteProxy> getProxiesByIp(String ip) {
        List<RemoteProxy> proxies = new ArrayList<RemoteProxy>();
        for (RemoteProxy proxyOnPort : proxy.getRegistry().getAllProxies()) {
            if (proxyOnPort.getRemoteHost().toString().endsWith(ip)) {
                proxies.add(proxyOnPort);
            }
        }
        return proxies;
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


    public void restartAppiumNode(String host, String port) {
        logger.info("Asking SeleniumGridExtras to reboot appium " + host + ":"+ port);
        Map<String, String> params = new HashMap<String, String>();
        params.put("port", port);

        Future<String> f = CommonThreadPool.startCallable(
                new RemoteGridExtrasAsyncCallable(
                        host,
                        RuntimeConfig.getGridExtrasPort(),
                        TaskDescriptions.Endpoints.KILL_PORT,
                        params));
        try {
            logger.info(f.get());
        } catch (Exception e) {
            logger.error(String.format("Error rebooting node %s, \n %s", host, Throwables.getStackTraceAsString(e)));
        }
        proxy.addNewEvent(new RemoteUnregisterException(String.format("Taking proxy %s offline", proxy.getId())));

        params.put("action", "restart");
        f = CommonThreadPool.startCallable(
                new RemoteGridExtrasAsyncCallable(
                        host,
                        RuntimeConfig.getGridExtrasPort(),
                        TaskDescriptions.Endpoints.APPIUM_TASKS,
                        params));
        try {
            logger.info(f.get());
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
            logger.info(f.get());
            unregister();
        } catch (Exception e) {
            logger.error(String.format("Error stopping proxy %s", proxy.getId()), e);
        }
    }

    public void unregister() {
        proxy.addNewEvent(new RemoteUnregisterException(String.format("Taking proxy %s offline", proxy.getId())));
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
            logger.info(response);
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

        return false;
    }
    public static boolean timeToRebootAppium(String nodeHost, String proxyId) {
        Future<String> f = CommonThreadPool.startCallable(
                new RemoteGridExtrasAsyncCallable(
                        nodeHost,
                        RuntimeConfig.getGridExtrasPort(),
                        TaskDescriptions.Endpoints.GRID_STATUS,
                        new HashMap<String, String>()));

        String response = "";
        try {
            response = f.get();
            logger.info(response);
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



        return false;
    }
}
