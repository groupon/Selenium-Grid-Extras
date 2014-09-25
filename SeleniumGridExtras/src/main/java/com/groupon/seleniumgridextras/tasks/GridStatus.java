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

import com.groupon.seleniumgridextras.JsonResponseBuilder;
import com.groupon.seleniumgridextras.PortChecker;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

public class GridStatus extends ExecuteOSTask {

  private static final String HUB_RUNNING = "hub_running";
  private static final String NODE_RUNNING = "node_running";
  private static final String HUB_INFO = "hub_info";
  private static final String NODE_INFO = "node_info";
  private static final String NODE_SESSIONS_STARTED = "node_sessions_started";
  private static final String NODE_SESSIONS_CLOSED = "node_sessions_closed";
  private static final String NODE_SESSIONS_LIMIT = "node_sessions_limit";

  public GridStatus() {
    setEndpoint("/grid_status");
    setDescription(
        "Returns status of the Selenium Grid hub/node. If currently running and what is the PID");
    JsonObject params = new JsonObject();
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass("btn-success");
    setButtonText("Grid Status");
    setEnabledInGui(true);

    addResponseDescription(HUB_RUNNING, "Boolean if hub is running on given port");
    addResponseDescription(NODE_RUNNING, "Boolean if node is running on given port");
    addResponseDescription(HUB_INFO, "Hash object describing the Hub Process");
    addResponseDescription(NODE_INFO, "Hash object describing the NodeConfig Process");

    addResponseDescription(NODE_SESSIONS_STARTED,
                           "Integer how many times grid connected to this computer");
    addResponseDescription(NODE_SESSIONS_CLOSED,
                           "Integer how many sessions where properly closed");
    addResponseDescription(NODE_SESSIONS_LIMIT, "Integer upper limit before the box reboots");

  }


  @Override
  public JsonObject execute() {
    try {
      JsonObject hubInfo = PortChecker.getParsedPortInfo(4444);
      JsonObject nodeInfo = PortChecker.getParsedPortInfo(5555);

      getJsonResponse().addKeyValues(HUB_RUNNING, hubInfo.isJsonNull() ? false : true);
      getJsonResponse().addKeyValues(NODE_RUNNING, nodeInfo.isJsonNull() ? false : true);
      getJsonResponse().addKeyValues(HUB_INFO, hubInfo);
      getJsonResponse().addKeyValues(NODE_INFO, nodeInfo);

      getJsonResponse().addKeyValues(NODE_SESSIONS_STARTED,
                                     RuntimeConfig.getTestSessionTracker().getSessionsStarted());
      getJsonResponse().addKeyValues(NODE_SESSIONS_CLOSED,
                                     RuntimeConfig.getTestSessionTracker().getSessionsEnded());
      getJsonResponse()
          .addKeyValues(NODE_SESSIONS_LIMIT, RuntimeConfig.getConfig().getRebootAfterSessions());

      return getJsonResponse().getJson();
    } catch (Exception error) {
      getJsonResponse().addKeyValues(JsonResponseBuilder.ERROR, error.toString());
      return getJsonResponse().getJson();
    }
  }

}
