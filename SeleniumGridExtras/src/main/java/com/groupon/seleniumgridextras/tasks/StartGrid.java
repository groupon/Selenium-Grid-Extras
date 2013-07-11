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
import com.groupon.seleniumgridextras.OSChecker;
import com.groupon.seleniumgridextras.PortChecker;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.grid.GridWrapper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Map;

public class StartGrid extends ExecuteOSTask {

  public StartGrid() {
    waitToFinishTask = false;

    setEndpoint("/start_grid");
    setDescription("Starts an instance of Selenium Grid Hub or Node");
    JsonObject params = new JsonObject();
    params.addProperty("role", "hub|node - defaults to 'default_role' param in config file");
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass("btn-info");
    setButtonText("StartGrid");
    setEnabledInGui(false);
  }

  @Override
  public JsonObject execute() {
    return execute(GridWrapper.getDefaultRole());
  }

  @Override
  public JsonObject execute(String role) {
    try {
      String servicePort = GridWrapper.getGridConfigPortForRole(role);
      JsonObject occupiedPid = PortChecker.getParsedPortInfo(servicePort);

      if (occupiedPid.has("pid")) {
        System.out.println(servicePort + " port is busy, won't try to start a service");
        getJsonResponse().addKeyValues("error", "Port: " + servicePort
            + " is occupied by some other process: "
            + occupiedPid);

        return getJsonResponse().getJson();
      }

      String

          command =
          OSChecker.isWindows() ? getWindowsCommand(role)
              : OSChecker.isMac() ? getMacCommand(role) : getLinuxCommand(role);

      JsonObject serviceStartResponse = ExecuteCommand.execRuntime(command, false);

      if (serviceStartResponse.get("exit_code").toString().equals("0")) {
        getJsonResponse().addKeyValues("out",
            "Service start command sent, might take as long as 10 seconds to spin up");
        return getJsonResponse().getJson();
      } else {
        System.out.println("Something didn't go right in launching service");
        System.out.println(serviceStartResponse);
        return serviceStartResponse;
      }
    } catch (Exception error) {
      getJsonResponse().addKeyValues("error", error.toString());
      return getJsonResponse().getJson();
    }
  }

  @Override
  public JsonObject execute(Map<String, String> parameter) {
    if (parameter.isEmpty() || !parameter.containsKey("role")) {
      return execute();
    } else {
      return execute(parameter.get("role").toString());
    }
  }

  @Override
  public String getLinuxCommand(String role) {
    return GridWrapper.getStartCommand(role) + " &";
  }

  @Override
  public String getWindowsCommand(String role) {
    String batchFile = RuntimeConfig.getSeleniungGridExtrasHomePath() + "start_" + role + ".bat";

    writeBatchFile(batchFile, GridWrapper.getWindowsStartCommand(role));

    return "powershell.exe /c \"Start-Process " + batchFile + "\"";
  }

  private void writeBatchFile(String filename, String input) {

    File file = new File(filename);

    try {
      FileUtils.writeStringToFile(file, input);
    } catch (Exception error) {
      System.out
          .println("Could not write default config file, exit with error " + error.toString());

    }
  }
}
