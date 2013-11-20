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

package com.groupon.seleniumgridextras.tasks;


import com.google.gson.JsonObject;

import com.groupon.seleniumgridextras.OS;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.os.LinuxSystemInfo;
import com.groupon.seleniumgridextras.os.MacSystemInfo;
import com.groupon.seleniumgridextras.os.OSInfo;
import com.groupon.seleniumgridextras.os.WindowsSystemInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SystemInfo extends ExecuteOSTask {

  public SystemInfo() {
    setEndpoint("/system");
    setDescription("Returns system details about the current node");
    JsonObject params = new JsonObject();
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass("btn-info");
    setButtonText("System Info");
    setEnabledInGui(true);

    addResponseDescription("drives", "Hash of all mounted drives and their info");
    addResponseDescription("processor", "Info about processors on machine");
    addResponseDescription("ram", "Info in bytes on how much RAM machine has/uses");
    addResponseDescription("uptime", "System uptime since last reboot in seconds");

    addResponseDescription("hostname", "Host name");
    addResponseDescription("ip", "Host ip");

  }

  @Override
  public JsonObject execute() {

    try {
      OSInfo info;

      if (RuntimeConfig.getOS().isWindows()) {
        info = new WindowsSystemInfo();
      } else if (RuntimeConfig.getOS().isMac()) {
        info = new MacSystemInfo();
      } else {
        info = new LinuxSystemInfo();
      }

      getJsonResponse().addListOfHashes("drives", info.getDiskInfo());
      getJsonResponse().addKeyValues("processor", info.getProcessorInfo());
      getJsonResponse().addKeyValues("ram", info.getMemoryInfo());
      getJsonResponse().addKeyValues("uptime", info.getSystemUptime());
    } catch (Exception e) {
      getJsonResponse().addKeyValues("error", e.toString());
    }

    List<String> hostNetworking = getComputerNetworkInfo();
    getJsonResponse().addKeyValues("hostname", hostNetworking.get(0));
    getJsonResponse().addKeyValues("ip", hostNetworking.get(1));

    return getJsonResponse().getJson();
  }

  @Override
  public JsonObject execute(Map<String, String> parameter) {
    return execute();
  }

  private List<String> getComputerNetworkInfo() {
    List<String> host = new LinkedList<String>();

    try {
      InetAddress addr;
      addr = InetAddress.getLocalHost();
      host.add(addr.getHostName());
      host.add(addr.getHostAddress());
    } catch (UnknownHostException ex) {
      System.out.println("Hostname can not be resolved");
      host.add("N/A");
      host.add("N/A");
    }

    return host;
  }


}
