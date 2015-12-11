/**
 * Copyright (c) 2013, Groupon, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * Neither the name of GROUPON nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * Created with IntelliJ IDEA.
 * User: Dima Kovalenko (@dimacus) && Darko Marinov
 * Date: 5/10/13
 * Time: 4:06 PM
 */

package com.groupon.seleniumgridextras;


import com.sun.jna.platform.win32.Kernel32;

import org.apache.log4j.Logger;

import java.awt.*;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class OS {

    private static final String WINDOWS = "Windows";
    private static final String MAC = "Mac";
    private static final String OS_NAME = "os.name";
    private static final String USER_HOME = "user.home";
    private static final String USER_NAME = "user.name";
    private static final String FILE_SEPARATOR = "file.separator";
    private static final String PATH_SEPARATOR = "path.separator";
    private static Logger logger = Logger.getLogger(OS.class);


    public boolean isWindows() {
        return getOSName().startsWith(WINDOWS);
    }

    public boolean isMac() {
        return getOSName().startsWith(MAC);
    }

    public String getOSName() {
        return System.getProperty(OS_NAME);
    }

    public String getUserHome() {
        return System.getProperty(USER_HOME);
    }

    public String getUserName() {
        return System.getProperty(USER_NAME);
    }

    public String getFileSeparator() {
        return System.getProperty(FILE_SEPARATOR);
    }

    public String getPathSeparator() {
        return System.getProperty(PATH_SEPARATOR);
    }

    public String getCurrentPid() {
        if (isWindows()) {
            return getWindowsPid();
        } else {
            return getUnixPid();
        }

    }

    public String getHostName() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            return addr.getHostName();
        } catch (UnknownHostException e) {
            logger.warn(e.toString());
            return null;
        }
    }

    public String getHostIp() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            return addr.getHostAddress();
        } catch (UnknownHostException e) {
            logger.warn(e.toString());
            return null;
        }
    }

    public boolean hasGUI() {
        return !GraphicsEnvironment.isHeadless();
    }

    public String getWindowsRealArchitecture() {
        String arch = System.getenv("PROCESSOR_ARCHITECTURE");
        String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");

        String realArch = arch.endsWith("64")
                || wow64Arch != null && wow64Arch.endsWith("64")
                ? "64" : "32";

        return realArch;
    }


    private String getUnixPid() {
        return ManagementFactory.getRuntimeMXBean().getName().replaceAll("@.*", "");
    }

    private String getWindowsPid() {
        int pid = Kernel32.INSTANCE.GetCurrentProcessId();
        return String.valueOf(pid);
    }

}
