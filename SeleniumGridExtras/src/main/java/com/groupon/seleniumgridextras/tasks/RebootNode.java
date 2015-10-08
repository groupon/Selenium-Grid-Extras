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

import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.TimeStampUtility;
import org.apache.log4j.Logger;

public class RebootNode extends ExecuteOSTask {

    private static Logger logger = Logger.getLogger(RebootNode.class);

    public RebootNode() {
        setEndpoint(TaskDescriptions.Endpoints.REBOOT);
        setDescription(TaskDescriptions.Description.REBOOT);
        JsonObject params = new JsonObject();
        setAcceptedParams(params);
        setRequestType("GET");
        setResponseType("json");
        setClassname(this.getClass().getCanonicalName().toString());
        setCssClass(TaskDescriptions.UI.BTN_DANGER);
        setButtonText(TaskDescriptions.UI.ButtonText.REBOOT);
        setEnabledInGui(true);
    }

    @Override
    public String getWindowsCommand() {
        return getWindowsCommand("");
    }

    @Override
    public String getWindowsCommand(String param) {
        logReboot();
        return "shutdown -r -t 1 -f";
    }

    @Override
    public String getLinuxCommand() {
        return getLinuxCommand("");
    }

    @Override
    public String getLinuxCommand(String param) {
        logReboot();
        return "sudo shutdown -r now";
    }

    @Override
    public String getMacCommand() {
        return getMacCommand("");
    }

    @Override
    public String getMacCommand(String param) {
        logReboot();
        return "shutdown -r now";
    }

    protected void logReboot() {
        logger.info("Rebooting " + RuntimeConfig.getOS().getHostName() + " at " + TimeStampUtility.getTimestampAsString());
    }
}
