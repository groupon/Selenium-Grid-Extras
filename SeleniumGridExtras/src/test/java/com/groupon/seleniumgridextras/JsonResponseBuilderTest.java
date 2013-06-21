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

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JsonResponseBuilderTest {

  public JsonResponseBuilder jsonResponseObject;
  public Map defaultDescriptions;

  @Before
  public void setUp() throws Exception {
    jsonResponseObject = new JsonResponseBuilder();

    defaultDescriptions = new HashMap<String, String>();
    defaultDescriptions.put("error", "Error recived during execution of command");
    defaultDescriptions.put("exit_code", "Exit code for operation");
    defaultDescriptions.put("out", "All of the StandardOut received from the system");
  }

  @Test
  public void testAddKeyDescriptions() throws Exception {
    assertEquals(defaultDescriptions, jsonResponseObject.getKeyDescriptions());
    jsonResponseObject.addKeyDescriptions("new", "key");
    defaultDescriptions.put("new", "key");
    assertEquals(defaultDescriptions, jsonResponseObject.getKeyDescriptions());
    assertEquals("{\"exit_code\":0,\"new\":[\"\"],\"error\":[],\"out\":[]}",
                 jsonResponseObject.toString());
  }

  @Test
  public void testAddKeyValuesString() throws Exception {
    jsonResponseObject.addKeyValues("foo", "bar");
    assertEquals("{\"exit_code\":0,\"error\":[],\"foo\":[\"bar\"],\"out\":[]}",
                 jsonResponseObject.toString());
  }

  @Test
  public void testAddKeyValuesBoolean() throws Exception {
    jsonResponseObject.addKeyValues("foo", true);
    assertEquals("{\"exit_code\":0,\"error\":[],\"foo\":true,\"out\":[]}",
                 jsonResponseObject.toString());

    jsonResponseObject.addKeyValues("foo", "true");
    assertEquals("{\"exit_code\":0,\"error\":[],\"foo\":[\"true\"],\"out\":[]}",
                 jsonResponseObject.toString());
  }

  @Test
  public void testAddKeyValuesMap() throws Exception {
    Map<String, String> bar = new HashMap<String, String>();
    jsonResponseObject.addKeyValues("foo", bar);
    assertEquals("{\"exit_code\":0,\"error\":[],\"foo\":{},\"out\":[]}",
                 jsonResponseObject.toString());
  }

  @Test
  public void testAddKeyValuesInt() throws Exception {
    jsonResponseObject.addKeyValues("foo", 1);
    assertEquals("{\"exit_code\":0,\"error\":[],\"foo\":1,\"out\":[]}",
                 jsonResponseObject.toString());

    jsonResponseObject.addKeyValues("foo", "1");
    assertEquals("{\"exit_code\":0,\"error\":[],\"foo\":[\"1\"],\"out\":[]}",
                 jsonResponseObject.toString());
  }

  @Test
  public void testAddKeyValuesArray() throws Exception {
    List foo = new LinkedList<String>();
    foo.add("a");
    foo.add("b");
    jsonResponseObject.addKeyValues("foo", foo);
    assertEquals("{\"exit_code\":0,\"error\":[],\"foo\":[\"a\",\"b\"],\"out\":[]}",
                 jsonResponseObject.toString());
  }

  @Test
  public void testCallingToStringMultipleTimesDoesNotClearDescriptions() throws Exception {
    assertEquals("{\"exit_code\":0,\"error\":[],\"out\":[]}", jsonResponseObject.toString());
  }

  @Test
  public void testStringValueWithAndWithoutLineSplit() throws Exception {
    jsonResponseObject.addKeyValues("no_split", "all\none\nline\nplease", false);
    assertEquals(
        "{\"exit_code\":0,\"error\":[],\"no_split\":[\"all\\none\\nline\\nplease\"],\"out\":[]}",
        jsonResponseObject.toString());

    jsonResponseObject.addKeyValues("with_split", "all\non\nnew\nlines\nplease", true);
    assertEquals(
        "{\"exit_code\":0,\"error\":[],\"with_split\":[\"all\",\"on\",\"new\",\"lines\",\"please\"],\"out\":[]}",
        jsonResponseObject.toString());

    jsonResponseObject.addKeyValues("with_split_default", "all\non\nnew\nlines\nplease", true);
    assertEquals(
        "{\"exit_code\":0,\"with_split_default\":[\"all\",\"on\",\"new\",\"lines\",\"please\"],\"error\":[],\"out\":[]}",
        jsonResponseObject.toString());
  }


  @Test
  public void testToStringDefault() throws Exception {

    final String descriptionMessage = "should still be here when done";

    jsonResponseObject.addKeyDescriptions("test", descriptionMessage);
    jsonResponseObject.toString();
    jsonResponseObject.toString();
    assertEquals(descriptionMessage, jsonResponseObject.getKeyDescriptions().get("test"));
  }

  @Test
  public void testAddingErrorMessageAutoChangesExitCode() throws Exception {
    assertEquals("{\"exit_code\":0,\"error\":[],\"out\":[]}", jsonResponseObject.toString());
    jsonResponseObject.addKeyValues("error", "this is an error");
    assertEquals("{\"exit_code\":1,\"error\":[\"this is an error\"],\"out\":[]}",
                 jsonResponseObject.toString());
  }

  @Test
  public void testGetKeyDescriptions() throws Exception {
    assertEquals(defaultDescriptions, jsonResponseObject.getKeyDescriptions());
  }
}
