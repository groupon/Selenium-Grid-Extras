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

import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.tasks.ExecuteOSTask;
import com.groupon.seleniumgridextras.tasks.MoveMouse;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MoveMouseTest {

  public ExecuteOSTask task;

  @Before
  public void setUp() throws Exception {
    task = new MoveMouse();
  }

  @Test
  public void testGetEndpoint() throws Exception {
    assertEquals("/move_mouse", task.getEndpoint());
  }

  @Test
  public void testGetDescription() throws Exception {
    assertEquals("Moves the computers mouse to x and y location. (Default 0,0)",
        task.getDescription());
  }

  @Test
  public void testExecuteWithParam() throws Exception {

    Map foo = new HashMap();
    foo.put("x", "20");
    foo.put("y", "20");

    if (RuntimeConfig.getOS().hasGUI()) {
      assertEquals(new JsonParser().parse("{\"exit_code\":0,\"error\":[],\"y\":20,\"x\":20,\"out\":[]}"),
              task.execute(foo));
    }
  }

  @Test
  public void testExecuteNoParam() throws Exception {
    if (RuntimeConfig.getOS().hasGUI()) {
      assertEquals(new JsonParser().parse("{\"exit_code\":0,\"error\":[],\"y\":0,\"x\":0,\"out\":[]}"), task.execute());
    }
  }

  @Test
  public void testGetJsonResponse() throws Exception {
    if (RuntimeConfig.getOS().hasGUI()) {
      assertEquals(new JsonParser().parse("{\"exit_code\":0,\"error\":[],\"y\":[\"\"],\"x\":[\"\"],\"out\":[]}"),
          task.getJsonResponse().getJson());

      assertEquals("Current Y postion of the mouse",

          task.getJsonResponse().getKeyDescriptions().get("y").getAsString());
      assertEquals("Current X postion of the mouse",
          task.getJsonResponse().getKeyDescriptions().get("x").getAsString());

    }
  }

  @Test
  public void testGetAcceptedParams() throws Exception {
    assertEquals("X - Coordinate", task.getAcceptedParams().get("x").getAsString());
    assertEquals("Y - Coordinate", task.getAcceptedParams().get("y").getAsString());
    assertEquals(2, task.getAcceptedParams().entrySet().size());
  }
}
