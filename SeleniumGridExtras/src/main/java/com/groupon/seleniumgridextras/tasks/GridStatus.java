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

import com.groupon.seleniumgridextras.grid.GridWrapper;
import com.groupon.seleniumgridextras.JsonResponseBuilder;
import com.groupon.seleniumgridextras.PortChecker;
import com.groupon.seleniumgridextras.tasks.ExecuteOSTask;

import java.util.Map;

public class GridStatus extends ExecuteOSTask {

  private JsonResponseBuilder jsonResponse;

  @Override
  public String getEndpoint() {
    return "/grid_status";
  }

  @Override
  public String getDescription() {
    return "Returns status of the Selenium Grid hub/node. If currently running and what is the PID";
  }

  @Override
  public Map getResponseDescription() {

    jsonResponse = new JsonResponseBuilder();
    jsonResponse.addKeyDescriptions("hub_running", "Boolean if hub is running on given port");
    jsonResponse.addKeyDescriptions("node_running", "Boolean if node is running on given port");
    jsonResponse.addKeyDescriptions("hub_info", "Hash object describing the Hub Process");
    jsonResponse.addKeyDescriptions("node_info", "Hash object describing the Node Process");

    return jsonResponse.getKeyDescriptions();
  }

  @Override
  public String execute() {
    try {
      String hubPort = GridWrapper.getGridConfigPortForRole("hub");
      String nodePort = GridWrapper.getGridConfigPortForRole("node");

      Map<String, String> hubInfo = PortChecker.getParsedPortInfo(hubPort);
      Map<String, String> nodeInfo = PortChecker.getParsedPortInfo(nodePort);

      jsonResponse.addKeyValues("hub_running", hubInfo.isEmpty() ? false : true);
      jsonResponse.addKeyValues("node_running", nodeInfo.isEmpty() ? false : true);
      jsonResponse.addKeyValues("hub_info", hubInfo);
      jsonResponse.addKeyValues("node_info", nodeInfo);

      return jsonResponse.toString();
    } catch (Exception error) {
      jsonResponse.addKeyValues("error", error.toString());
      return jsonResponse.toString();
    }
  }

}
