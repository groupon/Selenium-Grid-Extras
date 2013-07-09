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

import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class GridStatusTest {

  public ExecuteOSTask task;

  @Before
  public void setUp() throws Exception {
    task = new GridStatus();
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
    assertEquals(0, task.getAcceptedParams().entrySet().size());
  }

  @Test
  public void testGetResponseDescription() throws Exception {
    assertEquals("Boolean if hub is running on given port", task.getResponseDescription().get(
        "hub_running").getAsString());
    assertEquals("Boolean if node is running on given port", task.getResponseDescription().get("node_running").getAsString());
    assertEquals("Hash object describing the Hub Process", task.getResponseDescription().get("hub_info").getAsString());
    assertEquals("Hash object describing the Node Process", task.getResponseDescription().get("node_info").getAsString());
    assertEquals(7, task.getResponseDescription().entrySet().size());
  }

  @Test
  public void testGetJsonResponse() throws Exception {
    if (!java.awt.GraphicsEnvironment.isHeadless()) {
      assertEquals(
          new JsonParser().parse("{\"node_running\":[\"\"],\"exit_code\":0,\"node_info\":[\"\"],\"hub_running\":[\"\"],\"error\":[],\"hub_info\":[\"\"],\"out\":[]}"),
          task.getJsonResponse().getJson());
    }
  }


}
