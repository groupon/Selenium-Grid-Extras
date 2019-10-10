package com.groupon.seleniumgridextras.config.driver;


public class EdgeDriver  extends DriverInfo {
    @Override
    public String getExecutablePath() {
        return "C:\\windows\\syswow64\\" + getExecutableName();
    }

    @Override
    public String getExecutableName() {
        return "MicrosoftWebDriver.exe";
    }
}
