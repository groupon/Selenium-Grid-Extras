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

import com.groupon.seleniumgridextras.config.RuntimeConfig;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GetInfoForPortTest {

  public ExecuteOSTask task;

  @Before
  public void setUp() throws Exception {
    task = new GetInfoForPort();
  }


  @Test
  public void testGetEndpoint() throws Exception {
    assertEquals("/port_info", task.getEndpoint());
  }

  @Test
  public void testGetDescription() throws Exception {
    assertEquals("Returns parsed information on a PID occupying a given port",
        task.getDescription());
  }

  @Test
  public void testGetAcceptedParams() throws Exception {
    assertEquals("(Required) Port to be used", task.getAcceptedParams().get("port").getAsString());
    assertEquals(1, task.getAcceptedParams().entrySet().size());
  }

  @Test
  public void testGetResponseDescription() throws Exception {
    assertEquals("Process name/type (ie java, ruby, etc..)", task.getResponseDescription().get(
        "process_name").getAsString());
    assertEquals("Process ID", task.getResponseDescription().get("pid").getAsString());
    assertEquals("User who is running process", task.getResponseDescription().get("user").getAsString());
    assertEquals("Port searched for", task.getResponseDescription().get("port").getAsString());
    assertEquals(7, task.getResponseDescription().entrySet().size());
  }

  @Test
  public void testGetJsonResponse() throws Exception {
    if (RuntimeConfig.getOS().hasGUI()) {
      assertEquals(
          new JsonParser().parse("{\"port\":[\"\"],\"exit_code\":0,\"error\":[],\"process_name\":[\"\"],\"pid\":[\"\"],\"user\":[\"\"],\"out\":[]}"),
          task.getJsonResponse().getJson());
    }
  }
}
