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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.ExecuteCommand;
import com.groupon.seleniumgridextras.OSChecker;

public class KillAllChrome extends KillAllByName {

  public KillAllChrome() {
    setEndpoint("/kill_chrome");
    setDescription("Executes os level kill command on all instance of Google Chrome");
    JsonObject params = new JsonObject();
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass("btn-danger");
    setButtonText("Kill all chrome");
    setEnabledInGui(true);
  }


  @Override
  public JsonObject execute(String param) {

    if (OSChecker.isWindows()) {
      return killChromeOnWindows();
    } else {
      return killChromeOnLinux();
    }
  }


  private JsonObject killChromeOnLinux() {
    return ExecuteCommand.execRuntime(getLinuxCommand("[Cc]hrome"));
  }

  private JsonObject killChromeOnWindows() {

    JsonObject killBrowserResult = ExecuteCommand.execRuntime(getWindowsKillCommand("chrome.exe"));

    JsonObject killDriverResult = ExecuteCommand.execRuntime(getWindowsKillCommand("chromedriver.exe"));

    JsonArray response = new JsonArray();
    response.add(killBrowserResult);
    response.add(killDriverResult);

    if (killBrowserResult.get("exit_code").equals("0") && killDriverResult.get("exit_code")
        .equals("0")) {
      getJsonResponse().addKeyValues("out", response);
    } else {
      getJsonResponse().addKeyValues("error", response);
    }

    return getJsonResponse().getJson();

  }
}
