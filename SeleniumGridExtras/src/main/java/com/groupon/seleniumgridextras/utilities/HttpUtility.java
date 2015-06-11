package com.groupon.seleniumgridextras.utilities;

import com.google.common.base.Throwables;
import com.groupon.seleniumgridextras.VideoHttpExecutor;
import com.groupon.seleniumgridextras.config.DefaultConfig;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public class HttpUtility {

    private static Logger logger = Logger.getLogger(HttpUtility.class);


    public static String getRequestAsString(URI uri) throws IOException, URISyntaxException {
        return getRequestAsString(uri.toURL());
    }

    public static String getRequestAsString(URL url) throws IOException {
        return getRequestAsString(url, RuntimeConfig.getConfig() != null ?
                RuntimeConfig.getConfig().getHttpRequestTimeout() :
                DefaultConfig.HTTP_REQUEST_TIMEOUT);
    }

    public static String getRequestAsString(URL url, int timeout) throws IOException {
        HttpURLConnection conn = getRequest(url, timeout);

        if (conn.getResponseCode() == 200) {
            return StreamUtility.inputStreamToString(conn.getInputStream());
        } else {
            return "";
        }
    }

    public static HttpURLConnection getRequest(URI uri) throws IOException {
        return getRequest(uri.toURL());
    }

    public static HttpURLConnection getRequest(URL url) throws IOException {
        return getRequest(url, RuntimeConfig.getConfig() != null ?
                RuntimeConfig.getConfig().getHttpRequestTimeout() :
                DefaultConfig.HTTP_REQUEST_TIMEOUT);
    }

    public static HttpURLConnection getRequest(URL url, int timeout) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(timeout);
        conn.setReadTimeout(timeout);

        logger.debug("Response code is " + conn.getResponseCode());
        return conn;
    }

    public static File downloadVideoFromUri(URI uri) {
        //Don't modify this without running the comment out tests!

        File destinationDir;
        if (RuntimeConfig.getConfig() != null) {
            destinationDir = RuntimeConfig.getConfig().getVideoRecording().getOutputDir();
        } else {
            destinationDir = new File(DefaultConfig.VIDEO_OUTPUT_DIRECTORY);
        }

        if (!destinationDir.exists()) {
            destinationDir.mkdir();
        }

        File destFile = new File(
                destinationDir.getAbsolutePath(),
                uri.getRawPath().replaceAll(
                        VideoHttpExecutor.GET_VIDEO_FILE_ENDPOINT,
                        "").replaceAll(
                        "/",
                        ""));

        try {
            FileUtils.copyURLToFile(uri.toURL(), destFile);
        } catch (Exception e) {
            logger.error(
                    String.format(
                            "Exception happened while downloading video. file: %s, dest dir: %s, source url: %s",
                            destFile.getAbsolutePath(),
                            destinationDir.getAbsolutePath(),
                            uri.toString()),
                    e);
            return null;
        }

        return destFile;
    }

    public static int checkIfUrlStatusCode(URL u) {

        HttpURLConnection huc = null;
        try {

            huc = (HttpURLConnection) u.openConnection();
            huc.setRequestMethod("GET");
            huc.setInstanceFollowRedirects(true);
            huc.connect();
//            OutputStream os = huc.getOutputStream();
            return huc.getResponseCode();
        } catch (IOException e) {
            String message = String.format("URL: %s, \n %s", u, Throwables.getStackTraceAsString(e));
            System.out.println(message);
            logger.warn(message);
        }

        return -1;
    }


}
