package com.groupon.seleniumgridextras.utilities;

import com.groupon.seleniumgridextras.config.RuntimeConfig;
import org.junit.Test;

import java.io.File;
import java.net.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HttpUtilityTest {

  @Test(expected = ConnectException.class)
  public void testConnectionRefusedError() throws Exception {
    ServerSocket serverSocket = new ServerSocket(0);
    int port = serverSocket.getLocalPort();
    serverSocket
        .close(); //Find a garanteed open port by taking one and closing. Why doesn't Java allow me to get a list of open ports?
    HttpUtility.getRequest(new URL("http://localhost:" + port)).getResponseCode();
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
  public void testGetAsString() throws Exception {
    assertEquals("", HttpUtility.getRequestAsString(new URL("http://xkcd.com/404")));
  }

//    @Test //This is commented out until we find a more consistent place to download videos from
//    public void testGetVideoFromUri() throws Exception {
//        RuntimeConfig.load();
//        File actual = HttpUtility.downloadVideoFromUri(new URI("http://192.168.168.144:3000/download_video/ad895b34-ee0a-4362-b542-a63d90ea221d.mp4"));
//        assertTrue(actual.exists());
//    }
}
