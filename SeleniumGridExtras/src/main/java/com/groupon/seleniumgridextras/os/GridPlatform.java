package com.groupon.seleniumgridextras.os;


import java.util.LinkedList;
import java.util.List;

public class GridPlatform {

  protected List<String> xpFamily = new LinkedList<>();
  protected List<String> vistaFamily = new LinkedList<>();
  protected List<String> win8Family = new LinkedList<>();
  protected List<String> win8_1Family = new LinkedList<>();


  public GridPlatform() {
    xpFamily.add("Windows 95");
    xpFamily.add("Windows 98");
    xpFamily.add("Windows Me");
    xpFamily.add("Windows NT");
    xpFamily.add("Windows 2000");
    xpFamily.add("Windows XP");
    xpFamily.add("Windows 2003");

    vistaFamily.add("Windows 7");
    vistaFamily.add("Windows Vista");
    vistaFamily.add("Windows 2008");

    win8Family.add("Windows 8");
    win8Family.add("Windows Server 2012");

    win8_1Family.add("Windows 8.");

  }


  public String getWindowsFamily(String osName) {

    if (xpFamily.contains(osName)) {
      return "XP";
    } else if (vistaFamily.contains(osName)) {
      return "VISTA";
    } else if (win8Family.contains(osName)) {
      return "WIN8";
    } else if (win8_1Family.contains(osName)) {
      return "WIN8_1";
    } else {
      return "WINDOWS";
    }
  }
}
