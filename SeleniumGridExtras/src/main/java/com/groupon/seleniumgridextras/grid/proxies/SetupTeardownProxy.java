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


package com.groupon.seleniumgridextras.grid.proxies;

import com.groupon.seleniumgridextras.ExecuteCommand;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.internal.listeners.TestSessionListener;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;
import org.openqa.selenium.remote.CapabilityType;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SetupTeardownProxy extends DefaultRemoteProxy implements TestSessionListener {

  private Pattern urlPattern = Pattern.compile("http://([^:/]+)");

  public SetupTeardownProxy(RegistrationRequest request, Registry registry) {
    super(request, registry);
    System.out.println("Attaching a node with " + getClass().getSimpleName());
  }

  @Override
  public void beforeSession(TestSession session) {
    super.beforeSession(session);
    Map<String,Object> cap =session.getRequestedCapabilities();
    if(cap.get(CapabilityType.BROWSER_NAME).equals("internet explorer"))
        callAction(session,"kill_ie"); //kill IE if starting IE
    callAction(session, "setup");
  }

  @Override
  public void afterSession(TestSession session) {
    super.afterSession(session);
    Map<String,Object> cap =session.getRequestedCapabilities();
    if(cap.get(CapabilityType.BROWSER_NAME).equals("internet explorer"))
        callAction(session,"kill_ie"); //kill IE if starting IE
    callAction(session, "teardown");
  }

  synchronized private void callAction(TestSession session, String action) {
    try {
      String nodeAddress = session.getSlot().getProxy().getRemoteHost().toString();
      Matcher matcher = urlPattern.matcher(nodeAddress);
      String cleanAddress = "";
      while (matcher.find()) {
        cleanAddress = matcher.group();
      }
      URL url = new URL(cleanAddress + ":3000/" + action);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      System.out.println(ExecuteCommand.inputStreamToString(conn.getInputStream()));
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (ProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


}
