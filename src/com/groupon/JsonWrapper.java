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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JsonWrapper {


  public static String taskResultToJson(int result, String output, String error) {

    JSONObject resultsHash = new JSONObject();
    JSONArray standardOut = new JSONArray();
    JSONArray standardError = new JSONArray();

    String stdOutLines[] = output.split("\n");
    for(String line: stdOutLines) {
      standardOut.add(line);
    }

    String stdErrorLines[] = error.split("\n");
    for(String line: stdErrorLines) {
      standardError.add(line);
    }



    resultsHash.put("exit_code", result);
    resultsHash.put("standard_out", standardOut);
    resultsHash.put("standard_error", standardError);

    return resultsHash.toString();
  }

  public static Map parseJson(String inputString) {
    Map returnHash = new HashMap();

    JSONParser parser = new JSONParser();
    ContainerFactory containerFactory = new ContainerFactory() {
      public List creatArrayContainer() {
        return new LinkedList();
      }

      public Map createObjectContainer() {
        return new LinkedHashMap();
      }

    };

    try {
      Map json = (Map) parser.parse(inputString, containerFactory);
      Iterator iter = json.entrySet().iterator();
      while (iter.hasNext()) {
        Map.Entry entry = (Map.Entry) iter.next();
        returnHash.put(entry.getKey(), entry.getValue());
      }

    } catch (ParseException error) {
      System.out.println("position: " + error.getPosition());
      System.out.println(error);
    }

    return returnHash;
  }

  public static String fileArrayToJson(File[] inputArray){
    JSONArray fileList = new JSONArray();

    for(File f : inputArray){
      fileList.add(f.toString());
    }

    return fileList.toString();
  }

  public static String filenameToJson(String file){
    JSONArray fileArray = new JSONArray();
    fileArray.add(file);
    return fileArray.toString();
  }


}
