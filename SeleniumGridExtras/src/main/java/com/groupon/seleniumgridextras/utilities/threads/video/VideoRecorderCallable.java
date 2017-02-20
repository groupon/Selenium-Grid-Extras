package com.groupon.seleniumgridextras.utilities.threads.video;


import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.utilities.ScreenshotUtility;
import com.groupon.seleniumgridextras.utilities.TimeStampUtility;
import com.groupon.seleniumgridextras.videorecording.ImageProcessor;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IRational;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;


public class VideoRecorderCallable implements Callable {

    private static Logger logger = Logger.getLogger(VideoRecorderCallable.class);
    protected String lastAction;
    protected Date lastActionTimestamp;
    protected boolean recording = true;
    protected String sessionId;
    final protected File outputDir = RuntimeConfig.getConfig().getVideoRecording().getOutputDir();


    protected String nodeName;
    protected String lastCommand;
    protected int idleTimeout;


    final private static
    IRational
            FRAME_RATE =
            IRational.make(RuntimeConfig.getConfig().getVideoRecording().getFrames(),
                    RuntimeConfig.getConfig().getVideoRecording().getSecondsPerFrame());
    private static Dimension dimension;


    public VideoRecorderCallable(String sessionID, int timeout) {
        this.sessionId = sessionID;
        this.idleTimeout = timeout;
        setOutputDirExists(this.sessionId);
        dynamicallySetDimension();
        if (!isResolutionDivisibleByTwo(dimension)) {
            logger.warn(String.format(
                    "\n\n\nCurrent dimension of %s x %s does not evenly divide into 2. On some OS's such as Linux this will prevent video from being recorded!\n\n\n",
                    dimension.getWidth(),
                    dimension.getHeight()));
        }
        VideoRecorderCallable.deleteOldMovies(outputDir);
    }

    @Override
    public String call() throws Exception {
        //Probably overkill to null these out, but i'm playing it safe until proven otherwise
        this.nodeName =
                "Node: " + RuntimeConfig.getOS().getHostName() + " (" + RuntimeConfig.getHostIp()
                        + ")";
        this.lastCommand = null;

        // This is the robot for taking a snapshot of the
        // screen.  It's part of Java AWT
        final Robot robot = new Robot();
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Rectangle screenBounds = new Rectangle(toolkit.getScreenSize());

        screenBounds.setBounds(0, 0, dimension.width, dimension.height);
        // First, let's make a IMediaWriter to write the file.
        // Note we're writing to a temp file.  This is to prevent it from being
        // downloaded while we're mid-write.
        final File tempFile = new File(outputDir, sessionId + ".temp");
        final
        IMediaWriter
                writer =
                ToolFactory.makeWriter(tempFile.getAbsolutePath());

        // We tell it we're going to add one video stream, with id 0,
        // at position 0, and that it will have a fixed frame rate of
        // FRAME_RATE.
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264,
                FRAME_RATE,
                screenBounds.width, screenBounds.height);

        logger
                .info("Starting video recording for session " + getSessionId() + " to " + outputDir
                        .getAbsolutePath());

        try {
            int imageFrame = 1;
            long startTime = System.nanoTime();
            addTitleFrame(writer);

            while (stopActionNotCalled() && idleTimeoutNotReached()) {

                // take the screen shot
                BufferedImage
                        screenshot =
                        ScreenshotUtility.getResizedScreenshot(dimension.width, dimension.height);

                screenshot = ImageProcessor.addTextCaption(screenshot,
                        "Session: " + this.sessionId,
                        "Host: " + this.nodeName,
                        "Timestamp: " + getTimestamp().toString(),
                        this.lastAction
                );

                // convert to the right image type
                BufferedImage bgrScreen = convertToType(screenshot,
                        BufferedImage.TYPE_3BYTE_BGR);

                // encode the image
                writer.encodeVideo(0, bgrScreen,
                        System.nanoTime() - startTime, TimeUnit.NANOSECONDS);

                // sleep for framerate milliseconds
                Thread.sleep((long) (1000 / FRAME_RATE.getDouble()));

            }
        } finally {
            writer.close();

            // Now, rename our temporary file to the final filename, so that the downloaders can detect it
            final File finalFile = new File(outputDir, sessionId + ".mp4");
            if(!tempFile.exists()) {
                logger.warn("Temporary video file for session " + getSessionId() + " doesn't exist at " + outputDir.getAbsolutePath());
            } else if(finalFile.exists()) {
                logger.warn("Destination video file for session " + getSessionId() + " already exists at " + outputDir.getAbsolutePath());
            } else {
                boolean success = tempFile.renameTo(finalFile);

                if(!success) {
                    logger.warn("Unable to rename temporary video file for session " + getSessionId());
                }
            }
        }

        return getSessionId();
    }

    protected void addTitleFrame(IMediaWriter writer) {
        writer.encodeVideo(0,
                ImageProcessor
                        .createTitleFrame(dimension, BufferedImage.TYPE_3BYTE_BGR,
                                "Session :" + this.sessionId,
                                "Host :" + RuntimeConfig.getOS().getHostName() + " ("
                                        + RuntimeConfig.getHostIp() + ")",
                                getTimestamp().toString()),
                0,
                TimeUnit.NANOSECONDS);
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    public void lastAction(String action) {
        this.lastActionTimestamp = getTimestamp();
        this.lastAction = action;
    }

    protected Date getTimestamp() {
        return TimeStampUtility.getTimestamp();
    }

    public void stop() {
        this.recording = false;
    }

    protected void setOutputDirExists(String sessionId) {
        if (!outputDir.exists()) {
            System.out.println(
                    "Root Video output dir does not exist, creating it here " + outputDir.getAbsolutePath());
            outputDir.mkdir();
        }
    }


    protected boolean idleTimeoutNotReached() {
        if (this.lastActionTimestamp == null) {
            this.lastActionTimestamp = getTimestamp();
        }

        long seconds = (getTimestamp().getTime() - this.lastActionTimestamp.getTime()) / 1000;

        if (seconds < this.idleTimeout) {
            return true;
        } else {
            logger.info("Video Timeout Reached for " + this.sessionId);
            return false;
        }
    }

    protected boolean stopActionNotCalled() {
        return recording;
    }


    protected String getSessionId() {
        return sessionId;
    }

    public static boolean isResolutionDivisibleByTwo(Dimension d) {
        return (d.getWidth() % 2 == 0 && d.getHeight() % 2 == 0);
    }

    protected void dynamicallySetDimension() {
        try {
            BufferedImage
                    sample =
                    ScreenshotUtility
                            .getResizedScreenshot(RuntimeConfig.getConfig().getVideoRecording().getWidth(),
                                    RuntimeConfig.getConfig().getVideoRecording().getHeight());
            dimension = new Dimension(sample.getWidth(), sample.getHeight());
        } catch (AWTException e) {
            e.printStackTrace();
            logger.equals(e);
            dimension =
                    new Dimension(RuntimeConfig.getConfig().getVideoRecording().getWidth(),
                            RuntimeConfig.getConfig().getVideoRecording().getHeight());
        }

    }

    /**
     * Convert a {@link BufferedImage} of any type, to {@link BufferedImage} of a specified type. If
     * the source image is the same type as the target type, then original image is returned,
     * otherwise new image of the correct type is created and the content of the source image is
     * copied into the new image.
     *
     * @param sourceImage the image to be converted
     * @param targetType  the desired BufferedImage type
     * @return a BufferedImage of the specifed target type.
     * @see BufferedImage
     */

    public static BufferedImage convertToType(BufferedImage sourceImage,
                                              int targetType) {
        BufferedImage image;

        // if the source image is already the target type, return the source image

        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        }

        // otherwise create a new image of the target type and draw the new
        // image

        else {
            image = new BufferedImage(sourceImage.getWidth(),
                    sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }

        return image;
    }

    public static void deleteOldMovies(File moviesDir) {
        File[] files = moviesDir.listFiles();
        //TODO: This is tested, but don't you dare modify this without writing a new test!
        int filesToKeep = RuntimeConfig.getConfig().getVideoRecording().getVideosToKeep();
        int currentFileCount = files.length;

        if (currentFileCount > filesToKeep) {
            Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
            int
                    filesToDelete = currentFileCount - filesToKeep;

            for (int i = 0; i < filesToDelete; i++) {
                logger.info("Cleaning up recorded video: " + files[i].getAbsolutePath());
                files[i].delete();
            }


        }

    }
}
