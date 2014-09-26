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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PortChecker {

  public static JsonObject getParsedPortInfo(String port) {

    JsonObject status = getPortInfo(port);
    JsonArray standardOut = (JsonArray) status.get(JsonCodec.OUT);

    if (RuntimeConfig.getOS().isWindows()) {
      return parseWindowsInfo(standardOut);
    }

    return parseLinuxInfo(standardOut);

  }

  public static JsonObject getParsedPortInfo(int port) {
    return getParsedPortInfo(String.valueOf(port));
  }

  public static JsonObject getPortInfo(String port) {
    StringBuilder command = new StringBuilder();

    command.append(getCommand());

    if (!port.equals("")) {
      command.append(getParameters());
      command.append(port);
    }

    return ExecuteCommand.execRuntime(command.toString());
  }

  private static JsonObject parseLinuxInfo(JsonArray status) {
    JsonObject info = new JsonObject();
    info.addProperty(JsonCodec.OUT, status.toString());

    for (JsonElement line : status) {
      Matcher m = Pattern.compile("(\\w*)\\s*(\\d*)\\s*(\\w*)\\s*.*(\\(LISTEN\\))").matcher(
          line.getAsString());
      if (m.find()) {
        info.addProperty(JsonCodec.OS.PROCESS, m.group(1));
        info.addProperty(JsonCodec.OS.PID, m.group(2));
        info.addProperty(JsonCodec.OS.USER, m.group(3));
        break;
      }
    }
    return info;
  }

  private static JsonObject parseWindowsInfo(JsonArray status) {

    JsonObject info = new JsonObject();

    for (JsonElement line : status) {
      Matcher
          m =
          Pattern.compile("\\s*(TCP)\\s*([0-9.:]*)\\s*([0-9.:]*)\\s*(LISTENING)\\s*(\\d*)")
              .matcher(line.getAsString());
      if (m.find()) {
        info.addProperty(JsonCodec.OS.PID, m.group(5));
        break;
      }
    }

    return info;
  }


  private static String getCommand() {
    if (RuntimeConfig.getOS().isWindows()) {
      return "netstat -aon ";
    } else {
      return "lsof -sTCP:LISTEN -i TCP";
    }
  }

  private static String getParameters() {
    if (RuntimeConfig.getOS().isWindows()) {
      return " | findstr :";
    } else {
      return ":";
    }
  }

}
