package com.groupon.seleniumgridextras.downloader.webdriverreleasemanager;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EdgeDriverRelease extends WebDriverRelease {

  public EdgeDriverRelease(String input) {
    super(input);
    input = input.trim().substring(2).replaceAll("\u0000", "");
    Matcher m = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)").matcher(input);

    if (m.find()){
      setMajorVersion(Integer.valueOf(m.group(1)));
      setMinorVersion(Integer.valueOf(m.group(2)));
      setPatchVersion(Integer.valueOf(m.group(3)));
      setPatch2Version(Integer.valueOf(m.group(4)));
    }

    setName("msedgedriver");
    setRelativePath("index.html?path=" + getPrettyPrintVersion(".") + "/");

  }

  public String getPrettyPrintVersion(String separator){
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append(getMajorVersion());
    stringBuilder.append(separator);
    stringBuilder.append(getMinorVersion());
    stringBuilder.append(separator);
    stringBuilder.append(getPatchVersion());
    stringBuilder.append(separator);
    stringBuilder.append(getPatch2Version());

    return stringBuilder.toString();
  }
}
