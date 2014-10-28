package com.groupon.seleniumgridextras.grid;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SessionTrackerTest {


    @Test
    public void testSessionsStarted() throws Exception {

        SessionTracker s = new SessionTracker();

        s.startSession("a");
        s.startSession("b");
        s.startSession("c");

        List<String> expected = new LinkedList<String>();
        expected.add("a");
        expected.add("b");
        expected.add("c");

        assertEquals(3, s.getSessions().size());
        assertEquals(expected, s.getSessions());


    }

}
