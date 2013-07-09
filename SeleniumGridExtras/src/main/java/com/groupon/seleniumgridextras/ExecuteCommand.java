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

import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;


public class ExecuteCommand {
  public static JsonObject execRuntime(String cmd) {
    return execRuntime(cmd, true);
  }

  public static JsonObject execRuntime(String cmd, boolean waitToFinish) {
    System.out.println("Starting to execute - " + cmd);

    JsonResponseBuilder jsonResponse = new JsonResponseBuilder();

    Process process;

    try {
      process = Runtime.getRuntime().exec(cmd);
    } catch (IOException e) {
      jsonResponse.addKeyValues("error", "Problems in running " + cmd + "\n" + e.toString());
      return jsonResponse.getJson();
    }

    int exitCode;
    if (waitToFinish) {
      try {
        System.out.println("Waiting to finish");
        exitCode = process.waitFor();
        System.out.println("Command Finished");
      } catch (InterruptedException e) {

        jsonResponse.addKeyValues("error", "Interrupted running " + cmd + "\n" + e.toString());
        return jsonResponse.getJson();
      }
    } else {
      System.out.println("Not waiting for finish");
      jsonResponse.addKeyValues("out", "Background process started");
      return jsonResponse.getJson();
    }

    try {
      String output = inputStreamToString(process.getInputStream());
      String error = inputStreamToString(process.getErrorStream());
      jsonResponse.addKeyValues("exit_code", exitCode);
      jsonResponse.addKeyValues("out", output);
      if (!error.equals("")) {
        //Only add error if there is one, this way we have a nice empty array instead of [""]
        jsonResponse.addKeyValues("error", error);
      }
      return jsonResponse.getJson();
    } catch (IOException e) {
      jsonResponse.addKeyValues("error", "Problems reading stdout and stderr from " + cmd + "\n" + e
          .toString());
      return jsonResponse.getJson();

    } finally {
      process.destroy();
    }
  }

  public static String inputStreamToString(InputStream is) throws IOException {
    StringBuilder result = new StringBuilder();
    int in;
    while ((in = is.read()) != -1) {
      result.append((char) in);
    }
    is.close();
    return result.toString();
  }
}
