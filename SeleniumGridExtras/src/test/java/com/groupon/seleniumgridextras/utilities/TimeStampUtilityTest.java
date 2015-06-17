package com.groupon.seleniumgridextras.utilities;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TimeStampUtilityTest {

    @Test
    public void testOsFriendlyTimestamp() throws Exception {
        assertEquals(new SimpleDateFormat("dd_MM_yyyy").format(
                new java.sql.Timestamp(Calendar.getInstance().getTime().getTime())),
                TimeStampUtility.osFriendlyTimestamp());
    }

    @Test
    public void testTimestampInMs() throws Exception {
        long actual = TimeStampUtility.timestampInMs();

        Thread.sleep(1000);

        long expected = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()).getTime();

        assertTrue(expected > actual);
    }
}
