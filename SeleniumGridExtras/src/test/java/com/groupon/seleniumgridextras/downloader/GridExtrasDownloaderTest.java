package com.groupon.seleniumgridextras.downloader;

import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GridExtrasDownloaderTest {

    public static final String EXPECTED_VERSION = "1.3.3";
    public static final String EXPECTED_133_DOWNLOAD_URL = "https://github.com/groupon/Selenium-Grid-Extras/releases/download/v1.3.3/SeleniumGridExtras-1.3.3-SNAPSHOT-jar-with-dependencies.jar";
    public static final String GRID_EXTRAS_RELEASE_API_URL = "https://api.github.com/repos/groupon/Selenium-Grid-Extras/releases";
    public static final String EXPECTED_171_URL = "https://github.com/groupon/Selenium-Grid-Extras/releases/download/v1.7.1/SeleniumGridExtras-1.7.1-SNAPSHOT-jar-with-dependencies.jar";
    public static final String EXPECTED_JAR_NAME = "SeleniumGridExtras-1.3.3-SNAPSHOT-jar-with-dependencies.jar";
    private GridExtrasDownloader downloader;
    private File testDir = new File("grid_extras_downloader_test");
    private File expectedFile = new File(testDir, EXPECTED_JAR_NAME);

    @Before
    public void setUp() throws Exception {
        RuntimeConfig.setConfigFile("extras_download_test.json");
        Config config = new Config();

        config.setGridExtrasReleaseUrl(GRID_EXTRAS_RELEASE_API_URL);
        config.writeToDisk(RuntimeConfig.getConfigFile());
        RuntimeConfig.load();

        downloader = new GridExtrasDownloader();
        downloader.setVersion(EXPECTED_VERSION);

        testDir.mkdir();

        downloader.setDestinationDir(testDir.getAbsolutePath());
        downloader.setDestinationFile(downloader.getJarName(downloader.getVersion()));
    }

    @After
    public void tearDown() throws Exception {
        new File(RuntimeConfig.getConfigFile()).delete();
        new File(RuntimeConfig.getConfigFile() + ".example").delete();
        expectedFile.delete();
        testDir.delete();
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
    public void testGetReleaseApiUrl() throws Exception{
        assertEquals(GRID_EXTRAS_RELEASE_API_URL, downloader.getReleaseApiUrl());
    }

    @Test
    public void testGetDestinations() throws Exception{
        assertEquals(testDir.getAbsolutePath(), downloader.getDestinationDir());
        assertEquals(EXPECTED_JAR_NAME, downloader.getDestinationFile());
    }

    @Test
    public void testDownload() throws Exception{

        assertEquals(true, downloader.download());
        assertEquals(true, expectedFile.exists());

        assertTrue(expectedFile.length() > 4697835);
        assertTrue(expectedFile.length() < 8697835);

    }

    @Test
    public void testBadVersion() throws Exception{
        GridExtrasDownloader downloader2 = new GridExtrasDownloader();

        downloader2.setVersion("aaaaa");
        assertEquals(false , downloader2.download());
        assertEquals(0, testDir.listFiles().length);
    }

}
