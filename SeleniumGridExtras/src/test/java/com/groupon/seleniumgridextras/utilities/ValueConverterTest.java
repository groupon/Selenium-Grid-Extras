package com.groupon.seleniumgridextras.utilities;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ValueConverterTest {

    @Test
    public void testBytesToHumanReadable() throws Exception {
        assertEquals("20.0 MB", ValueConverter.bytesToHumanReadable(20000000, true));
        assertEquals("19.1 MiB", ValueConverter.bytesToHumanReadable(20000000, false));
    }

    @Test
    public void testDaysMilliseconds() throws Exception {
        assertEquals(1728000000, ValueConverter.daysToMilliseconds(20));
    }

    @Test
    public void testMillisecondsToDays() throws Exception {
        assertEquals(20, ValueConverter.millisecondsToDays(1728000000));
    }

    @Test
    public void testMillisecondsToHours() throws Exception {
        assertEquals(1, ValueConverter.millisecondsToHours(3600000));
        assertEquals(20, ValueConverter.millisecondsToHours(72000000));
    }

    @Test
    public void testMillisecondsToSeconds() throws Exception {
        assertEquals(20, ValueConverter.millisecondsToSeconds(20000));
    }

    @Test
    public void testMillisecondsToMinutes() throws Exception {
        assertEquals(20, ValueConverter.millisecondsToMinutes(1200000));
    }
}
