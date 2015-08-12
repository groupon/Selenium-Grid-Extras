package com.groupon.seleniumgridextras.config.capabilities;

//Copied from package org.openqa.selenium.remote.BrowserType.java in Selenium


/**
 * All the browsers supported by selenium
 */
public interface BrowserType {

    String FIREFOX = "firefox";

    String EDGE = "MicrosoftEdge";

    @Deprecated
    String FIREFOX_2 = "firefox2";
    @Deprecated
    String FIREFOX_3 = "firefox3";
    @Deprecated
    String FIREFOX_PROXY = "firefoxproxy";
    @Deprecated
    String FIREFOX_CHROME = "firefoxchrome";
    @Deprecated
    String GOOGLECHROME = "googlechrome";
    String SAFARI = "safari";
    String OPERA = "opera";
    @Deprecated
    String IEXPLORE = "iexplore";

    @Deprecated
    String IEXPLORE_PROXY = "iexploreproxy";
    @Deprecated
    String SAFARI_PROXY = "safariproxy";
    String CHROME = "chrome";
    @Deprecated
    String KONQUEROR = "konqueror";
    @Deprecated
    String MOCK = "mock";
    @Deprecated
    String IE_HTA = "iehta";

    String ANDROID = "android";
    String HTMLUNIT = "htmlunit";
    String IE = "internet explorer";
    String IPHONE = "iPhone";
    String IPAD = "iPad";
    String PHANTOMJS = "phantomjs";

    // Android browsers
    String CHROMIUM = "chromium";
    String BROWSER = "browser";


}
