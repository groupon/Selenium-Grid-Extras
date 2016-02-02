package com.groupon.seleniumgridextras.utilities;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * Created by xhu on 9/06/2014.
 */
public class OSX extends Env {
    public OSX() {
        commandLib = new OSXCommandLib();
    }
    @Override
    public String getJavaExecutable() {
        return getExecutable("java");
    }

    @Override
    public String getNPMExecutable() {
        return getExecutable("npm");
    }

    @Override
    public String getEmulatorExecutable() {
        return getExecutable("emulator");
    }

    @Override
    public String getClassPath(String... paths) {
        return StringUtils.join(paths, getPathSeparator());
    }

    @Override
    public String getAppiumExecutable() {
        return getExecutable("appium");
    }

    @Override
    public void install() {
        // TODO implement me
        System.out.println("Install GridExt as daemon on OS X.");
    }

    @Override
    public void uninstall() {
        // TODO implement me
    }

    @Override
    public File getInstallDir() {
        return new File("/usr/local/gridext");
    }

    @Override
    public void killProcessByName(String name) {
        // TODO implement me
    }

    @Override
    public boolean isSoftwareInstalled(String name) {
        // TODO implement me
        return false;
    }
}
