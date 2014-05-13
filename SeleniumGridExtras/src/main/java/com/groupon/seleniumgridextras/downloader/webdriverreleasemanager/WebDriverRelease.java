package com.groupon.seleniumgridextras.downloader.webdriverreleasemanager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebDriverRelease {

  private int majorVersion;
  private int minorVersion;
  private int patchVersion;
  private String name;
  private String relativePath;

  public WebDriverRelease(String input) {

    Matcher initialMatcher = Pattern.compile("(selenium-server-standalone)|(IEDriverServer)").matcher(input);
    if (initialMatcher.find()) {

      Matcher
          detailedMatcher =
          Pattern.compile(
              "(\\d+)\\.(\\d+)\\/((IEDriverServer)|(selenium-server-standalone))[_-]?(Win32_)?(\\d+)\\.(\\d+)\\.(\\d+)(\\.\\w+)")
              .matcher(input);

      if (detailedMatcher.find()) {
        this.relativePath = detailedMatcher.group(0);
        this.majorVersion = Integer.valueOf(detailedMatcher.group(1));
        this.minorVersion = Integer.valueOf(detailedMatcher.group(2));
        this.patchVersion = Integer.valueOf(detailedMatcher.group(9));
        this.name = detailedMatcher.group(3);

      }

    }


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

  public int getComparableVersion(){
    return Integer.valueOf(getPrettyPrintVersion("0"));
  }

  public int getMajorVersion() {
    return majorVersion;
  }

  public int getMinorVersion() {
    return minorVersion;
  }

  public int getPatchVersion() {
    return patchVersion;
  }

  public String getName() {
    return name;
  }

  public String getRelativePath() {
    return relativePath;
  }


}
