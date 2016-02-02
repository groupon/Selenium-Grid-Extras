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
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class KillPort extends ExecuteOSTask {

    private static Logger logger = Logger.getLogger(KillPort.class);

  public KillPort() {
    setEndpoint(TaskDescriptions.Endpoints.KILL_PORT);
    setDescription(TaskDescriptions.Description.KILL_PORT);
    JsonObject params = new JsonObject();
    params.addProperty(JsonCodec.OS.KillCommands.PORT, "(Required) -  Process ID (PID) to terminate.");
    //params.addProperty(JsonCodec.OS.KillCommands.SIGNAL, "(unix only) - Signal Term number such as 1, 2...9");
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass(TaskDescriptions.UI.BTN_DANGER);
    setButtonText(TaskDescriptions.UI.ButtonText.KILL_PORT);
    setEnabledInGui(false);
  }

  @Override
  public JsonObject execute() {

    getJsonResponse().addKeyValues(JsonCodec.ERROR, "Port is a required parameter");
    return getJsonResponse().getJson();
  }

  @Override
  public JsonObject execute(Map<String, String> parameter) {
      logger.debug("got command to kill port ");
    if (parameter.isEmpty() || !parameter.containsKey(JsonCodec.OS.KillCommands.PORT)) {
        logger.debug("Port is required parameter ");
      return execute();
    } else {
      String port = parameter.get(JsonCodec.OS.KillCommands.PORT).toString();

      String command = "";
      if (RuntimeConfig.getOS().isWindows()) {
          command = getWindowsCommand(port);
      } else if (RuntimeConfig.getOS().isMac()) {
          command = getLinuxCommand(port);
      } else {
          command = getLinuxCommand(port);
      }
        logger.info("about to attempt to kill port " + port);
        logger.info(command);
      JsonObject response = ExecuteCommand.execRuntime(command, waitToFinishTask);
      return response;
    }
  }

  @Override
  public String getWindowsCommand(String parameter) {
    return "FOR /F \"tokens=5 delims= \" %P IN ('netstat -a -n -o ^|  findstr :"+parameter+"') DO TaskKill.exe /PID %P /T /F";
  }

  @Override
  public String getLinuxCommand(String parameter) {
      /*int port = Integer.parseInt(parameter);
      return new Command().startWith("/bin/bash").arg("-c").
              arg(String.format("pid=$(lsof -t -iTCP:%d -sTCP:LISTEN); if [[ -n $pid ]]; then kill $pid; else echo 'nothing to kill'; fi", port )).asString();*/

      String getPidCommand = "lsof -t -iTCP:"+parameter+" -sTCP:LISTEN";
      logger.info("finding process id to kill by running " + getPidCommand);
      Process cmdProc = null;
      try {
          cmdProc = Runtime.getRuntime().exec(getPidCommand);

      } catch (IOException e) {
          logger.debug(e.getMessage());
          e.printStackTrace();
      }

      BufferedReader stdoutReader = new BufferedReader(
              new InputStreamReader(cmdProc.getInputStream()));
      String line;
      String pid = "";
      try {
          Thread.sleep(2000);
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
      //TODO add additional lines for adb.  when it ends in device then the device is online and operational.
      try {
          while ((line = stdoutReader.readLine()) != null) {
              pid = line;
              logger.debug("setting pid to " + pid);
              break;
          }
      } catch (IOException e) {
          logger.debug(e.getMessage());
          e.printStackTrace();
      }

      try {
          stdoutReader.close();
      } catch (IOException e) {
          e.printStackTrace();
      }

      logger.info("pid to kill = " + pid);
      if(pid!=null)
          if(!pid.equals(""))
              return "kill "+ pid;
      return "";
      //return "kill "+ pid;

  }
}
