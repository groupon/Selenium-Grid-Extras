package com.groupon.seleniumgridextras.utilities.threads.video;


import com.google.common.io.*;
import com.groupon.seleniumgridextras.*;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.utilities.ScreenshotUtility;
import com.groupon.seleniumgridextras.utilities.TimeStampUtility;
import com.groupon.seleniumgridextras.videorecording.ImageProcessor;

import org.apache.commons.io.*;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.lang3.*;
import org.apache.commons.lang3.time.*;
import org.apache.log4j.Logger;

import javax.imageio.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Callable;


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

        logger.info(
            "Starting video recording for session " + getSessionId()
                + " to " + outputDir.getAbsolutePath());

        File tempOutputFolder = Files.createTempDir();

        try {
            int imageFrame = 0;

            // creates the title frame
            {
                BufferedImage titleFrame
                    = ImageProcessor.createTitleFrame(
                    dimension,
                    BufferedImage.TYPE_INT_RGB,
                    "Session :" + this.sessionId,
                    "Host :" + RuntimeConfig.getOS().getHostName() + " ("
                        + RuntimeConfig.getHostIp() + ")",
                    getTimestamp().toString());

                // 4 symbol string from 0000 to 9999
                // with 5 frame per second this gives us ~2000 seconds
                // more than 30 minutes of recording
                ImageIO.write(titleFrame, "png",
                    new File(tempOutputFolder,
                        StringUtils.leftPad(String.valueOf(imageFrame++), 4, '0')
                            + ".png"));
            }

            while (stopActionNotCalled() && idleTimeoutNotReached()) {

                // take the screen shot
                BufferedImage screenshot
                    = ScreenshotUtility.getResizedScreenshot(
                    dimension.width, dimension.height);

                screenshot = ImageProcessor.addTextCaption(screenshot,
                    "Session: " + this.sessionId,
                    "Host: " + this.nodeName,
                    "Timestamp: " + getTimestamp().toString(),
                    this.lastAction
                );

                ImageIO.write(screenshot, "png",
                    new File(tempOutputFolder,
                        StringUtils.leftPad(String.valueOf(imageFrame++), 4, '0')
                            + ".png"));

                Thread.sleep(
                    1000 / RuntimeConfig.getConfig().getVideoRecording().getFrames());
            }
        } finally {
            final File tempFile = new File(outputDir, sessionId + ".temp.mp4");

            // no execute ffmpeg and wait for it
            //ffmpeg -r $FRAME_RATE(5) -f image2 -s $RESOLUTION(1024x768) -i %04d.png -vcodec libx264 -crf 25  -pix_fmt yuv420p test.mp4
            String ffmpegCommand
                = "ffmpeg -r %s -f image2 -s %sx%s -i %s.png -vcodec libx264 -crf 25 -pix_fmt yuv420p %s";

            String cmd = String.format(
                ffmpegCommand,
                String.valueOf(RuntimeConfig.getConfig().getVideoRecording().getFrames()),
                new Double(dimension.getWidth()).intValue(),
                new Double(dimension.getHeight()).intValue(),
                // %04d means that zeros will be padded until the length of the string is 4 i.e 0001…0020…0030…2000 and so on.
                tempOutputFolder.getAbsolutePath() + File.separator + "%04d",
                tempFile.getAbsolutePath());

            long t1 = System.currentTimeMillis();
            Object result = ExecuteCommand.execRuntime(cmd, true);
            logger.debug(cmd + " result:" + result);
            logger.info("Video encoding for " + this.sessionId + " took: "
                + DurationFormatUtils.formatDuration(
                    System.currentTimeMillis() - t1, "mm 'minutes' ss.SSS 'seconds'."));

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

            FileUtils.deleteDirectory(tempOutputFolder);
        }

        return getSessionId();
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
            Graphics g = image.getGraphics();
            g.drawImage(sourceImage, 0, 0, null);
            g.dispose();
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
