package com.groupon.seleniumgridextras.utilities;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by xhu on 1/12/14.
 */
public class WindowsCommandLib implements CommandLib {
    @Override
    public Command killProcessByPort(int port) {
        throw new NotImplementedException();
        //return new Command("FOR /F \"tokens=5 delims= \" %P IN ('netstat -a -n -o ^|  findstr :"+parameter+"') DO TaskKill.exe /PID %P /T /F";)
        //return null;
    }

    @Override
    public Command killProcessByPid(int pid) {

        throw new NotImplementedException();
        //return null;
    }

    @Override
    public Command reboot() {

        throw new NotImplementedException();
        //return null;
    }

    @Override
    public Command adbKillServer() {

        throw new NotImplementedException();
        //return null;
    }

    @Override
    public Command restartPhone(String udid) {

        return new Command().startWith("adb ").arg("-s").arg(udid).arg("reboot");
        //return null;
    }
}
