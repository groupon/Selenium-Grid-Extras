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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.groupon.seleniumgridextras.OSChecker;
import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.config.driver.DriverInfo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;


public class GridWrapperTest {

  public static Config.GridInfo gridConfig;
  public static DriverInfo wdConfig;
  public static String wdVersion;
  public static String wdHome;

  @Before
  public void setUp() throws Exception {
    RuntimeConfig.setConfigFile("grid_wrapper_test.json");
    RuntimeConfig.loadDefaults();
    RuntimeConfig.load();
    gridConfig = RuntimeConfig.getConfig().getGrid();
    wdConfig = RuntimeConfig.getConfig().getWebdriver();
    wdVersion = wdConfig.getVersion();
    wdHome = "/tmp/webdriver";

  }

  @After
  public void tearDown() throws Exception {
    File config = new File(RuntimeConfig.getConfigFile());
    config.delete();
  }

  private String getNodeStartCommand(Boolean windows) {

    String command = "java -cp ";
    String colon = ":";

    if (windows) {
      colon = ";";
    }

    command = command + RuntimeConfig.getSeleniungGridExtrasHomePath();

    String
        wdJarPath =
        colon + wdHome + "/" + wdVersion
        + ".jar  ";
    if (windows) {
      wdJarPath = OSChecker.toWindowsPath(wdJarPath);
    }

    command =
        command + wdJarPath;

    command = command + "org.openqa.grid.selenium.GridLauncher -role wd -port 4445 ";
    command = command + "-host " + RuntimeConfig.getCurrentHostIP() ;
    command =
        command + " -hub http://localhost:4444 -nodeTimeout 240 -maxSession 1" +
        " -proxy com.groupon.seleniumgridextras.grid.proxies.SetupTeardownProxy";

    return command;
  }

  @Test
  public void testGetCurrentJarPath() throws Exception {
    assertEquals(wdHome + "/" + wdVersion + ".jar", GridWrapper.getCurrentWebDriverJarPath());
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
    assertEquals("4445", GridWrapper.getGridConfigPortForRole("node"));
    assertEquals("4444", GridWrapper.getGridConfigPortForRole("hub"));
  }

  @Test
  public void testGetGridNodeConfig() throws Exception {
    JsonObject expectedConfig = new JsonObject();
    expectedConfig.addProperty("-port", "4445");
    expectedConfig.addProperty("-hub", "http://localhost:4444");
    expectedConfig.addProperty("-host", RuntimeConfig.getCurrentHostIP());
    expectedConfig.addProperty("-proxy", "com.groupon.seleniumgridextras.grid.proxies.SetupTeardownProxy");
    expectedConfig.addProperty("-role", "wd");
    expectedConfig.addProperty("-nodeTimeout", "240");
    expectedConfig.addProperty("-maxSession", 1);


    assertEquals(expectedConfig, new JsonParser().parse(new Gson().toJson(gridConfig.getNode())));
  }

  @Test
  public void testGetGridHubConfig() throws Exception {
    JsonObject expectedConfig = new JsonObject();
    expectedConfig.addProperty("-port", "4444");
    expectedConfig.addProperty("-host", RuntimeConfig.getCurrentHostIP());
    expectedConfig.addProperty("-role", "hub");
    expectedConfig
        .addProperty("-servlets", "com.groupon.seleniumgridextras.grid.servlets.SeleniumGridExtrasServlet");

    assertEquals(expectedConfig, new JsonParser().parse(new Gson().toJson(gridConfig.getHub())));
  }

  @Test
  public void testGetDefaultRole() throws Exception {
    assertEquals("hub", GridWrapper.getDefaultRole());
  }
}
