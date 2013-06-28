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

package com.groupon.seleniumgridextras.grid;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.groupon.seleniumgridextras.OSChecker;
import com.groupon.seleniumgridextras.RuntimeConfig;
import com.groupon.seleniumgridextras.WriteDefaultConfigs;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class GridWrapperTest {

  public static Map gridConfig;
  public static Map wdConfig;
  public static String wdVersion;
  public static String wdHome;

  @Before
  public void setUp() throws Exception {
    RuntimeConfig.setConfig("grid_wrapper_test.json");
    WriteDefaultConfigs.writeConfig(RuntimeConfig.getConfigFile(), false);
    RuntimeConfig.loadConfig();
    gridConfig = RuntimeConfig.getGridConfig();
    wdConfig = RuntimeConfig.getWebdriverConfig();
    wdVersion = RuntimeConfig.getWebdriverConfig().get("version").toString();
    wdHome = "webdriver";

  }

  @After
  public void tearDown() throws Exception{
    File config = new File(RuntimeConfig.getConfigFile());
    config.delete();
  }

  private String getNodeStartCommand(Boolean windows) {

    String command = "java -cp ";
    String colon = ":";

    if (windows){
      colon = ";";
    }


    command = command + RuntimeConfig.getSeleniungGridExtrasHomePath();

    String
        stuff =
        colon + RuntimeConfig.getSeleniungGridExtrasHomePath() + wdHome + "/" + wdVersion + ".jar  ";
    if (windows) {
      stuff = OSChecker.toWindowsPath(stuff);
    }

    command =
        command + stuff;

    command = command + "org.openqa.grid.selenium.GridLauncher  -port 5555";
    command = command + " -hub http://localhost:4444 -host http://127.0.0.1 -role wd";

    return command;
  }

  @Test
  public void testGetCurrentJarPath() throws Exception {
    assertEquals(RuntimeConfig.getSeleniungGridExtrasHomePath() + wdHome + "/" + wdVersion + ".jar",
                 GridWrapper.getCurrentWebDriverJarPath());
  }

  @Test
  public void testGetWebdriverVersion() throws Exception {
    assertEquals(wdVersion, GridWrapper.getWebdriverVersion());
  }

  @Test
  public void testGetSeleniumGridExtrasPath() throws Exception {
    assertEquals(RuntimeConfig.getSeleniungGridExtrasHomePath(),
                 GridWrapper.getSeleniumGridExtrasPath());
  }

  @Test
  public void testGetWebdriverHome() throws Exception {
    assertEquals(wdHome, GridWrapper.getWebdriverHome());
  }

  @Test
  public void testGetStartCommand() throws Exception {
    assertEquals(getNodeStartCommand(false), GridWrapper.getStartCommand("node"));
  }

  @Test
  public void testGetWindowsStartCommand() throws Exception {
    assertEquals(getNodeStartCommand(true), GridWrapper.getWindowsStartCommand("node"));
  }

  @Test
  public void testGetGridConfigPortForRole() throws Exception {
    assertEquals("5555", GridWrapper.getGridConfigPortForRole("node"));
    assertEquals("4444", GridWrapper.getGridConfigPortForRole("hub"));
  }

  @Test
  public void testGetGridNodeConfig() throws Exception {
    Map<String, String> expectedConfig = new HashMap<String, String>();
    expectedConfig.put("-port", "5555");
    expectedConfig.put("-hub", "http://localhost:4444");
    expectedConfig.put("-host", "http://127.0.0.1");
    expectedConfig.put("-role", "wd");

    assertEquals(expectedConfig, GridWrapper.getGridConfig("node"));
  }

  @Test
  public void testGetGridHubConfig() throws Exception {
    Map<String, String> expectedConfig = new HashMap<String, String>();
    expectedConfig.put("-port", "4444");
    expectedConfig.put("-host", "http://127.0.0.1");
    expectedConfig.put("-role", "hub");
    expectedConfig
        .put("-servlets", "com.groupon.seleniumgridextras.grid.servlets.SeleniumGridExtrasServlet");

    assertEquals(expectedConfig, GridWrapper.getGridConfig("hub"));
  }

  @Test
  public void testGetDefaultRole() throws Exception {
    assertEquals("hub", GridWrapper.getDefaultRole());
  }
}
