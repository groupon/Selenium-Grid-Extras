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
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import java.util.HashMap;
import java.util.Map;

public class IEMixedContent extends ExecuteOSTask {

  private static
  org.apache.log4j.Logger
      logger = org.apache.log4j.Logger.getLogger(IEMixedContent.class);

  public IEMixedContent() {
    setEndpoint(TaskDescriptions.Endpoints.IE_MIXED_CONTENT);
    setDescription(
        TaskDescriptions.Description.IE_MIXED_CONTENT);
    JsonObject params = new JsonObject();
    params.addProperty(JsonCodec.OS.Windows.InternetExplorer.ENABLED,
                       "(Optional)0 for enabling mixed content for all zones, 1 for prompting, and 3 for disabling");
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass(TaskDescriptions.UI.BTN_DANGER);
    setButtonText(TaskDescriptions.UI.ButtonText.IE_MIXED_CONTENT);
    setEnabledInGui(true);

    getJsonResponse()
        .addKeyDescriptions(JsonCodec.OS.Windows.InternetExplorer.INTERNET, "Current setting for Internet");
    getJsonResponse().addKeyDescriptions(JsonCodec.OS.Windows.InternetExplorer.LOCAL_INTRANET,
                                         "Current setting for Local Intranet");
    getJsonResponse().addKeyDescriptions(JsonCodec.OS.Windows.InternetExplorer.TRUSTED_SITES,
                                         "Current setting for Trusted Sites");
    getJsonResponse()
        .addKeyDescriptions(JsonCodec.OS.Windows.InternetExplorer.RESTRICTED_SITES,
                            "Current setting for Restricted Sites");
  }

  public String
      regLocation =
      "Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Zones\\%s";


  public final HashMap<String, String> zone = new HashMap<String, String>() {
    {
      put(JsonCodec.OS.Windows.InternetExplorer.INTERNET_ZONE, JsonCodec.OS.Windows.InternetExplorer.INTERNET);
      put(JsonCodec.OS.Windows.InternetExplorer.INTRANET_ZONE,
          JsonCodec.OS.Windows.InternetExplorer.LOCAL_INTRANET);
      put(JsonCodec.OS.Windows.InternetExplorer.TRUSTED_ZONE, JsonCodec.OS.Windows.InternetExplorer.TRUSTED_SITES);
      put(JsonCodec.OS.Windows.InternetExplorer.RESTRICTED_ZONE,
          JsonCodec.OS.Windows.InternetExplorer.RESTRICTED_SITES);
    }
  };

  public HashMap<String, String> getZones() {
    return zone;
  }

  public String getCurrentSettingForZone(String zoneId) {
    return String.format(regLocation, zoneId);
  }


  @Override
  public JsonObject execute(Map<String, String> parameter) {
    if (!parameter.isEmpty() && parameter.containsKey(JsonCodec.OS.Windows.InternetExplorer.ENABLED)) {
      return execute(parameter.get(JsonCodec.OS.Windows.InternetExplorer.ENABLED).toString());
    }
    return execute();
  }

  @Override
  public JsonObject execute() {
    if (RuntimeConfig.getOS().isWindows()) {
      return getAllMixedContentStatus();
    } else {
      getJsonResponse().addKeyValues(JsonCodec.ERROR,
                                     "IE Mixed Content command is only implemented in Windows");
      return getJsonResponse().getJson();
    }
  }

  @Override
  public JsonObject execute(String status) {
    if (RuntimeConfig.getOS().isWindows()) {
      int value = 0;
      if (status.equals("0")) {
        value = 0;
      } else if (status.equals("1")) {
        value = 1;
      } else if (status.equals("3")) {
        value = 3;
      } else {
        getJsonResponse().addKeyValues(JsonCodec.ERROR,
            "Value of " + status + " is not valid. Only 0, 1, 3 are valid.");
        return getJsonResponse().getJson();
      }
      setAllMixedContent(value);
      getJsonResponse()
          .addKeyValues(JsonCodec.OUT, "IE needs to restart before you see the changes");
      return getAllMixedContentStatus();
    } else {
      getJsonResponse().addKeyValues(JsonCodec.ERROR,
                                     "IE Mixed Content command is only implemented in Windows");
      return getJsonResponse().getJson();
    }
  }

  private void setAllMixedContent(int value) {
    try {
      for (String key : getZones().keySet()) {
        Advapi32Util
            .registrySetIntValue(WinReg.HKEY_CURRENT_USER, getCurrentSettingForZone(key), JsonCodec.OS.Windows.RegistryKeys.IE_MIXED_CONTENT,
                                 value);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private String getMixedContentEnabledForZone(String zone) {
    int status =
        Advapi32Util
            .registryGetIntValue(WinReg.HKEY_CURRENT_USER, getCurrentSettingForZone(zone), JsonCodec.OS.Windows.RegistryKeys.IE_MIXED_CONTENT);

    if (status == 0) {
      return "enabled";
    } else if (status == 1) {
      return "prompt";
    } else if (status == 3) {
      return "disabled";
    } else {
      return "unknown";
    }
  }


  private JsonObject getAllMixedContentStatus() {
    for (String key : getZones().keySet()) {
      String status = getMixedContentEnabledForZone(key);

      logger.info("Zone " + key + " is set to " + status);
      getJsonResponse().addKeyValues(getZones().get(key), status);
    }

    return getJsonResponse().getJson();
  }

}
