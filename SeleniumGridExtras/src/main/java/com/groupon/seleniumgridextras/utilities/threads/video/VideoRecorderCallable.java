package com.groupon.seleniumgridextras.utilities.threads.video;


import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.utilities.ScreenshotUtility;
import com.groupon.seleniumgridextras.utilities.TimeStampUtility;
import com.groupon.seleniumgridextras.videorecording.ImageProcessor;

import io.humble.ferry.*;
import io.humble.video.*;
import io.humble.video.awt.*;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.log4j.Logger;

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


    final private static
    Rational
        FRAME_RATE =
        Rational.make(RuntimeConfig.getConfig().getVideoRecording().getSecondsPerFrame(),
                RuntimeConfig.getConfig().getVideoRecording().getFrames());
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
        if (Boolean.getBoolean("memory.debug")) {
            JNIMemoryManager.getMgr().setMemoryDebugging(true);
        }

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
        final File tempFile = new File(outputDir, sessionId + ".temp.mp4");

        /** First we create a muxer using the passed in filename and formatname if given. */
        Muxer muxer = Muxer.make(tempFile.getAbsolutePath(), null, /*formatname*/null);

        /** Now, we need to decide what type of codec to use to encode video. Muxers
         * have limited sets of codecs they can use. We're going to pick the first one that
         * works, or if the user supplied a codec name, we're going to force-fit that
         * in instead.
         */
        MuxerFormat format = muxer.getFormat();

        final Codec codec = Codec.findEncodingCodec(Codec.ID.CODEC_ID_H264);

        // We tell it we're going to add one video stream, with id 0,
        // at position 0, and that it will have a fixed frame rate of
        // FRAME_RATE.

        /**
         * Now that we know what codec, we need to create an encoder
         */
        Encoder encoder = Encoder.make(codec);

        /**
         * Video encoders need to know at a minimum:
         *   width
         *   height
         *   pixel format
         * Some also need to know frame-rate (older codecs that had a fixed rate at which video files could
         * be written needed this). There are many other options you can set on an encoder, but we're
         * going to keep it simpler here.
         */
        encoder.setWidth(screenBounds.width);
        encoder.setHeight(screenBounds.height);
        // We are going to use 420P as the format because that's what most video formats these days use
        final PixelFormat.Type pixelformat = PixelFormat.Type.PIX_FMT_YUV420P;
        encoder.setPixelFormat(pixelformat);
        encoder.setTimeBase(FRAME_RATE);

        /** An annoynace of some formats is that they need global (rather than per-stream) headers,
         * and in that case you have to tell the encoder. And since Encoders are decoupled from
         * Muxers, there is no easy way to know this beyond
         */
        if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER))
            encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);

        /** Open the encoder. */
        encoder.open(null, null);

        /** Add this stream to the muxer. */
        muxer.addNewStream(encoder);

        /** And open the muxer for business. */
        muxer.open(null, null);

        int n = muxer.getNumStreams();
        MuxerStream[] muxerStreams = new MuxerStream[n];
        Coder[] coder = new Coder[n];
        for (int i = 0; i < n; i++) {
            muxerStreams[i] = muxer.getStream(i);
            if (muxerStreams[i] != null) {
                coder[i] = muxerStreams[i].getCoder();
            }
        }

        /** Next, we need to make sure we have the right MediaPicture format objects
         * to encode data with. Java (and most on-screen graphics programs) use some
         * variant of Red-Green-Blue image encoding (a.k.a. RGB or BGR). Most video
         * codecs use some variant of YCrCb formatting. So we're going to have to
         * convert. To do that, we'll introduce a MediaPictureConverter object later. object.
         */
        MediaPictureConverter converter = null;
        final MediaPicture picture = MediaPicture.make(
            encoder.getWidth(),
            encoder.getHeight(),
            pixelformat);
        picture.setTimeBase(FRAME_RATE);

        logger
            .info("Starting video recording for session " + getSessionId() + " to " + outputDir
                .getAbsolutePath());

        MediaPacket packet = MediaPacket.make();
        try {
            int imageFrame = 0;

            {
                BufferedImage titleFrame
                    = ImageProcessor.createTitleFrame(
                    dimension,
                    BufferedImage.TYPE_3BYTE_BGR,
                    "Session :" + this.sessionId,
                    "Host :" + RuntimeConfig.getOS().getHostName() + " ("
                        + RuntimeConfig.getHostIp() + ")",
                    getTimestamp().toString());
                if (converter == null)
                    converter = MediaPictureConverterFactory
                        .createConverter(titleFrame, picture);
                converter.toPicture(picture, titleFrame, imageFrame++);

                do
                {
                    encoder.encode(packet, picture);
                    if (packet.isComplete())
                        muxer.write(packet, false);
                }
                while (packet.isComplete());
                try
                {
                    Thread.sleep(2);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

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
                /** This is LIKELY not in YUV420P format, so we're going to convert it using some handy utilities. */
                if (converter == null)
                    converter = MediaPictureConverterFactory.createConverter(bgrScreen, picture);
                converter.toPicture(picture, bgrScreen, imageFrame++);

                do {
                    encoder.encode(packet, picture);
                    if (packet.isComplete())
                        muxer.write(packet, false);
                } while (packet.isComplete());

                // sleep for framerate milliseconds
                Thread.sleep((long) (1000 * FRAME_RATE.getDouble()));

            }
        } finally {
            /** Encoders, like decoders, sometimes cache pictures so it can do the right key-frame optimizations.
             * So, they need to be flushed as well. As with the decoders, the convention is to pass in a null
             * input until the output is not complete.
             */
            do {
                encoder.encode(packet, null);
                if (packet.isComplete())
                    muxer.write(packet,  false);
            } while (packet.isComplete());

            /** Finally, let's clean up after ourselves. */
            muxer.close();

            muxer.delete();
            converter.delete();
            packet.delete();
            format.delete();

            muxer = null;
            converter = null;
            packet = null;
            format = null;

            for (int i=0; i < muxerStreams.length; i++) {
                if (muxerStreams[i] != null) {
                    muxerStreams[i].delete();
                    muxerStreams[i] = null;
                }
                if (coder[i] != null) {
                    coder[i].delete();
                    coder[i] = null;
                }
            }

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

        if (Boolean.getBoolean("memory.debug")) {
            logger.info("number of alive objects:" + JNIMemoryManager.getMgr().getNumPinnedObjects());
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
