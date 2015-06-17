package com.groupon.seleniumgridextras.utilities.shutdownhooks;

import com.groupon.seleniumgridextras.utilities.FileIOUtility;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CleanTempShutdownHookTest {

    private File testTemp = new File("test_cleanup_shutdown_hook");
    private File testTempDir1 = new File(testTemp, "dir1");
    private File testTempDir2 = new File(testTemp, "dir2");
    private File testTempDir3 = new File(testTemp, "dir3");
    private File notEmpty = new File(testTemp, "not_empty");
    private File testFile = new File(notEmpty, "output.txt");

    @Before
    public void setUp() throws Exception {
        testTemp.mkdirs();
        testTempDir1.mkdir();
        testTempDir2.mkdir();
        testTempDir3.mkdir();

        FileIOUtility.writeToFile(testFile, "This is a test");

    }

    @After
    public void tearDown() throws Exception {
        if(testTemp.exists()){
            FileUtils.deleteDirectory(testTemp);
        }
    }

    @Test
    public void testAttachShutDownHook() throws Exception {
        new CleanTempShutdownHook(testTemp).cleanTempDriverDirs();

        assertFalse(testTempDir1.exists());
        assertFalse(testTempDir2.exists());
        assertFalse(testTempDir3.exists());
        assertTrue(notEmpty.exists());
        assertTrue(testFile.exists());
    }
}
