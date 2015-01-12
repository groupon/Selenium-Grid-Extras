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

import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.os.OSInfo;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;

import org.apache.log4j.Logger;

import java.util.Map;

public class SystemInfo extends ExecuteOSTask {
  private static Logger logger = Logger.getLogger(SystemInfo.class);

  public SystemInfo() {
    setEndpoint(TaskDescriptions.Endpoints.SYSTEM);
    setDescription(TaskDescriptions.Description.SYSTEM);
    JsonObject params = new JsonObject();
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass("btn-info");
    setButtonText(TaskDescriptions.UI.ButtonText.SYSTEM);
    setEnabledInGui(true);

    addResponseDescription(JsonCodec.OS.Hardware.HardDrive.DRIVES, "Hash of all mounted drives and their info");
    addResponseDescription(JsonCodec.OS.Hardware.Processor.PROCESSOR, "Info about processors on machine");
    addResponseDescription(JsonCodec.OS.Hardware.Ram.RAM, "Info in bytes on how much RAM machine has/uses");
    addResponseDescription(JsonCodec.OS.JVM.JVM_INFO, "JVM Info");
    addResponseDescription(JsonCodec.OS.UPTIME, "Uptime in minutes");

    addResponseDescription(JsonCodec.OS.HOSTNAME, "Host name");
    addResponseDescription(JsonCodec.OS.IP, "Host ip");

  }

  @Override
  public JsonObject execute() {

    try {

      OSInfo info = new OSInfo();

      getJsonResponse().addListOfHashes(JsonCodec.OS.Hardware.HardDrive.DRIVES, info.getDiskInfo());
      getJsonResponse().addKeyValues(JsonCodec.OS.Hardware.Processor.PROCESSOR, info.getProcessorInfo());
      getJsonResponse().addKeyValues(JsonCodec.OS.UPTIME, info.getSystemUptime());
      getJsonResponse().addKeyValues(JsonCodec.OS.Hardware.Ram.RAM, info.getMemoryInfo());
      getJsonResponse().addKeyValues(JsonCodec.OS.JVM.JVM_INFO, info.getJvmMemoryInfo());
    } catch (Exception e) {
      logger.error(e);
      e.printStackTrace();
      getJsonResponse().addKeyValues(JsonCodec.ERROR, e.toString());
    }

    getJsonResponse().addKeyValues(JsonCodec.OS.HOSTNAME, RuntimeConfig.getOS().getHostName());
    getJsonResponse().addKeyValues(JsonCodec.OS.IP, RuntimeConfig.getHostIp());

    return getJsonResponse().getJson();
  }

  @Override
  public JsonObject execute(Map<String, String> parameter) {
    return execute();
  }



}
