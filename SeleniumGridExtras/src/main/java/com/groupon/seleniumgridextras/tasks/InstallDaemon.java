package com.groupon.seleniumgridextras.tasks;

import com.google.gson.JsonObject;

import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.daemons.DaemonWrapper;

import java.io.File;
import java.util.Map;

public class InstallDaemon extends ExecuteOSTask {

  DaemonWrapper daemon;

  public InstallDaemon() {
    setEndpoint("/install_daemon");
    setDescription("Installs and uninstalls OS specific daemon/service for Grid Extras");
    JsonObject params = new JsonObject();
    params.addProperty("action", "install|uninstall");
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass("btn-info");
    setButtonText("InstallDaemon");
    setEnabledInGui(true);

    daemon = RuntimeConfig.getConfig().getDaemon();

  }

  @Override
  public JsonObject execute() {
    getJsonResponse().addKeyValues("error", "'action' parameter is required");
    return getJsonResponse().getJson();
  }

  @Override
  public JsonObject execute(Map<String, String> parameter) {
    if (parameter.isEmpty() || !parameter.containsKey("action")) {
      return execute();
    } else {
      return execute(parameter.get("action").toString());
    }
  }

  @Override
  public JsonObject execute(String action) {
    try {
      if (action.equals("install")) {
        getDaemon().installDaemon();
        getJsonResponse().addKeyValues("out",
                                       "Install command was sent, should take affect next time user logs in");
        return getJsonResponse().getJson();
      } else if (action.equals("uninstall")) {
        getJsonResponse().addKeyValues("out",
                                       "Un-Install command was sent, should take affect next time user logs in");
        getDaemon().uninstallDaemon();
        return getJsonResponse().getJson();
      } else {
        getJsonResponse()
            .addKeyValues("error", "Unrecognized value, only use 'install' or 'uninstall'");
        return getJsonResponse().getJson();
      }


    } catch (Exception error) {
      getJsonResponse().addKeyValues("error", error.toString());
      return getJsonResponse().getJson();
    }
  }

  @Override
  public boolean initialize() {
    try {
      if (getDaemon().getAutoInstallDaemon()) {
        getDaemon().installDaemon();
      } else {
        getDaemon().uninstallDaemon();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;

  }

  protected DaemonWrapper getDaemon() {
    return daemon;
  }

}
