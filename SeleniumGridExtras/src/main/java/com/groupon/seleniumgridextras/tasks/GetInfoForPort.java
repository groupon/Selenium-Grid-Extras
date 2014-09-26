
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

import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import com.groupon.seleniumgridextras.utilities.json.JsonResponseBuilder;
import com.groupon.seleniumgridextras.PortChecker;

import java.util.Map;

public class GetInfoForPort extends ExecuteOSTask {

  private static final String PROCESS_NAME = "process_name";
  private static final String PID = "pid";
  private static final String USER = "user";
  private static final String PORT = "port";
  private static final String PROCESS = "process";

  public GetInfoForPort() {
    setEndpoint("/port_info");
    setDescription("Returns parsed information on a PID occupying a given port");
    JsonObject params = new JsonObject();
    params.addProperty(PORT, "(Required) Port to be used");
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass("btn-success");
    setButtonText("Get Info for Port");
    setEnabledInGui(true);

    addResponseDescription(PROCESS_NAME, "Process name/type (ie java, ruby, etc..)");
    addResponseDescription(PID, "Process ID");
    addResponseDescription(USER, "User who is running process");
    addResponseDescription(PORT, "Port searched for");
  }


  @Override
  public JsonObject getAcceptedParams() {
    JsonObject params = new JsonObject();
    params.addProperty(PORT, "(Required) Port to be used");
    return params;
  }

  @Override
  public JsonObject execute() {
    getJsonResponse().addKeyValues(JsonCodec.ERROR, "Port parameter is required");
    return getJsonResponse().getJson();
  }

  @Override
  public JsonObject execute(Map<String, String> parameter) {

    if (!parameter.isEmpty() && parameter.containsKey(PORT)) {
      return execute(parameter.get(PORT).toString());
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
        process = portInfo.get(PROCESS).getAsString();
      } catch (NullPointerException error) {
      }
      try {
        pid = portInfo.get(PID).getAsString();
      } catch (NullPointerException error) {
      }
      try {
        user = portInfo.get(USER).getAsString();
      } catch (NullPointerException error) {
      }

      try {
        out = portInfo.get(JsonCodec.OUT).getAsString();
      } catch (NullPointerException error) {
      }

      getJsonResponse().addKeyValues(PROCESS_NAME, process);
      getJsonResponse().addKeyValues(PID, pid);
      getJsonResponse().addKeyValues(USER, user);
      getJsonResponse().addKeyValues(PORT, port);
      getJsonResponse().addKeyValues(JsonCodec.OUT, out);
      return getJsonResponse().getJson();

    } catch (Exception error) {
      //Big try catch to see if anything at all went wrong
      getJsonResponse().addKeyValues(JsonCodec.ERROR, error.toString());
      return getJsonResponse().getJson();
    }

  }
}
