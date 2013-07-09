/**
 * Copyright (c) 2013, Groupon, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * Neither the name of GROUPON nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * Created with IntelliJ IDEA.
 * User: Dima Kovalenko (@dimacus) && Darko Marinov
 * Date: 5/10/13
 * Time: 4:06 PM
 */
package com.groupon.seleniumgridextras.grid;

import com.groupon.seleniumgridextras.OSChecker;
import com.groupon.seleniumgridextras.config.GridRole;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

public class GridWrapper {

  public static String getCurrentWebDriverJarPath() {
    return getWebdriverHome() + "/" + getWebdriverVersion() + ".jar";
  }

  public static String getWebdriverVersion() {
    return RuntimeConfig.getConfig().getWebdriver().getVersion();
  }

  public static String getSeleniumGridExtrasPath() {
    return RuntimeConfig.getSeleniungGridExtrasHomePath();
  }

  public static String getGridExtrasJarFilePath() {
    return RuntimeConfig.getSeleniumGridExtrasJarFile();
  }

  public static String getWebdriverHome() {
    return RuntimeConfig.getConfig().getWebdriver().getDirectory();
  }

  public static String getStartCommand(String role) {
    return getOsSpecificStartCommand(role, false);
  }

  public static String getWindowsStartCommand(String role) {
    return getOsSpecificStartCommand(role, true);
  }

  private static String getOsSpecificStartCommand(String role, Boolean windows) {
    String colon = windows ? ";" : ":";

    StringBuilder command = new StringBuilder();
    command.append("java -cp ");
    command.append(getGridExtrasJarFilePath());

    String jarPath = colon + getCurrentWebDriverJarPath() + " ";

    if (windows) {
      jarPath = OSChecker.toWindowsPath(jarPath);
    }

    command.append(jarPath);
    command.append(" org.openqa.grid.selenium.GridLauncher ");
    command.append(getGridRole(role).getStartCommand());

    return String.valueOf(command);
  }

  public static String getGridConfigPortForRole(String role) {
    GridRole config = getGridRole(role);
    return config.getPort();
  }

  public static String getDefaultRole() {
    return RuntimeConfig.getConfig().getGrid().getDefaultRole();
  }

  private static GridRole getGridRole(String role) {
    GridRole config = null;
    if (role.equals("hub")) {
      config = RuntimeConfig.getConfig().getGrid().getHub();
    } else if (role.equals("node")) {
      config = RuntimeConfig.getConfig().getGrid().getNode();
    }
    return config;
  }

}
