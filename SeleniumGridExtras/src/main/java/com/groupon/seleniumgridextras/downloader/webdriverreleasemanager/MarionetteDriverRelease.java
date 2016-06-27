package com.groupon.seleniumgridextras.downloader.webdriverreleasemanager;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarionetteDriverRelease extends WebDriverRelease {

  public MarionetteDriverRelease(String input) {
    super(input);

    Matcher m = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)").matcher(input);

    if (m.find()){
      setMajorVersion(Integer.valueOf(m.group(1)));
      setMinorVersion(Integer.valueOf(m.group(2)));
      setPatchVersion(Integer.valueOf(m.group(3)));
    }

    setName("marionettedriver");
    setRelativePath("index.html?path=" + getPrettyPrintVersion(".") + "/");

  }

  public String getPrettyPrintVersion(String separator){
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append(getMajorVersion());
    stringBuilder.append(separator);
    stringBuilder.append(getMinorVersion());
    stringBuilder.append(separator);
    stringBuilder.append(getPatchVersion());

    return stringBuilder.toString();
  }
}
