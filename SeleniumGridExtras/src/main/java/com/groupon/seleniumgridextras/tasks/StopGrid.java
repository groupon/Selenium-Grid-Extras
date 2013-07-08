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
import com.groupon.seleniumgridextras.PortChecker;
import com.groupon.seleniumgridextras.grid.GridWrapper;

import java.util.Map;

public class StopGrid extends ExecuteOSTask {

  public StopGrid() {
    setEndpoint("/stop_grid");
    setDescription("Stops grid or node process");
    JsonObject params = new JsonObject();
    params.addProperty("role", "hub|node - defaults to 'default_role' param in config file");
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass("btn-info");
    setButtonText("Stop Grid");
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
      JsonObject processInfo = PortChecker.getParsedPortInfo(servicePort);
      ExecuteOSTask killer = new KillPid();
      return killer.execute(processInfo.get("pid").getAsString());
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

}
