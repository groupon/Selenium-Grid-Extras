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

public class NodeConfig implements GridRole {

  @SerializedName("-port")
  private String port;
  @SerializedName("-hub")
  private String hub;
  @SerializedName("-host")
  private String host;
  @SerializedName("-role")
  private String role;
  @SerializedName("-nodeTimeout")
  private String nodeTimeout;
  @SerializedName("-maxSession")
  private int maxSession;
  @SerializedName("-proxy")
  private String proxy;
  @SerializedName("-Dwebdriver.ie.driver")
  private String ieDriver;

  public void setIeDriver(String ieDriverPath){
    this.ieDriver = ieDriverPath;
  }

  @Override
  public String getPort() {
    return port;
  }

  public void setPort(String port) {
    this.port = port;
  }

  @Override
  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  @Override
  public String getStartCommand() {
    String command = "-role " + role + " -port " + port + " -host " + host + " -hub " + hub
                     + " -nodeTimeout " + nodeTimeout + " -maxSession " + maxSession + " -proxy " + proxy;

    if(ieDriver != null){
      command = command + " -Dwebdriver.ie.driver=" + ieDriver;
    }

    return command;
  }

  public String getHub() {
    return hub;
  }

  public void setHub(String hub) {
    this.hub = hub;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getNodeTimeout() {
    return nodeTimeout;
  }

  public void setNodeTimeout(String nodeTimeout) {
    this.nodeTimeout = nodeTimeout;
  }

  public int getMaxSession() {
    return maxSession;
  }

  public void setMaxSession(int maxSession) {
    this.maxSession = maxSession;
  }

  public String getProxy() {
    return proxy;
  }

  public void setProxy(String proxy) {
    this.proxy = proxy;
  }
}
