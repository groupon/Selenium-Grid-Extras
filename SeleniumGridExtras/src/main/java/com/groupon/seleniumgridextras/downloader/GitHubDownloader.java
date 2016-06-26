package com.groupon.seleniumgridextras.downloader;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.internal.LinkedTreeMap;
import com.groupon.seleniumgridextras.utilities.HttpUtility;
import com.groupon.seleniumgridextras.utilities.json.JsonParserWrapper;

public class GitHubDownloader {
    public static final String ASSETS_KEY = "assets";
    public static final String NAME_KEY = "name";
    public static final String BROWSER_DOWNLOAD_URL = "browser_download_url";
    public static final String CACHED_RELEASE_LIST_JSON = "cached_release_list.json";
    private final String releaseApiUrl;
	
    private static Logger logger = Logger.getLogger(GitHubDownloader.class);

    public GitHubDownloader(String releaseApiUrl) {
        this.releaseApiUrl = releaseApiUrl;
    }

    public String getReleaseApiUrl() {
        return releaseApiUrl;
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

    protected String getReleaseList() throws URISyntaxException, IOException {
        String response;
        response = HttpUtility.getRequestAsString(new URI(this.releaseApiUrl));
        return response;
    }

    private List<LinkedTreeMap> parseAllReleases() throws URISyntaxException, IOException {
        return (List<LinkedTreeMap>) JsonParserWrapper.toList(getReleaseList());
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
