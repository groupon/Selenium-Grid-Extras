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

import com.groupon.seleniumgridextras.utilities.json.JsonResponseBuilder;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

public class Setup extends ExecuteOSTask {

  private static final String CLASSES_TO_EXECUTE = "classes_to_execute";
  private static final String RESULTS = "results";
  private List<ExecuteOSTask> setupTasks;
  private final
  String
      shortDescription =
      "Calls several pre-defined tasks to act as setup before build";

  private static Logger logger = Logger.getLogger(Setup.class);

  public Setup() {
    setEndpoint("/setup");
    setDescription(shortDescription);
    JsonObject params = new JsonObject();
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass("btn");
    setButtonText("setup");
    setEnabledInGui(false);

    addResponseDescription(CLASSES_TO_EXECUTE,
        "List of full canonical classes to execute on Setup");
    addResponseDescription(RESULTS, "Hash object of tasks ran and their results");

  }


  private JsonArray getClassesToRun() {
    JsonArray listOfTasks = new JsonArray();
    for (ExecuteOSTask task : setupTasks) {
      listOfTasks.add(new JsonPrimitive(task.getClass().getSimpleName()));
    }
    return listOfTasks;
  }


  @Override
  public JsonObject execute(String param) {

    try {
      JsonObject results = new JsonObject();

      for (ExecuteOSTask task : setupTasks) {
        results.add(task.getClass().getSimpleName(), task.execute());
      }

      getJsonResponse().addKeyValues(RESULTS, results);

      return getJsonResponse().getJson();
    } catch (Exception error) {
      getJsonResponse().addKeyValues(JsonResponseBuilder.ERROR, error.toString());
      return getJsonResponse().getJson();
    }
  }

  @Override
  public boolean initialize() {
    Boolean initialized = true;
    logger.info("Setup Tasks");
    setupTasks = new LinkedList<ExecuteOSTask>();
    for (String module : RuntimeConfig.getConfig().getSetup()) {
      try {
        ExecuteOSTask task = (ExecuteOSTask) Class.forName(module).newInstance();
        setupTasks.add(task);
        logger.debug("    " + task.getClass().getSimpleName());
      } catch (ClassNotFoundException error) {
        logger.error(module + "   " + error);
        initialized = false;
      } catch (InstantiationException error) {
        logger.error(module + "   " + error);
        initialized = false;
      } catch (IllegalAccessException error) {
        logger.error(module + "   " + error);
        initialized = false;
      }
    }

    if (initialized.equals(false)) {
      printInitilizedFailure();
      System.exit(1);
    }

    getJsonResponse().addKeyValues(CLASSES_TO_EXECUTE, getClassesToRun());
    return true;

  }

}
