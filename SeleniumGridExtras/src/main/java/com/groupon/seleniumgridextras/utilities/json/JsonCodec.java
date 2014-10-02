package com.groupon.seleniumgridextras.utilities.json;

public class JsonCodec {

  public static final String OUT = "out";
  public static final String ERROR = "error";
  public static final String EXIT_CODE = "exit_code";
  public static final String PARAMETER = "parameter";
  public static final String FILES = "files";
  public static final String TIMESTAMP = "timestamp";
  public static final String CONFIRM = "confirm";
  public static final String TRUE = "true";
  public static final String N_A = "N/A";
  public static final String FALSE_INT = "0";
  public static final String TRUE_INT = "1";


  public static class SetupTeardown{
    public static final String RESULTS = "results";
    public static final String CLASSES_TO_EXECUTE = "classes_to_execute";
  }

  public static class Images{
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String KEEP = "keep";
    public static final String FILE_TYPE = "file_type";
    public static final String FILE = "file";
    public static final String IMAGE = "image";
    public static final String PNG = "png";
  }

  public static class Video{

    public static final String SESSION = "session";
    public static final String ACTION = "action";
    public static final String DESCRIPTION = "description";
    public static final String CURRENT_VIDEOS = "current_videos";
    public static final String START = "start";
    public static final String STOP = "stop";
    public static final String HEARTBEAT = "heartbeat";
    public static final String STATUS = "status";
    public static final String STOP_ALL = "stop_all";
  }

  public static class OS {

    public static final String PROCESS_NAME = "process_name";
    public static final String PORT = "port";
    public static final String PID = "pid";
    public static final String PROCESS = "process";
    public static final String USER = "user";
    public static final String HOSTNAME = "hostname";
    public static final String IP = "ip";
    public static final String UPTIME = "uptime";

    public static class Mouse {
      public static final String X = "x";
      public static final String Y = "y";
    }

    public static class KillCommands {

      public static final String COMMAND = "command";
      public static final String WAIT_TO_FINISH = "wait_to_finish";
      public static final String NAME = "name";
      public static final String SIGNAL = "signal";
      public static final String ID = "id";
    }

    public static class InternetExplorer {
      public static final String INTERNET_ZONE = "1";
      public static final String INTRANET_ZONE = "2";
      public static final String TRUSTED_ZONE = "3";
      public static final String RESTRICTED_ZONE = "4";
      public static final String INTERNET = "Internet";
      public static final String LOCAL_INTRANET = "Local Intranet";
      public static final String TRUSTED_SITES = "Trusted Sites";
      public static final String RESTRICTED_SITES = "Restricted Sites";
      public static final String ENABLED = "enabled";
    }

    public static class Hardware{

      public static class Ram{

        public static final String RAM = "ram";
        public static final String TOTAL = "total";
        public static final String FREE = "free";
        public static final String TOTAL_SWAP = "total_swap";
        public static final String FREE_SWAP = "free_swap";
      }

      public static class HardDrive{

        public static final String DRIVES = "drives";
        public static final String FREE = "free";
        public static final String SIZE = "size";
        public static final String DRIVE = "drive";
        public static final String USABLE = "usable";
      }

      public static class Processor{

        public static final String PROCESSOR = "processor";
        public static final String INFO = "info";
        public static final String ARCHITECTURE = "architecture";
        public static final String CORES = "cores";
        public static final String LOAD = "load";
      }
    }

  }

  public static class Config {

    public static final String CONFIG_FILE = "config_file";
    public static final String CONFIG_RUNTIME = "config_runtime";
    public static final String CONTENT = "content";
    public static final String FILENAME = "filename";
  }

  public static class WebDriver {

    public static final String OLD_WEB_DRIVER_JAR = "old_web_driver_jar";
    public static final String OLD_CHROME_DRIVER = "old_chrome_driver";
    public static final String OLD_IE_DRIVER = "old_ie_driver";
    public static final String NEW_WEB_DRIVER_JAR = "new_web_driver_jar";
    public static final String NEW_CHROME_DRIVER = "new_chrome_driver";
    public static final String NEW_IE_DRIVER = "new_ie_driver";


    public static class Grid {
      public static final String HUB_RUNNING = "hub_running";
      public static final String NODE_RUNNING = "node_running";
      public static final String HUB_INFO = "hub_info";
      public static final String NODE_INFO = "node_info";
      public static final String NODE_SESSIONS_STARTED = "node_sessions_started";
      public static final String NODE_SESSIONS_CLOSED = "node_sessions_closed";
      public static final String NODE_SESSIONS_LIMIT = "node_sessions_limit";
      public static final String NODE = "node";
      public static final String PORT = "port";
      public static final String HOST = "host";
      public static final String ROLE = "role";
      public static final String SERVLETS = "servlets";
      public static final String HUB = "hub";
    }

    public static class Downloader {

      public static final String BIT_32 = "32";
      public static final String ROOT_DIR = "root_dir";
      public static final String BIT = "bit";
      public static final String WIN32 = "Win32";
      public static final String FILE = "file";
      public static final String FILE_FULL_PATH = "file_full_path";
      public static final String SOURCE_URL = "source_url";
      public static final String VERSION = "version";
    }
  }
}
