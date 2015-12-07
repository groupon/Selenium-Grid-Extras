package com.groupon.seleniumgridextras.grid;

import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.ExecuteCommand;
import com.groupon.seleniumgridextras.config.GridNode;
import com.groupon.seleniumgridextras.config.GridNode.GridNodeConfiguration;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import com.groupon.seleniumgridextras.utilities.json.JsonResponseBuilder;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class GridStarter {

    private static Logger logger = Logger.getLogger(GridStarter.class);

    public static String getOsSpecificHubStartCommand(String configFile, Boolean windows) {

        StringBuilder command = new StringBuilder();
        command.append(getJavaExe() + " ");
        command.append(RuntimeConfig.getConfig().getGridJvmXOptions());
        command.append(RuntimeConfig.getConfig().getGridJvmOptions());
        command.append("-cp " + getOsSpecificQuote() + getGridExtrasJarFilePath());

        String jarPath = RuntimeConfig.getOS().getPathSeparator() + getCurrentWebDriverJarPath();
        
        List<String> additionalClassPathItems = RuntimeConfig.getConfig().getAdditionalHubConfig();
        for(String additionalJarPath : additionalClassPathItems) {
        	command.append(RuntimeConfig.getOS().getPathSeparator() + additionalJarPath);
        }

        command.append(jarPath + getOsSpecificQuote());
        command.append(" org.openqa.grid.selenium.GridLauncher -role hub ");
//	    command.append(RuntimeConfig.getConfig().getHub().getStartCommand()); // TODO Removed 

        String logFile = configFile.replace("json", "log");
        String logCommand = " -log log" + RuntimeConfig.getOS().getFileSeparator() + logFile;

        command.append(logCommand);
//      command.append(" -browserTimeout 120 -timeout 120"); // TODO Removed
        command.append(" -hubConfig " + configFile);

        logger.info("Hub Start Command: \n\n" + String.valueOf(command));
        return String.valueOf(command);
    }

    private static String getOsSpecificQuote() {
        if (RuntimeConfig.getOS().isWindows()) {
            return "\"";
        } else {
            return "";
        }

    }


    public static JsonObject startAllNodes(JsonResponseBuilder jsonResponseBuilder) {
        for (String command : getStartCommandsForNodes(RuntimeConfig.getOS().isWindows())) {
            logger.info(command);
            try {

                JsonObject startResponse = startOneNode(command);
                logger.info(startResponse);

                if (!startResponse.get(JsonCodec.EXIT_CODE).toString().equals("0")) {
                    jsonResponseBuilder
                            .addKeyValues(JsonCodec
                                    .ERROR,
                                    "Error running " + startResponse.get(JsonCodec.ERROR).toString());
                }
            } catch (Exception e) {
                jsonResponseBuilder
                        .addKeyValues(JsonCodec.ERROR, "Error running " + command);
                jsonResponseBuilder
                        .addKeyValues(JsonCodec.ERROR, e.toString());

                e.printStackTrace();
            }

        }

        return jsonResponseBuilder.getJson();
    }

    public static JsonObject startAllHubs(JsonResponseBuilder jsonResponseBuilder) {
        for (String configFile : RuntimeConfig.getConfig().getHubConfigFiles()) {
            String command = getOsSpecificHubStartCommand(configFile, RuntimeConfig.getOS().isWindows());
            logger.info(command);

            try {
                JsonObject startResponse = ExecuteCommand.execRuntime(command, false);
                logger.info(startResponse);

                if (!startResponse.get(JsonCodec.EXIT_CODE).toString().equals("0")) {
                    jsonResponseBuilder
                        .addKeyValues(JsonCodec.ERROR, "Error running " + startResponse.get(JsonCodec.ERROR).toString());
                }
            } catch (Exception e) {
                jsonResponseBuilder
                    .addKeyValues(JsonCodec.ERROR, "Error running " + command);
                jsonResponseBuilder
                    .addKeyValues(JsonCodec.ERROR, e.toString());

                e.printStackTrace();
            }
        }
        return jsonResponseBuilder.getJson();
    }

    public static JsonObject startOneNode(String command) {
        return ExecuteCommand.execRuntime(command, false);
    }

    public static List<String> getStartCommandsForNodes(Boolean windows) {
        List<String> commands = new LinkedList<String>();

        for (String configFile : RuntimeConfig.getConfig().getNodeConfigFiles()) {

            String
                    backgroundCommand =
                    getBackgroundStartCommandForNode(getNodeStartCommand(configFile, windows),
                            configFile.replace("json", "log"),
                            windows);

            commands.add(backgroundCommand);
        }

        logger.info("Node Start Command: \n\n" + String.valueOf(commands));
        return commands;
    }

    protected static String getBackgroundStartCommandForWebNode(String command, String logFile) {
        String logFileFullPath = "log" + RuntimeConfig.getOS().getFileSeparator() + logFile;
        return command + " -log " + logFileFullPath;
    }

    protected static String getBackgroundStartCommandForAppiumNode(String command, String logFile) {
        String workingDirectory = System.getProperty("user.dir");
        String logFileFullPath = workingDirectory + RuntimeConfig.getOS().getFileSeparator() + "log" +
                RuntimeConfig.getOS().getFileSeparator() + logFile;
        return command + " --log " + logFileFullPath;
    }

    protected static String getBackgroundStartCommandForNode(String command, String logFile,
                                                             Boolean windows) {
        if (logFile.startsWith("appium")) {
            command = getBackgroundStartCommandForAppiumNode(command, logFile);
        } else {
            command = getBackgroundStartCommandForWebNode(command, logFile);
        }

        if (windows) {


            String batchFile = logFile.replace("log", "bat");
            writeBatchFile(batchFile, command);
            return "start /MIN " + batchFile;
        } else {
            return command;
        }

    }

    protected static String getWebNodeStartCommand(String configFile, Boolean windows) {

        String host = "";

        if (RuntimeConfig.getHostIp() != null) {
            host = " -host " + RuntimeConfig.getHostIp();
        }

        if (RuntimeConfig.getOS().getHostName() != null) {
            host = " -friendlyHostName " + RuntimeConfig.getOS().getHostName();
        }

        StringBuilder command = new StringBuilder();
        command.append(getJavaExe() + " ");
        command.append(RuntimeConfig.getConfig().getGridJvmXOptions());
        command.append(RuntimeConfig.getConfig().getGridJvmOptions());

        if (windows) {
            command.append(getIEDriverExecutionPathParam());
            command.append(getEdgeDriverExecutionPathParam());
        }

        command.append(getChromeDriverExecutionPathParam());
        command.append(" -cp " + getOsSpecificQuote() + getGridExtrasJarFilePath());
        command.append(RuntimeConfig.getOS().getPathSeparator() + getCurrentWebDriverJarPath()
                + getOsSpecificQuote());
        command.append(" org.openqa.grid.selenium.GridLauncher -role wd ");
        command.append(host);
        command.append(" -nodeConfig " + configFile);

        return String.valueOf(command);
    }

    protected static String getAppiumNodeStartCommand(String configFile) {
        StringBuilder command = new StringBuilder();

        GridNodeConfiguration config = GridNode.loadFromFile(configFile).getConfiguration();
        command.append(config.getAppiumStartCommand());
        command.append(" -p " + config.getPort());

        String workingDirectory = System.getProperty("user.dir");
        String configFileFullPath = workingDirectory + RuntimeConfig.getOS().getFileSeparator() + configFile;
        command.append(" --log-timestamp --nodeconfig " + configFileFullPath);

        return String.valueOf(command);
    }

    protected static String getNodeStartCommand(String configFile, Boolean windows) {
        if (configFile.startsWith("appium")) {
            return getAppiumNodeStartCommand(configFile);
        } else {
            return getWebNodeStartCommand(configFile, windows);
        }
    }

    protected static String getIEDriverExecutionPathParam() {
        if (RuntimeConfig.getOS().isWindows()) { //TODO: Clean this conditional up and test!!!
            return " -Dwebdriver.ie.driver=" + RuntimeConfig.getConfig().getIEdriver()
                    .getExecutablePath();
        } else {
            return "";
        }
    }

    public static String getEdgeDriverExecutionPathParam() {
        return String.format(" -Dwebdriver.edge.driver=\"%s\"", RuntimeConfig.getConfig().getEdgeDriver().getExecutablePath());
    }

    protected static String getChromeDriverExecutionPathParam() {
        return " -Dwebdriver.chrome.driver=" + RuntimeConfig.getConfig().getChromeDriver()
                .getExecutablePath();
    }

    protected static String buildBackgroundStartCommand(String command, Boolean windows) {
        String backgroundCommand;
        final String batchFile = "start_hub.bat";

        if (windows) {
            writeBatchFile(batchFile, command);
            backgroundCommand =
                    "start " + batchFile;
        } else {
            backgroundCommand = command;
        }

        return backgroundCommand;
    }

    protected static String getGridExtrasJarFilePath() {
        return RuntimeConfig.getSeleniumGridExtrasJarFile().getAbsolutePath();
    }

    protected static String getCurrentWebDriverJarPath() {
        return getWebdriverHome() + RuntimeConfig.getOS().getFileSeparator() + getWebdriverVersion()
                + ".jar";
    }

    protected static String getWebdriverVersion() {
        return RuntimeConfig.getConfig().getWebdriver().getVersion();
    }


    protected static String getWebdriverHome() {
        return RuntimeConfig.getConfig().getWebdriver().getDirectory();
    }

    private static void writeBatchFile(String filename, String input) {

        File file = new File(filename);

        try {
            FileUtils.writeStringToFile(file, input);
        } catch (Exception error) {
            logger.fatal("Could not write default config file, exit with error " + error.toString());
            System.exit(1);
        }
    }

    private static String getJavaExe() {
        if (RuntimeConfig.getOS().isWindows()) {
            return "java";
        } else {
            String javaHome = System.getProperty("java.home");
            File f = new File(javaHome);
            f = new File(f, "bin");
            f = new File(f, "java");
            return f.getAbsolutePath();
        }
    }
}
