package com.groupon.seleniumgridextras.homepage;

import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.utilities.FileIOUtility;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

public class HtmlRenderer {

  private Map parameters;

  public HtmlRenderer(Map params) {
    this.parameters = params;
  }

  public static String getNavBar() {
    try {
      return FileIOUtility.getAsString(RuntimeConfig.getConfig().getHtmlRender().getHtmlNavBar());
    } catch (FileNotFoundException e) {
      return "";
    }
  }

  public static String getPageHead() {
    try {
      return FileIOUtility.getAsString(RuntimeConfig.getConfig().getHtmlRender().getHtmlHeadFile());
    } catch (FileNotFoundException e) {
      return "<html><head></head><body>";
    }
  }

  public static String getPageFooter() {
    try {
      return FileIOUtility.getAsString(RuntimeConfig.getConfig().getHtmlRender().getHtmlFooter());
    } catch (FileNotFoundException e) {
      return "\n\t</body>\n</html>";
    }
  }

  public static String openDiv(String divClass){
    return "\n<div class='" + divClass + "'>\n";
  }

  public static String closeDiv(String comment) {
    return "\n</div> <!-- " + comment + " -->\n";
  }

  public String toString() {
    return new HtmlNodeRenderer().getFullHtml();
  }


  public static String getMainJs() {
    //TODO: Ignoring local JS for now, the find replace on string is not working, needs fixing
    return getJsContent(new File(""),
                        RuntimeConfig.getConfig().getHtmlRender().getMainJsFallBack());
  }

  public static String getMainCss() {

    return getCssContent(RuntimeConfig.getConfig().getHtmlRender().getMainCss(),
                         RuntimeConfig.getConfig().getHtmlRender()
                             .getMainCssFallBack());
  }


  public static String getJquery() {
    //TODO: Ignoring local JS for now, the find replace on string is not working, needs fixing
    return getJsContent(new File(""),
                        RuntimeConfig.getConfig().getHtmlRender().getJqueryFallBack());
  }

  public static String getTemplateSource() {
    return getCssContent(RuntimeConfig.getConfig().getHtmlRender().getTemplateSource(),
                         RuntimeConfig.getConfig().getHtmlRender().getTemplateJsFallback());
  }


  protected static String getJsContent(File sourceFile, String fallback) {
    String returnString = null;

    try {
      if (sourceFile.exists()) {
        returnString =
            "<script>" + FileIOUtility.getAsString(sourceFile) + "</script>";
      }

    } catch (Exception e) {
      //Do nothing
    }

    if (returnString == null) {
      returnString = "<script src=\"" + fallback + "\"></script>";
    }

    return returnString;
  }

  protected static String getCssContent(File sourceFile, String fallback) {
    String returnString = null;

    try {
      if (sourceFile.exists()) {
        returnString =
            "<style>" + FileIOUtility.getAsString(sourceFile) + "</style>";
      }

    } catch (Exception e) {
      //Do nothing
    }

    if (returnString == null) {
      returnString =
          "<link rel=\"stylesheet\" href=\"" + fallback + "\">";
    }

    return returnString;
  }

}
