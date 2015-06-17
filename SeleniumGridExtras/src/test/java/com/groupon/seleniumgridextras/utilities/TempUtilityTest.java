package com.groupon.seleniumgridextras.utilities;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class TempUtilityTest {
    @Test
    public void testGetWindowsTempForCurrentUser() throws Exception {
        File expected = new File(System.getProperty("user.home"), "AppData/Local/Temp");
        assertEquals(expected, TempUtility.getWindowsTempForCurrentUser());

    }

    @Test
    public void testGetLinuxTempDir() throws Exception{
        assertEquals(new File("/tmp"), TempUtility.getLinuxTemp());
    }
}
