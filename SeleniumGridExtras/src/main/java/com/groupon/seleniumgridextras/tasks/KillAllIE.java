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


import com.google.common.base.Throwables;
import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.ExecuteCommand;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import com.groupon.seleniumgridextras.utilities.json.JsonParserWrapper;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class KillAllIE extends KillAllByName {

    public static final String CLEAR_HISTORY_RESPONSE = "clear_history_response";
    public static final String KILL_IE_RESPONSE = "kill_ie_response";
    public static final String KILL_DRIVER_RESPONSE = "kill_driver_response";

    private static Logger logger = Logger.getLogger(KillAllIE.class);

    public KillAllIE() {
        setEndpoint(TaskDescriptions.Endpoints.KILL_IE);
        setDescription(TaskDescriptions.Description.KILL_IE);
        JsonObject params = new JsonObject();
        setAcceptedParams(params);
        setRequestType("GET");
        setResponseType("json");
        setClassname(this.getClass().getCanonicalName().toString());
        setCssClass(TaskDescriptions.UI.BTN_DANGER);
        setButtonText(TaskDescriptions.UI.ButtonText.KILL_IE);
        setEnabledInGui(true);
    }


    @Override
    public JsonObject execute(String parameter) {
        return execute();
    }

    @Override
    public JsonObject execute(Map<String, String> parameter) {
        return execute();
    }

    public JsonObject execute() {

        if (!RuntimeConfig.getOS().isWindows()) {
            getJsonResponse().addKeyValues(JsonCodec.ERROR, "This command can only be executed on Windows");
            return getJsonResponse().getJson();
        }


        Map<String, String> responsesList = new HashMap<String, String>();
        try {
            logger.info(String.format("Killing all IE Driver instances with command %s", getKillDriverCommand()));
            JsonObject killDriverResponse = ExecuteCommand.execRuntime(getKillDriverCommand(), true);
            logger.debug(killDriverResponse);
            responsesList.put(KILL_DRIVER_RESPONSE, JsonParserWrapper.prettyPrintString(killDriverResponse));

            logger.info(String.format("Killing all IE instances with command %s", getKillIECommand()));
            JsonObject killIEResponse = ExecuteCommand.execRuntime(getKillIECommand(), true);
            logger.debug(killIEResponse);
            responsesList.put(KILL_IE_RESPONSE, JsonParserWrapper.prettyPrintString(killIEResponse));

            logger.info(String.format("Clearing all browser data with command %s", getClearHistoryCommand()));
            JsonObject clearHistoryResponse = ExecuteCommand.execRuntime(getClearHistoryCommand(), true);
            logger.debug(clearHistoryResponse);
            responsesList.put(CLEAR_HISTORY_RESPONSE, JsonParserWrapper.prettyPrintString(clearHistoryResponse));
        } catch (Exception e) {
            getJsonResponse().addKeyValues(JsonCodec.ERROR, Throwables.getStackTraceAsString(e));
        }

        getJsonResponse().addKeyValues(JsonCodec.OUT, responsesList);
        return getJsonResponse().getJson();
    }

    public String getKillCrashReportCommand(){
        return "taskkill -F -T -IM WerFault*";
    }

    public String getKillDriverCommand() {
        return "taskkill -F -T -IM iedriver*";
    }

    public String getKillIECommand() {
        return "taskkill -F -T -IM iexplore*";
    }

    public String getClearHistoryCommand() {
        return "RunDll32.exe InetCpl.cpl,ClearMyTracksByProcess 4351";
    }

    @Override
    public boolean initialize() {
        try {
            if (RuntimeConfig.getOS().isWindows()) {
                execute();
            }
        } catch (Exception error) {
            printInitilizedFailure();
            logger.error(error);
            return false;
        }

        printInitilizedSuccessAndRegisterWithAPI();
        return true;

    }
}
