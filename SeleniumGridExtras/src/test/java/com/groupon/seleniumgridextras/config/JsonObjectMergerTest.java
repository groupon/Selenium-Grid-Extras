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


import com.google.gson.Gson;
import com.google.gson.JsonArray;

import com.google.gson.JsonObject;

import com.google.gson.JsonPrimitive;

import org.junit.Before;
import org.junit.Test;


import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;


//These tests are ugly and stupid, but i'm so tired and sleepy right now and just need them to work

public class JsonObjectMergerTest {

  public JsonObject defaultObject;
  public JsonObject overwriteObject;
  public JsonObject overwriteArray;


  @Before
  public void setUp() throws Exception {
    defaultObject = new JsonObject();
    overwriteObject = new JsonObject();
    overwriteArray = new JsonObject();

    defaultObject.addProperty("a", 1);
    defaultObject.addProperty("b", 2);

    JsonArray array = new JsonArray();

    array.add(new JsonPrimitive(1));
    array.add(new JsonPrimitive("a"));

    defaultObject.add("c", array);

    overwriteObject.addProperty("a", "string");
    overwriteObject.addProperty("b", 5);

    JsonArray oArray = new JsonArray();

    oArray.add(new JsonPrimitive(2));
    oArray.add(new JsonPrimitive("b"));

    overwriteArray.add("c", oArray);

  }

  @Test
  public void testMergeNoNewValuesInOverwrite() throws Exception {

    JsonObject emptyObject = new JsonObject();

    JsonObject actual = JsonObjectMerger.mergeWithDefaults(defaultObject, emptyObject);

    assertEquals(defaultObject, actual);

  }

  @Test
  public void testMergePrimative() throws Exception {
    JsonObject expected = new JsonObject();
    expected.addProperty("a", "string");
    expected.addProperty("b", 5);

    JsonArray array = new JsonArray();

    array.add(new JsonPrimitive(1));
    array.add(new JsonPrimitive("a"));
    expected.add("c", array);

    JsonObject actual = JsonObjectMerger.mergeWithDefaults(defaultObject, overwriteObject);

    assertEquals(actual, expected);
  }

  @Test
  public void testMergeArray() throws Exception {

    JsonObject expected = new JsonObject();
    expected.addProperty("a", 1);
    expected.addProperty("b", 2);

    JsonArray array = new JsonArray();

    array.add(new JsonPrimitive(2));
    array.add(new JsonPrimitive("b"));

    expected.add("c", array);

    JsonObject actual = JsonObjectMerger.mergeWithDefaults(defaultObject, overwriteArray);

    assertEquals(expected, actual);
  }

  @Test
  public void testMergeJsonObject() throws Exception {

    JsonObject toAddToDefault = new JsonObject();
    toAddToDefault.addProperty("aa", 1);
    toAddToDefault.addProperty("bb", "bb");
    defaultObject.add("d", toAddToDefault);


    JsonObject expected = new JsonObject();
    expected.addProperty("a", 1);
    expected.addProperty("b", 2);

    JsonArray array = new JsonArray();

    array.add(new JsonPrimitive(2));
    array.add(new JsonPrimitive("b"));

    expected.add("c", array);

    JsonObject addToExpected = new JsonObject();
    addToExpected.addProperty("aa", 2);
    addToExpected.addProperty("bb", "dd");

    expected.add("d", addToExpected);

    JsonObject addToOverwrite = new JsonObject();
    addToOverwrite.addProperty("aa", 2);
    addToOverwrite.addProperty("bb", "dd");
    overwriteArray.add("d", addToOverwrite);


    JsonObject actual = JsonObjectMerger.mergeWithDefaults(defaultObject, overwriteArray);

    assertEquals(expected, actual);


  }

  @Test
  public void testMergeNestedJsonObject() throws Exception {

    JsonObject startingObject = new JsonObject();

    startingObject.add("a", new JsonPrimitive(1));

    JsonObject firstLevelObject = new JsonObject();
    JsonObject secondLevelObject = new JsonObject();

    secondLevelObject.add("aaa", new JsonPrimitive(1));
    secondLevelObject.add("bbb", new JsonPrimitive("string"));
    secondLevelObject.add("ccc", new JsonPrimitive("will not change"));
    firstLevelObject.add("aa", secondLevelObject);
    startingObject.add("b", firstLevelObject);

    //////////////////////////////////////////////////////////////////////////////////////////////

    JsonObject overwrite = new JsonObject();

    JsonObject firstLevelOverwrite = new JsonObject();
    JsonObject secondLevelOverwrite = new JsonObject();

    secondLevelOverwrite.add("aaa", new JsonPrimitive(2));
    secondLevelOverwrite.add("bbb", new JsonPrimitive("overwritten"));

    firstLevelOverwrite.add("aa", secondLevelOverwrite);
    overwrite.add("b", firstLevelOverwrite);

    //////////////////////////////////////////////////////////////////////////////////////////////

    JsonObject expecteResult = new JsonObject();
    JsonObject firstLevelExpected = new JsonObject();
    JsonObject secondLevelExpected = new JsonObject();

    secondLevelExpected.add("aaa", new JsonPrimitive(2));
    secondLevelExpected.add("bbb", new JsonPrimitive("overwritten"));
    secondLevelExpected.add("ccc", new JsonPrimitive("will not change"));

    firstLevelExpected.add("aa", secondLevelExpected);

    expecteResult.add("a", new JsonPrimitive(1));
    expecteResult.add("b", firstLevelExpected);

    //////////////////////////////////////////////////////////////////////////////////////////////

    System.out.println(expecteResult);

    assertEquals(expecteResult, JsonObjectMerger.mergeWithDefaults(startingObject, overwrite));


  }

  @Test
  public void testDima() throws Exception {

    String dima = "{\n"
                  + "  \"capabilities\":\n"
                  + "      [\n"
                  + "        {\n"
                  + "          \"browserName\": \"*firefox\",\n"
                  + "          \"maxInstances\": 3,\n"
                  + "          \"seleniumProtocol\": \"Selenium\"\n"
                  + "        }\n"
                  + "      ],\n"
                  + "  \"configuration\":\n"
                  + "  {\n"
                  + "    \"proxy\": \"org.openqa.grid.selenium.proxy.DefaultRemoteProxy\",\n"
                  + "    \"maxSession\": 5,\n"
                  + "    \"port\": 5555,\n"
                  + "    \"host\": \"127.0.0.1\",\n"
                  + "    \"register\": true,\n"
                  + "    \"registerCycle\": 5000,\n"
                  + "    \"hubPort\": 4444,\n"
                  + "    \"hubHost\": \"127.0.0.1\"\n"
                  + "  }\n"
                  + "}";

    Config foo = new Config();

    foo.toPrettyJsonString();
    Gson gson = new Gson();

    Map newHash = new HashMap();

    newHash.put("dima", "was here");
    newHash.put("configuration", new HashMap());
    Map config = (HashMap) newHash.get("configuration");
    config.put("proxy", "12334");

    Map bar = gson.fromJson(dima,  HashMap.class);

    bar.putAll(newHash);



    System.out.println(bar);

  }




}
