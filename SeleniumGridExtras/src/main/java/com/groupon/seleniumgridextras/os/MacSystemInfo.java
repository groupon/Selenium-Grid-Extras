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

import com.google.gson.JsonObject;

import com.groupon.seleniumgridextras.ExecuteCommand;
import com.groupon.seleniumgridextras.utilities.json.JsonResponseBuilder;

import java.util.List;
import java.util.Map;

public class MacSystemInfo implements OSInfo {

  private String memoryLine;
  private String cpuLine;
  private String[] lines;
  private String top;
  private String df;

  public MacSystemInfo() {
    top = ExecuteCommand.execRuntime("top -l 1").get(JsonResponseBuilder.OUT).toString();
    lines = top.split("\",\"");
    memoryLine = lines[6];
    cpuLine = lines[3];



  }

  public String[] foo() {
    return lines;
  }


  @Override
  public List<Map<String, String>> getDiskInfo() throws Exception {
    return UnixDFParser.getSystemDrives().toPreJsonArray();
  }

  @Override
  public Map<String, String> getProcessorInfo() throws Exception {
    String
        coreCount =
        ExecuteCommand.execRuntime("sysctl hw.ncpu").get(JsonResponseBuilder.OUT).toString()
            .replaceAll("hw.ncpu:\\s*", "");
    Float idle = Float.parseFloat(cpuLine.split(" ")[6].replaceAll("\\%", ""));

    Processor processor = new Processor();
    processor.setCoreCount(coreCount);
    processor.setLoad(Float.toString(100 - idle));

    return processor.toHash();
  }

  @Override
  public Map<String, String> getMemoryInfo() throws Exception {
//    top -l 1? | grep PhysMem | awk '{print $8}' -- used
    //top -l 1 | grep PhysMem | awk '{print $10}'
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public String getSystemUptime() throws Exception {

    JsonObject val = ExecuteCommand.execRuntime("sysctl -n kern.boottime | cut -c14-18");

    String[] foo = val.toString().split(" ");

    return foo[3].replaceAll(",", "");
  }
}
