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
import com.groupon.seleniumgridextras.windows.jWMI;

import java.util.List;
import java.util.Map;

public class WindowsSystemInfo implements OSInfo {

  public String getSystemUptime() throws Exception {
    return jWMI.getWMIValue("Select SystemUpTime From Win32_PerfFormattedData_PerfOS_System",
                            "SystemUpTime");
  }

  public Map<String, String> getMemoryInfo() throws Exception {
    RAM ram = new RAM();

    ram.setFreeMemory(jWMI.getWMIValue(
        "SELECT AvailableKBytes FROM Win32_PerfFormattedData_PerfOS_Memory",
        "AvailableKBytes"));

    ram.setTotalMemory(jWMI.getWMIValue("Select TotalPhysicalMemory FROM Win32_ComputerSystem",
                                        "TotalPhysicalMemory"));

    return ram.toHash();
  }


  public Map<String, String> getProcessorInfo() throws Exception {
    Processor processor = new Processor();
    processor.setCoreCount(jWMI.getWMIValue("SELECT NumberOfCores FROM Win32_Processor",
                                            "NumberOfCores"));

    processor.setProcessorCount(jWMI.getWMIValue(
        "SELECT NumberOfLogicalProcessors FROM Win32_Processor",
        "NumberOfLogicalProcessors"));

    processor.setLoad(jWMI.getWMIValue("SELECT LoadPercentage FROM Win32_Processor",
                                       "LoadPercentage"));

    return processor.toHash();
  }

  public List<Map<String, String>> getDiskInfo() throws Exception {

    String hdInfo = jWMI.getWMIValue("SELECT * FROM Win32_LogicalDisk",
                                     "DeviceID,FreeSpace,Size");

    AllDiskDrives allDisks = new AllDiskDrives();

    final String[] diskDriveLines = hdInfo.split("[\\n]");
    int counter = 0;

    while (counter < diskDriveLines.length) {
      if (diskDriveLines[counter].contains(":")) {

        DiskDrive currentDrive = new DiskDrive(diskDriveLines[counter]);

        if (!diskDriveLines[counter + 1].contains(":")) {
          currentDrive.setFreeSpace(diskDriveLines[counter + 1]);
          currentDrive.setSize(diskDriveLines[counter + 2]);
        }

        allDisks.addDisk(currentDrive);


      }

      counter++;
    }

    return allDisks.toPreJsonArray();
  }
}
