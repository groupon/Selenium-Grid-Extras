package com.groupon.seleniumgridextras.loggers;

import com.groupon.seleniumgridextras.utilities.FileIOUtility;
import com.groupon.seleniumgridextras.utilities.TimeStampUtility;
import com.groupon.seleniumgridextras.utilities.json.JsonParserWrapper;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
//This test file is making me sad

public class SessionHistoryLogTest {

    private final File testDir = new File("test_dir");
    private final String node = "node";
    private final String node2 = "node2";
    private final String session = "123456";
    private final String session2 = "654321";
    private Map sessionInfo;
    private Map sessionInfo2;

    private final String expectedNode1 =
            "[\n" +
                    "  {\n" +
                    "    \"id\": \"123456\",\n" +
                    "    \"stuff\": \"stuff\"\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"id\": \"654321\",\n" +
                    "    \"stuff\": \"stuff\"\n" +
                    "  }\n" +
                    "]";

    private final String expectedNode2 =
            "[\n" +
                    "  {\n" +
                    "    \"id\": \"654321\",\n" +
                    "    \"stuff\": \"stuff\"\n" +
                    "  }\n" +
                    "]";

    @Before
    public void setUp() throws Exception {
        deleteTestFiles();
        SessionHistoryLog.setOutputDir(testDir);

        sessionInfo = new HashMap();
        sessionInfo.put("id", session);
        sessionInfo.put("stuff", "stuff");

        sessionInfo2 = new HashMap();
        sessionInfo2.put("id", session2);
        sessionInfo2.put("stuff", "stuff");
    }

    @After
    public void tearDown() throws Exception {
        deleteTestFiles();
    }

    @Test
    public void testSetOutputDir() throws Exception {
        assertEquals(testDir.getAbsolutePath(), SessionHistoryLog.getOutputDir().getAbsolutePath());
    }

    @Test
    public void testRegisterSession() throws Exception {
        deleteTestFiles();
        SessionHistoryLog.newSession(node, sessionInfo);
        SessionHistoryLog.newSession(node2, sessionInfo2);
        assertTrue(testDir.exists());
        assertEquals(2, testDir.listFiles().length);
    }


    @Test
    public void testGetAllCurrentHistoryEmpty() throws Exception {
        deleteTestFiles();
        assertEquals("{}", SessionHistoryLog.getTodaysHistoryAsString());
    }



    private void deleteTestFiles() throws IOException {
        if (testDir.exists()) {
            FileUtils.deleteDirectory(testDir);
            SessionHistoryLog.resetMemory();
        }
    }
}
