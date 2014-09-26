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

import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;

public class GetConfig extends ExecuteOSTask {

  public GetConfig() {
    setEndpoint(TaskDescriptions.Endpoints.CONFIG);
    setDescription(TaskDescriptions.Description.CONFIG);
    JsonObject params = new JsonObject();
    setAcceptedParams(params);
    setRequestType(TaskDescriptions.HTTP.GET);
    setResponseType(TaskDescriptions.HTTP.JSON);
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass(TaskDescriptions.UI.BTN_SUCCESS);
    setButtonText(TaskDescriptions.UI.ButtonText.CONFIG);
    setEnabledInGui(true);

    addResponseDescription(JsonCodec.Config.CONFIG_FILE, "Config that currently lives saved on file");
    addResponseDescription(JsonCodec.Config.CONFIG_RUNTIME, "Runtime config that currently set in memory");
    addResponseDescription(JsonCodec.Config.FILENAME, "Filename from which the config was read");

    getJsonResponse().addKeyValues(JsonCodec.Config.FILENAME, RuntimeConfig.getConfigFile());
  }

  @Override
  public JsonObject execute(String param) {

    getJsonResponse().addKeyValues(JsonCodec.Config.CONFIG_RUNTIME, RuntimeConfig.getConfig().asJsonObject());
    getJsonResponse().addKeyValues(JsonCodec.Config.FILENAME, RuntimeConfig.getConfigFile());

    return getJsonResponse().getJson();
  }
}
