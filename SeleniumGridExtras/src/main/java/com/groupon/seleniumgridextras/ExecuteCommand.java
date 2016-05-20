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
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import com.groupon.seleniumgridextras.utilities.json.JsonResponseBuilder;
import com.groupon.seleniumgridextras.utilities.threads.CommonThreadPool;
import com.groupon.seleniumgridextras.utilities.threads.ExecuteOsTaskCallable;
import com.groupon.seleniumgridextras.utilities.threads.StreamGobbler;

import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;


public class ExecuteCommand {

    private static Logger logger = Logger.getLogger(ExecuteCommand.class);

    public static JsonObject execRuntime(String cmd) {
        return execRuntime(cmd, true);
    }

    public static JsonObject execRuntime(String cmd, boolean waitToFinish) {
        logger.debug("Starting to execute - " + cmd);

        JsonResponseBuilder jsonResponse = new JsonResponseBuilder();

        jsonResponse.addKeyDescriptions(JsonCodec.COMMAND, "Command executed");
        jsonResponse.addKeyValues(JsonCodec.COMMAND, cmd);
        Process process;

        try {
            if (RuntimeConfig.getOS().isWindows()) {
                process = Runtime.getRuntime().exec("cmd /C " + cmd);
            } else {
                process = Runtime.getRuntime().exec(cmd);
            }
        } catch (IOException e) {
            final String message = "Problems in running " + cmd + "\n" + e.toString();
            jsonResponse.addKeyValues(JsonCodec.ERROR, message);
            logger.warn(message);
            return jsonResponse.getJson();
        }

        int exitCode;
        if (waitToFinish) {
            // Starts a thread to read error stream
            ByteArrayOutputStream errorOutputStream = new ByteArrayOutputStream();
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), errorOutputStream);
            errorGobbler.start();

            // Starts a thread to read output stream
            ByteArrayOutputStream standardOutputStream = new ByteArrayOutputStream();
            StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), standardOutputStream);
            outputGobbler.start();

            try {
                logger.debug("Waiting to finish");
                exitCode = process.waitFor();
                logger.debug("Command Finished");

                // Waits for threads to die
                errorGobbler.join();
                outputGobbler.join();
            } catch (InterruptedException e) {
                final String message = String.format("Interrupted running %s\n%s", cmd, e.getMessage());
                jsonResponse.addKeyValues(JsonCodec.ERROR, message);
                logger.error(message, e);
                return jsonResponse.getJson();
            }

            // Get output and error messages
            String error = errorOutputStream.toString();
            String output = standardOutputStream.toString();
            output = output.replace("\r", "");
            output = output.replace("\u0000", "");

            jsonResponse.addKeyValues(JsonCodec.EXIT_CODE, exitCode);
            jsonResponse.addKeyValues(JsonCodec.OUT, output);
            if (!error.equals("")) {
                //Only add error if there is one, this way we have a nice empty array instead of [""]
                jsonResponse.addKeyValues(JsonCodec.ERROR, error);
            }
        } else {
            CommonThreadPool.startCallable(new ExecuteOsTaskCallable(cmd, process));
            jsonResponse.addKeyValues(JsonCodec.OUT, "Background process started, check log for output");
        }
        return jsonResponse.getJson();
    }

    public static JsonObject execRuntime(String[] cmd, boolean waitToFinish) {
        StringBuilder command = new StringBuilder();
        for(String s : cmd) {
            command.append(s + " ");
        }        
        logger.debug("Starting to execute - " + command);

        JsonResponseBuilder jsonResponse = new JsonResponseBuilder();

        jsonResponse.addKeyDescriptions(JsonCodec.COMMAND, "Command executed");
        jsonResponse.addKeyValues(JsonCodec.COMMAND, command.toString());
        Process process;
        ProcessBuilder pb;
        
        try {
            if (RuntimeConfig.getOS().isWindows()) {
                pb = new ProcessBuilder(cmd);
                pb.redirectErrorStream(true);
                pb.redirectInput(Redirect.from(new File("NUL")));
                process = pb.start();
            } else {
                process = Runtime.getRuntime().exec(cmd);
            }
        } catch (IOException e) {
            final String message = "Problems in running " + command.toString() + "\n" + e.toString();
            jsonResponse.addKeyValues(JsonCodec.ERROR, message);
            logger.warn(message);
            return jsonResponse.getJson();
        }

        int exitCode;
        if (waitToFinish) {
            // Starts a thread to read error stream
            ByteArrayOutputStream errorOutputStream = new ByteArrayOutputStream();
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), errorOutputStream);
            errorGobbler.start();

            // Starts a thread to read output stream
            ByteArrayOutputStream standardOutputStream = new ByteArrayOutputStream();
            StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), standardOutputStream);
            outputGobbler.start();

            try {
                logger.debug("Waiting to finish");
                exitCode = process.waitFor();
                logger.debug("Command Finished");

                // Waits for threads to die
                errorGobbler.join();
                outputGobbler.join();
            } catch (InterruptedException e) {
                final String message = String.format("Interrupted running %s\n%s", command, e.getMessage());
                jsonResponse.addKeyValues(JsonCodec.ERROR, message);
                logger.error(message, e);
                return jsonResponse.getJson();
            }

            // Get output and error messages
            String error = errorOutputStream.toString();
            String output = standardOutputStream.toString();
            output = output.replace("\r", "");
            output = output.replace("\u0000", "");

            jsonResponse.addKeyValues(JsonCodec.EXIT_CODE, exitCode);
            jsonResponse.addKeyValues(JsonCodec.OUT, output);
            if (!error.equals("")) {
                //Only add error if there is one, this way we have a nice empty array instead of [""]
                jsonResponse.addKeyValues(JsonCodec.ERROR, error);
            }
        } else {
            CommonThreadPool.startCallable(new ExecuteOsTaskCallable(command.toString(), process));
            jsonResponse.addKeyValues(JsonCodec.OUT, "Background process started, check log for output");
        }
        return jsonResponse.getJson();
    }
}
