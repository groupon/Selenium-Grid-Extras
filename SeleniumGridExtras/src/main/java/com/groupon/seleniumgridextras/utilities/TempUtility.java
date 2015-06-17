package com.groupon.seleniumgridextras.utilities;


import java.io.File;

public class TempUtility {

    public static File getWindowsTempForCurrentUser(){
        return new File(new File(new File(System.getProperty("user.home"), "AppData"), "Local"), "Temp");
    }

    public static File getLinuxTemp(){
        return new File("/tmp");
    }

}
