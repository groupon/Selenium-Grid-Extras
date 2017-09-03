package com.groupon.seleniumgridextras.grid;

import org.openqa.grid.selenium.GridLauncherV3;

public class Demo {

  public static void main(String[] args) throws Exception {
    String[] a = {"-port", "4444",
        "-role", "hub",
        "-servlets", "com.groupon.seleniumgridextras.grid.servlets.SeleniumGridExtrasServlet"};
    GridLauncherV3.main(a);
  }
}
