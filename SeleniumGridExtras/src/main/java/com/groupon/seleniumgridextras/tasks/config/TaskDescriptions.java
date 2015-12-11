package com.groupon.seleniumgridextras.tasks.config;

public class TaskDescriptions {

    public static class Endpoints {
        public static final String AUTO_UPGRADE_WEBDRIVER = "/auto_upgrade_webdriver";
        public static final String DOWNLOAD_CHROMEDRIVER = "/download_chromedriver";
        public static final String DOWNLOAD_IEDRIVER = "/download_iedriver";
        public static final String DOWNLOAD_WEBDRIVER = "/download_webdriver";
        public static final String DIR = "/dir";
        public static final String CONFIG = "/config";
        public static final String GET_FILE = "/get_file";
        public static final String PORT_INFO = "/port_info";
        public static final String GET_NODE_CONFIG = "/get_node_config";
        public static final String PS = "/ps";
        public static final String GRID_STATUS = "/grid_status";
        public static final String IE_PROTECTED_MODE = "/ie_protected_mode";
        public static final String IE_MIXED_CONTENT = "/ie_mixed_content";
        public static final String KILL_ALL_BY_NAME = "/kill_all_by_name";
        public static final String KILL_CHROME = "/kill_chrome";
        public static final String KILL_FIREFOX = "/kill_firefox";
        public static final String KILL_IE = "/kill_ie";
        public static final String KILL_SAFARI = "/kill_safari";
        public static final String KILL_PID = "/kill_pid";
        public static final String MOVE_MOUSE = "/move_mouse";
        public static final String LOG_DELETE = "/log_delete";
        public static final String NETSTAT = "/netstat";
        public static final String REBOOT = "/reboot";
        public static final String SCREENSHOT = "/screenshot";
        public static final String SETUP = "/setup";
        public static final String START_GRID = "/start_grid";
        public static final String STOP_GRID = "/stop_grid";
        public static final String STOP_GRID_EXTRAS = "/stop_extras";
        public static final String SYSTEM = "/system";
        public static final String TEARDOWN = "/teardown";
        public static final String UPDATE_NODE_CONFIG = "/update_node_config";
        public static final String VIDEO = "/video";
        public static final String SESSION_HISTORY = "/session_history";
        public static final String UPGRADE_GRID_EXTRAS = "/upgrade_grid_extras";
        public static final String USER_AUTO_LOGON = "/user_auto_logon";
    }


    public static class Description {

        public static final
        String AUTO_UPGRADE_WEBDRIVER =
                "Automatically checks the latest versions of all drivers and upgrades the current config to use them";
        public static final
        String DOWNLOAD_CHROMEDRIVER =
                "Downloads a version of ChromeDriver to local machine";
        public static final
        String DOWNLOAD_IEDRIVER =
                "Downloads a version of IEDriver.exe to local machine";
        public static final
        String DOWNLOAD_WEBDRIVER =
                "Downloads a version of WebDriver jar to local machine";
        public static final
        String DIR =
                "Gives accesses to a shared directory, user has access to put files into it and get files from it. Directory deleted on restart.";
        public static final
        String CONFIG =
                "Returns JSON view of the full configuration of the Selenium Grid Extras";
        public static final
        String GET_FILE = "(Not yet implemented) - Retrives a file from shared Directory";
        public static final
        String PORT_INFO = "Returns parsed information on a PID occupying a given port";
        public static final
        String
                GET_NODE_CONFIG =
                "Provides the grid node config from central location";
        public static final String PS = "Gets a list of currently running processes";
        public static final
        String
                GRID_STATUS =
                "Returns status of the Selenium Grid hub/node. If currently running and what is the PID";
        public static final
        String
                IE_PROTECTED_MODE =
                "Changes protected mode for Internet Explorer on/off. No param for current status";
        public static final
        String
                KILL_ALL_BY_NAME =
                "Executes os level kill command on a given PID name";
        public static final
        String
                KILL_CHROME =
                "Executes os level kill command on all instance of Google Chrome";
        public static final
        String
                KILL_FIREFOX =
                "Executes os level kill command on all instance of Firefox";
        public static final
        String
                KILL_IE =
                "Executes os level kill command on all instance of Internet Explorer";
        public static final
        String
                KILL_SAFARI =
                "Executes os level kill command on all instance of Safari";
        public static final String KILL_PID = "Kills a given process id";
        public static final String LOG_DELETE = "Delete logs older than X Days. X is define in the config.json";
        public static final
        String
                MOVE_MOUSE =
                "Moves the computers mouse to x and y location. (Default 0,0)";
        public static final
        String
                NETSTAT =
                "Returns a system call for all ports. Use /port_info to get parsed details";
        public static final String REBOOT = "Restart the host node";
        public static final String SCREENSHOT = "Take a full OS screen Screen Shot of the node";
        public static final
        String
                SETUP =
                "Calls several pre-defined tasks to act as setup before build";
        public static final String START_GRID = "Starts an instance of Selenium Grid Hub or NodeConfig";
        public static final String STOP_GRID = "Stops grid or node process";
        public static final String STOP_GRID_EXTRAS = "Shuts down Grid Extras service";
        public static final String SYSTEM = "Returns system details about the current node";
        public static final
        String
                TEARDOWN =
                "Calls several pre-defined tasks to act as teardown after build";
        public static final
        String
                UPDATE_NODE_CONFIG =
                "Send the current config to the central location to be stored";
        public static final String VIDEO = "Starts and stops video recording";
        public static final String SESSION_HISTORY = "Displays the threads of test session on all nodes or per node basis";
        public static final String IE_MIXED_CONTENT = "Changes mixed content for Internet Explorer on/off. No param for current status";
        public static final String UPGRADE_GRID_EXTRAS = "Download specified version of Selenium Grid Extras";
        public static final String SETS_A_USER_TO_AUTOMATICALLY_LOGON_INTO_SYSTEM_POST_REBOOT = "Sets a user to automatically logon into system post reboot";
    }

    public static class HTTP {

        public static final String GET = "GET";
        public static final String JSON = "json";
    }


    public static class UI {

        public static final String BTN_SUCCESS = "btn-success";
        public static final String BTN_DANGER = "btn-danger";
        public static final String BTN_INFO = "btn-info";
        public static final String BTN = "btn";
        public static final String AUTO_LOGON_USER = "Auto Logon User";

        public static class ButtonText {

            public static final String DOWNLOAD_CHROMEDRIVER = "Download Chrome-Driver";
            public static final String DOWNLOAD_IEDRIVER = "Download IE-Driver";
            public static final String DOWNLOAD_WEBDRIVER = "Download WebDriver";
            public static final String DIR = "List Shared Dir";
            public static final String CONFIG = "Get Config";
            public static final String GET_FILE = "Get File";
            public static final String PORT_INFO = "Get Info for Port";
            public static final String GET_NODE_CONFIG = "Get Node Config";
            public static final String PS = "Get Processes";
            public static final String GRID_STATUS = "Grid Status";
            public static final String IE_PROTECTED_MODE = "Enanble/Disable Protected Mode";
            public static final String IE_MIXED_CONTENT = "Enanble/Disable Mixed Content";
            public static final String KILL_ALL_BY_NAME = "Kill all by name";
            public static final String KILL_CHROME = "Kill all chrome";
            public static final String KILL_FIREFOX = "Kill all firefox";
            public static final String KILL_IE = "Kill all IE";
            public static final String KILL_SAFARI = "Kill all Safari";
            public static final String KILL_PID = "Kill PID";
            public static final String MOVE_MOUSE = "Move mouse";
            public static final String NETSTAT = "netstat";
            public static final String REBOOT = "reboot";
            public static final String SCREENSHOT = "screenshot";
            public static final String SETUP = "setup";
            public static final String START_GRID = "StartGrid";
            public static final String STOP_GRID = "Stop Grid";
            public static final String STOP_GRID_EXTRAS = "Shut Down Grid Extras";
            public static final String SYSTEM = "System Info";
            public static final String TEARDOWN = "Teardown";
            public static final String UPDATE_NODE_CONFIG = "Get Node Config";
            public static final String SESSION_HISTORY = "Get Session History";
        }

    }
}
