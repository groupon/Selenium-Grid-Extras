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


import com.groupon.seleniumgridextras.JsonResponseBuilder;
import com.groupon.seleniumgridextras.RuntimeConfig;
import com.groupon.seleniumgridextras.tasks.ExecuteOSTask;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GetConfig extends ExecuteOSTask {

  public GetConfig(){
    setEndpoint("/config");
    setDescription("Returns JSON view of the full configuration of the Selenium Grid Extras");
    Map<String, String> params = new HashMap();
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass("btn-success");
    setButtonText("Get Config");
    setEnabledInGui(true);
  }

  @Override
  public JsonResponseBuilder getJsonResponse() {

    if (jsonResponse == null) {
      jsonResponse = new JsonResponseBuilder();
      jsonResponse.addKeyDescriptions("config_file", "Config that currently lives saved on file");
      jsonResponse
          .addKeyDescriptions("config_runtime", "Runtime config that currently set in memory");
      jsonResponse.addKeyValues("filename", "Filename from which the config was read");
    }
    return jsonResponse;
  }

  @Override
  public String execute(String param) {

    readConfigFile(RuntimeConfig.getConfigFile());

    getJsonResponse().addKeyValues("config_runtime", RuntimeConfig.getConfig());
    getJsonResponse().addKeyValues("filename", RuntimeConfig.getConfigFile());

    return getJsonResponse().toString();
  }

  private void readConfigFile(String filename) {
    String returnString = "";
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      String line = null;
      while ((line = reader.readLine()) != null) {
        returnString = returnString + line;
      }

      getJsonResponse().addKeyValues("config_file", returnString, false);

    } catch (FileNotFoundException error) {
      getJsonResponse().addKeyValues("error", error.toString());
    } catch (IOException error) {
      getJsonResponse().addKeyValues("error", error.toString());
    }

  }

}
