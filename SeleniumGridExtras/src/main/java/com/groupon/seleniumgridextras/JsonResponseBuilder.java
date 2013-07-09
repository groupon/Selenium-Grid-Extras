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

package com.groupon.seleniumgridextras;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.List;
import java.util.Map;

public class JsonResponseBuilder {


  private JsonObject keyDescriptions;
  private JsonObject keyValues;

  public JsonResponseBuilder() {
    keyDescriptions = new JsonObject();
    keyDescriptions.addProperty("error", "Error received during execution of command");
    keyDescriptions.addProperty("exit_code", "Exit code for operation");
    keyDescriptions.addProperty("out", "All of the StandardOut received from the system");
    clearValues();
  }

  private void clearValues() {

    keyValues = new JsonObject();

    JsonArray out = new JsonArray();
    JsonArray error = new JsonArray();

    addKeyValues("exit_code", 0);
    addKeyValues("out", out);
    addKeyValues("error", error);
  }

  public void addKeyDescriptions(String key, String description) {
    keyDescriptions.addProperty(key, description);
    addKeyValues(key, "");
  }

  public void addKeyValues(String key, String value) {
    addKeyValues(key, value, true);
  }

  public void addKeyValues(String key, String value, Boolean splitLineToArray) {
    checkIfKeyDescriptionExist(key);
    JsonArray valueArray = new JsonArray();
    if (splitLineToArray) {
      valueArray = convertLineToArray(value);
    } else {
      valueArray.add(new JsonPrimitive(value));
    }

    if (key.equals("out")) {
      addKeyValues(key, valueArray);
    } else if (key.equals("error")) {
      addKeyValues("exit_code", 1);
      addKeyValues(key, valueArray);
    } else {
      keyValues.add(key, valueArray);
    }

  }

  public void addKeyValues(String key, Boolean value) {
    checkIfKeyDescriptionExist(key);
    keyValues.addProperty(key, value);
  }

  public void addKeyValues(String key, JsonArray value) {
    checkIfKeyDescriptionExist(key);
    keyValues.add(key, value);
  }

  public void addKeyValues(String key, JsonObject value) {
    checkIfKeyDescriptionExist(key);
    keyValues.add(key, value);
  }

  public void addKeyValues(String key, Map value) {
    checkIfKeyDescriptionExist(key);
    keyValues.addProperty(key, new Gson().toJson(value));
  }

  public void addKeyValues(String key, int value) {
    checkIfKeyDescriptionExist(key);
    keyValues.addProperty(key, value);
  }

  public void addKeyValues(String key, List<String> value) {
    checkIfKeyDescriptionExist(key);

    JsonArray valueArray = new JsonArray();
    for (String item : value){
      valueArray.add(new JsonPrimitive(item));
    }
    keyValues.add(key, valueArray);
  }

  public String toString() {
    String values = keyValues.toString();
    clearValues();
    return values;
  }

  public JsonObject getJson() {
    JsonObject values = keyValues;
    clearValues();
    return values;
  }

  public JsonObject getKeyDescriptions() {
    return keyDescriptions;
  }

  private void checkIfKeyDescriptionExist(String key) {
    if (!keyDescriptions.has(key)) {
      throw new RuntimeException(
          "You cannot add an entry to Json Response without adding description for it first");
    }
  }

  private JsonArray convertLineToArray(String input) {
    JsonArray output = new JsonArray();

    String stdOutLines[] = input.split("\n");
    for (String line : stdOutLines) {
      output.add(new JsonPrimitive(line));
    }
    return output;
  }


}
