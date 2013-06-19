
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

package com.groupon;

import java.util.HashMap;
import java.util.Map;

public class GetInfoForPort extends ExecuteOSTask {

  @Override
  public String getEndpoint() {
    return "/port_info";
  }

  @Override
  public String getDescription() {
    return "Returns parsed information on a PID occupying a given port";
  }

  @Override
  public Map getResponseDescription() {
    Map response = new HashMap();
    response.put("process_name", "Process name/type (ie java, ruby, etc..)");
    response.put("pid", "Process ID");
    response.put("user", "User who is running process");
    response.put("port", "Port searched for");
    response.put("error", "Any errors from command");
    return response;
  }


  @Override
  public Map getAcceptedParams() {
    Map<String, String> params = new HashMap();
    params.put("port", "(Required) Port to be used");
    return params;
  }

  @Override
  public String execute() {
    return JsonWrapper.getPortInfoToJson("", "", "", "", "Port parameter is required");
  }

  @Override
  public String execute(Map<String, String> parameter) {
    if (parameter.isEmpty() || !parameter.containsKey("port")) {
      return execute();
    } else {
      return execute(parameter.get("port").toString());
    }
  }

  @Override
  public String execute(String port) {

    try {
      Map<String, String> portInfo = PortChecker.getParsedPortInfo(port);

      String process = "";
      String pid = "";
      String user = "";
      String returnError = "";

      try {
        process = portInfo.get("process").toString();
      } catch (NullPointerException error) {
      }
      try {
        pid = portInfo.get("pid").toString();
      } catch (NullPointerException error) {
      }
      try {
        user = portInfo.get("user").toString();
      } catch (NullPointerException error) {
      }

      if (process.equals("") && pid.equals("") && user.equals("")){
        returnError = "No info found for this port";
      }

      return JsonWrapper.getPortInfoToJson(process, pid, user, port, returnError);

    } catch (Exception error) {
      //Big try catch to see if anything at all went wrong
      return JsonWrapper.getPortInfoToJson("", "", "", "", error.toString());
    }

  }
}
