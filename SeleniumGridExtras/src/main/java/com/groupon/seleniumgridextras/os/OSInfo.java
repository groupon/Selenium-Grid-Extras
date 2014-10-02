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

package com.groupon.seleniumgridextras.os;


import com.groupon.seleniumgridextras.utilities.json.JsonCodec;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;

public class OSInfo {

  private OperatingSystemMXBean osMBean;

  public OSInfo() {
    MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();

    try {
      osMBean = ManagementFactory.newPlatformMXBeanProxy(
          mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
    } catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }


  public List<Map<String, String>> getDiskInfo() throws Exception {
    return AllDiskDrives.toPreJsonArray();
  }

  public Map<String, String> getProcessorInfo() {

    Map<String, String> processorInfo = new HashMap<String, String>();

    processorInfo.put(JsonCodec.OS.Hardware.Processor.INFO,
                      "" + osMBean.getName() + " " + osMBean.getVersion());
    processorInfo.put(JsonCodec.OS.Hardware.Processor.ARCHITECTURE, osMBean.getArch());
    processorInfo.put(JsonCodec.OS.Hardware.Processor.CORES, "" + osMBean.getAvailableProcessors());
    processorInfo.put(JsonCodec.OS.Hardware.Processor.LOAD, "" + osMBean.getSystemLoadAverage());

    return processorInfo;
  }


  public Map<String, String> getMemoryInfo() {

    return null;
  }

  public String getSystemUptime() {
    return null;

  }

}
