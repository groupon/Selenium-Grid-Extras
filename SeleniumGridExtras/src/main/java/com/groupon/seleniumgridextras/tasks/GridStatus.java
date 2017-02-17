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

import com.google.gson.JsonObject;

import com.groupon.seleniumgridextras.PortChecker;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;

import java.util.Map;

public class GridStatus extends ExecuteOSTask {

    public static final String SESSION_PARAM_DESCRIPTION = "(optional) - When provided, the current session ID will be added to list of existing sessions";
    public static final String BOOLEAN_IF_HUB_IS_RUNNING_ON_GIVEN_PORT = "Boolean if hub is running on given port";
    public static final String BOOLEAN_IF_NODE_IS_RUNNING_ON_GIVEN_PORT = "Boolean if node is running on given port";
    public static final String HASH_OBJECT_DESCRIBING_THE_HUB_PROCESS = "Hash object describing the Hub Process";
    public static final String HASH_OBJECT_DESCRIBING_THE_NODE_CONFIG_PROCESS = "Hash object describing the NodeConfig Process";
    public static final String LIST_OF_RECORDED_SESSIONS = "List of recorded sessions";
    public static final String INTEGER_UPPER_LIMIT_BEFORE_THE_BOX_REBOOTS = "Integer upper limit before the box reboots";
    public static final String BOOLEAN_IF_NODE_WILL_UNREGISTER_DURING_REBOOT = "Boolean if node will unregister during reboot immediately so test clients will get an error if they try to connect. Otherwise the node will be only marked as down and test clients are stored in a queue until node is up again.";

    public static final String DEPRECATED_STARTED_SESSIONS_KEY = "node_sessions_started";
    public static final String DEPRECATION_WARNING = "[DEPRECATION WARNING] - The " + DEPRECATED_STARTED_SESSIONS_KEY + " key returned from the node's " + TaskDescriptions.Endpoints.GRID_STATUS +"  endpoint indicates that the node is using old version of Selenium Grid Extras, please update it";

    public GridStatus() {
        setEndpoint(TaskDescriptions.Endpoints.GRID_STATUS);
        setDescription(
                TaskDescriptions.Description.GRID_STATUS);
        JsonObject params = new JsonObject();
        setAcceptedParams(params);
        setRequestType("GET");
        setResponseType("json");
        setClassname(this.getClass().getCanonicalName().toString());
        setCssClass("btn-success");
        setButtonText(TaskDescriptions.UI.ButtonText.GRID_STATUS);
        setEnabledInGui(true);

        params.addProperty(JsonCodec.WebDriver.Grid.NEW_SESSION_PARAM,
                SESSION_PARAM_DESCRIPTION);

        addResponseDescription(JsonCodec.WebDriver.Grid.HUB_RUNNING, BOOLEAN_IF_HUB_IS_RUNNING_ON_GIVEN_PORT);
        addResponseDescription(JsonCodec.WebDriver.Grid.NODE_RUNNING, BOOLEAN_IF_NODE_IS_RUNNING_ON_GIVEN_PORT);
        addResponseDescription(JsonCodec.WebDriver.Grid.HUB_INFO, HASH_OBJECT_DESCRIBING_THE_HUB_PROCESS);
        addResponseDescription(JsonCodec.WebDriver.Grid.NODE_INFO, HASH_OBJECT_DESCRIBING_THE_NODE_CONFIG_PROCESS);
        addResponseDescription(JsonCodec.WebDriver.Grid.RECORDED_SESSIONS, LIST_OF_RECORDED_SESSIONS);
        addResponseDescription(JsonCodec.WebDriver.Grid.NODE_SESSIONS_LIMIT, INTEGER_UPPER_LIMIT_BEFORE_THE_BOX_REBOOTS);
        addResponseDescription(JsonCodec.WebDriver.Grid.NODE_WILL_UNREGISTER_DURING_REBOOT, BOOLEAN_IF_NODE_WILL_UNREGISTER_DURING_REBOOT);

    }


    @Override
    public JsonObject execute() {
        try {
            JsonObject hubInfo = PortChecker.getParsedPortInfo(4444);
            JsonObject nodeInfo = PortChecker.getParsedPortInfo(5555);

            getJsonResponse().addKeyValues(JsonCodec.WebDriver.Grid.HUB_RUNNING, hubInfo.isJsonNull() || hubInfo.toString().equals("{}") ? false : true);
            getJsonResponse().addKeyValues(JsonCodec.WebDriver.Grid.NODE_RUNNING, nodeInfo.isJsonNull() || nodeInfo.toString().equals("{}") ? false : true);
            getJsonResponse().addKeyValues(JsonCodec.WebDriver.Grid.HUB_INFO, hubInfo);
            getJsonResponse().addKeyValues(JsonCodec.WebDriver.Grid.NODE_INFO, nodeInfo);

            getJsonResponse().addKeyValues(JsonCodec.WebDriver.Grid.RECORDED_SESSIONS,
                    RuntimeConfig.getTestSessionTracker().getSessions());

            getJsonResponse()
                    .addKeyValues(JsonCodec.WebDriver.Grid.NODE_SESSIONS_LIMIT,
                            RuntimeConfig.getConfig().getRebootAfterSessions());
            getJsonResponse()
            .addKeyValues(JsonCodec.WebDriver.Grid.NODE_WILL_UNREGISTER_DURING_REBOOT,
                    RuntimeConfig.getConfig().getUnregisterNodeDuringReboot());

            return getJsonResponse().getJson();
        } catch (Exception error) {
            getJsonResponse().addKeyValues(JsonCodec.ERROR, error.toString());
            return getJsonResponse().getJson();
        }
    }

    @Override
    public JsonObject execute(Map<String, String> parameter) {


        if (parameter.containsKey(JsonCodec.WebDriver.Grid.NEW_SESSION_PARAM)) {
            RuntimeConfig.getTestSessionTracker().startSession(
                    parameter.get(JsonCodec.WebDriver.Grid.NEW_SESSION_PARAM));
        }


        return execute();
    }

}
