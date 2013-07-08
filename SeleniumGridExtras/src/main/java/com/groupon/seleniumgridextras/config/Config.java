package com.groupon.seleniumgridextras.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

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
  private WebDriver webdriver;

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

  public WebDriver getWebdriver() {
    return webdriver;
  }

  public String getConfigVersion() {
    return config_version;
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
    this.expose_directory = sharedDir;
  }

  public String toJsonString() {
    return new Gson().toJson(this);
  }

  public String toPrettyJsonString() {
    return new GsonBuilder().setPrettyPrinting().create().toJson(this);
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
        return "-role " + role + " -port " + port + " -host " + host + " -hub " + hub + " -nodeTimeout " + nodeTimeout + " -maxSession " + maxSession + " -proxy " + proxy;
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

  public class WebDriver {
    private String directory;
    private String version;

    public WebDriver() {
    }

    public String getDirectory() {
      return directory;
    }

    public void setDirectory(String directory) {
      this.directory = directory;
    }

    public String getVersion() {
      return version;
    }

    public void setVersion(String version) {
      this.version = version;
    }
  }

}
