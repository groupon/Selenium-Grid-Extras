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

import com.groupon.seleniumgridextras.ExecuteCommand;

import java.util.Map;

public class IEProtectedMode extends ExecuteOSTask {
  public IEProtectedMode() {
    setEndpoint("/ie_protected_mode");
    setDescription("Changes protected mode for Internet Explorer on/off. No param for current status");
    JsonObject params = new JsonObject();
    params.addProperty("enabled", "(Optional)1 for enabling protected mode for all zones, 0 for disabling");
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass("btn-danger");
    setButtonText("Enanble/Disable Protected Mode");
    setEnabledInGui(true);
  }

  public final String getPropertyCommand = "powershell.exe /c \"Get-ItemProperty -Path 'HKCU:\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\%03d' -Name 2500 | %{ Write-Host $_.'2500' }\"";
  public final String getSetPropertyCommand = "powershell.exe /c \"Set-ItemProperty -Path 'HKCU:\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\%03d' -Name 2500 -Value %03d \"";


  public String getSetPropertyCommand(){
    return getSetPropertyCommand;
  }

  public String getGetPropertyCommand(){
    return getPropertyCommand;
  }


  @Override
  public String getWindowsCommand(String parameter) {
    return getWindowsKillCommand(parameter);
  }


  @Override
  public String getLinuxCommand(String parameter) {
    return getLinuxKillCommand(parameter);
  }

  @Override
  public JsonObject execute(Map<String, String> parameter) {
    if (!parameter.isEmpty() && parameter.containsKey("enabled")) {
      return execute(parameter.get("enabled").toString());
    }
    return execute();
  }

  @Override
  public JsonObject execute(){
    return ExecuteCommand.execRuntime("");
  }

  @Override
  public JsonObject execute(String status){
    return ExecuteCommand.execRuntime("");
  }


  protected String getWindowsKillCommand(String parameter) {
    return "taskkill -F -IM " + parameter;
  }

  protected String getLinuxKillCommand(String parameter) {
    return "killall -v -m " + parameter;
  }

}
