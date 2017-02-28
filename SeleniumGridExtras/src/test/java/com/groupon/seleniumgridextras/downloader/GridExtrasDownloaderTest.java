package com.groupon.seleniumgridextras.downloader;

import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.DefaultConfig;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.utilities.TimeStampUtility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GridExtrasDownloaderTest {

    public static final String EXPECTED_VERSION = "1.3.3";
    public static final String EXPECTED_133_DOWNLOAD_URL = "https://github.com/groupon/Selenium-Grid-Extras/releases/download/v1.3.3/SeleniumGridExtras-1.3.3-SNAPSHOT-jar-with-dependencies.jar";
    public static final String GRID_EXTRAS_RELEASE_API_URL = "https://api.github.com/repos/groupon/Selenium-Grid-Extras/releases";
    public static final String EXPECTED_171_URL = "https://github.com/groupon/Selenium-Grid-Extras/releases/download/v1.7.1/SeleniumGridExtras-1.7.1-SNAPSHOT-jar-with-dependencies.jar";
    public static final String EXPECTED_JAR_NAME = "SeleniumGridExtras-1.3.3-SNAPSHOT-jar-with-dependencies.jar";
    public static final int GRID_EXTRAS_AUTO_UPDATE_CHECK_INTERVAL = 2000;
    private GridExtrasDownloader downloader;
    private File testDir = new File("grid_extras_downloader_test");
    private File expectedFile = new File(testDir, EXPECTED_JAR_NAME);

    @Before
    public void setUp() throws Exception {
        RuntimeConfig.setConfigFile("extras_download_test.json");
        Config config = new Config();

        config.setGridExtrasReleaseUrl(GRID_EXTRAS_RELEASE_API_URL);
        config.writeToDisk(RuntimeConfig.getConfigFile());
        config.setGridExtrasAutoUpdateCheckInterval(GRID_EXTRAS_AUTO_UPDATE_CHECK_INTERVAL);
        config.writeToDisk(RuntimeConfig.getConfigFile());
        RuntimeConfig.load();

        downloader = new GridExtrasDownloader();
        downloader.setVersion(EXPECTED_VERSION);

        testDir.mkdir();

        downloader.setDestinationDir(testDir.getAbsolutePath());
        downloader.setDestinationFile(downloader.getJarName(downloader.getVersion()));

        if (downloader.getCachedReleaseResponseFile().exists()){
            downloader.getCachedReleaseResponseFile().delete();
        }

    }

    @After
    public void tearDown() throws Exception {
        new File(RuntimeConfig.getConfigFile()).delete();
        new File(RuntimeConfig.getConfigFile() + ".example").delete();
        expectedFile.delete();
        testDir.delete();

        if (downloader.getCachedReleaseResponseFile().exists()){
            downloader.getCachedReleaseResponseFile().delete();
        }

        if (new File(RuntimeConfig.getConfigFile()).exists()){
            new File(RuntimeConfig.getConfigFile()).delete();
        }

        if (new File(RuntimeConfig.getConfigFile() + ".example").exists()){
            new File(RuntimeConfig.getConfigFile() + ".example").delete();
        }
    }


    @Test
    public void testGetCachedReleaseList() throws Exception {
        assertEquals(new File(GridExtrasDownloader.CACHED_RELEASE_LIST_JSON), downloader.getCachedReleaseResponseFile());
        assertFalse(downloader.getCachedReleaseResponseFile().exists());

        String initialResponse = downloader.getCachedReleaseList();
        assertTrue(downloader.getCachedReleaseResponseFile().exists());

        long msRange = TimeStampUtility.timestampInMs() - downloader.getCachedReleaseResponseFile().lastModified();
        assertTrue(msRange < 10000); //Make sure that file created is no older than 10 seconds
        System.out.println("INITIAL RESPONSE : " + initialResponse);
        System.out.println("downloader.getCachedReleaseList() : " + downloader.getCachedReleaseList());
        assertEquals(initialResponse, downloader.getCachedReleaseList());

        Thread.sleep(5000);
        //Let file expire and try to download cached file again, make sure response is still the same
        //But the file gets re-written

        assertEquals(initialResponse, downloader.getCachedReleaseList());
        System.out.println("INITIAL RESPONSE : " + initialResponse);
        System.out.println("downloader.getCachedReleaseList() : " + downloader.getCachedReleaseList());
        long msRange2 = TimeStampUtility.timestampInMs() - downloader.getCachedReleaseResponseFile().lastModified();
        assertTrue(msRange2 < 2000); //Make sure that file created is no older than 2 seconds
    }

    @Test
    public void testCheckInterval() throws Exception {
        //Check if interval is written/loaded
        assertEquals(GRID_EXTRAS_AUTO_UPDATE_CHECK_INTERVAL,
                RuntimeConfig.getConfig().getGridExtrasAutoUpdateCheckInterval());

        //Check empty default
        assertEquals(DefaultConfig.GRID_EXTRAS_AUTO_UPDATE_CHECK_INTERVAL,
                new Config().getGridExtrasAutoUpdateCheckInterval());
    }

    @Test
    public void testGetVersion() throws Exception {
        assertEquals(EXPECTED_VERSION, downloader.getVersion());
    }

    @Test
    public void testGetDownloadJarUrl() throws Exception {
        assertEquals(EXPECTED_133_DOWNLOAD_URL, downloader.getJarUrl());

        GridExtrasDownloader downloader2 = new GridExtrasDownloader();

        downloader2.setVersion("1.7.1");
        assertEquals(EXPECTED_171_URL, downloader2.getJarUrl());
    }

    @Test
    public void testGetReleaseApiUrl() throws Exception {
        assertEquals(GRID_EXTRAS_RELEASE_API_URL, downloader.getReleaseApiUrl());
    }

    @Test
    public void testGetDestinations() throws Exception {
        assertEquals(testDir.getAbsolutePath(), downloader.getDestinationDir());
        assertEquals(EXPECTED_JAR_NAME, downloader.getDestinationFile());
    }

    @Test
    public void testDownload() throws Exception {

        assertEquals(true, downloader.download());
        assertEquals(true, expectedFile.exists());

        assertTrue(expectedFile.length() > 4697835);
        assertTrue(expectedFile.length() < 8697835);

    }

    @Test
    public void testBadVersion() throws Exception {
        GridExtrasDownloader downloader2 = new GridExtrasDownloader();

        downloader2.setVersion("aaaaa");
        assertEquals(false, downloader2.download());
        assertEquals(0, testDir.listFiles().length);
    }

    @Test
    public void testGetAllAssets() throws Exception {
        String expectedVersionOldest = "1.2.3";
        String expectedVersionFifthOldest = "1.3.3";
        List<Map<String, String>> actual = downloader.getAllDownloadableAssets();
        int actualSize = actual.size();

        assertTrue(actualSize > 0);

        assertEquals("SeleniumGridExtras-" + expectedVersionOldest +"-SNAPSHOT-jar-with-dependencies.jar",
                actual.get(actualSize - 1).keySet().toArray()[0]);

        assertEquals("https://github.com/groupon/Selenium-Grid-Extras/releases/download/v" + expectedVersionOldest + "/SeleniumGridExtras-" + expectedVersionOldest + "-SNAPSHOT-jar-with-dependencies.jar",
                actual.get(actualSize - 1).values().toArray()[0]);


        assertEquals("SeleniumGridExtras-" + expectedVersionFifthOldest + "-SNAPSHOT-jar-with-dependencies.jar",
                actual.get(actualSize - 5).keySet().toArray()[0]);

        assertEquals("https://github.com/groupon/Selenium-Grid-Extras/releases/download/v" + expectedVersionFifthOldest + "/SeleniumGridExtras-" + expectedVersionFifthOldest + "-SNAPSHOT-jar-with-dependencies.jar",
                actual.get(actualSize - 5).values().toArray()[0]);
    }

    @Test
    public void testSanitizeVersions() throws Exception {

        List<Map<String, String>> inputList = new LinkedList<Map<String, String>>();
        Map<String, String> tempInputMap = new HashMap<String, String>();
        tempInputMap.put("SeleniumGridExtras-1.6.1-SNAPSHOT-jar-with-dependencies.jar", "https://github.com/groupon/Selenium-Grid-Extras/releases/download/v1.6.1/SeleniumGridExtras-1.6.1-SNAPSHOT-jar-with-dependencies.jar");
        inputList.add(tempInputMap);


        List<Map<String, String>> expectedList = new LinkedList<Map<String, String>>();
        Map<String, String> tempOutputMa = new HashMap<String, String>();
        tempOutputMa.put("1.6.1", "https://github.com/groupon/Selenium-Grid-Extras/releases/download/v1.6.1/SeleniumGridExtras-1.6.1-SNAPSHOT-jar-with-dependencies.jar");
        expectedList.add(tempOutputMa);

        assertEquals(expectedList, GridExtrasDownloader.sanitizeDownloadableAssetsVersions(inputList));

    }

}
