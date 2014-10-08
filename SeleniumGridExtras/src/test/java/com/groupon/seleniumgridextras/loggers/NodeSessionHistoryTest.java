package com.groupon.seleniumgridextras.loggers;

import com.groupon.seleniumgridextras.utilities.FileIOUtility;
import com.groupon.seleniumgridextras.utilities.json.JsonParserWrapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class NodeSessionHistoryTest {

    private NodeSessionHistory history;
    private File outputFile = new File("session_output_log.json");
    private Map expectedSession;
    private final String expectedJson =
            "[\n" +
                    "  {\n" +
                    "    \"id\": \"123456\",\n" +
                    "    \"time\": \"time\"\n" +
                    "  }\n" +
                    "]";

    @Before
    public void setUp() throws Exception {
        deleteOutput();
        history = new NodeSessionHistory(outputFile);

        expectedSession = new HashMap();
        expectedSession.put("id", "123456");
        expectedSession.put("time", "time");

        history.addNewSession(expectedSession);
    }

    @After
    public void tearDown() throws Exception {
        deleteOutput();
    }

    @Test
    public void testAddNewSession() throws Exception {
        assertEquals(1, history.getSessions().size());
        assertEquals(expectedSession, history.getSessions().get(0));
    }

    @Test
    public void testGetSessionsAsJson() throws Exception {
        assertEquals(expectedJson, history.toJson());
    }


    @Test
    public void testBackupToFile() throws Exception {
        deleteOutput();
        history.backupToFile();

        assertTrue(outputFile.exists());

        List actual = JsonParserWrapper.toList(FileIOUtility.getAsString(outputFile));
        assertEquals(1, actual.size());
        assertEquals(expectedSession, actual.get(0));

    }

    @Test
    public void testTimeToRotateLog() throws Exception {
        NodeSessionHistory timedHistory = new NodeSessionHistory(outputFile);

        assertFalse(timedHistory.timeToRotateLog());
        timedHistory.setLogRotationDuration(1000);
        Thread.sleep(1500);
        assertTrue(timedHistory.timeToRotateLog());
    }

    private void deleteOutput() {
        if (outputFile.exists()) {
            outputFile.delete();
        }
    }
}
