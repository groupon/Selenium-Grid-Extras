package com.groupon.seleniumgridextras.utilities;

/**
 * Created by xhu on 1/12/14.
 */
public interface CommandLib {
    public Command killProcessByPort(int port);

    public Command killProcessByPid(int pid);

    public Command reboot();

    public Command adbKillServer();

    public Command restartPhone(String udid);
}
