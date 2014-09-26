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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import com.groupon.seleniumgridextras.utilities.json.JsonResponseBuilder;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JsonResponseBuilderTest {

  public JsonResponseBuilder jsonResponseObject;
  public JsonObject defaultDescriptions;

  @Before
  public void setUp() throws Exception {
    jsonResponseObject = new JsonResponseBuilder();

    defaultDescriptions = new JsonObject();
    defaultDescriptions.addProperty("error", "Error received during execution of command");
    defaultDescriptions.addProperty("exit_code", "Exit code for operation");
    defaultDescriptions.addProperty("out", "All of the StandardOut received from the system");
  }

  @Test
  public void testAddKeyDescriptions() throws Exception {
    assertEquals(defaultDescriptions, jsonResponseObject.getKeyDescriptions());
    jsonResponseObject.addKeyDescriptions("new", "key");
    defaultDescriptions.addProperty("new", "key");
    assertEquals(defaultDescriptions, jsonResponseObject.getKeyDescriptions());
    assertEquals(new JsonParser().parse("{\"exit_code\":0,\"new\":[\"\"],\"error\":[],\"out\":[]}"),
        jsonResponseObject.getJson());
  }

  @Test(expected = RuntimeException.class)
  public void testExceptionOnKeyNotExistingForString() throws Exception {
    jsonResponseObject.addKeyValues("foo", "bar");
  }

  @Test(expected = RuntimeException.class)
  public void testExceptionOnKeyNotExistingForBoolean() throws Exception {
    jsonResponseObject.addKeyValues("foo", true);
  }

  @Test(expected = RuntimeException.class)
  public void testExceptionOnKeyNotExistingForMap() throws Exception {
    Map foo = new HashMap();
    jsonResponseObject.addKeyValues("foo", foo);
  }

  @Test(expected = RuntimeException.class)
  public void testExceptionOnKeyNotExistingForInt() throws Exception {
    jsonResponseObject.addKeyValues("foo", 1);
  }

  @Test(expected = RuntimeException.class)
  public void testExceptionOnKeyNotExistingForList() throws Exception {
    List foo = new LinkedList();
    jsonResponseObject.addKeyValues("foo", foo);
  }

  @Test
  public void testAddKeyValuesString() throws Exception {
    jsonResponseObject.addKeyDescriptions("foo", "test");
    jsonResponseObject.addKeyValues("foo", "bar");
    assertEquals(new JsonParser().parse("{\"exit_code\":0,\"error\":[],\"foo\":[\"bar\"],\"out\":[]}"),
        jsonResponseObject.getJson());
  }

  @Test
  public void testAddKeyValuesBoolean() throws Exception {
    jsonResponseObject.addKeyDescriptions("foo", "test");
    jsonResponseObject.addKeyValues("foo", true);
    assertEquals(new JsonParser().parse("{\"exit_code\":0,\"error\":[],\"foo\":true,\"out\":[]}"),
        jsonResponseObject.getJson());

    jsonResponseObject.addKeyValues("foo", "true");
    assertEquals(new JsonParser().parse("{\"exit_code\":0,\"error\":[],\"foo\":[\"true\"],\"out\":[]}"),
        jsonResponseObject.getJson());
  }

  @Test
  public void testAddKeyValuesMap() throws Exception {
    jsonResponseObject.addKeyDescriptions("foo", "test");
    Map<String, String> bar = new HashMap<String, String>();
    bar.put("a", "b");
    bar.put("c", "d");
    jsonResponseObject.addKeyValues("foo", bar);
    assertEquals(new JsonParser().parse("{\"exit_code\":0,\"out\":[],\"error\":[],\"foo\":{\"c\":\"d\",\"a\":\"b\"}}"),
        jsonResponseObject.getJson());
  }

  @Test
  public void testAddKeyValuesInt() throws Exception {
    jsonResponseObject.addKeyDescriptions("foo", "test");
    jsonResponseObject.addKeyValues("foo", 1);
    assertEquals(new JsonParser().parse("{\"exit_code\":0,\"error\":[],\"foo\":1,\"out\":[]}"),
        jsonResponseObject.getJson());

    jsonResponseObject.addKeyValues("foo", "1");
    assertEquals(new JsonParser().parse("{\"exit_code\":0,\"error\":[],\"foo\":[\"1\"],\"out\":[]}"),
        jsonResponseObject.getJson());
  }

  @Test
  public void testAddKeyValuesArray() throws Exception {
    jsonResponseObject.addKeyDescriptions("foo", "test");
    JsonArray foo = new JsonArray();
    foo.add(new JsonPrimitive("a"));
    foo.add(new JsonPrimitive("b"));
    jsonResponseObject.addKeyValues("foo", foo);
    assertEquals(new JsonParser().parse("{\"exit_code\":0,\"error\":[],\"foo\":[\"a\",\"b\"],\"out\":[]}"),
        jsonResponseObject.getJson());
  }

  @Test
  public void testCallingToStringMultipleTimesDoesNotClearDescriptions() throws Exception {
    assertEquals(new JsonParser().parse("{\"exit_code\":0,\"error\":[],\"out\":[]}"), jsonResponseObject.getJson());
  }

  @Test
  public void testStringValueWithAndWithoutLineSplit() throws Exception {
    jsonResponseObject.addKeyDescriptions("no_split", "test");
    jsonResponseObject.addKeyValues("no_split", "all\none\nline\nplease", false);
    assertEquals(
        new JsonParser().parse("{\"exit_code\":0,\"error\":[],\"no_split\":[\"all\\none\\nline\\nplease\"],\"out\":[]}"),
        jsonResponseObject.getJson());

    jsonResponseObject.addKeyDescriptions("with_split", "test");
    jsonResponseObject.addKeyValues("with_split", "all\non\nnew\nlines\nplease", true);
    assertEquals(
        new JsonParser().parse("{\"exit_code\":0,\"error\":[],\"with_split\":[\"all\",\"on\",\"new\",\"lines\",\"please\"],\"out\":[]}"),
        jsonResponseObject.getJson());

    jsonResponseObject.addKeyDescriptions("with_split_default", "test");
    jsonResponseObject.addKeyValues("with_split_default", "all\non\nnew\nlines\nplease", true);
    assertEquals(
        new JsonParser().parse("{\"exit_code\":0,\"with_split_default\":[\"all\",\"on\",\"new\",\"lines\",\"please\"],\"error\":[],\"out\":[]}"),
        jsonResponseObject.getJson());
  }


  @Test
  public void testToStringDefault() throws Exception {

    final String descriptionMessage = "should still be here when done";

    jsonResponseObject.addKeyDescriptions("test", descriptionMessage);
    jsonResponseObject.toString();
    assertEquals(descriptionMessage, jsonResponseObject.getKeyDescriptions().get("test").getAsString());
  }

  @Test
  public void testAddingErrorMessageAutoChangesExitCode() throws Exception {
    assertEquals(new JsonParser().parse("{\"exit_code\":0,\"error\":[],\"out\":[]}"), jsonResponseObject.getJson());
    jsonResponseObject.addKeyValues("error", "this is an error");
    assertEquals(new JsonParser().parse("{\"exit_code\":1,\"error\":[\"this is an error\"],\"out\":[]}"),
        jsonResponseObject.getJson());
  }

  @Test
  public void testGetKeyDescriptions() throws Exception {
    assertEquals(defaultDescriptions, jsonResponseObject.getKeyDescriptions());
  }
}
