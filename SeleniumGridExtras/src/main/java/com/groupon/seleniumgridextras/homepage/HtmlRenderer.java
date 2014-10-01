package com.groupon.seleniumgridextras.homepage;

import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.utilities.FileIOUtility;

import java.io.File;
import java.util.Map;

public class HtmlRenderer {

  private Map parameters;
  private static File bootstrapCss;

  public HtmlRenderer(Map params) {
    this.parameters = params;
  }

  public String toString() {
    return "<html><body><h1>Hello world</h1>" + this.parameters + " </body></html>";
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
