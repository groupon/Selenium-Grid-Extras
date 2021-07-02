package com.groupon.seleniumgridextras.downloader.webdriverreleasemanager;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MsEdgeDriverRelease extends WebDriverRelease {

  private int buildVersion;

  public MsEdgeDriverRelease(String input) {
    super(input);

    Matcher m = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)").matcher(input);

    if (m.find()){
      setMajorVersion(Integer.valueOf(m.group(1)));
      setMinorVersion(Integer.valueOf(m.group(2)));
      setBuildVersion(Integer.valueOf(m.group(3)));
      setPatchVersion(Integer.valueOf(m.group(4)));
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
    stringBuilder.append(getBuildVersion());
    stringBuilder.append(separator);
    stringBuilder.append(getPatchVersion());

    return stringBuilder.toString();
  }

  public int getBuildVersion() {
    return buildVersion;
  }

  public void setBuildVersion(int buildVersion) {
    this.buildVersion = buildVersion;
  }

}
