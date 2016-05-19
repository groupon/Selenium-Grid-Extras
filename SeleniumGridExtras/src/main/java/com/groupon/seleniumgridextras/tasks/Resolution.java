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

// TODO Add Linux for get and set resolution. Add OSX set resolution.
public class Resolution extends ExecuteOSTask {
  private final String[] osxGetResolution = {"bash", "-c", "system_profiler SPDisplaysDataType | grep Resolution"};
  private final String windowsSetResolution = "powershell.exe Set-DisplayResolution -Width %s -Height %s -Force";

  public Resolution() {
    setEndpoint(TaskDescriptions.Endpoints.RESOLUTION);
    setDescription(
        TaskDescriptions.Description.RESOLUTION);
    JsonObject params = new JsonObject();
    params.addProperty(JsonCodec.OS.RESOLUTION_WIDTH, "(Optional)Resolution Width");
    params.addProperty(JsonCodec.OS.RESOLUTION_HEIGHT, "(Optional)Resolution Height");
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass(TaskDescriptions.UI.BTN_DANGER);
    setButtonText(TaskDescriptions.UI.ButtonText.RESOLUTION);
    setEnabledInGui(true);
  }

  @Override
  public JsonObject execute() {
    if (RuntimeConfig.getOS().isWindows()) {
      return getWindowsResolution();
    } else if (RuntimeConfig.getOS().isMac()) {
      return getOSXResolution();
    } else {
      getJsonResponse().addKeyValues(JsonCodec.ERROR,
                                     "Not yet implemented in Linux");
      return getJsonResponse().getJson();
    }
  }
  
  @Override
  public JsonObject execute(Map<String, String> parameter) {
      if (!parameter.isEmpty() && 
    		  parameter.containsKey(JsonCodec.OS.RESOLUTION_WIDTH) && 
    		  parameter.containsKey(JsonCodec.OS.RESOLUTION_HEIGHT)) {
    	  if (RuntimeConfig.getOS().isWindows()) {
	          return setWindowsResolution(parameter.get(JsonCodec.OS.RESOLUTION_WIDTH).toString(),
	        		  parameter.get(JsonCodec.OS.RESOLUTION_HEIGHT).toString());
    	  } else {
    	    getJsonResponse().addKeyValues(JsonCodec.ERROR,
                      "Not yet implemented in OSX or Linux");
    	    return getJsonResponse().getJson();
    	  }
      }
      return execute();
  }

  private JsonObject setWindowsResolution(String width, String height) {
    String[] getCommand = {"powershell.exe", "Get-Command", "Set-DisplayResolution", "-errorAction", "SilentlyContinue"};
    JsonObject object = ExecuteCommand.execRuntime(getCommand, waitToFinishTask);
    if (object.get("out").getAsJsonArray().size() > 1) { // Set-DisplayResolution is a valid command for Windows Server
	  final String command = String.format(windowsSetResolution, width, height);
      return ExecuteCommand.execRuntime(command, waitToFinishTask);
    } else {
	    getJsonResponse().addKeyValues(JsonCodec.ERROR,
                "Set-DisplayResolution not found.");
	    return getJsonResponse().getJson();
    }
  }

  private JsonObject getWindowsResolution() {
	String[] command = {"powershell.exe", "Get-Command", "Get-DisplayResolution", "-errorAction", "SilentlyContinue"};
    JsonObject object = ExecuteCommand.execRuntime(command, waitToFinishTask);
    if (object.get("out").getAsJsonArray().size() > 1) { // Get-DisplayResolution is a valid command for Windows Server
	  final String[] windowsGetResolution = {"powershell.exe", "Get-DisplayResolution"};
	  return ExecuteCommand.execRuntime(windowsGetResolution, waitToFinishTask);
	} else {
      final String windowsGetResolution = "wmic desktopmonitor get screenheight, screenwidth";
      return ExecuteCommand.execRuntime(windowsGetResolution, waitToFinishTask);
	}
  }

  private JsonObject getOSXResolution() {
    return ExecuteCommand.execRuntime(osxGetResolution, waitToFinishTask);
  }

}
