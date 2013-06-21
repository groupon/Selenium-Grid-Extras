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

package com.groupon.seleniumgridextras;

import java.util.HashMap;
import java.util.Map;

public class StartGrid extends ExecuteOSTask {

  public boolean waitToFinishTask = false;

  @Override
  public String getEndpoint() {
    return "/start_grid";
  }

  @Override
  public String getDescription() {
    return "Starts an instance of Selenium Grid Hub or Node";
  }

  @Override
  public String execute() {
    return execute(GridWrapper.getDefaultRole());
  }

  @Override
  public String execute(String role) {
    try {
      String servicePort = GridWrapper.getGridConfigPortForRole(role);
      Map<String, String> occupiedPid = PortChecker.getParsedPortInfo(servicePort);

      if (!occupiedPid.isEmpty()) {
        System.out.println(servicePort + " port is busy, won't try to start a service");
        getJsonResponse().addKeyValues("error", "Port: " + servicePort
                                                + " is occupied by some other process: "
                                                + occupiedPid);

        return getJsonResponse().toString();
      }

      String

          command =
          OSChecker.isWindows() ? getWindowsCommand(role)
                                : OSChecker.isMac() ? getMacCommand(role) : getLinuxCommand(role);

      String serviceStartResponse = ExecuteCommand.execRuntime(command, waitToFinishTask);

      Map result = JsonWrapper.parseJson(serviceStartResponse);

      if (result.get("exit_code").toString().equals("0")) {
        getJsonResponse().addKeyValues("out",
                                       "Service start command sent, might take as long as 10 seconds to spin up");
        return getJsonResponse().toString();
      } else {
        System.out.println("Something didn't go right in launching service");
        return serviceStartResponse;
      }
    } catch (Exception error) {
      getJsonResponse().addKeyValues("error", error.toString());
      return getJsonResponse().toString();
    }


  }

  @Override
  public String execute(Map<String, String> parameter) {
    if (parameter.isEmpty() || !parameter.containsKey("role")) {
      return execute();
    } else {
      return execute(parameter.get("role").toString());
    }
  }

  @Override
  public Map getAcceptedParams() {
    Map<String, String> params = new HashMap();
    params.put("role", "hub|node - defaults to 'default_role' param in config file");
    return params;
  }

  @Override
  public String getLinuxCommand(String role) {
    return GridWrapper.getStartCommand(role) + " &";
  }


}
