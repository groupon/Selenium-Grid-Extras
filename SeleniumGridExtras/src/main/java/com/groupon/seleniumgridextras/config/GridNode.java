package com.groupon.seleniumgridextras.config;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import com.groupon.seleniumgridextras.config.capabilities.Capability;
import com.groupon.seleniumgridextras.utilities.json.JsonParserWrapper;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class GridNode {

    private LinkedList<Capability> capabilities;
    private GridNodeConfiguration configuration;
    private String loadedFromFile;

    private static Logger logger = Logger.getLogger(GridNode.class);

    public GridNode() {
        capabilities = new LinkedList<Capability>();
        configuration = new GridNodeConfiguration();
    }

    private GridNode(LinkedList<Capability> caps, GridNodeConfiguration config) {
        capabilities = caps;
        configuration = config;
    }

    public static GridNode loadFromFile(String filename) {

        String configString = readConfigFile(filename);
        JsonObject topLevelJson = new JsonParser().parse(configString).getAsJsonObject();

        String configFromFile = topLevelJson.getAsJsonObject("configuration").toString();

        GridNodeConfiguration
                nodeConfiguration =
                new Gson().fromJson(configFromFile, GridNodeConfiguration.class);

        LinkedList<Capability> filteredCapabilities = new LinkedList<Capability>();
        for (JsonElement cap : topLevelJson.getAsJsonArray("capabilities")) {
            Map capHash = JsonParserWrapper.toHashMap(cap.toString());
            if (capHash.containsKey("browserName")) {
                filteredCapabilities.add(Capability.getCapabilityFor((String) capHash.get("browserName"), capHash));
            }

        }

        GridNode node = new GridNode(filteredCapabilities, nodeConfiguration);
        node.setLoadedFromFile(filename);

        return node;
    }

    public String getLoadedFromFile() {
        return this.loadedFromFile;
    }

    public void setLoadedFromFile(String file) {
        this.loadedFromFile = file;
    }


    public LinkedList<Capability> getCapabilities() {
        return capabilities;
    }

    public GridNodeConfiguration getConfiguration() {
        return configuration;
    }

    public boolean isAppiumNode() {
        return getLoadedFromFile().startsWith("appium");
    }

    public void writeToFile(String filename) {

        try {
            File f = new File(filename);
            String config = this.toPrettyJsonString();
            FileUtils.writeStringToFile(f, config);
        } catch (Exception e) {
            logger.fatal("Could not write node config for '" + filename + "' with following error");
            logger.fatal(e.toString());
            System.exit(1);
        }


    }

    private String toPrettyJsonString() {
        return JsonParserWrapper.prettyPrintString(this);
    }


    protected static String readConfigFile(String filePath) {
        String returnString = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line = null;
            while ((line = reader.readLine()) != null) {
                returnString = returnString + line;
            }
        } catch (FileNotFoundException error) {
            String e = String.format(
                    "Error loading config from %s, %s, Will have to exit. \n%s",
                    filePath,
                    error.getMessage(),
                    Throwables.getStackTraceAsString(error));
            System.out.println(e);
            logger.error(e);

            System.exit(1);
        } catch (IOException error) {
            String e = String.format(
                    "Error loading config from %s, %s, Will have to exit. \n%s",
                    filePath,
                    error.getMessage(),
                    Throwables.getStackTraceAsString(error));
            System.out.println(e);
            logger.error(e);

            System.exit(1);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    System.out.println("Error closing buffered reader");
                    logger.warn("Error closing buffered reader");
                }
            }
        }
        return returnString;
    }


    //<Grumble Grumble>, google parsing Gson, Grumble
    protected static Map doubleToIntConverter(Map input) {
        for (Object key : input.keySet()) {

            if (input.get(key) instanceof Double) {
                input.put(key, ((Double) input.get(key)).intValue());
            }
        }

        return input;
    }

    public static Map linkedTreeMapToHashMap(LinkedTreeMap input) {
        Map output = new HashMap();
        output.putAll(input);

        return output;
    }

    //</Grubmle>


    public class GridNodeConfiguration {

        private String proxy = "com.groupon.seleniumgridextras.grid.proxies.SetupTeardownProxy";
        private int maxSession = 3;
        private int port;
        private boolean register = true;
        private int unregisterIfStillDownAfter = 10000;
        private int hubPort;
        private String hubHost;
        private String host;
        private String url;
        private Integer registerCycle;
        //    private int browserTimeout = 120;
//    private int timeout = 120;
        private int nodeStatusCheckTimeout = 10000;
        private String appiumStartCommand;

        //Only test the node status 1 time, since the limit checker is
        //Since DefaultRemoteProxy.java does this check failedPollingTries >= downPollingLimit
        private int downPollingLimit = 0;


        //java -jar 2.41.0.jar -role node -hub http://192.168.168.17:4444 -maxSession 3 -register true -unregisterIfStillDownAfter 20000 -browserTimeout 120 -timeout 120 -port

        public int getMaxSession() {
            return this.maxSession;
        }

        public void setMaxSession(int maxSession) {
            this.maxSession = maxSession;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public int getHubPort() {
            return hubPort;
        }

        public void setHubPort(int hubPort) {
            this.hubPort = hubPort;
        }

        public String getHubHost() {
            return hubHost;
        }

        public void setHubHost(String hubHost) {
            this.hubHost = hubHost;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getRegisterCycle() {
            return registerCycle.intValue();
        }

        public void setRegisterCycle(int registerCycle) {
            this.registerCycle = new Integer(registerCycle);
        }

        public String getAppiumStartCommand() {
            return appiumStartCommand;
        }

        public void setAppiumStartCommand(String appiumStartCommand) {
            this.appiumStartCommand = appiumStartCommand;
        }
    }

}


