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

public class GridStatus extends ExecuteOSTask {

  public GridStatus() {
    setEndpoint("/grid_status");
    setDescription("Returns status of the Selenium Grid hub/node. If currently running and what is the PID");
    JsonObject params = new JsonObject();
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass("btn-success");
    setButtonText("Grid Status");
    setEnabledInGui(true);

    addResponseDescription("hub_running", "Boolean if hub is running on given port");
    addResponseDescription("node_running", "Boolean if node is running on given port");
    addResponseDescription("hub_info", "Hash object describing the Hub Process");
    addResponseDescription("node_info", "Hash object describing the Node Process");

  }


  @Override
  public JsonObject execute() {
    try {
      String hubPort = GridWrapper.getGridConfigPortForRole("hub");
      String nodePort = GridWrapper.getGridConfigPortForRole("node");

      JsonObject hubInfo = PortChecker.getParsedPortInfo(hubPort);
      JsonObject nodeInfo = PortChecker.getParsedPortInfo(nodePort);

      getJsonResponse().addKeyValues("hub_running", hubInfo.isJsonNull() ? false : true);
      getJsonResponse().addKeyValues("node_running", nodeInfo.isJsonNull() ? false : true);
      getJsonResponse().addKeyValues("hub_info", hubInfo);
      getJsonResponse().addKeyValues("node_info", nodeInfo);

      return getJsonResponse().getJson();
    } catch (Exception error) {
      getJsonResponse().addKeyValues("error", error.toString());
      return getJsonResponse().getJson();
    }
  }

}
