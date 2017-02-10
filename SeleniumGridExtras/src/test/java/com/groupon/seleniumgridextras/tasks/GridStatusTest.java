/**
 * Copyright (c) 2013, Groupon, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * Neither the name of GROUPON nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * Created with IntelliJ IDEA.
 * User: Dima Kovalenko (@dimacus) && Darko Marinov
 * Date: 5/10/13
 * Time: 4:06 PM
 */
package com.groupon.seleniumgridextras.tasks;


import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import com.groupon.seleniumgridextras.utilities.json.JsonParserWrapper;
import com.groupon.seleniumgridextras.utilities.json.JsonResponseBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class GridStatusTest {

    protected ExecuteOSTask task;
    protected Map expectedJsonResponse;

    @Before
    public void setUp() throws Exception {
        task = new GridStatus();

        expectedJsonResponse = new HashMap();
        expectedJsonResponse.put("exit_code", 0.0);
        expectedJsonResponse.put("out", new ArrayList());
        expectedJsonResponse.put("error", new ArrayList());
        expectedJsonResponse.put("node_sessions_limit", new ArrayList());
        expectedJsonResponse.put("node_will_unregister_during_reboot", new ArrayList());
        expectedJsonResponse.put("node_running", new ArrayList());
        expectedJsonResponse.put("node_info", new ArrayList());
        expectedJsonResponse.put("hub_running", new ArrayList());
        expectedJsonResponse.put("hub_info", new ArrayList());
        expectedJsonResponse.put("sessions", new ArrayList());

    }

    @Test
    public void testGetEndpoint() throws Exception {
        assertEquals("/grid_status", task.getEndpoint());
    }

    @Test
    public void testGetDescription() throws Exception {
        assertEquals("Returns status of the Selenium Grid hub/node. If currently running and what is the PID",
                task.getDescription());
    }

    @Test
    public void testGetAcceptedParams() throws Exception {
        assertEquals(1, task.getAcceptedParams().entrySet().size());
    }

    @Test
    public void testGetResponseDescription() throws Exception {
        Map expected = new HashMap();
        expected.put(JsonCodec.WebDriver.Grid.HUB_RUNNING, GridStatus.BOOLEAN_IF_HUB_IS_RUNNING_ON_GIVEN_PORT);
        expected.put(JsonCodec.WebDriver.Grid.NODE_RUNNING, GridStatus.BOOLEAN_IF_NODE_IS_RUNNING_ON_GIVEN_PORT);
        expected.put(JsonCodec.WebDriver.Grid.HUB_INFO, GridStatus.HASH_OBJECT_DESCRIBING_THE_HUB_PROCESS);
        expected.put(JsonCodec.WebDriver.Grid.NODE_INFO, GridStatus.HASH_OBJECT_DESCRIBING_THE_NODE_CONFIG_PROCESS);
        expected.put(JsonCodec.WebDriver.Grid.RECORDED_SESSIONS, GridStatus.LIST_OF_RECORDED_SESSIONS);
        expected.put(JsonCodec.WebDriver.Grid.NODE_SESSIONS_LIMIT, GridStatus.INTEGER_UPPER_LIMIT_BEFORE_THE_BOX_REBOOTS);
        expected.put(JsonCodec.WebDriver.Grid.NODE_WILL_UNREGISTER_DURING_REBOOT, GridStatus.BOOLEAN_IF_NODE_WILL_UNREGISTER_DURING_REBOOT);
        expected.put(JsonCodec.ERROR, JsonResponseBuilder.ERROR_RECEIVED_DURING_EXECUTION_OF_COMMAND);
        expected.put(JsonCodec.OUT, JsonResponseBuilder.ALL_OF_THE_STANDARD_OUT_RECEIVED_FROM_THE_SYSTEM );
        expected.put(JsonCodec.EXIT_CODE, JsonResponseBuilder.EXIT_CODE_FOR_OPERATION );

        assertEquals(expected, JsonParserWrapper.toHashMap(task.getResponseDescription()));
    }

    @Test
    public void testGetEmptyJsonResponse() throws Exception {
        Map actual = JsonParserWrapper.toHashMap(task.getJsonResponse().getJson());
        assertEquals(expectedJsonResponse.keySet(), actual.keySet());
        for (Object key : expectedJsonResponse.keySet()) {

            assertEquals(formatKey(key.toString(), expectedJsonResponse.get(key)),
                         formatKey(key.toString(), actual.get(key)));
        }
    }

    @Test
    public void testAddingNewSessionWithBadParam() throws Exception{
        GridStatus status = new GridStatus();

        Map<String, String> shouldNotRecord = new HashMap<String, String>();
        shouldNotRecord.put("not-session", "123445");

        Map actualResultsNotRecordedSession = JsonParserWrapper.toHashMap(status.execute(shouldNotRecord));

        assertEquals(formatKey("sessions", new ArrayList()), formatKey("sessions",
                actualResultsNotRecordedSession.get("sessions")));


        Map<String, String> shouldRecord = new HashMap<String, String>();
        shouldRecord.put("session", "123456");

        ArrayList expectedResults = new ArrayList();
        expectedResults.add("123456");

        Map actualResultsWithRecordedSession = JsonParserWrapper.toHashMap(status.execute(shouldRecord));
        assertEquals(formatKey("sessions", expectedResults), formatKey("sessions",
                actualResultsWithRecordedSession.get("sessions")));
    }


    private String formatKey(String key, Object value) {
        return String.format("Key: %s, Class: %s, Value: %s", key, value.getClass().getCanonicalName(), value.toString());
    }


}
