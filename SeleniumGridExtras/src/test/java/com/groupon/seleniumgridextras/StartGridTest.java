///**
// * Copyright (c) 2013, Groupon, Inc.
// * All rights reserved.
// *
// * Redistribution and use in source and binary forms, with or without
// * modification, are permitted provided that the following conditions
// * are met:
// *
// * Redistributions of source code must retain the above copyright notice,
// * this list of conditions and the following disclaimer.
// *
// * Redistributions in binary form must reproduce the above copyright
// * notice, this list of conditions and the following disclaimer in the
// * documentation and/or other materials provided with the distribution.
// *
// * Neither the name of GROUPON nor the names of its contributors may be
// * used to endorse or promote products derived from this software without
// * specific prior written permission.
// *
// * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
// * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
// * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
// * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
// * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
// * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
// * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
// * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// * Created with IntelliJ IDEA.
// * User: Dima Kovalenko (@dimacus) && Darko Marinov
// * Date: 5/10/13
// * Time: 4:06 PM
// */
//
//package com.groupon.seleniumgridextras;
//
//import com.groupon.seleniumgridextras.config.RuntimeConfig;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//
//import static org.junit.Assert.assertEquals;
//
//public class StartGridTest {
//
//  public static final
//  String
//      hubBatch =
//      RuntimeConfig.getSeleniungGridExtrasHomePath() + "start_hub.bat";
//  public static final
//  String
//      nodeBatch =
//      RuntimeConfig.getSeleniungGridExtrasHomePath() + "start_node.bat";
//  public com.groupon.seleniumgridextras.tasks.ExecuteOSTask task;
//
//  @Before
//  public void setUp() throws Exception {
//    RuntimeConfig.setConfigFile("starg_grid_test.json");
//    RuntimeConfig.loadDefaults();
//    task = new com.groupon.seleniumgridextras.tasks.StartGrid();
//
//  }
//
//  @After
//  public void teardown() throws Exception {
//    File file = new File(hubBatch);
//    if (file.exists()) {
//      file.delete();
//    }
//
//    File file2 = new File(nodeBatch);
//    if (file2.exists()) {
//      file.delete();
//    }
//
//    File config = new File(RuntimeConfig.getConfigFile());
//    config.delete();
//  }
//
//  public void testGetEndpoint() throws Exception {
//    assertEquals("/start_grid", task.getEndpoint());
//  }
//
//  @Test
//  public void testGetDescription() throws Exception {
//    assertEquals("Starts an instance of Selenium Grid Hub or NodeConfig",
//        task.getDescription());
//  }
//
//  @Test
//  public void testGetAcceptedParams() throws Exception {
//    assertEquals("hub|node - defaults to 'default_role' param in config file",
//        task.getAcceptedParams().get("role").getAsString());
//    assertEquals(1, task.getAcceptedParams().entrySet().size());
//  }
//
//  @Test
//  public void testGetLinuxHubCommand() throws Exception {
//    String expectedCommand = "java -cp replaced/for/now/:/tmp/webdriver/2.33.0.jar  " +
//                             "org.openqa.grid.selenium.GridLauncher -role hub -port 4444 " +
//                             "-host " + RuntimeConfig.getCurrentHostIP() + " -servlets " +
//                             "com.groupon.seleniumgridextras.grid.servlets.SeleniumGridExtrasServlet,com.groupon.seleniumgridextras.grid.servlets.ProxyStatusJsonServlet &";
//
//    String modifiedActual = task.getLinuxCommand("hub");
//
//    modifiedActual =
//        modifiedActual.replace(RuntimeConfig.getSeleniungGridExtrasHomePath(), "replaced/for/now/");
//
//    assertEquals(expectedCommand, modifiedActual);
//  }
//
//  @Test
//  public void testGetLinuxNodeCommand() throws Exception {
//
//    String expectedCommand = "java -cp replaced/for/now/:/tmp/webdriver/2.33.0.jar  " +
//                             "org.openqa.grid.selenium.GridLauncher -role wd -port 4445 -host " +
//                              RuntimeConfig.getCurrentHostIP() +
//                              " -hub http://localhost:4444 -nodeTimeout 240 -maxSession 1" +
//                             " -proxy com.groupon.seleniumgridextras.grid.proxies.SetupTeardownProxy &";
//
//    String modifiedActual = task.getLinuxCommand("node");
//
//    modifiedActual =
//        modifiedActual.replace(RuntimeConfig.getSeleniungGridExtrasHomePath(), "replaced/for/now/");
//
//    assertEquals(expectedCommand, modifiedActual);
//  }
//
//  @Test
//  public void testGetWindowsHubCommand() throws Exception {
//    String
//        expectedCommand =
//        "java -cp replaced-for-now-;\\tmp\\webdriver\\2.33.0.jar  " +
//        "org.openqa.grid.selenium.GridLauncher -role hub -port 4444 " +
//        "-host " + RuntimeConfig.getCurrentHostIP() + " -servlets " +
//        "com.groupon.seleniumgridextras.grid.servlets.SeleniumGridExtrasServlet,com.groupon.seleniumgridextras.grid.servlets.ProxyStatusJsonServlet";
//
//    assertEquals("powershell.exe /c \"Start-Process " + hubBatch + "\"",
//        task.getWindowsCommand("hub"));
//
//    File batch = new File(hubBatch);
//    assertEquals(true, batch.exists());
//
//    String modifiedActual = readBatchFile(batch.getPath());
//
//    modifiedActual =
//        modifiedActual
//            .replaceAll(RuntimeConfig.getSeleniungGridExtrasHomePath(), "replaced-for-now-");
//
//    modifiedActual =
//        modifiedActual
//            .replace(OS.toWindowsPath(RuntimeConfig.getSeleniungGridExtrasHomePath()),
//                "replaced-for-now-");
//
//    assertEquals(expectedCommand, modifiedActual);
//  }
//
//  @Test
//  public void testGetWindowsNodeCommand() throws Exception {
//
//    RuntimeConfig.getConfig().getNode().setIeDriver("\\webdriver\\iedriver\\foo.exe");
//
//    String
//        expectedCommand =
//        "java -cp replaced-for-now-;\\tmp\\webdriver\\2.33.0.jar  " +
//        "org.openqa.grid.selenium.GridLauncher -role wd -port 4445 " +
//        "-host " + RuntimeConfig.getCurrentHostIP()
//        + " -hub http://localhost:4444 -nodeTimeout 240 -maxSession 1 " +
//        "-proxy com.groupon.seleniumgridextras.grid.proxies.SetupTeardownProxy " +
//        "-Dwebdriver.ie.driver=\\webdriver\\iedriver\\foo.exe";
//
//    assertEquals("powershell.exe /c \"Start-Process " + nodeBatch + "\"",
//        task.getWindowsCommand("node"));
//
//    File batch = new File(nodeBatch);
//    assertEquals(true, batch.exists());
//
//    String modifiedActual = readBatchFile(batch.getPath());
//    modifiedActual =
//        modifiedActual.replace(RuntimeConfig.getSeleniungGridExtrasHomePath(), "replaced-for-now-");
//
//    modifiedActual =
//        modifiedActual
//            .replace(OS.toWindowsPath(RuntimeConfig.getSeleniungGridExtrasHomePath()),
//                "replaced-for-now-");
//
//    assertEquals(expectedCommand, modifiedActual);
//
//  }
//
//  private String readBatchFile(String filePath) {
//    String returnString = "";
//    try {
//      BufferedReader reader = new BufferedReader(new FileReader(filePath));
//      String line = null;
//      while ((line = reader.readLine()) != null) {
//        returnString = returnString + line;
//      }
//    } catch (FileNotFoundException error) {
//      System.out.println("File " + filePath + " does not exist, going to use default configs");
//    } catch (IOException error) {
//      System.out.println("Error reading" + filePath + ". Going with default configs");
//    }
//
//    return returnString;
//  }
//
//}
