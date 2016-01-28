package com.groupon.seleniumgridextras.utilities.threads.video;

import org.apache.log4j.Logger;

import com.groupon.seleniumgridextras.config.RuntimeConfig;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class VideoRecordingThreadPool {

    private static Logger logger = Logger.getLogger(VideoRecordingThreadPool.class);
    protected static ExecutorService cachedPool;
    protected static Map<String, Future<String>> futures = new HashMap<String, Future<String>>();
    protected static
    Map<String, VideoRecorderCallable>
            videos =
            new HashMap<String, VideoRecorderCallable>();


    public static Future<String> startVideoRecording(String sessionName, int timeout) {
        if (cachedPool == null) {
            initializeThreadPool();
        }

        VideoRecorderCallable aCallable = new VideoRecorderCallable(sessionName, timeout);

        Future callableFuture = cachedPool.submit(aCallable);
        futures.put(sessionName, callableFuture);
        videos.put(sessionName, aCallable);
        return callableFuture;
    }

    public static Future<String> startVideoRecording(String sessionName) {
        return startVideoRecording(sessionName, RuntimeConfig.getConfig().getVideoRecording().getIdleTimeout());
    }

    public static Future<String> stopVideoRecording(String sessionName) {
        Future<String> future = futures.get(sessionName);
        VideoRecorderCallable video = videos.get(sessionName);

        if (future.isDone() || future.isCancelled()) {
            logger.info(
                    "Tried to stop video for session " + sessionName + " but recording was already stopped");
        } else {
            logger.info("Politely asking video recording to stop");
            video.stop();
            int timeout = 5;
            while(!future.isDone() && !future.isCancelled() && timeout > 0) {
                try {
                    timeout -= 1;
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.error(e);
                    e.printStackTrace();
                }
            }

            if (future.isDone() || future.isCancelled()) {
                logger.info("Video for session " + sessionName + " has stopped.");
            } else {
                logger.warn("Have to force cancel video for session " + sessionName);
                future.cancel(true);

                if (!future.isCancelled()) {
                    logger.warn("Seems to be a runaway thread " + sessionName);
                    logger.warn(future);
                    logger.warn(video);
                }
            }
        }

        return future;
    }

    public static void addNewDescriptionToLowerThird(String sessionName, String newDescription) {
        getVideo(sessionName).lastAction(newDescription);
    }

    public static void removeSession(String sessionName) {
        futures.remove(sessionName);
        videos.remove(sessionName);
    }

    public static VideoRecorderCallable getVideo(String sessionName) {
        return videos.get(sessionName);
    }

    public static List<String> getAllVideos() {
        cleanUpOrphanedVideos();
        List<String> listOfVideos = new LinkedList<String>();
        listOfVideos.addAll(videos.keySet());
        return listOfVideos;
    }

    public static void stopAndFinalizeAllVideos() {
        for (String session : getAllVideos()) {
            stopVideoRecording(session);
        }

        System.out.println("Please wait while we stop all videos being recorded");
        for (String session : getAllVideos()) {
            logger.info("Waiting for session video to finalize for session: " + session);
            waitForThreadToStop(session);
        }
    }

    public static void waitForThreadToStop(String sessionName) {
        try {
            futures.get(sessionName).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    protected static void initializeThreadPool() {
        logger.info("Initializing a new thread pool");
        cachedPool = Executors.newCachedThreadPool();
    }

    protected static void cleanUpOrphanedVideos() {
        try {
            List<String> sessionsToCleanUp = new LinkedList<String>();
            for (String session : futures.keySet()) {
                Future<String> f = futures.get(session);
                if (f.isCancelled() || f.isDone()) {
                    sessionsToCleanUp.add(session);
                }
            }

            for (String s : sessionsToCleanUp) {
                logger.info(String.format("Video Session %s seems to have timed out, removing it from memory", s));
                removeSession(s);
            }
        } catch (Exception e) {
            logger.error("Error cleaning up orphaned video recording threads", e);
        }
    }


}
