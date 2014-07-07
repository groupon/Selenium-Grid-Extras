package com.groupon.seleniumgridextras.config;

import com.groupon.seleniumgridextras.utilities.FileIOUtility;

import org.apache.http.HttpResponse;
import org.apache.http.client.utils.URIBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ConfigPusherTest {

  private ConfigPusher config;
  File testFile = new File("test.txt");

  @Before
  public void setUp() throws Exception {
    config = new ConfigPusher();
    config.setHubHost("host");
    config.addConfigFile(testFile.getName());

  }

  @After
  public void tearDown() throws Exception {
    if (testFile.exists()) {
      testFile.delete();
    }
  }

  @Test
  public void testBasicParam() throws Exception {

    assertEquals("http://host:3000/update_node_config?node=" +
                 RuntimeConfig.getOS().getHostName()
                 + "&filename=file&content=content", config.buildUrl("file", "content").toString());
  }


  @Test
  public void testSendRequest() throws Exception{
    URI uri = new URIBuilder()
        .setScheme("http")
        .setHost("google.com")
        .setPort(80)
        .setPath("/")
        .build();

    HttpResponse actual = config.sendRequest(uri);

    assertEquals(200, actual.getStatusLine().getStatusCode());
  }

  @Test
  public void testBadUrlSend() throws Exception{
    URI uri = new URIBuilder()
        .setScheme("http")
        .setHost("localhost")
        .setPort(8888)
        .setPath("/")
        .build();

    HttpResponse actual = config.sendRequest(uri);

    assertEquals(404, actual.getStatusLine().getStatusCode());
  }

  @Test
  public void testConfigFileReader() throws Exception {
    final String content = "This is a test";
    Map<String, String> expected = new HashMap<String, String>();
    expected.put(testFile.getName(), "VGhpcyBpcyBhIHRlc3Q=");

    FileIOUtility.writeToFile(testFile, content);

    assertEquals(expected, config.getConfigFiles());


  }

}
