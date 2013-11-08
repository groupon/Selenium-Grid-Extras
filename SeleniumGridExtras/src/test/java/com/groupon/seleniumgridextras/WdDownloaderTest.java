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
//
//package com.groupon.seleniumgridextras;
//
//import com.groupon.seleniumgridextras.config.Config;
//import com.groupon.seleniumgridextras.config.RuntimeConfig;
//import com.groupon.seleniumgridextras.downloader.Downloader;
//import com.groupon.seleniumgridextras.downloader.WdDownloader;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.File;
//
//import static org.junit.Assert.assertEquals;
//
//public class WdDownloaderTest {
//
//  public Downloader downloader;
//  public String version = "2.31.0";
//
//  @Before
//  public void setUp() throws Exception {
//    RuntimeConfig.setConfigFile("downloader_test.json");
//    Config config = new Config(true);
//    config.writeToDisk(RuntimeConfig.getConfigFile());
//
//    RuntimeConfig.load();
//    downloader = new WdDownloader(version);
//  }
//
//  @After
//  public void tearDown() throws Exception {
//    File config = new File(RuntimeConfig.getConfigFile());
//    config.delete();
//  }
//
//  @Test
//  public void testSetSourceURL() throws Exception {
//    assertEquals("http://selenium.googlecode.com/files/selenium-server-standalone-2.31.0.jar",
//                 downloader.getSourceURL());
//  }
//
//  @Test
//  public void testSetDestinationFile() throws Exception {
//    assertEquals(version + ".jar", downloader.getDestinationFile());
//  }
//
//  @Test
//  public void testSetDestinationDir() throws Exception {
//    assertEquals(GridWrapper.getWebdriverHome(), downloader.getDestinationDir());
//  }
//
//  @Test
//  public void testSetMalformedUrl() throws Exception{
//    Downloader temp = new WdDownloader(version);
//
//    temp.setSourceURL("httpSSSSS://google.com");
//    Boolean result = temp.download();
//
//    assertEquals(false, result);
//    assertEquals("java.net.MalformedURLException: unknown protocol: httpsssss", temp.getErrorMessage());
//  }
//
//  @Test
//  public void test404Url() throws Exception{
//    Downloader temp = new WdDownloader(version);
//
//    temp.setSourceURL("https://www.google.com/images/srpr/logo33333w.png");
//    Boolean result = temp.download();
//
//    assertEquals(false, result);
//    assertEquals("java.io.FileNotFoundException: https://www.google.com/images/srpr/logo33333w.png", temp.getErrorMessage());
//  }
//
//
//}
