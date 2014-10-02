package com.groupon.seleniumgridextras.homepage;

import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.utilities.FileIOUtility;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class HtmlNodeRendererTest {

  public static final String HTML_RENDER_TEST_JSON = "html_render_test.json";
  public static final String FILE_THAT_DOES_NOT_EXIST = "foobar_does_not_exist.css";
  private final File configJson = new File(HTML_RENDER_TEST_JSON);
  private HtmlNodeRenderer renderer;

  @Before
  public void setUp() throws Exception {
    RuntimeConfig.setConfigFile(configJson.getName());
    Config config = new Config();

    config.writeToDisk(RuntimeConfig.getConfigFile());
    RuntimeConfig.load();
    renderer = new HtmlNodeRenderer();
  }

  @After
  public void tearDown() throws Exception {
    if (configJson.exists()) {
      configJson.delete();
      new File(configJson.getAbsoluteFile() + ".example").delete();
    }
  }

//  @Test
//  public void testGetSystemInfo() throws Exception {
//    assertEquals("<div class='col-lg-6'>\n"
//                 + "<h4>CPU</h4><p>10% Load</p>\n"
//                 + "<h4>RAM</h4><p>20% Load</p>\n"
//                 + "<h4>HD</h4><p>30% Full</p></div><!-- col-lg-6 -->",
//                 renderer.getSystemInfo("10", "20", "30"));
//  }
//
//  @Test
//  public void testGetFooter() throws Exception {
//    assertEquals(
//        FileIOUtility.getAsString(RuntimeConfig.getConfig().getHtmlRender().getHtmlFooter()),
//        renderer.getFooterHtml());
//  }
//
//
//  @Test
//  public void testGetTopBar() throws Exception {
//    assertEquals(
//        FileIOUtility.getAsString(RuntimeConfig.getConfig().getHtmlRender().getHtmlNavBar())
//            .replaceAll("BOX_INFO",
//                        RuntimeConfig.getOS().getHostName() + " (" + RuntimeConfig.getOS()
//                            .getHostIp() + ")"), renderer.getTopBar());
//  }
}
