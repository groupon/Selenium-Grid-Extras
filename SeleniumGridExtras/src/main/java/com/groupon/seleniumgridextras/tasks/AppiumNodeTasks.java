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
import com.groupon.seleniumgridextras.ExecuteCommand;
import com.groupon.seleniumgridextras.utilities.JsonResponseBuilder;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AppiumNodeTasks extends ExecuteOSTask {

    private static ConcurrentHashMap<String, String> appiumNodes;
    private static Logger logger = Logger.getLogger(AppiumNodeTasks.class);

    public String action = null;
    public String port = null;
    public String command = null;

    public AppiumNodeTasks() {
        setEndpoint(TaskDescriptions.Endpoints.APPIUM_TASKS);
        setDescription(TaskDescriptions.Description.APPIUM_TASKS);
        JsonObject params = new JsonObject();
        setAcceptedParams(params);
        setRequestType("GET");
        setResponseType("json");
        setClassname(this.getClass().getCanonicalName().toString());
        setCssClass("btn-success");
        setButtonText(TaskDescriptions.UI.ButtonText.APPIUM_TASKS);
        setEnabledInGui(true);


    }




    @Override
    public JsonObject execute(Map<String, String> parameter) {
        if (appiumNodes == null) {
            appiumNodes = new ConcurrentHashMap();
        }

        loadParameters(parameter);

        if (action.equals("add")) {
            appiumNodes.put(port, command);
            return JsonResponseBuilder.newResponse(JsonResponseBuilder.ResponseCode.SUCCESS).withProperty("added", port).build();
        } else if (action.equals("restart")) {
            command = appiumNodes.get(port);
            ExecuteCommand.execRuntime(command, false);
            return JsonResponseBuilder.newResponse(JsonResponseBuilder.ResponseCode.SUCCESS).withProperty("executed command", command).build();
        }
        return null;
    }

    private void loadParameters(Map<String, String> parameter) {
        try {
            action = parameter.get("action").toString();
        }catch(Exception e){}
        try {
            port = parameter.get("port").toString();
        }catch(Exception e){}
        try {
            command = parameter.get("command").toString();
        }catch(Exception e){}
    }

}
