package com.groupon.seleniumgridextras.config.driver;


import com.groupon.seleniumgridextras.config.RuntimeConfig;

public class EdgeDriver  extends DriverInfo {
    @Override
    public String getExecutablePath() {
        String
                path =
                this.getDirectory() + RuntimeConfig.getOS().getFileSeparator() + getExecutableName();

        return path;
    }

    @Override
    public String getExecutableName() {
        return "msedgedriver.exe";
    }
}
