package com.groupon.seleniumgridextras.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import com.groupon.seleniumgridextras.config.driver.*;
import com.groupon.seleniumgridextras.utilities.FileIOUtility;
import com.groupon.seleniumgridextras.utilities.json.JsonParserWrapper;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.*;

public class Config {

    public static final String ACTIVATE_MODULES = "active_modules";
    public static final String DISABLED_MODULES = "disabled_modules";
    public static final String SETUP = "setup";
    public static final String TEAR_DOWN = "tear_down";
    public static final String GRID = "grid";
    public static final String WEBDRIVER = "webdriver";
    public static final String IEDRIVER = "iedriver";
    public static final String EDGEDRIVER = "edgedriver";
    public static final String CHROME_DRIVER = "chromedriver";
    public static final String GECKO_DRIVER = "geckodriver";
    public static final String SHARED_DIR = "expose_directory";

    public static final String AUTO_START_NODE = "auto_start_node";
    public static final String AUTO_START_HUB = "auto_start_hub";
    public static final String DEFAULT_ROLE = "default_role";
    public static final String HUB_CONFIG = "hub_config";
    public static final String NODE_CONFIG_FILES = "node_config_files";
    public static final String HUB_CONFIG_FILES = "hub_config_files";
    public static final String HUB_ADDITIONAL_CLASSPATH = "hub_additional_classpath";
    public static final String NODE_ADDITIONAL_CLASSPATH = "node_additional_classpath";

    public static final String GRID_JVM_OPTIONS = "grid_jvm_options";
    public static final String GRID_JVM_X_OPTIONS = "grid_jvm_x_options";
    public static final String GRID_EXTRAS_JVM_OPTIONS = "grid_extras_jvm_options";
    public static final String GRID_EXTRAS_PORT = "grid_extras_port";

    public static final String AUTO_UPDATE_DRIVERS = "auto_update_drivers";
    public static final String AUTO_UPDATE_BROWSER_VERSIONS = "auto_update_browser_versions";
    public static final String REBOOT_AFTER_SESSIONS = "reboot_after_sessions";
    public static final String UNREGISTER_NODE_DURING_REBOOT = "unregisterNodeDuringReboot";

    public static final String VIDEO_RECORDING_OPTIONS = "video_recording_options";
    public static final String HTTP_REQUEST_TIMEOUT = "http_request_timeout";
    public static final String CONFIG_PULLER_HTTP_TIMEOUT = "config_puller_http_timeout";
    public static final String HTML_RENDER_OPTIONS = "html_render_options";

    public static final String LOG_MAXIMUM_SIZE = "log_maximum_size";

    public static final String HOST_IP = "host";
    public static final String GRID_EXTRAS_RELEASE_URL = "grid_extras_release_url";
    public static final String GRID_EXTRAS_AUTO_UPDATE = "grid_extras_auto_update";
    public static final String GRID_EXTRAS_AUTO_UPDATE_CHECK_INTERVAL = "grid_extras_auto_update_check_interval";
    public static final String LOG_MAXIMUM_AGE_MS = "log_maximum_age_ms";

    public static final String ENABLE_SESSION_HISTORY = "enable_session_history";

    private static Logger logger = Logger.getLogger(Config.class);


    protected Map theConfigMap;
    protected List<GridNode> gridNodeList;
    protected List<GridHub> gridHubList;

    public Config() {
        theConfigMap = new HashMap();
        gridNodeList = new LinkedList<GridNode>();
        gridHubList = new LinkedList<GridHub>();
        getConfigMap().put(NODE_CONFIG_FILES, new LinkedList<String>());
        getConfigMap().put(HUB_CONFIG_FILES, new LinkedList<String>());
        initialize();
    }

    public Config(Boolean emptyConfig) {
        theConfigMap = new HashMap();
        gridNodeList = new LinkedList<GridNode>();
        gridHubList = new LinkedList<GridHub>();
        getConfigMap().put(NODE_CONFIG_FILES, new LinkedList<String>());
        getConfigMap().put(HUB_CONFIG_FILES, new LinkedList<String>());
        if (!emptyConfig) {
            initialize();
        }

    }

    public List<String> getNodeConfigFiles() {
        return (List<String>) getConfigMap().get(NODE_CONFIG_FILES);
    }

    public List<GridNode> getNodes() {
        return this.gridNodeList;
    }

    public List<String> getHubConfigFiles() {
        return (List<String>) getConfigMap().get(HUB_CONFIG_FILES);
    }

    public List<GridHub> getHubs() {
        return this.gridHubList;
    }

    public void addNode(GridNode node, String filename) {
        getNodes().add(node);
        addNodeConfigFile(filename);
    }

    public void addHub(GridHub hub, String filename) {
        getHubs().add(hub);
        addHubConfigFile(filename);
    }

    public void loadNodeClasses() {
        boolean isSelenium3 = RuntimeConfig.getConfig().getWebdriver().getVersion().startsWith("3.");
        for (String filename : getNodeConfigFiles()) {
            GridNode node = GridNode.loadFromFile(filename, isSelenium3);
            getNodes().add(node);
        }
    }

    public void loadHubClasses() { // TODO do I need this ?
        for (String filename : getHubConfigFiles()) {
            GridHub hub = GridHub.loadFromFile(filename);
            getHubs().add(hub);
        }
    }

    private void initialize() {
        getConfigMap().put(ACTIVATE_MODULES, new ArrayList<String>());
        getConfigMap().put(DISABLED_MODULES, new ArrayList<String>());
        getConfigMap().put(SETUP, new ArrayList<String>());
        getConfigMap().put(TEAR_DOWN, new ArrayList<String>());

        getConfigMap().put(GRID, new LinkedTreeMap());
        initializeWebdriver();
        initializeIEDriver();
        initializeChromeDriver();
        initializeGeckoDriver();

        getConfigMap().put(NODE_CONFIG_FILES, new LinkedList<String>());
        getConfigMap().put(HUB_CONFIG_FILES, new LinkedList<String>());

        initializeHubConfig();

        getConfigMap().put(HUB_ADDITIONAL_CLASSPATH, new ArrayList<String>());
        getConfigMap().put(NODE_ADDITIONAL_CLASSPATH, new ArrayList<String>());
        getConfigMap().put(GRID_JVM_OPTIONS, new HashMap<String, Object>());
        getConfigMap().put(GRID_EXTRAS_JVM_OPTIONS, new HashMap<String, Object>());
        getConfigMap().put(GRID_EXTRAS_PORT, 3000);

        getConfigMap().put(AUTO_UPDATE_DRIVERS, "");
        getConfigMap().put(AUTO_UPDATE_BROWSER_VERSIONS, "");

        getConfigMap().put(REBOOT_AFTER_SESSIONS, 0);
        getConfigMap().put(UNREGISTER_NODE_DURING_REBOOT, "true");
        initializeVideoRecorder();
        getConfigMap().put(HTML_RENDER_OPTIONS, new HtmlConfig());

        getConfigMap().put(HOST_IP, null);

    }

    public boolean getAutoUpdateDrivers() {
        if (getConfigMap().get(AUTO_UPDATE_DRIVERS).equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    public void setAutoUpdateDrivers(String input) {
        if (input.equals("1")) {
            getConfigMap().put(AUTO_UPDATE_DRIVERS, input);
        } else {
            getConfigMap().put(AUTO_UPDATE_DRIVERS, "0");
        }

    }

    public boolean getAutoUpdateBrowserVersions() {
        if (getConfigMap().get(AUTO_UPDATE_BROWSER_VERSIONS).equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    public void setAutoUpdateBrowserVersions(String input) {
        if (input.equals("1")) {
            getConfigMap().put(AUTO_UPDATE_BROWSER_VERSIONS, input);
        } else {
            getConfigMap().put(AUTO_UPDATE_BROWSER_VERSIONS, "0");
        }

    }


    public void initializeVideoRecorder() {
        getConfigMap().put(VIDEO_RECORDING_OPTIONS, new VideoRecordingOptions());
    }

    private void initializeIEDriver() {
        getConfigMap().put(IEDRIVER, new IEDriver());
    }

    private void initializeChromeDriver() {
        getConfigMap().put(CHROME_DRIVER, new ChromeDriver());
    }

    private void initializeGeckoDriver() {
        getConfigMap().put(GECKO_DRIVER, new GeckoDriver());
    }

    public void addNodeConfigFile(String filename) {
        LinkedList<String> files = (LinkedList<String>) getConfigMap().get(NODE_CONFIG_FILES);
        files.add(filename);
    }

    public void addHubConfigFile(String filename) {
        LinkedList<String> hubFiles = (LinkedList<String>) getConfigMap().get(HUB_CONFIG_FILES);
        hubFiles.add(filename);
    }


    private void initializeHubConfig() {
        getConfigMap().put(HUB_CONFIG, new Hub());
    }

    private void initializeWebdriver() {
        getConfigMap().put(WEBDRIVER, new WebDriver());
    }

    private Map getConfigMap() {
        return theConfigMap;
    }


    public static Config initilizedFromUserInput() {
        Config config = new Config(true);
        config.initializeWebdriver();
        config.initializeHubConfig();
        config.initializeIEDriver();
        config.initializeChromeDriver();
        config.initializeGeckoDriver();

        return FirstTimeRunConfig.customiseConfig(config);
    }

    public void overwriteConfig(Map overwrites) {
        if (overwrites.containsKey("theConfigMap")) {
            HashMapMerger.overwriteMergeStrategy(getConfigMap(),
                    (Map<String, Object>) overwrites.get("theConfigMap"));
        }
    }


    public VideoRecordingOptions getVideoRecording() {
        return (VideoRecordingOptions) getConfigMap().get(VIDEO_RECORDING_OPTIONS);
    }

    public List<String> getActivatedModules() {
        return (List<String>) getConfigMap().get(ACTIVATE_MODULES);
    }

    public List<String> getDisabledModules() {
        return (List<String>) getConfigMap().get(DISABLED_MODULES);
    }

    public String getSharedDirectory() {
        return (String) getConfigMap().get(SHARED_DIR);
    }

    public List<String> getSetup() {
        return (List<String>) getConfigMap().get(SETUP);
    }

    public List<String> getTeardown() {
        return (List<String>) getConfigMap().get(TEAR_DOWN);
    }

    public LinkedTreeMap getGrid() {
        return (LinkedTreeMap) getConfigMap().get(GRID);
    }

    public DriverInfo getIEdriver() {
        try {
            return (IEDriver) getConfigMap().get(IEDRIVER);
        } catch (ClassCastException e) {
            LinkedTreeMap
                    stringMapFromGoogleWhoCantUseHashMapOnNestedObjects =
                    (LinkedTreeMap) getConfigMap().get(IEDRIVER);
            IEDriver ieDriver = new IEDriver();

            ieDriver.putAll(stringMapFromGoogleWhoCantUseHashMapOnNestedObjects);

            getConfigMap().put(IEDRIVER, ieDriver);

            return ieDriver;
        }
    }

    public DriverInfo getChromeDriver() {
        try {
            return (ChromeDriver) getConfigMap().get(CHROME_DRIVER);
        } catch (ClassCastException e) {
            LinkedTreeMap
                    stringMapFromGoogleWhoCantUseHashMapOnNestedObjects =
                    (LinkedTreeMap) getConfigMap().get(CHROME_DRIVER);
            DriverInfo chromeDriver = new ChromeDriver();

            chromeDriver.putAll(stringMapFromGoogleWhoCantUseHashMapOnNestedObjects);

            getConfigMap().put(CHROME_DRIVER, chromeDriver);

            return chromeDriver;
        }
    }

    public DriverInfo getGeckoDriver() {
        try {
            return (GeckoDriver) getConfigMap().get(GECKO_DRIVER);
        } catch (ClassCastException e) {
            LinkedTreeMap
                    stringMapFromGoogleWhoCantUseHashMapOnNestedObjects =
                    (LinkedTreeMap) getConfigMap().get(GECKO_DRIVER);
            DriverInfo geckoDriver = new GeckoDriver();

            geckoDriver.putAll(stringMapFromGoogleWhoCantUseHashMapOnNestedObjects);

            getConfigMap().put(GECKO_DRIVER, geckoDriver);

            return geckoDriver;
        }
    }

    public DriverInfo getEdgeDriver() {
        try {
            EdgeDriver driver;
            driver = (EdgeDriver) getConfigMap().get(EDGEDRIVER);
            if (driver == null){
                driver = new EdgeDriver();
                getConfigMap().put(EDGEDRIVER, driver);

            }
            return driver;
        } catch (ClassCastException e) {
            LinkedTreeMap
                    stringMapFromGoogleWhoCantUseHashMapOnNestedObjects =
                    (LinkedTreeMap) getConfigMap().get(EDGEDRIVER);
            DriverInfo edgeDriver = new EdgeDriver();

            edgeDriver.putAll(stringMapFromGoogleWhoCantUseHashMapOnNestedObjects);

            getConfigMap().put(EDGEDRIVER, edgeDriver);

            return edgeDriver;
        }
    }


    public WebDriver getWebdriver() {
        try {
            return (WebDriver) getConfigMap().get(WEBDRIVER);
        } catch (ClassCastException e) {
            LinkedTreeMap
                    stringMapFromGoogleWhoCantUseHashMapOnNestedObjects =
                    (LinkedTreeMap) getConfigMap().get(WEBDRIVER);
            WebDriver webDriver = new WebDriver();

            webDriver.putAll(stringMapFromGoogleWhoCantUseHashMapOnNestedObjects);

            getConfigMap().put(WEBDRIVER, webDriver);

            return webDriver;
        }
    }


    public void writeToDisk(String file) {
        try {
            File f = new File(file);
            Map temp = new HashMap();
            temp.put("theConfigMap", getConfigMap());

            String config = toPrettyJsonString(temp);
            FileIOUtility.writeToFile(f, config);
        } catch (Exception error) {
            logger.fatal("Could not write default config file, exit with error " + error.toString());
            System.exit(1);
        }
    }

    public void addSetupTask(String task) {
        getSetup().add(task);
    }

    public void addTeardownTask(String task) {
        getTeardown().add(task);
    }

    public void addActivatedModules(String module) {
        getActivatedModules().add(module);
    }

    public void addDisabledModule(String module) {
        getDisabledModules().add(module);
    }

    public void setSharedDir(String sharedDir) {
        getConfigMap().put(SHARED_DIR, sharedDir);
    }

    public String toJsonString() {
        return new Gson().toJson(this);
    }

    public String toJson() {
        return toPrettyJsonString(this);
    }

    public String toPrettyJsonString(Object object) {
        return JsonParserWrapper.prettyPrintString(object);
    }

    public boolean checkIfModuleEnabled(String module) {
        return getActivatedModules().contains(module);
    }


    public JsonObject asJsonObject() {
        return (JsonObject) new JsonParser().parse(this.toJsonString());
    }

    public int getHttpRequestTimeout() {
        return (Integer) getConfigMap().get(HTTP_REQUEST_TIMEOUT);
    }

    public void setHttpRequestTimeout(int timeout) {
        getConfigMap().put(HTTP_REQUEST_TIMEOUT, timeout);
    }

    public void setConfigPullerHttpTimeout(int timeout) {
        getConfigMap().put(CONFIG_PULLER_HTTP_TIMEOUT, timeout);
    }

    public int getConfigPullerHttpTimeout() {
        return (Integer) getConfigMap().get(CONFIG_PULLER_HTTP_TIMEOUT);
    }

    public void setDefaultRole(String defaultRole) {
        getConfigMap().put(DEFAULT_ROLE, defaultRole);
    }

    public void setHostIp(String hostIp) {
        getConfigMap().put(HOST_IP, hostIp);
    }

    public void setAutoStartHub(String autoStartHub) {
        getConfigMap().put(AUTO_START_HUB, autoStartHub);
    }

    public void setAutoStartNode(String autoStartNode) {
        getConfigMap().put(AUTO_START_NODE, autoStartNode);
    }

    public boolean getAutoStartNode() {
        return getConfigMap().get(AUTO_START_NODE).equals("1") ? true : false;
    }

    public boolean getAutoStartHub() {
        return getConfigMap().get(AUTO_START_HUB).equals("1") ? true : false;
    }

    public String getGridJvmOptions() {
        logger.info(getConfigMap().get(GRID_JVM_OPTIONS));
        return mapToJvmParams((Map<String, Object>) getConfigMap().get(GRID_JVM_OPTIONS));
    }

    public String getGridJvmXOptions() {
        logger.info(getConfigMap().get(GRID_JVM_X_OPTIONS));
        Object options = getConfigMap().get(GRID_JVM_X_OPTIONS);
        if (!(options instanceof String)) {
            return ""; // If the options is null or an invalid type, just return the empty string.
        }
        return ((String)options).trim() + " "; // The convention is to return a string with a trailing space
    }

    public String getGridExtrasJvmOptions() {
        logger.info(getConfigMap().get(GRID_EXTRAS_JVM_OPTIONS));
        return mapToJvmParams((Map<String, Object>) getConfigMap().get(GRID_EXTRAS_JVM_OPTIONS));
    }

    public int getLogMaximumSize() {
        return Integer.valueOf(getConfigMap().get(LOG_MAXIMUM_SIZE).toString());
    }

    public void setLogMaximumSize(String size) {
        getConfigMap().put(LOG_MAXIMUM_SIZE, size);
    }

    public void addGridJvmOptions(String key, Object value) {
        logger.info(key + " " + value);
        Map<String, Object> params = (Map<String, Object>) getConfigMap().get(GRID_JVM_OPTIONS);
        params.put(key, String.valueOf(value));
    }

    public void addGridExtrasJvmOptions(String key, Object value) {
        logger.info(key + " " + value);
        Map<String, Object> params = (Map<String, Object>) getConfigMap().get(GRID_EXTRAS_JVM_OPTIONS);
        params.put(key, String.valueOf(value));
    }

    public File getLogsDirectory() {
        return new File("log");
    }

    protected String mapToJvmParams(Map<String, Object> params) {
        String returnString = "";

        for (String key : params.keySet()) {
            Object value = params.get(key);
            String formattedValue = "";

            if (value instanceof Number) {
                formattedValue = "" + ((Number) value).intValue() + "";
            } else {
                formattedValue = value.toString();
            }

            returnString = returnString + "-D" + key + "=" + formattedValue + " ";
        }

        return returnString;
    }


    public Hub getHub() {

        try {
            return (Hub) getConfigMap().get(HUB_CONFIG);
        } catch (ClassCastException e) {
            LinkedTreeMap
                    stringMapFromGoogleWhoCantUseHashMapOnNestedObjects =
                    (LinkedTreeMap) getConfigMap().get(HUB_CONFIG);
            Hub hubConfig = new Hub();

            hubConfig.putAll(stringMapFromGoogleWhoCantUseHashMapOnNestedObjects);

            getConfigMap().put(HUB_CONFIG, hubConfig);

            return hubConfig;
        }
    }

    public void setHub(Hub hub) {
        getConfigMap().put(HUB_CONFIG, hub);
    }


    public String getDefaultRole() {
        return (String) getConfigMap().get(DEFAULT_ROLE);
    }

    public void setRebootAfterSessions(String sessions) {
        getConfigMap().put(REBOOT_AFTER_SESSIONS, sessions);
    }

    public int getRebootAfterSessions() {
        return Integer.valueOf((String) getConfigMap().get(REBOOT_AFTER_SESSIONS));
    }
    
    public void setUnregisterNodeDuringReboot(String unregisterNodeDuringReboot) {
        getConfigMap().put(UNREGISTER_NODE_DURING_REBOOT, unregisterNodeDuringReboot);
    }

    public boolean getUnregisterNodeDuringReboot() {
        return Boolean.valueOf((String) getConfigMap().get(UNREGISTER_NODE_DURING_REBOOT));
    }

    public File getConfigsDirectory() {
        return new File("configs");
    }

    public String getCentralConfigFileName() {
        return "central_config_repo_config.txt";
    }

    public HtmlConfig getHtmlRender() {
        return (HtmlConfig) getConfigMap().get(HTML_RENDER_OPTIONS);
    }

    public String getHostIp() {
        return (String) getConfigMap().get(HOST_IP);
    }

    public void setGridExtrasReleaseUrl(String url) {
        getConfigMap().put(GRID_EXTRAS_RELEASE_URL, url);
    }

    public String getGridExtrasReleaseUrl() {
        return (String) getConfigMap().get(GRID_EXTRAS_RELEASE_URL);
    }

    public void setGridExtrasPort(String port) {
        getConfigMap().put(GRID_EXTRAS_PORT, port);
    }

    public Integer getGridExtrasPort() {
        Object value = getConfigMap().get(GRID_EXTRAS_PORT);
        if (value instanceof Integer) {
            return ((Integer) value).intValue();
        } else if (value instanceof String) {
            return Integer.valueOf((String) value).intValue();
        } else if (value instanceof Double) {
            return ((Double) value).intValue();
        } else {
            return 3000;
        }
    }

    public void setLogMaximumAge(long milliseconds) {
        getConfigMap().put(LOG_MAXIMUM_AGE_MS, milliseconds);
    }

    public long getLogMaximumAge() {
        Object value = getConfigMap().get(LOG_MAXIMUM_AGE_MS);

        if (value instanceof Long) {
            return ((Long) value).longValue();
        } else if (value instanceof String) {
            return Long.valueOf((String) value).longValue();
        } else {
            return DefaultConfig.LOG_MAX_AGE;
        }
    }

    public void setGridExtrasAutoUpdateCheckInterval(long gridExtrasAutoUpdateCheckInterval) {
        getConfigMap().put(GRID_EXTRAS_AUTO_UPDATE_CHECK_INTERVAL, String.valueOf(gridExtrasAutoUpdateCheckInterval));
    }

    public long getGridExtrasAutoUpdateCheckInterval() {
        if (getConfigMap().containsKey(GRID_EXTRAS_AUTO_UPDATE_CHECK_INTERVAL)) {
            return Long.valueOf((String) getConfigMap().get(GRID_EXTRAS_AUTO_UPDATE_CHECK_INTERVAL));
        } else {
            return DefaultConfig.GRID_EXTRAS_AUTO_UPDATE_CHECK_INTERVAL;
        }
    }

    public boolean getEnableSessionHistory() {
        if (getConfigMap().get(ENABLE_SESSION_HISTORY).equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    public void setEnableSessionHistory(String input) {
        if (input.equals("1")) {
            getConfigMap().put(ENABLE_SESSION_HISTORY, input);
        } else {
            getConfigMap().put(ENABLE_SESSION_HISTORY, "0");
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> getAdditionalHubConfig() {
        return (List<String>) getConfigMap().get(HUB_ADDITIONAL_CLASSPATH);
    }

    @SuppressWarnings("unchecked")
    public void addHubClasspathItem(String item) {
        ((List<String>) getConfigMap().get(HUB_ADDITIONAL_CLASSPATH)).add(item);
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getAdditionalNodeConfig() {
        return (List<String>) getConfigMap().get(NODE_ADDITIONAL_CLASSPATH);
    }

    @SuppressWarnings("unchecked")
    public void addNodeClasspathItem(String item) {
        ((List<String>) getConfigMap().get(NODE_ADDITIONAL_CLASSPATH)).add(item);
    }
}
