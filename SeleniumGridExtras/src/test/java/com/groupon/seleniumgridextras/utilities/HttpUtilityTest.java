package com.groupon.seleniumgridextras.utilities;

import org.junit.Test;

import java.net.ConnectException;
import java.net.URL;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA. User: dima Date: 7/8/14 Time: 4:09 PM To change this template use
 * File | Settings | File Templates.
 */
public class HttpUtilityTest {

  @Test(expected=ConnectException.class)
  public void testConnectionRefusedError() throws Exception {
    HttpUtility.getRequest(new URL("http://localhost:9999")).getResponseCode();
  }

  @Test
  public void test404Page() throws Exception {
    assertEquals(404, HttpUtility.getRequest(new URL("http://xkcd.com/404")).getResponseCode());
  }

  @Test
  public void test200Page() throws Exception {
    assertEquals(200, HttpUtility.getRequest(new URL("http://google.com")).getResponseCode());
  }

  @Test(expected = UnknownHostException.class)
  public void testUnknownHost() throws Exception {
    HttpUtility.getRequest(new URL("http://googasdfasfdkjashfdkjahsfdle.com/")).getResponseCode();
  }

  @Test
  public void testGetAsString() throws Exception{
    assertEquals("", HttpUtility.getRequestAsString(new URL("http://xkcd.com/404")));
  }
}
