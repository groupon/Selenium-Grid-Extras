package com.groupon.seleniumgridextras;

import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;


public class VideoHttpExecutor implements HttpHandler {

    private static Logger logger = Logger.getLogger(VideoHttpExecutor.class);
    public static final String GET_VIDEO_FILE_ENDPOINT = "/download_video";
    private File outputDir;


    @Override
    public void handle(HttpExchange t) throws IOException {

        if (outputDir == null) {
            outputDir = RuntimeConfig.getConfig().getVideoRecording().getOutputDir();
            logger.info("Setting video source directory to " + outputDir.getAbsolutePath());
        }

        URI uri = t.getRequestURI();
        String desiredFile = uri.getPath().replaceAll(GET_VIDEO_FILE_ENDPOINT + "/", "");
        File videoFile = new File(outputDir, desiredFile);
        logger.info("New video download request for " + videoFile.getAbsolutePath());

        if (videoFile.exists()) {
            OutputStream os = null;
            FileInputStream fs = null;
            try {
                logger.info(String.format("Requested video %s does exist, responding with 200", videoFile.getAbsolutePath()));
                Headers h = t.getResponseHeaders();
                h.add("Content-Type", "video/mp4");

                t.sendResponseHeaders(200, 0);
                os = t.getResponseBody();
                fs = new FileInputStream(videoFile);
                final byte[] buffer = new byte[0x10000];
                int count = 0;
                while ((count = fs.read(buffer)) >= 0) {
                    os.write(buffer, 0, count);
                }
            } catch (Exception e) {
                logger.error("Something went wrong when serving video file " + videoFile.getAbsolutePath(), e);
            } finally {
                if(fs != null)
                    fs.close();
                if(os != null)
                    os.close();
            }

        } else {
            logger.info(String.format("Requested video %s does not exist, responding with 404", videoFile.getAbsolutePath()));
            // Object does not exist or is not a file: reject with 404 error.
            String response = "404 (Not Found)\n";
            t.sendResponseHeaders(404, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

    }

//    abstract String execute(Map params);
}


