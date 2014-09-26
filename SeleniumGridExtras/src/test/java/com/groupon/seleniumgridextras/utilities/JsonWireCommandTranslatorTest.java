package com.groupon.seleniumgridextras.utilities;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JsonWireCommandTranslatorTest {

  private static final String POST = "POST";


  @Test
  public void testNewSessionCommand() throws Exception {
    String url = "/session";
    String
        body =
        "{\"desiredCapabilities\":{\"platform\":\"ANY\",\"javascriptEnabled\":true,\"cssSelectorsEnabled\":true,\"browserName\":\"firefox\",\"nativeEvents\":false,\"rotatable\":false,\"takesScreenshot\":true,\"version\":\"\"}}";

    assertEquals("newSession: " + body, new JsonWireCommandTranslator(POST, url, body).toString());

  }

  @Test
  public void testFindElement() throws Exception {
    String url = "/wd/hub/session/a4e9aff3-2d78-4394-b9ff-0883232244d7/element";
    String body = "{\"using\":\"id\",\"value\":\"gbqfq\"}";

    assertEquals("findElement: " + body, new JsonWireCommandTranslator(POST, url, body).toString());
  }

  @Test
  public void testGetCommand() throws Exception {
    String url = "/wd/hub/session/a4e9aff3-2d78-4394-b9ff-0883232244d7/url";
    String body = "{\"url\":\"http://google.com\"}";

    assertEquals("get: " + body, new JsonWireCommandTranslator(POST, url, body).toString());
  }

  @Test
  public void testUnknownCommand() throws Exception {
    String url = "/session/a4e9aff3-2d78-4394-b9ff-0883232244d7/lalalalalalalala";
    String body = "{\"url\":\"http://google.com\"}";

    assertEquals(url + ": " + body, new JsonWireCommandTranslator(POST, url, body).toString());
  }


}
