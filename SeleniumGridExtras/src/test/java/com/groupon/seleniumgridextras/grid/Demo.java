package com.groupon.seleniumgridextras.grid;

import org.openqa.grid.selenium.GridLauncher;

public class Demo {

  public static void main(String[] args) throws Exception {
    String[] a = {"-port", "4444",
        "-host", "localhost",
        "-role", "hub",
        "-servlets", "com.groupon.seleniumgridextras.grid.SeleniumGridExtrasServlet"};
    GridLauncher.main(a);
  }
}
