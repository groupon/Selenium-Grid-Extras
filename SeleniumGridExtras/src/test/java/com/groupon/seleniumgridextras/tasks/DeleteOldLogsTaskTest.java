package com.groupon.seleniumgridextras.tasks;

import com.groupon.seleniumgridextras.utilities.FileIOUtility;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;


public class DeleteOldLogsTaskTest {
    private File testDir = new File("log_clean_test");
    private File log1 = new File(testDir, "1.log.foo");
    private File log2 = new File(testDir, "2.log.foo");
    private File log3 = new File(testDir, "grid_hub.log");

    DeleteOldLogsTask task = new DeleteOldLogsTask();

    @Before
    public void setUp() throws Exception {
        deleteDir();
        testDir.mkdir();
        createLogFiles();
    }

    @After
    public void tearDown() throws Exception {
        deleteDir();
    }

    @Test
    public void testDeleteOldLogs() throws Exception {
        task.deleteOldLogs(120, testDir);

        assertEquals(true, log1.exists());
        assertEquals(false, log2.exists());
        assertEquals(false, log3.exists());

    }


    private void deleteDir() throws IOException {
        if (testDir.exists()) {
            FileUtils.deleteDirectory(testDir);
        }
    }

    private void createLogFiles() throws IOException {
        String tempString = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.";

        FileIOUtility.writeToFile(log1, tempString);

        FileIOUtility.writeToFile(log2, tempString + tempString);

        FileIOUtility.writeToFile(log3, tempString + tempString + tempString);


    }
}
