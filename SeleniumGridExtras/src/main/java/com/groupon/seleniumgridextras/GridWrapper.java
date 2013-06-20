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


package com.groupon.seleniumgridextras;

import java.util.HashMap;
import java.util.Map;

public class GridWrapper {

  public static String getCurrentJarPath() {
    return getWebdriverHome() + "/" + getWebdriverVersion()
           + ".jar";
  }

  public static String getWebdriverVersion() {
    return RuntimeConfig.getWebdriverVersion();
  }

  public static String getWebdriverHome() {
    return RuntimeConfig.getWebdriverParentDir();
  }

  public static String getStartCommand(String role) {
    return "java -jar " + getCurrentJarPath() + " " + getFormattedConfig(role);
  }

  public static String getGridConfigPortForRole(String role) {
    Map<String, String> config = getGridConfig(role);
    return config.get("-port");
  }

  public static Map<String, String> getGridConfig(String role) {
    Map grid = RuntimeConfig.getGridConfig();
    Map config = (HashMap<String, String>) grid.get(role);

    return config;
  }

  public static String getDefaultRole() {
    Map grid = RuntimeConfig.getGridConfig();
    return grid.get("default_role").toString();
  }

  private static String getFormattedConfig(String role) {
    Map<String, String> config = getGridConfig(role);
    StringBuilder commandLineParam = new StringBuilder();

    for (Map.Entry<String, String> entry : config.entrySet()) {
      commandLineParam.append(" " + entry.getKey());
      commandLineParam.append(" " + entry.getValue());
    }

    return commandLineParam.toString();
  }


}
