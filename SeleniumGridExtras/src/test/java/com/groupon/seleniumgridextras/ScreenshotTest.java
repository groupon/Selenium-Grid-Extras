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

package com.groupon.seleniumgridextras;


import com.google.gson.JsonParser;

import com.groupon.seleniumgridextras.tasks.ExecuteOSTask;
import com.groupon.seleniumgridextras.tasks.Screenshot;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ScreenshotTest {

  private ExecuteOSTask task;


  @Before
  public void setUp() throws Exception {
    task = new Screenshot();
  }


  @Test
  public void testGetEndpoint() throws Exception {
    assertEquals("/screenshot", task.getEndpoint());
  }

  @Test
  public void testGetDescription() throws Exception {
    assertEquals("Take a full OS screen Screen Shot of the node", task.getDescription());
  }

  @Test
  public void testGetDependencies() throws Exception {
    List<String> actualDependencies = task.getDependencies();
    List<String> expectedDependencies = new LinkedList<String>();
    expectedDependencies.add("com.groupon.seleniumgridextras.tasks.ExposeDirectory");
    assertEquals(expectedDependencies, actualDependencies);
  }

  @Test
  public void testGetJsonResponse() throws Exception {
    assertEquals(
        new JsonParser().parse("{\"exit_code\":0,\"out\":[],\"error\":[],\"file_type\":[\"\"],\"file\":[\"\"],\"image\":[\"\"],\"hostname\":[\"\"],\"ip\":[\"\"],\"timestamp\":[\"\"]}"),
        task.getJsonResponse().getJson());
  }

  @Test
  public void testAPIDescription() throws Exception {
    assertEquals("Base64 URL Encoded (ISO-8859-1) string of the image",
        task.getJsonResponse().getKeyDescriptions().get("image").getAsString());
    assertEquals("Type of file returned (PNG/JPG/GIF)",
        task.getJsonResponse().getKeyDescriptions().get("file_type").getAsString());
    assertEquals("Name of the file saved on the NodeConfig's HD",
        task.getJsonResponse().getKeyDescriptions().get("file").getAsString());
    assertEquals("Human readable machine name",
                 task.getJsonResponse().getKeyDescriptions().get("hostname").getAsString());
    assertEquals("IP Address of current machine",
                 task.getJsonResponse().getKeyDescriptions().get("ip").getAsString());
    assertEquals("Timestamp of the screenshot",
                 task.getJsonResponse().getKeyDescriptions().get("timestamp").getAsString());
    assertEquals(9, task.getJsonResponse().getKeyDescriptions().entrySet().size());
  }

}
