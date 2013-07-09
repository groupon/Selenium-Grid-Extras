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
import com.groupon.seleniumgridextras.ExtrasEndPoint;
import com.groupon.seleniumgridextras.OSChecker;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class ExecuteOSTask extends ExtrasEndPoint {

  final private
  String
      notImplementedError =
      "This task was not implemented on " + OSChecker.getOSName();
  public boolean waitToFinishTask = true;

  public JsonObject execute() {
    return execute("");
  }

  public JsonObject execute(Map<String, String> parameter) {
    if (!parameter.isEmpty() && parameter.containsKey("parameter")) {
      return execute(parameter.get("parameter").toString());
    } else {
      return execute();
    }
  }


  public JsonObject execute(String parameter) {

    String command = OSChecker.isWindows() ? getWindowsCommand()
        : OSChecker.isMac() ? getMacCommand() : getLinuxCommand();

    return ExecuteCommand.execRuntime(command + parameter, waitToFinishTask);
  }

  public String getWindowsCommand(String parameter) {

    getJsonResponse().addKeyValues("error",
        notImplementedError + " " + this.getClass().getCanonicalName());

    return getJsonResponse().toString();

  }

  public String getWindowsCommand() {
    return getWindowsCommand("");
  }

  public String getLinuxCommand(String parameter) {
    getJsonResponse().addKeyValues("error",
        notImplementedError + " " + this.getClass().getCanonicalName());

    return getJsonResponse().toString();

  }

  public String getLinuxCommand() {
    return getLinuxCommand("");
  }

  public String getMacCommand(String parameter) {
    return getLinuxCommand(parameter);
  }

  public String getMacCommand() {
    return getLinuxCommand();
  }

  public boolean initialize() {

    if (allDependenciesLoaded()) {
      printInitilizedSuccessAndRegisterWithAPI();
      return true;
    } else {
      printInitilizedFailure();
      return false;
    }

  }

  public void printInitilizedSuccessAndRegisterWithAPI() {
    System.out.println(
        "Y " + this.getClass().getSimpleName() + " - " + this.getEndpoint() + " - " + this
            .getDescription());

    registerApi();
  }

  public void printInitilizedFailure() {
    System.out.println("N " + this.getClass().getSimpleName());
  }

  public Boolean allDependenciesLoaded() {
    Boolean returnValue = true;

    for (String module : getDependencies()) {
      if (RuntimeConfig.getConfig().checkIfModuleEnabled(module) && returnValue) {

      } else {
        System.out.println("  " + this.getClass().getSimpleName() + " depends on " + module
            + " but it is not activated");
        returnValue = false;
      }
    }

    return returnValue;
  }

  public List<String> getDependencies() {
    List<String> dependencies = new LinkedList();
    return dependencies;
  }

}
