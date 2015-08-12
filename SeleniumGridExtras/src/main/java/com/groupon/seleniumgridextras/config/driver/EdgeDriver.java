package com.groupon.seleniumgridextras.config.driver;


public class EdgeDriver  extends DriverInfo {
    @Override
    public String getExecutablePath() {
        return "C:\\Program Files (x86)\\Microsoft Web Driver\\" + getExecutableName();
    }

    @Override
    public String getExecutableName() {
        return "MicrosoftWebDriver.exe";
    }
}
