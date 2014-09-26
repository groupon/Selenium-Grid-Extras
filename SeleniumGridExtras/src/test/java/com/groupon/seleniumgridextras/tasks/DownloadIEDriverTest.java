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

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class DownloadIEDriverTest {

  public ExecuteOSTask task;

  @Before
  public void setUp() throws Exception {
    RuntimeConfig.setConfigFile("download_ie_test.json");
    Config config = new Config(true);
    config.writeToDisk(RuntimeConfig.getConfigFile());

    RuntimeConfig.load();
    task = new DownloadIEDriver();
  }

  @After
  public void tearDown() throws Exception {
    File config = new File(RuntimeConfig.getConfigFile());
    config.delete();
    new File(RuntimeConfig.getConfigFile() + ".example").delete();
  }

  @Test
  public void testGetEndpoint() throws Exception {
    assertEquals("/download_iedriver", task.getEndpoint());
  }

  @Test
  public void testGetDescription() throws Exception {
    assertEquals("Downloads a version of IEDriver.exe to local machine", task.getDescription());
  }

  @Test
  public void testGetJsonResponse() throws Exception {
      final JsonElement rootDir = new JsonPrimitive(RuntimeConfig.getConfig().getIEdriver().getDirectory());
      final String expectedResponse = "{\"exit_code\":0,\"out\":[],\"error\":[],\"root_dir\":["
              + rootDir.toString() + "],\"file\":[\"\"],\"file_full_path\":[\""
              + "\"],\"source_url\":[\"\"]}";
    assertEquals(expectedResponse , task.getJsonResponse().toString());
  }

  @Test
  public void testGetAcceptedParams() throws Exception {
    assertEquals("Version of IEDriver to download, such as 2.33.0",
                 task.getAcceptedParams().get("version").getAsString());

    assertEquals("Bit version of IEDriver Win32/x64 - (default: Win32)",
                 task.getAcceptedParams().get("bit").getAsString());


    assertEquals(2, task.getAcceptedParams().entrySet().size());
  }

//  @Test
//  public void testExecute() throws Exception {
//
//  }
//
//  @Test
//  public void testExecute() throws Exception {
//
//  }
}
