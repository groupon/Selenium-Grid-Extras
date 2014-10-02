package com.groupon.seleniumgridextras.homepage;

import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.utilities.ResourceRetriever;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class HtmlRendererTest {

  public static final String HTML_RENDER_TEST_JSON = "html_render_test.json";
  public static final String FILE_THAT_DOES_NOT_EXIST = "foobar_does_not_exist.css";
  private final File configJson = new File(HTML_RENDER_TEST_JSON);

  @Before
  public void setUp() throws Exception {
    RuntimeConfig.setConfigFile(configJson.getName());
    Config config = new Config();

    config.writeToDisk(RuntimeConfig.getConfigFile());
    RuntimeConfig.load();
  }

  @After
  public void tearDown() throws Exception {
    if (configJson.exists()) {
      configJson.delete();
      new File(configJson.getAbsoluteFile() + ".example").delete();
    }
  }

  @Test
  public void testGetHeadHtml() throws Exception {
    assertEquals(
        new ResourceRetriever().getAsString(
            RuntimeConfig.getConfig().getHtmlRender().getHtmlHeadFile()),
        HtmlRenderer.getPageHead());

    RuntimeConfig.getConfig().getHtmlRender().setHtmlHeadFile(FILE_THAT_DOES_NOT_EXIST);

    assertEquals("<html><head></head><body>", HtmlRenderer.getPageHead());

  }


  @Test
  public void testGeTemplateJs() throws Exception {
    assertEquals("<style>" + new ResourceRetriever().getAsString(
        RuntimeConfig.getConfig().getHtmlRender().getTemplateSource()) + "</style>",
                 HtmlRenderer.getTemplateSource());

    RuntimeConfig.getConfig().getHtmlRender().setTemplateJs(FILE_THAT_DOES_NOT_EXIST);

    assertEquals(
        "<link rel=\"stylesheet\" href=\"http://getbootstrap.com/examples/jumbotron-narrow/jumbotron-narrow.css\">",
        HtmlRenderer.getTemplateSource());
  }

  @Test
  public void testGeJquery() throws Exception {
    assertEquals("<script>" + new ResourceRetriever()
        .getAsString(RuntimeConfig.getConfig().getHtmlRender().getJquery()) + "</script>",
                 HtmlRenderer.getJquery());

    RuntimeConfig.getConfig().getHtmlRender().setJquery(FILE_THAT_DOES_NOT_EXIST);

    assertEquals(
        "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js\"></script>",
        HtmlRenderer.getJquery());
  }

  @Test
  public void testGetMainCss() throws Exception {
    assertEquals("<style>" + new ResourceRetriever().getAsString(
        RuntimeConfig.getConfig().getHtmlRender().getMainCss()) + "</style>",
                 HtmlRenderer.getMainCss());

    RuntimeConfig.getConfig().getHtmlRender().setMainCss(FILE_THAT_DOES_NOT_EXIST);

    assertEquals(
        "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css\">",
        HtmlRenderer.getMainCss());

  }

  @Test
  public void testGetMainJs() throws Exception {
    assertEquals("<script>" + new ResourceRetriever().getAsString(
        RuntimeConfig.getConfig().getHtmlRender().getMainJs()) + "</script>",
                 HtmlRenderer.getMainJs());

    RuntimeConfig.getConfig().getHtmlRender().setMainJs(FILE_THAT_DOES_NOT_EXIST);
    assertEquals(
        "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js\"></script>",
        HtmlRenderer.getMainJs());
  }
}
