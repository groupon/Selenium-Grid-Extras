package com.groupon.seleniumgridextras.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;

import com.groupon.seleniumgridextras.config.driver.IEDriver;
import com.groupon.seleniumgridextras.config.driver.WebDriver;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Config {

  private String config_version;
  private List<String> activated_modules;
  private List<String> disabled_modules;
  private String expose_directory;
  private List<String> setup;
  private List<String> teardown;
  private GridInfo grid;
  private WebDriver webdriver = new WebDriver();
  private IEDriver iedriver = new IEDriver();

  public Config() {
    activated_modules = new ArrayList<String>();
    disabled_modules = new ArrayList<String>();
    setup = new ArrayList<String>();
    teardown = new ArrayList<String>();
    grid = new GridInfo();
    webdriver = new WebDriver();
  }

  public List<String> getActivatedModules() {
    return activated_modules;
  }

  public List<String> getDisabledModules() {
    return disabled_modules;
  }

  public String getExposeDirectory() {
    return expose_directory;
  }

  public List<String> getSetup() {
    return setup;
  }

  public List<String> getTeardown() {
    return teardown;
  }

  public GridInfo getGrid() {
    return grid;
  }

  public IEDriver getIEdriver() {
    return iedriver;
  }

  public WebDriver getWebdriver() {
    return webdriver;
  }

  public String getConfigVersion() {
    return config_version;
  }

  public void writeToDisk(String file) {
    try {
      File f = new File(file);
      String config = this.toPrettyJsonString();
      FileUtils.writeStringToFile(f, config);
    } catch (Exception error) {
      System.out
          .println("Could not write default config file, exit with error " + error.toString());
      System.exit(1);
    }
  }

  public void setConfigVersion(String config_version) {
    this.config_version = config_version;
  }

  public void addSetupTask(String task) {
    setup.add(task);
  }

  public void addTeardownTask(String task) {
    teardown.add(task);
  }

  public void addEnabledModule(String module) {
    activated_modules.add(module);
  }

  public void addDisabledModule(String module) {
    disabled_modules.add(module);
  }

  public void setSharedDir(String sharedDir) {
    expose_directory = sharedDir;
  }

  public String toJsonString() {
    return new Gson().toJson(this);
  }

  public String toPrettyJsonString() {
    return new GsonBuilder().setPrettyPrinting().create().toJson(this);
  }

  public boolean checkIfModuleEnabled(String module) {
    return activated_modules.contains(module);
  }

  public String getExposedDirectory() {
    return expose_directory;
  }

  public class GridInfo {

    private int auto_start_hub;
    private int auto_start_node;
    private String default_role;
    private Node node;
    private Hub hub;

    public GridInfo() {
      node = new Node();
      hub = new Hub();
    }

    public boolean getAutoStartHub() {
      return auto_start_hub == 1 ? true : false;
    }

    public void setAutoStartHub(int autoStartHub) {
      this.auto_start_hub = autoStartHub;
    }

    public boolean getAutoStartNode() {
      return auto_start_node == 1 ? true : false;
    }

    public void setAutoStartNode(int autoStartNode) {
      this.auto_start_node = autoStartNode;
    }

    public String getDefaultRole() {
      return default_role;
    }

    public void setDefaultRole(String defaultRole) {
      this.default_role = defaultRole;
    }

    public Node getNode() {
      return node;
    }

    public Hub getHub() {
      return hub;
    }

    public class Node implements GridRole {

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

    public class Hub implements GridRole {

      @SerializedName("-port")
      private String port;
      @SerializedName("-host")
      private String host;
      @SerializedName("-role")
      private String role;
      @SerializedName("-servlets")
      private String servlets;

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
        return "-role " + role + " -port " + port + " -host " + host + " -servlets " + servlets;
      }

      public String getRole() {
        return role;
      }

      public void setRole(String role) {
        this.role = role;
      }

      public String getServlets() {
        return servlets;
      }

      public void setServlets(String servlets) {
        this.servlets = servlets;
      }
    }

  }


  public JsonObject asJsonObject() {
    return (JsonObject) new JsonParser().parse(this.toJsonString());
  }

}
