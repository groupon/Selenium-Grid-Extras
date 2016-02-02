package com.groupon.seleniumgridextras.utilities;


import com.groupon.seleniumgridextras.SeleniumGridExtras;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * Created by xhu on 10/06/2014.
 */
public abstract class Env {

    private static Logger logger = Logger.getLogger(Env.class);

    public static final int UNKNOWN = 0;    // 0000
    public static final int OSX = 1;        // 0001
    public static final int WINDOWS = 2;    // 0010

    private static Env instance = null;

    protected static CommandLib commandLib;

    public static Env current() {
        if (instance == null) {
            String osName = System.getProperty("os.name");
            if (osName.startsWith("Windows")) {
                instance = new Windows();
            } else if (osName.startsWith("Mac")) {
                instance = new OSX();
            } else {
                instance = null;
            }
        }

        return instance;
    }

    public CommandLib getCommand() {
        return commandLib;
    }

    public boolean isWindows() {
        return getOSName().startsWith("Windows");
    }

    public boolean isOSX() {
        return getOSName().startsWith("Mac");
    }

    public String getOSName() {
        return System.getProperty("os.name");
    }

    public int getOSMask() {
        if (isWindows()) {
            return WINDOWS;
        } else if (isOSX()) {
            return OSX;
        } else {
            return UNKNOWN;
        }
    }

    public String getUserName() {
        return System.getProperty("user.name");
    }

    public String getPathSeparator() {
        return System.getProperty("path.separator");
    }

    public String getFileSeparator() {
        return System.getProperty("file.separator");
    }


    public abstract String getClassPath(String... paths);

    //this should also look in JAVA_HOME????
    protected String getExecutable(String name) {
        String path = System.getenv("PATH");
        String[] pathDirs = path.split(File.pathSeparator);
        for (String dir : pathDirs) {
            File file = new File(dir, name);
            if (file.isFile() && file.canExecute()) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }


    public abstract String getAppiumExecutable();
    public abstract String getJavaExecutable();
    public abstract String getNPMExecutable();
    public abstract String getEmulatorExecutable();

    public abstract void install() throws IOException, InterruptedException;

    public abstract void uninstall() throws IOException, InterruptedException;

    public abstract File getInstallDir();

    public abstract void killProcessByName(String name) throws IOException;

    public abstract boolean isSoftwareInstalled(String name);
    // positions

    public File getGridExtJarFile() {
        return new File(SeleniumGridExtras.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    }

    public File getHomeDir() {
        return new File(FilenameUtils.getFullPathNoEndSeparator(getGridExtJarFile().getAbsolutePath()));
    }

    public File getTmpDir() {
        return getRelativeDir("tmp");
    }

    public File getRelativeDir(String path) {
        return getDir(new File(getHomeDir(), path));
    }

    public File touch(File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    public File getDir(File path) {
        if (!path.exists()) {
            path.mkdirs();
        }
        return path;
    }

    public File dumpTmpFile(String fileName, String content) throws IOException {
        File tmpFile = new File(getTmpDir(), fileName);
        return saveToFile(content, tmpFile);
    }

    public File saveToFile(String content, File to) throws IOException {
        if (!to.exists()) {
            to.createNewFile();
        }

        FileWriter fw = new FileWriter(to.getAbsoluteFile());
        BufferedWriter bw =new BufferedWriter(fw);
        bw.write(content);
        bw.close();
        return to;
    }

    public File getWatchDir() {
        return getRelativeDir("config");
    }

    public File getOutputDir() {
        return getRelativeDir("out");
    }

    public File getOutputFile(String name) throws IOException {
        File file = new File(getOutputDir(), name);
        touch(file);
        return file;
    }

    public File getCommandDir() {
        return getRelativeDir("cmd");
    }

    public int getDaemonCheckIntervalInMinutes() {
        return 1;
    }

    public String getDaemonName() {
        return "GridExt";
    }

}
