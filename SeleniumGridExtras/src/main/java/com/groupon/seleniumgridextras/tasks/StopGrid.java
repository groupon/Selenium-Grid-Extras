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

import com.groupon.seleniumgridextras.ExecuteCommand;
import com.groupon.seleniumgridextras.PortChecker;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;

import java.util.Map;

public class StopGrid extends ExecuteOSTask {

  public StopGrid() {
    setEndpoint(TaskDescriptions.Endpoints.STOP_GRID);
    setDescription(TaskDescriptions.Description.STOP_GRID);
    JsonObject params = new JsonObject();
    params.addProperty(JsonCodec.OS.PORT, "(Required) Port on which the node/hub is running.");
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass(TaskDescriptions.UI.BTN_DANGER);
    setButtonText(TaskDescriptions.UI.ButtonText.STOP_GRID);
    setEnabledInGui(false);
  }

  @Override
  public JsonObject getAcceptedParams() {
    JsonObject params = new JsonObject();
    params.addProperty(JsonCodec.OS.PORT, "(Required) Port on which the node/hub is running");
    return params;
  }

  @Override
  public JsonObject execute() {
    getJsonResponse().addKeyValues(JsonCodec.ERROR, "Port parameter is required");
    return getJsonResponse().getJson();
  }


  /**
   * Get all the tasks currently running with verbose description. Search in the task description
   * for execution of the start_&lt;port>.bat and kill the corresponding process.
   */
  @Override
  public String getWindowsCommand(String port) {
    JsonObject portInfo = PortChecker.getParsedPortInfo(port);

    if (portInfo.has(JsonCodec.OS.PID)) {
      KillPid processKiller = new KillPid();
      return processKiller.getWindowsCommand(portInfo.get(JsonCodec.OS.PID).toString());
    }
    return "";
  }

  public String getWindowsCommand(int port) {
    return getWindowsCommand(String.valueOf(port));
  }

  public String getLinuxCommand(int port) {
    return getLinuxCommand(String.valueOf(port));
  }

  @Override
  public String getLinuxCommand(String port) {
    JsonObject status = PortChecker.getParsedPortInfo(port);

    if (status.has(JsonCodec.OS.PID)){
      KillPid killer = new KillPid();
      return killer.getLinuxCommand(status.get(JsonCodec.OS.PID).getAsString());
    }

      return "";
//    return "lsof -sTCP:LISTEN -i TCP:" + port + " | grep -v PID | awk '{print $2}' | xargs kill";
  }

  @Override
  public JsonObject execute(Map<String, String> parameter) {
    if (parameter.isEmpty() || !parameter.containsKey(JsonCodec.OS.PORT)) {
      return execute();
    } else {
      return this.execute(parameter.get(JsonCodec.OS.PORT).toString());
    }
  }

  @Override
  public JsonObject execute(String parameter) {
    String command;

    if (RuntimeConfig.getOS().isWindows()){
      command = getWindowsCommand(parameter);
    } else if (RuntimeConfig.getOS().isMac()){
      command = getMacCommand(parameter);
    } else {
      command = getLinuxCommand(parameter);
    }

    JsonObject foo = ExecuteCommand.execRuntime(command, waitToFinishTask);

    return foo;
  }

}
