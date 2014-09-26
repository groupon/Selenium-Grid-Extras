
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
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;

import java.util.Map;

public class GetInfoForPort extends ExecuteOSTask {

  public GetInfoForPort() {
    setEndpoint(TaskDescriptions.Endpoints.PORT_INFO);
    setDescription(TaskDescriptions.Description.PORT_INFO);
    JsonObject params = new JsonObject();
    params.addProperty(JsonCodec.OS.PORT, "(Required) Port to be used");
    setAcceptedParams(params);
    setRequestType(TaskDescriptions.HTTP.GET);
    setResponseType(TaskDescriptions.HTTP.JSON);
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass(TaskDescriptions.UI.BTN_SUCCESS);
    setButtonText(TaskDescriptions.UI.ButtonText.PORT_INFO);
    setEnabledInGui(true);

    addResponseDescription(JsonCodec.OS.PROCESS_NAME, "Process name/type (ie java, ruby, etc..)");
    addResponseDescription(JsonCodec.OS.PID, "Process ID");
    addResponseDescription(JsonCodec.OS.USER, "User who is running process");
    addResponseDescription(JsonCodec.OS.PORT, "Port searched for");
  }


  @Override
  public JsonObject getAcceptedParams() {
    JsonObject params = new JsonObject();
    params.addProperty(JsonCodec.OS.PORT, "(Required) Port to be used");
    return params;
  }

  @Override
  public JsonObject execute() {
    getJsonResponse().addKeyValues(JsonCodec.ERROR, "Port parameter is required");
    return getJsonResponse().getJson();
  }

  @Override
  public JsonObject execute(Map<String, String> parameter) {

    if (!parameter.isEmpty() && parameter.containsKey(JsonCodec.OS.PORT)) {
      return execute(parameter.get(JsonCodec.OS.PORT).toString());
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
        process = portInfo.get(JsonCodec.OS.PROCESS).getAsString();
      } catch (NullPointerException error) {
      }
      try {
        pid = portInfo.get(JsonCodec.OS.PID).getAsString();
      } catch (NullPointerException error) {
      }
      try {
        user = portInfo.get(JsonCodec.OS.USER).getAsString();
      } catch (NullPointerException error) {
      }

      try {
        out = portInfo.get(JsonCodec.OUT).getAsString();
      } catch (NullPointerException error) {
      }

      getJsonResponse().addKeyValues(JsonCodec.OS.PROCESS_NAME, process);
      getJsonResponse().addKeyValues(JsonCodec.OS.PID, pid);
      getJsonResponse().addKeyValues(JsonCodec.OS.USER, user);
      getJsonResponse().addKeyValues(JsonCodec.OS.PORT, port);
      getJsonResponse().addKeyValues(JsonCodec.OUT, out);
      return getJsonResponse().getJson();

    } catch (Exception error) {
      //Big try catch to see if anything at all went wrong
      getJsonResponse().addKeyValues(JsonCodec.ERROR, error.toString());
      return getJsonResponse().getJson();
    }

  }
}
