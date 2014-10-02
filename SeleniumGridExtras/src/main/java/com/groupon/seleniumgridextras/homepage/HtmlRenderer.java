package com.groupon.seleniumgridextras.homepage;

import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.utilities.FileIOUtility;
import com.groupon.seleniumgridextras.utilities.ResourceRetriever;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public class HtmlRenderer {

  private Map parameters;
  private static Logger logger = Logger.getLogger(HtmlRenderer.class);

  public HtmlRenderer(Map params) {
    this.parameters = params;
  }

  public static String getNavBar() {
    try {
      return new ResourceRetriever()
          .getAsString(RuntimeConfig.getConfig().getHtmlRender().getHtmlNavBar());
    } catch (IOException e) {
      logger.warn(e);
    } catch (NullPointerException e) {
      //do nothing
    }

    return "";
  }

  public static String getPageHead() {
    try {
      return new ResourceRetriever().getAsString(
          RuntimeConfig.getConfig().getHtmlRender().getHtmlHeadFile());
    } catch (IOException e) {
      logger.warn(e);
    } catch (NullPointerException e) {
      //do nothing
    }
    return "<html><head></head><body>";
  }

  public static String getPageFooter() {
    try {
      return new ResourceRetriever().getAsString(
          RuntimeConfig.getConfig().getHtmlRender().getHtmlFooter());
    } catch (IOException e) {
      logger.warn(e);

    } catch (NullPointerException e) {
      //do nothing
    }

    return "\n\t</body>\n</html>";
  }

  public static String openDiv(String divClass) {
    return "\n<div class='" + divClass + "'>\n";
  }

  public static String closeDiv(String comment) {
    return "\n</div> <!-- " + comment + " -->\n";
  }

  public String toString() {
    return new HtmlNodeRenderer().getFullHtml();
  }


  public static String getMainJs() {
    return getJsContent(RuntimeConfig.getConfig().getHtmlRender().getMainJs(),
                        RuntimeConfig.getConfig().getHtmlRender().getMainJsFallBack());
  }

  public static String getMainCss() {

    return getCssContent(RuntimeConfig.getConfig().getHtmlRender().getMainCss(),
                         RuntimeConfig.getConfig().getHtmlRender()
                             .getMainCssFallBack());
  }


  public static String getJquery() {
    return getJsContent(RuntimeConfig.getConfig().getHtmlRender().getJquery(),
                        RuntimeConfig.getConfig().getHtmlRender().getJqueryFallBack());
  }

  public static String getTemplateSource() {
    return getCssContent(RuntimeConfig.getConfig().getHtmlRender().getTemplateSource(),
                         RuntimeConfig.getConfig().getHtmlRender().getTemplateJsFallback());
  }


  protected static String getJsContent(String sourceFile, String fallback) {
    String returnString = null;

    try {

      returnString =
          "<script>" + new ResourceRetriever().getAsString(sourceFile) + "</script>";

    } catch (IOException e) {
      logger.warn(e);
    } catch (NullPointerException e) {
      //do nothing
    }

    if (returnString == null) {
      returnString = "<script src=\"" + fallback + "\"></script>";
    }

    return returnString;
  }

  protected static String getCssContent(String sourceFile, String fallback) {
    String returnString = null;

    try {
      returnString =
          "<style>" + new ResourceRetriever().getAsString(sourceFile) + "</style>";


    } catch (IOException e) {
      logger.warn(e);
    } catch (NullPointerException e) {
      //do nothing
    }

    if (returnString == null) {
      returnString =
          "<link rel=\"stylesheet\" href=\"" + fallback + "\">";
    }

    return returnString;
  }

}
