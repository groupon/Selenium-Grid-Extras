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
package com.groupon.seleniumgridextras.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public class JsonObjectMerger {

  public static JsonObject mergeWithDefaults(JsonObject defaultValues, JsonObject overwriteValues) {
    JsonObject returnValues = mergeCurrentLevel(defaultValues, overwriteValues);

    return returnValues;
  }


  private static JsonObject mergeCurrentLevel(JsonObject defaultValues,
                                              JsonObject overwriteValues) {
    //TODO: clean this up at some point but when you have some free time to waste
    JsonObject returnVal = new JsonObject();

    for (Map.Entry<String, JsonElement> entry : defaultValues.entrySet()) {
      String key = entry.getKey();

      JsonElement itemToAdd;

      if (overwriteValues.has(key)) {
        if (entry.getValue().isJsonObject() && overwriteValues.get(key).isJsonObject()) {

          JsonObject existingDefault = (JsonObject) entry.getValue();
          JsonObject newOverwriteVal = (JsonObject) overwriteValues.get(key);

          itemToAdd = JsonObjectMerger.mergeCurrentLevel(existingDefault, newOverwriteVal);

        } else {
          itemToAdd = overwriteValues.get(key);
        }

      } else {
        itemToAdd = entry.getValue();
      }

      returnVal.add(key, itemToAdd);
    }

    return returnVal;
  }


}
