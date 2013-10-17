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
package com.groupon.seleniumgridextras.config;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class NodeConfig extends HashMap<String, String> implements GridRole {


  public static final String IE_DRIVER = "ieDriver";
  private static final String PORT = "PORT";
  public static final String HUB = "HUB";
  public static final String ROLE = "ROLE";
  public static final String NODE_TIMEOUT = "nodeTimeout";
  public static final String MAX_SESSION = "maxSession";
  public static final String PROXY = "proxy";

  public void setIeDriver(String ieDriverPath) {
    this.put(IE_DRIVER, ieDriverPath);
  }

  public String getIeDriver() {
    return this.get(IE_DRIVER);
  }

  @Override
  public String getHost() {
    return RuntimeConfig.getCurrentHostIP();
  }

  @Override
  public String getPort() {
    return this.get(PORT);
  }


  public void setPort(String port) {
    this.put(PORT, port);
  }

  public String getHub() {
    return this.get(HUB);
  }

  public void setHub(String hub) {
    this.put(HUB, hub);
  }

  public String getRole() {
    return this.get(ROLE);
  }

  public void setRole(String role) {
    this.put(ROLE, role);
  }

  public String getNodeTimeout() {
    return this.get(NODE_TIMEOUT);
  }

  public void setNodeTimeout(String nodeTimeout) {
    this.put(NODE_TIMEOUT, nodeTimeout);
  }

  public String getMaxSession() {
    return this.get(MAX_SESSION);
  }

  public void setMaxSession(String maxSession) {
    this.put(MAX_SESSION, maxSession);
  }

  public String getProxy() {
    return this.get(PROXY);
  }

  public void setProxy(String proxy) {
    this.put(PROXY, proxy);
  }

  @Override
  public String getStartCommand() {
    String
        command =
        "-role " + getRole() + " -port " + getPort() + " -host " + getHost() + " -hub " + getHub()
        + " -nodeTimeout " + getNodeTimeout() + " -maxSession " + getMaxSession() + " -proxy "
        + getProxy();

    if (getIeDriver() != null) {
      command = command + " -Dwebdriver.ie.driver=" + getIeDriver();
    }

    return command;
  }

}
