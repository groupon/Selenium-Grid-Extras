package com.groupon.seleniumgridextras.downloader;
//        config.getGridExtrasReleaseUrl();

import com.google.common.base.Throwables;
import com.google.gson.internal.LinkedTreeMap;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.utilities.HttpUtility;
import com.groupon.seleniumgridextras.utilities.json.JsonParserWrapper;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GridExtrasDownloader extends Downloader {
    public static final String ASSETS_KEY = "assets";
    public static final String NAME_KEY = "name";
    public static final String BROWSER_DOWNLOAD_URL = "browser_download_url";
    private final String releaseApiUrl;
    private String version;

    private static Logger logger = Logger.getLogger(GridExtrasDownloader.class);

    public String getReleaseApiUrl() {
        return releaseApiUrl;
    }

    public GridExtrasDownloader() {
        this.releaseApiUrl = RuntimeConfig.getConfig().getGridExtrasReleaseUrl();
    }

    @Override
    protected boolean startDownload(){
        try {
            URL url = new URL(getJarUrl());
            logger.info("Starting to download from " + url);
            FileUtils.copyURLToFile(url, getDestinationFileFullPath());
            logger.info("Download complete");
            return true;
        } catch (MalformedURLException error){
            logger.error(Throwables.getStackTraceAsString(error));
            setErrorMessage(error.toString());
        } catch (IOException error) {
            logger.error(Throwables.getStackTraceAsString(error));
            setErrorMessage(error.toString());
        } catch (URISyntaxException error){
            logger.error(Throwables.getStackTraceAsString(error));
            setErrorMessage(error.toString());
        } catch (Exception error){
            logger.error(Throwables.getStackTraceAsString(error));
            setErrorMessage(error.toString());
        }
        logger.error("Download failed");
        return false;
    }

    @Override
    public void setSourceURL(String source) {
       //NOT USED
    }

    @Override
    public void setDestinationFile(String destination) {
        this.destinationFile = destination;
    }

    @Override
    public void setDestinationDir(String dir) {
        this.destinationDir = dir;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public String getJarName(String version){
        return String.format("SeleniumGridExtras-%s-SNAPSHOT-jar-with-dependencies.jar", version);
    }


    public String getJarUrl() throws IOException, URISyntaxException {
        String versionToFind = getJarName(getVersion());
        logger.info("Looking for grid extras " + versionToFind);

        for (LinkedTreeMap a : (List<LinkedTreeMap>) getReleaseUrl()) {
            if (a.containsKey(ASSETS_KEY)) {
                ArrayList listOfAssets = (ArrayList) a.get(ASSETS_KEY);

                for (int i = 0; i < listOfAssets.size(); i++) {

                    LinkedTreeMap currentAsset = (LinkedTreeMap) listOfAssets.get(i);

                    if (currentAsset.containsKey(NAME_KEY) && currentAsset.containsKey(BROWSER_DOWNLOAD_URL)) {

                        if (versionToFind.equals(currentAsset.get(NAME_KEY))) {
                            return currentAsset.get(BROWSER_DOWNLOAD_URL).toString();
                        }
                    } else {
                        malformedApiResponse(NAME_KEY + " or " + BROWSER_DOWNLOAD_URL, getReleaseApiUrl(), currentAsset.toString());
                    }
                }
            } else {
                malformedApiResponse(ASSETS_KEY, getReleaseApiUrl(), a.toString());
            }
        }


        return this.releaseApiUrl;
    }

    private List getReleaseUrl() throws URISyntaxException, IOException {
        return JsonParserWrapper.toList(HttpUtility.getRequestAsString(new URI(this.releaseApiUrl)));
    }

    private void malformedApiResponse(String key, String url, String json) {
        logger.warn(String.format(
                "Response API seems to be malformed and does not have %s key. \n URL: %s \n JSON: %s",
                ASSETS_KEY,
                getReleaseApiUrl(),
                json
        ));
    }
}
