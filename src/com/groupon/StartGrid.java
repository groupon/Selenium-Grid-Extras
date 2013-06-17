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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StartGrid extends ExecuteOSTask {

  public boolean waitToFinishTask = false;

  @Override
  public String getEndpoint() {
    return "/start_grid";
  }

  @Override
  public String getDescription() {
    return "Starts an instance of Selenium Grid Hub or Node";
  }

  @Override
  public String execute() {
    return execute("");
  }

  @Override
  public String execute(String role) {

    String servicePort = getConfigPort(role);
    String occupiedPid = PortChecker.getPidOnPort(servicePort);
    if (!occupiedPid.equals("")){
      System.out.println(servicePort + " port is busy, won't try to start a service");
      return JsonWrapper.startSerivceToJson(1, "", "Port: " + servicePort + " is occupied by some other process");
    }

    String

        command =
        OSChecker.isWindows() ? getWindowsCommand(role)
                              : OSChecker.isMac() ? getMacCommand(role) : getLinuxCommand(role);

    System.out.println(command);

    String serviceStartResponse = ExecuteCommand.execRuntime(command, waitToFinishTask);

    Map result = JsonWrapper.parseJson(serviceStartResponse);
    System.out.println(result.get("exit_code") + "\n\n" + result.get("exit_code").getClass().getCanonicalName());
    if (result.get("exit_code").toString().equals("0")) {
      System.out.println("Service start command sent, waiting to spin up");
      return waitForServiceToStart(servicePort);
    } else {
      System.out.println("Something didn't go right in launching service");
      return serviceStartResponse;
    }


  }

  @Override
  public String execute(Map<String, String> parameter) {

    if (parameter.isEmpty() || !parameter.containsKey("role")) {
      return execute();
    } else {
      return execute(parameter.get("role").toString());
    }
  }

  @Override
  public Map getResponseDescription() {
    Map response = new HashMap();
    response.put("exit_code", "Result of starting service, 0 success, anything else failure");
    response.put("pid", "Process ID of the service which was started");
    response.put("error", "Any error that might have come up");
    return response;
  }

  @Override
  public Map getAcceptedParams() {
    Map<String, String> params = new HashMap();
    params.put("role", "hub|node - defaults to 'default_role' param in config file");
    return params;
  }

  @Override
  public String getLinuxCommand(String role) {
    return "java -jar " + getWebdriverPath() + " " + getFormattedConfig(role) + " &";
  }

  private String waitForServiceToStart(String port) {
    sleep(10000);

    for (Integer i = 0; i < 10; i++) {
      String pid = getServicePid(port);
      if (!pid.equals("")) {
        return JsonWrapper.startSerivceToJson(0, pid, "");
      }
      System.out.println("Waiting for service to start on port: " + port);
      sleep(10000);
    }

    return JsonWrapper.startSerivceToJson(1, "", "Service failed to start");
  }

  private void sleep(int time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException error) {
      System.out.println("Something went wrong with sleep command \n" + error.toString());
    }
  }

  private String getWebdriverPath() {
    return RuntimeConfig.getWebdriverParentDir() + "/" + RuntimeConfig.getWebdriverVersion()
           + ".jar";
  }

  private String getConfigPort(String role) {
    Map<String, String> config = RuntimeConfig.getGridConfig(role);
    return config.get("-port");
  }

  private String getFormattedConfig(String role) {
    Map<String, String> config = RuntimeConfig.getGridConfig(role);
    StringBuilder commandLineParam = new StringBuilder();

    Iterator it = config.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pairs = (Map.Entry) it.next();

      commandLineParam.append(" " + pairs.getKey());
      commandLineParam.append(" " + pairs.getValue());

      it.remove();
    }

    return commandLineParam.toString();
  }

  private String getServicePid(String port) {
    return PortChecker.getPidOnPort(port);
  }

}
