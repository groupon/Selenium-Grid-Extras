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

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MoveMouse extends ExecuteOSTask {

  public MoveMouse() {
    setEndpoint("/move_mouse");
    setDescription("Moves the computers mouse to x and y location. (Default 0,0)");
    JsonObject params = new JsonObject();
    params.addProperty("x", "X - Coordinate");
    params.addProperty("y", "Y - Coordinate");
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass("btn-succes");
    setButtonText("Move mouse");

    addResponseDescription("x", "Current X postion of the mouse");
    addResponseDescription("y", "Current Y postion of the mouse");

    setEnabledInGui(true);
  }

  @Override
  public JsonObject execute(Map<String, String> parameter) {

    int x = 0;
    int y = 0;

    if (!parameter.isEmpty() && parameter.containsKey("x") && parameter.containsKey("y")) {
      x = Integer.parseInt(parameter.get("x"));
      y = Integer.parseInt(parameter.get("y"));
    }

    return moveMouse(x, y);
  }


  @Override
  public JsonObject execute() {
    return execute(new HashMap<String, String>());
  }

  private JsonObject moveMouse(Integer x, Integer y) {
    try {
      Robot moveMouse = new Robot();
      moveMouse.mouseMove(x, y);
      getJsonResponse().addKeyValues("x", x);
      getJsonResponse().addKeyValues("y", y);
      return getJsonResponse().getJson();
    } catch (AWTException error) {
      getJsonResponse().addKeyValues("error", error.toString());
      return getJsonResponse().getJson();
    }
  }

  @Override
  public boolean initialize() {
    if (allDependenciesLoaded() && !java.awt.GraphicsEnvironment.isHeadless()) {
      printInitilizedSuccessAndRegisterWithAPI();
      return true;
    } else {
      printInitilizedFailure();
      return false;
    }
  }

}
