
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

import java.util.Map;

public class GetInfoForPort extends ExecuteOSTask {

  public GetInfoForPort() {
    setEndpoint("/port_info");
    setDescription("Returns parsed information on a PID occupying a given port");
    JsonObject params = new JsonObject();
    params.addProperty("port", "(Required) Port to be used");
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass("btn-success");
    setButtonText("Get Info for Port");
    setEnabledInGui(true);

    addResponseDescription("process_name", "Process name/type (ie java, ruby, etc..)");
    addResponseDescription("pid", "Process ID");
    addResponseDescription("user", "User who is running process");
    addResponseDescription("port", "Port searched for");
  }


  @Override
  public JsonObject getAcceptedParams() {
    JsonObject params = new JsonObject();
    params.addProperty("port", "(Required) Port to be used");
    return params;
  }

  @Override
  public JsonObject execute() {
    getJsonResponse().addKeyValues("error", "Port parameter is required");
    return getJsonResponse().getJson();
  }

  @Override
  public JsonObject execute(Map<String, String> parameter) {

    if (!parameter.isEmpty() && parameter.containsKey("port")) {
      return execute(parameter.get("port").toString());
    } else {
      return execute();
    }

  }


  @Override
  public JsonObject execute(String port) {

    try {
      JsonObject portInfo = PortChecker.getParsedPortInfo(port);

      String process = "";
      String pid = "";
      String user = "";
      String out = "";

      try {
        process = portInfo.get("process").getAsString();
      } catch (NullPointerException error) {
      }
      try {
        pid = portInfo.get("pid").getAsString();
      } catch (NullPointerException error) {
      }
      try {
        user = portInfo.get("user").getAsString();
      } catch (NullPointerException error) {
      }

      try {
        out = portInfo.get("out").getAsString();
      } catch (NullPointerException error) {
      }

      getJsonResponse().addKeyValues("process_name", process);
      getJsonResponse().addKeyValues("pid", pid);
      getJsonResponse().addKeyValues("user", user);
      getJsonResponse().addKeyValues("port", port);
      getJsonResponse().addKeyValues("out", out);
      return getJsonResponse().getJson();

    } catch (Exception error) {
      //Big try catch to see if anything at all went wrong
      getJsonResponse().addKeyValues("error", error.toString());
      return getJsonResponse().getJson();
    }

  }
}
