package com.groupon.seleniumgridextras.tasks;

import com.google.common.base.Throwables;
import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.Version;
import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.downloader.GridExtrasDownloader;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.FileIOUtility;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UpgradeGridExtrasTask extends ExecuteOSTask {
    private static Logger logger = Logger.getLogger(UpgradeGridExtrasTask.class);
    private GridExtrasDownloader downloader = new GridExtrasDownloader();

    private static final String startGridExtrasShellFileName = "start_grid_extras";


    public UpgradeGridExtrasTask() {
        setEndpoint(TaskDescriptions.Endpoints.UPGRADE_GRID_EXTRAS);
        setDescription(TaskDescriptions.Description.UPGRADE_GRID_EXTRAS);
        JsonObject params = new JsonObject();
        params.addProperty(JsonCodec.GridExtras.VERSION,
                "Version of Selenium Grid Extras to download, such as " + Version.getSanitizedVersion());

        setAcceptedParams(params);
        setRequestType(TaskDescriptions.HTTP.GET);
        setResponseType(TaskDescriptions.HTTP.JSON);
        setClassname(this.getClass().getCanonicalName().toString());
        setCssClass(TaskDescriptions.UI.BTN_SUCCESS);
        setButtonText("Upgrade Grid Extras");
        setEnabledInGui(true);

        addResponseDescription(JsonCodec.GridExtras.CURRENT_VERSION,
                "Current version of Grid Extras running");
        addResponseDescription(JsonCodec.GridExtras.CURRENT_GRID_EXTRAS_FILE,
                "Path to current Grid Extras JAR");

        addResponseDescription(JsonCodec.GridExtras.NEW_VERSION,
                "Version of Grid Extras downloaded");
        addResponseDescription(JsonCodec.GridExtras.VERSION_GRID_EXTRAS_FILE,
                "Path to newly downloaded Grid Extras JAR");

        addResponseDescription(JsonCodec.GridExtras.REMOTE_JAR_URL,
                "URL of Grid Extras Jar");

        addResponseDescription(JsonCodec.GridExtras.DOWNLOADED_FILE_SIZE,
                "Size of the downloaded JAR in bites");

        addResponseDescription(JsonCodec.GridExtras.ALL_AVAILABLE_RELEASES,
                "List of all published releases of Selenium Grid Extras");
    }


    private void writeStartShellFile(File jarFile) throws IOException {

        File javaBin = new File(System.getProperty("java.home"), "bin");
        File javaExe = new File(javaBin, "java");

        String javaPath;

        if (javaExe.exists()) {
            javaPath = javaExe.getAbsolutePath();
        } else {
            javaPath = "java";
        }


        File gridExtrasHome = new File(RuntimeConfig.getSeleniungGridExtrasHomePath());
        File shellFile;
        String shellFileHeader = "";

        if (RuntimeConfig.getOS().isWindows()) {
            shellFile = new File(gridExtrasHome, startGridExtrasShellFileName + ".bat");
        } else {
            shellFileHeader = "#!/bin/bash\n\n";
            shellFile = new File(gridExtrasHome, startGridExtrasShellFileName + ".sh");
        }

        String startCommand = String.format("%s %s -jar \"%s\"",
                shellFileHeader,
                javaPath,
                jarFile);


        shellFile.setExecutable(true, false);
        shellFile.setReadable(true, false);
        shellFile.setWritable(true, false);

        FileIOUtility.writeToFile(shellFile, startCommand);
    }


    @Override
    public boolean initialize() {

        try {


            String latestVersion = (String) getSanitizedReleaseList().get(0).keySet().toArray()[0];
            File destinationJar = new File(RuntimeConfig.getSeleniungGridExtrasHomePath(),
                    String.format("SeleniumGridExtras-%s-SNAPSHOT-jar-with-dependencies.jar", latestVersion));

            writeStartShellFile(new File(RuntimeConfig.getSeleniungGridExtrasHomePath(),
                    String.format("SeleniumGridExtras-%s-SNAPSHOT-jar-with-dependencies.jar",
                            Version.getSanitizedVersion())));


            boolean isUpToDate = Version.getSanitizedVersion().equals(latestVersion);

            String spacer = "                          ";
            String message = "";
            if (isUpToDate) {
                printInitilizedSuccessAndRegisterWithAPI();
                message = "";
            }  else {
                printInitilizedSuccessAndRegisterWithAPI();
                message = spacer + message + "\n**************************************\n\n";
                message = spacer + message + "Selenium Grid Extras is out of date" + "\n";
                message = spacer + message + "Current Version: " + Version.getSanitizedVersion() + "\n";
                message = spacer + message + "Latest Version: " + latestVersion + "\n";
                message = spacer + message + String.format(
                        "You can set auto update to true by adding %s = '1' to the %s file \n",
                        Config.GRID_EXTRAS_AUTO_UPDATE,
                        RuntimeConfig.configFile
                );
                message = spacer + message + String.format(
                        "Or you can update the version manually by using http://%s:%s%s end-point\n",
                        RuntimeConfig.getHostIp(),
                        RuntimeConfig.getGridExtrasPort(),
                        TaskDescriptions.Endpoints.UPGRADE_GRID_EXTRAS
                );
                message = spacer + message + "\n**************************************\n";
                message = message + "\n\n";
            }
            System.out.println(message);
            logger.info(message);


            return true;
        } catch (Exception e) {
            //I know a catch all is bad practice, but the optional update should never cripple the grid completely.

            printInitilizedFailure();
            logger.error(Throwables.getStackTraceAsString(e));
            return false;
        }
    }

    protected List<Map<String, String>> getSanitizedReleaseList() {

        try {
            return GridExtrasDownloader
                    .sanitizeDownloadableAssetsVersions(downloader.getAllDownloadableAssets());
        } catch (IOException e) {
            logger.warn(Throwables.getStackTraceAsString(e));
            e.printStackTrace();
        } catch (URISyntaxException e) {
            logger.warn(Throwables.getStackTraceAsString(e));
            e.printStackTrace();
        }

        return new LinkedList<Map<String, String>>();
    }

    protected void aggAllVersionsToResponse() {
        getJsonResponse().addListOfHashes(JsonCodec.GridExtras.ALL_AVAILABLE_RELEASES, getSanitizedReleaseList());
    }

    @Override
    public JsonObject execute() {
        getJsonResponse().addKeyValues("current_version", Version.getSanitizedVersion());
        getJsonResponse().addKeyValues(JsonCodec.GridExtras.CURRENT_GRID_EXTRAS_FILE,
                RuntimeConfig.getSeleniumGridExtrasJarFile().getAbsolutePath());
        getJsonResponse().addKeyValues(JsonCodec.ERROR, "Version is required for this endpoint");

        aggAllVersionsToResponse();

        return getJsonResponse().getJson();
    }

    protected void configDownloaderInstance(String version, String destinationDir, String destinationFile) {
        downloader.setVersion(version);
        downloader.setDestinationDir(destinationDir);
        downloader.setDestinationFile(destinationFile);
    }

    @Override
    public JsonObject execute(String version) {
        aggAllVersionsToResponse();
        getJsonResponse().addKeyValues("current_version", Version.getSanitizedVersion());
        getJsonResponse().addKeyValues(JsonCodec.GridExtras.CURRENT_GRID_EXTRAS_FILE,
                RuntimeConfig.getSeleniumGridExtrasJarFile().getAbsolutePath());

        getJsonResponse().addKeyValues(JsonCodec.GridExtras.NEW_VERSION, version);


        File destinationJar = new File(RuntimeConfig.getSeleniungGridExtrasHomePath(),
                String.format("SeleniumGridExtras-%s-SNAPSHOT-jar-with-dependencies.jar", version));


        if (destinationJar.exists()) {
            String message = String.format("File %s already exists, will not download", destinationJar.getAbsolutePath());
            logger.info(message);
            getJsonResponse().addKeyValues(JsonCodec.OUT, message);

        } else {
            configDownloaderInstance(version, RuntimeConfig.getSeleniungGridExtrasHomePath(), destinationJar.getName());

            try {
                getJsonResponse().addKeyValues(JsonCodec.GridExtras.REMOTE_JAR_URL, downloader.getJarUrl());

                if (!downloader.download()) {
                    getJsonResponse().addKeyValues(JsonCodec.ERROR,
                            String.format("Error downloading Selenium Grid Extras Jar, %s, Are you sure the requested version exists?",
                                    downloader.getErrorMessage()));
                } else {

                    writeStartShellFile(destinationJar);

                    if (destinationJar.exists()) {
                        getJsonResponse().addKeyValues(JsonCodec.GridExtras.VERSION_GRID_EXTRAS_FILE, destinationJar.getAbsolutePath());
                        getJsonResponse().addKeyValues(JsonCodec.GridExtras.DOWNLOADED_FILE_SIZE, String.valueOf(destinationJar.length()));
                    } else {
                        getJsonResponse().addKeyValues(
                                JsonCodec.ERROR,
                                String.format(
                                        "Failed to download URL: %s, Destination: %s",
                                        downloader.getJarUrl(),
                                        destinationJar.getAbsolutePath()));
                    }
                }

            } catch (Exception e) {
                getJsonResponse().addKeyValues(JsonCodec.ERROR, Throwables.getStackTraceAsString(e));
            }
        }


        return getJsonResponse().getJson();
    }

    @Override
    public JsonObject execute(Map<String, String> parameter) {

        if (parameter.isEmpty() || !parameter.containsKey(
                JsonCodec.WebDriver.Downloader.VERSION)) {
            return execute();
        } else {
            return execute(parameter.get(JsonCodec.WebDriver.Downloader.VERSION).toString());
        }
    }


}
