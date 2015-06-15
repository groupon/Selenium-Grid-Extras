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
import java.util.*;

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
    protected boolean startDownload() {
        try {
            URL url = new URL(getJarUrl());

            int statusCode = HttpUtility.checkIfUrlStatusCode(url);
            if (statusCode != 200) {
                setErrorMessage(String.format("URL %s returned %s", url, statusCode));
                return false;
            }

            logger.info("Starting to download from " + url);
            FileUtils.copyURLToFile(url, getDestinationFileFullPath());
            logger.info("Download complete");
            return true;
        } catch (MalformedURLException error) {
            logger.error(Throwables.getStackTraceAsString(error));
            setErrorMessage(error.toString());
        } catch (IOException error) {
            logger.error(Throwables.getStackTraceAsString(error));
            setErrorMessage(error.toString());
        } catch (URISyntaxException error) {
            logger.error(Throwables.getStackTraceAsString(error));
            setErrorMessage(error.toString());
        } catch (Exception error) {
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

    public String getJarName(String version) {
        return String.format("SeleniumGridExtras-%s-SNAPSHOT-jar-with-dependencies.jar", version);
    }


    public String getJarUrl() throws IOException, URISyntaxException {
        String versionToFind = getJarName(getVersion());
        logger.info("Looking for grid extras " + versionToFind);
        List<Map<String, String>> downloadableAssets = getAllDownloadableAssets();
        for (Map<String, String> currentRelease : downloadableAssets) {
            if (versionToFind.equals(currentRelease.keySet().toArray()[0])) {
                return String.valueOf(currentRelease.values().toArray()[0]);
            }
        }
        return "";
    }

    public static List<Map<String, String>> sanitizeDownloadableAssetsVersions(List<Map<String, String>> input) {

        List<Map<String, String>> sanitized = new LinkedList<Map<String, String>>();

        for (Map<String, String> currentVersion : input) {
            Map<String, String> tempMap = new HashMap<String, String>();
            tempMap.put(
                    currentVersion.keySet().toArray()[0].toString()
                            .replace("SeleniumGridExtras-", "")
                            .replace("-SNAPSHOT-jar-with-dependencies.jar",
                                    ""),
                    currentVersion.values().toArray()[0].toString());


            sanitized.add(tempMap);
        }

        return sanitized;
    }

    public List<Map<String, String>> getAllDownloadableAssets() throws IOException, URISyntaxException {
        List<Map<String, String>> releaseList = new LinkedList<Map<String, String>>();

        List<LinkedTreeMap> releases = parseAllReleases();

        for (LinkedTreeMap currentRelease : releases) {
            if (currentRelease.containsKey(ASSETS_KEY)) {
                ArrayList listOfAssetsForCurrentRelease = (ArrayList) currentRelease.get(ASSETS_KEY);

                for (int i = 0; i < listOfAssetsForCurrentRelease.size(); i++) {
                    LinkedTreeMap currentAsset = (LinkedTreeMap) listOfAssetsForCurrentRelease.get(i);

                    if (currentAsset.containsKey(NAME_KEY) && currentAsset.containsKey(BROWSER_DOWNLOAD_URL)) {
                        Map<String, String> tempMap = new HashMap<String, String>();
                        tempMap.put(currentAsset.get(NAME_KEY).toString(), currentAsset.get(BROWSER_DOWNLOAD_URL).toString());
                        releaseList.add(tempMap);
                    } else {
                        malformedApiResponse(NAME_KEY + " or " + BROWSER_DOWNLOAD_URL, getReleaseApiUrl(), currentAsset.toString());
                    }

                }


            } else {
                malformedApiResponse(ASSETS_KEY, getReleaseApiUrl(), currentRelease.toString());
            }

        }

        return releaseList;
    }

    private List<LinkedTreeMap> parseAllReleases() throws URISyntaxException, IOException {
        return (List<LinkedTreeMap>) JsonParserWrapper.toList(HttpUtility.getRequestAsString(new URI(this.releaseApiUrl)));
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
