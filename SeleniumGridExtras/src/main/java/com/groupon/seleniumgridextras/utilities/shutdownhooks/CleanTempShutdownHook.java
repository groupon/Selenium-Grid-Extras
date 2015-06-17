package com.groupon.seleniumgridextras.utilities.shutdownhooks;


import com.google.common.base.Throwables;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class CleanTempShutdownHook {

    private static Logger logger = Logger.getLogger(CleanTempShutdownHook.class);
    private File tempDir;

    public CleanTempShutdownHook(File tempDir){
        this.tempDir = tempDir;


        logger.info("Creating instance of the clean temp shutdown hook");
    }

    public void cleanTempDriverDirs(){
        File[] allFiles = tempDir.listFiles();

        for(int i = 0; i < allFiles.length; i++){
            File currentFile = allFiles[i];
            if(currentFile.isDirectory()){
                if(currentFile.listFiles().length == 0){
                    logger.info(String.format("Deleting %s", currentFile.getAbsolutePath()));
                    try {
                        FileUtils.deleteDirectory(currentFile);
                    } catch (IOException e) {
                        logger.warn(
                                String.format("Error deleting %s, %s",
                                        currentFile.getAbsolutePath(),
                                        Throwables.getStackTraceAsString(e)));
                    }

                }
            }
        }
    }

    public void attachShutDownHook() {
        if (RuntimeConfig.getOS().isWindows()) {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    String message = String.format("Cleaning up %s temp dir", tempDir.getAbsolutePath());
                    System.out.println(message);
                    logger.info(message);
                    cleanTempDriverDirs();

                }
            });

            logger.info("Clean TEMP dir Shutdown Hook Attached.");
        }
    }

}
