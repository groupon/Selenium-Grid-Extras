package com.groupon.seleniumgridextras.downloader.webdriverreleasemanager;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChromeDriverRelease extends WebDriverRelease {

  public ChromeDriverRelease(String input) {
    super(input);

    Matcher m = Pattern.compile("(\\d+)\\.(\\d+)").matcher(input);

    if (m.find()){
      setMajorVersion(Integer.valueOf(m.group(1)));
      setMinorVersion(Integer.valueOf(m.group(2)));
    }

    setName("chromedriver");
    setRelativePath("index.html?path=" + getPrettyPrintVersion(".") + "/");

  }

  public String getPrettyPrintVersion(String separator){
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append(getMajorVersion());
    stringBuilder.append(separator);
    stringBuilder.append(getMinorVersion());

    return stringBuilder.toString();
  }
}
