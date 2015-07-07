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
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;

import java.util.Map;

public class KillPid extends ExecuteOSTask {

  public KillPid() {
    setEndpoint(TaskDescriptions.Endpoints.KILL_PID);
    setDescription(TaskDescriptions.Description.KILL_PID);
    JsonObject params = new JsonObject();
    params.addProperty(JsonCodec.OS.KillCommands.ID, "(Required) -  Process ID (PID) to terminate.");
    params.addProperty(JsonCodec.OS.KillCommands.SIGNAL, "(unix only) - Signal Term number such as 1, 2...9");
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass(TaskDescriptions.UI.BTN_DANGER);
    setButtonText(TaskDescriptions.UI.ButtonText.KILL_PID);
    setEnabledInGui(false);
  }

  @Override
  public JsonObject execute() {

    getJsonResponse().addKeyValues(JsonCodec.ERROR, "ID is a required parameter");
    return getJsonResponse().getJson();
  }

  @Override
  public JsonObject execute(Map<String, String> parameter) {

    if (parameter.isEmpty() || !parameter.containsKey(JsonCodec.OS.KillCommands.ID)) {

      return execute();
    } else {
      String pid = parameter.get(JsonCodec.OS.KillCommands.ID).toString();
      if (!RuntimeConfig.getOS().isWindows() && parameter.containsKey(
          JsonCodec.OS.KillCommands.SIGNAL)) {
        pid = "-" + parameter.get(JsonCodec.OS.KillCommands.SIGNAL).toString() + " " + pid;
      }
      
      String command = "";
      if (RuntimeConfig.getOS().isWindows()) {
          command = getWindowsCommand(pid);
      } else if (RuntimeConfig.getOS().isMac()) {
          command = getLinuxCommand(pid);
      } else {
          command = getLinuxCommand(pid);
      }

      JsonObject response = ExecuteCommand.execRuntime(command, waitToFinishTask);
      return response;
    }
  }

  @Override
  public String getWindowsCommand(String parameter) {
    return "taskkill -F -IM " + parameter;
  }

  @Override
  public String getLinuxCommand(String parameter) {
    return "kill -9 " + parameter;
  }
}
