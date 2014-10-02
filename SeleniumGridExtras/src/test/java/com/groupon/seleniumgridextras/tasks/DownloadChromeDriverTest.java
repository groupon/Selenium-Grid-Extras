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

import com.google.gson.Gson;

import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.DefaultConfig;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.utilities.json.JsonParserWrapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DownloadChromeDriverTest {

  public DownloadChromeDriver task;
  private String downloadDir = "/tmp/chrome_downloader_test";

  @Before
  public void setUp() throws Exception {
    deleteDownloadDir();
    RuntimeConfig.setConfigFile("download_chrome_test.json");
    Config config = DefaultConfig.getDefaultConfig();

    config.getChromeDriver().setDirectory(downloadDir);

    config.writeToDisk(RuntimeConfig.getConfigFile());

    RuntimeConfig.load();
    task = new DownloadChromeDriver();
  }

  @After
  public void tearDown() throws Exception {
    File config = new File(RuntimeConfig.getConfigFile());
    config.delete();
    new File(RuntimeConfig.getConfigFile() + ".example").delete();
    deleteDownloadDir();
  }

  @Test
  public void testGetEndpoint() throws Exception {
    assertEquals("/download_chromedriver", task.getEndpoint());
  }

  @Test
  public void testGetDescription() throws Exception {
    assertEquals("Downloads a version of ChromeDriver to local machine", task.getDescription());
  }

  @Test
  public void testGetJsonResponse() throws Exception {
    assertEquals(
        "{\"exit_code\":0,\"out\":[],\"error\":[],\"root_dir\":[\"" + downloadDir
        + "\"],\"file\":[\"\"],\"file_full_path\":[\""
        + "\"],\"source_url\":[\"\"]}",
        task.getJsonResponse().toString());
  }

  @Test
  public void testGetAcceptedParams() throws Exception {
    assertEquals("Version of ChromeDriver to download, such as 2.6",
                 task.getAcceptedParams().get("version").getAsString());

    assertEquals("Bit Version of ChromeDriver 32/64 - (default: 32)",
                 task.getAcceptedParams().get("bit").getAsString());

    assertEquals(2, task.getAcceptedParams().entrySet().size());
  }

  @Test
  public void testExecute() throws Exception {

    String os = getOS();
    // default setting from configuration
    String bit = "32";
    String version = "2.6";

    Map firstExec = JsonParserWrapper.toHashMap(task.execute(version));

    File expectedFile = new File(RuntimeConfig.getConfig().getChromeDriver().getExecutablePath());

    assertEquals(0.0, firstExec.get("exit_code"));
    assertEquals(expectedFile.getName(), ((ArrayList) firstExec.get("file")).get(0));
    assertEquals(0, ((ArrayList) firstExec.get("error")).size());
    assertEquals(0, ((ArrayList) firstExec.get("out")).size());
    assertEquals(downloadDir, ((ArrayList) firstExec.get("root_dir")).get(0));
    assertEquals(expectedFile.getAbsolutePath(),
                 ((ArrayList) firstExec.get("file_full_path")).get(0));
    assertEquals("http://chromedriver.storage.googleapis.com/" + version + "/chromedriver_" + os + bit + ".zip",
                 ((ArrayList) firstExec.get("source_url")).get(0));

    Map secondExec = JsonParserWrapper.toHashMap(task.execute(version));

    assertEquals(0.0, secondExec.get("exit_code"));
    assertEquals(0, ((ArrayList) secondExec.get("error")).size());
    assertEquals("File already downloaded, will not download again",
                 ((ArrayList) secondExec.get("out")).get(0));

    assertFalse(secondExec.containsKey("root_dir"));
    assertFalse(secondExec.containsKey("source_url"));

    assertEquals(expectedFile.getAbsolutePath(),
                 ((ArrayList) firstExec.get("file_full_path")).get(0));
  }

  @Test
  public void testCustomExecute() throws Exception {
    String os = getOS();
    String bit = "64";
    String version = "2.9";
    RuntimeConfig.getConfig().getChromeDriver().setBit(bit);

    DownloadChromeDriver customSettingTask = new DownloadChromeDriver();

    Map firstExec = JsonParserWrapper.toHashMap(customSettingTask.execute(version));
    assertEquals("http://chromedriver.storage.googleapis.com/" + version + "/chromedriver_" + os + bit + ".zip",
                 ((ArrayList) firstExec.get("source_url")).get(0));

  }

  private String getOS() {
    if (RuntimeConfig.getOS().isWindows()) {
      return "win";
    } else if (RuntimeConfig.getOS().isMac()) {
      return "mac";
    } else {
      return "linux";
    }
  }

  private void deleteDownloadDir() {

    File dir = new File(downloadDir);

    if (dir.exists()) {
      if (dir.list().length != 0) {
        for (String file : dir.list()) {
          new File(dir, file).delete();
        }
      }

      dir.delete();

    }

  }

}
