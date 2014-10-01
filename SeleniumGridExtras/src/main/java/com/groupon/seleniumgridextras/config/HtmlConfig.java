package com.groupon.seleniumgridextras.config;

import java.io.File;
import java.util.HashMap;

public class HtmlConfig extends HashMap {


  public static final String PRIMARY_CSS_FILE = "primary_css_file";
  public static final String PRIMARY_CSS_FILE_FALLBACK = "primary_css_file_fallback";
  public static final String JQUERY_FILE = "jquery_file";
  public static final String JQUERY_FALLBACK = "jquery_fallback";
  public static final String MAIN_JS_FILE = "main_js_file";
  public static final String MAIN_JS_FALLBACK = "main_js_fallback";
  public static final String MAIN_TEMPLATE = "main_template";
  public static final String MAIN_TEMPLATE_FALLBACK = "main_template_fallback";

  public void setMainCss(String cssFile) {
    this.put(PRIMARY_CSS_FILE, cssFile);
  }

  public void setFallBackCss(String cssUrl) {
    this.put(PRIMARY_CSS_FILE_FALLBACK, cssUrl);
  }

  public void setJquery(String jsFile) {
    this.put(JQUERY_FILE, jsFile);
  }

  public void setJqueryFallBack(String jsUrl) {
    this.put(JQUERY_FALLBACK, jsUrl);
  }

  public void setMainJs(String jsFile){
    this.put(MAIN_JS_FILE, jsFile);
  }

  public void setMainJsFallBack(String jsUrl){
    this.put(MAIN_JS_FALLBACK, jsUrl);
  }

  public void setTemplateJs(String jsFile){
    this.put(MAIN_TEMPLATE, jsFile);
  }

  public void setTemplateJsFallback(String jsUrl){
    this.put(MAIN_TEMPLATE_FALLBACK, jsUrl);
  }

  //Here

  public File getMainCss() {
    return new File((String) this.get(PRIMARY_CSS_FILE));
  }

  public String getMainCssFallBack() {
    return (String) this.get(PRIMARY_CSS_FILE_FALLBACK);
  }

  public File getJquery() {
    return new File((String) this.get(JQUERY_FILE));
  }

  public String getJqueryFallBack() {
    return (String) this.get(JQUERY_FALLBACK);
  }

  public File getMainJs(){
    return new File((String) this.get(MAIN_JS_FILE));
  }

  public String getMainJsFallBack(){
    return (String) this.get(MAIN_JS_FALLBACK);
  }

  public File getTemplateSource(){
    return new File((String) this.get(MAIN_TEMPLATE));
  }

  public String getTemplateJsFallback(){
    return (String) this.get(MAIN_TEMPLATE_FALLBACK);
  }



}
