package com.groupon.seleniumgridextras.utilities;

/**
 * Created by xhu on 1/12/14.
 */
public class OSXCommandLib implements CommandLib {
    @Override
    public Command killProcessByPort(int port) {
//        return new Command().startWith("/bin/bash").arg("-c").arg(String.format("kill -9 `lsof -t -iTCP:%d -sTCP:LISTEN`", port));
        return new Command().startWith("/bin/bash").arg("-c").
                arg(String.format("pid=$(lsof -t -iTCP:%d -sTCP:LISTEN); if [[ -n $pid ]]; then kill $pid; else echo 'nothing to kill'; fi", port));
    }

    @Override
    public Command killProcessByPid(int pid) {
        return new Command().startWith("kill").arg("-9").arg(String.valueOf(pid));
    }

    @Override
    public Command reboot() {
        return new Command().startWith("shutdown").arg("-r").arg("now");
    }

    @Override
    public Command adbKillServer() {
        return new Command().startWith("sleep").arg("20").arg("&&").arg("adb").arg("kill-server");
    }

    @Override
    public Command restartPhone(String udid) {
        return new Command().startWith("adb ").arg("-s").arg(udid).arg("reboot");
    }
}
