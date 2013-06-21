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

package com.groupon;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JsonResponseBuilder {


  private Map<String, String> keyDescriptions;
  private JSONObject keyValues;

  public JsonResponseBuilder() {
    keyDescriptions = new HashMap<String, String>();
    keyDescriptions.put("error", "Error recived during execution of command");
    keyDescriptions.put("exit_code", "Exit code for operation");
    keyDescriptions.put("out", "All of the StandardOut received from the system");
    clearValues();
  }

  private void clearValues(){

    keyValues = new JSONObject();

    List<String> out = new LinkedList();
    List<String> error = new LinkedList();

    addKeyValues("exit_code", 0);
    addKeyValues("out", out);
    addKeyValues("error", error);
  }


  public void addKeyDescriptions(String key, String description) {
    keyDescriptions.put(key, description);
    addKeyValues(key, "");
  }

  public void addKeyValues(String key, String value) {

    List<String> valueArray = convertLineToArray(value);

    if (key.equals("out")) {
      addKeyValues(key, valueArray);
    } else if (key.equals("error")) {
      addKeyValues(key, valueArray);
    } else {
      keyValues.put(key, valueArray);
    }


  }

  public void addKeyValues(String key, Boolean value) {
    keyValues.put(key, value);
  }

  public void addKeyValues(String key, Map value) {
    keyValues.put(key, value);
  }

  public void addKeyValues(String key, int value) {
    keyValues.put(key, value);
  }

  public void addKeyValues(String key, List<String> value) {
    keyValues.put(key, value);
  }

  public String toString() {
    String tempString = keyValues.toJSONString();
    clearValues();
    return tempString;
  }

  protected Map<String, String> getKeyDescriptions() {
    return keyDescriptions;
  }

  private List<String> convertLineToArray(String input) {
    List output = new LinkedList<String>();

    String stdOutLines[] = input.split("\n");
    for (String line : stdOutLines) {
      output.add(line);
    }

    return output;
  }


}
