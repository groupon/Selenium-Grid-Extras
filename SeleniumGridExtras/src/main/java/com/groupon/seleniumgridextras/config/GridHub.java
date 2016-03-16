package com.groupon.seleniumgridextras.config;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GridHub {
    private GridHubConfiguration configuration;
    private String loadedFromFile;

    private static Logger logger = Logger.getLogger(GridHub.class);

    public GridHub() {
        configuration = new GridHubConfiguration();
    }

    private GridHub(GridHubConfiguration config) {
        configuration = config;
    }

    public static GridHub loadFromFile(String filename) {
        String configString = readConfigFile(filename);

        GridHubConfiguration
                hubConfiguration =
                new Gson().fromJson(configString, GridHubConfiguration.class);

        GridHub hub = new GridHub(hubConfiguration);
        hub.setLoadedFromFile(filename);

        return hub;
    }

    public String getLoadedFromFile() {
        return this.loadedFromFile;
    }

    public void setLoadedFromFile(String file) {
        this.loadedFromFile = file;
    }

    public GridHubConfiguration getConfiguration() {
        return configuration;
    }

    public void writeToFile(String filename) {
        try {
            File f = new File(filename);
            String config = this.toPrettyJsonString();
            FileUtils.writeStringToFile(f, config);
        } catch (Exception e) {
            logger.fatal("Could not write hub config for '" + filename + "' with following error");
            logger.fatal(e.toString());
            System.exit(1);
        }
    }

    private String toPrettyJsonString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(configuration);
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

    public static Map getMapFromString(String input) {
        return new Gson().fromJson(input, HashMap.class);
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


    public class GridHubConfiguration {
        private String host;
        private int port;
        private long newSessionWaitTimeout = 25000;
        ArrayList<String> servlets = new ArrayList<String>() {{
            add("com.groupon.seleniumgridextras.grid.servlets.ProxyStatusJsonServlet");
        }};
        private String prioritizer;
        private String capabilityMatcher = "org.openqa.grid.internal.utils.DefaultCapabilityMatcher";
        private boolean throwOnCapabilityNotPresent = true;
        private long nodePolling = 5000;
        private long cleanUpCycle = 5000;
        private long browserTimeout = 120000;
        private long timeout = 120000;
        private int maxSession = 5;


        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public long getNewSessionWaitTimeout() {
            return newSessionWaitTimeout;
        }

        public void setNewSessionWaitTimeout(long newSessionWaitTimeout) {
            this.newSessionWaitTimeout = newSessionWaitTimeout;
        }

        public ArrayList<String> getServlets() {
            return servlets;
        }

        public void setServlets(ArrayList<String> servlets) {
            this.servlets = servlets;
        }

        public String getPrioritizer() {
            return prioritizer;
        }

        public void setPrioritizer(String prioritizer) {
            this.prioritizer = prioritizer;
        }

        public String getCapabilityMatcher() {
            return capabilityMatcher;
        }

        public void setCapabilityMatcher(String capabilityMatcher) {
            this.capabilityMatcher = capabilityMatcher;
        }

        public boolean isThrowOnCapabilityNotPresent() {
            return throwOnCapabilityNotPresent;
        }

        public void setThrowOnCapabilityNotPresent(boolean throwOnCapabilityNotPresent) {
            this.throwOnCapabilityNotPresent = throwOnCapabilityNotPresent;
        }

        public long getNodePolling() {
            return nodePolling;
        }

        public void setNodePolling(long nodePolling) {
            this.nodePolling = nodePolling;
        }

        public long getCleanUpCycle() {
            return cleanUpCycle;
        }

        public void setCleanUpCycle(long cleanUpCycle) {
            this.cleanUpCycle = cleanUpCycle;
        }

        public long getBrowserTimeout() {
            return browserTimeout;
        }

        public void setBrowserTimeout(long browserTimeout) {
            this.browserTimeout = browserTimeout;
        }

        public long getTimeout() {
            return timeout;
        }

        public void setTimeout(long timeout) {
            this.timeout = timeout;
        }

        public int getMaxSession() {
            return maxSession;
        }

        public void setMaxSession(int maxSession) {
            this.maxSession = maxSession;
        }
    }

}


