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
import com.google.gson.JsonPrimitive;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

import java.util.LinkedList;
import java.util.List;

public class Teardown extends ExecuteOSTask {

  private List<ExecuteOSTask> teardownTasks;

  public Teardown() {
    setEndpoint("/teardown");
    setDescription("Calls several pre-defined tasks to act as teardown after build");
    JsonObject params = new JsonObject();
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass("btn-info");
    setButtonText("Teardown");
    setEnabledInGui(false);

    addResponseDescription("classes_to_execute",
        "List of full canonical classes to execute on Tear-Down");
    addResponseDescription("results", "Hash object of tasks ran and their results");
  }

  private JsonArray getClassesToRun() {
    JsonArray listOfTasks = new JsonArray();
    for (ExecuteOSTask task : teardownTasks) {
      listOfTasks.add(new JsonPrimitive(task.getClass().getSimpleName()));
    }

    return listOfTasks;
  }

  @Override
  public JsonObject execute(String param) {

    try {
      JsonObject r = new JsonObject();
      for (ExecuteOSTask task : teardownTasks) {
        r.add(task.getClass().getSimpleName(), task.execute());
      }

      getJsonResponse().addKeyValues("results", r);

      return getJsonResponse().getJson();
    } catch (Exception error) {
      getJsonResponse().addKeyValues("error", error.toString());
      return getJsonResponse().getJson();
    }
  }


  @Override
  public boolean initialize() {
    Boolean initialized = true;
    System.out.println("Tear-Down Tasks");
    teardownTasks = new LinkedList<ExecuteOSTask>();
    for (String module : RuntimeConfig.getConfig().getTeardown()) {
      try {
        ExecuteOSTask task = (ExecuteOSTask) Class.forName(module).newInstance();
        teardownTasks.add(task);
        System.out.println("    " + task.getClass().getSimpleName());
      } catch (ClassNotFoundException error) {
        System.out.println(module + "   " + error);
        initialized = false;
      } catch (InstantiationException error) {
        System.out.println(module + "   " + error);
        initialized = false;
      } catch (IllegalAccessException error) {
        System.out.println(module + "   " + error);
        initialized = false;
      }
    }

    if (initialized.equals(false)) {
      printInitilizedFailure();
      System.exit(1);
    }


    getJsonResponse().addKeyValues("classes_to_execute", getClassesToRun());
    return true;

  }

}
