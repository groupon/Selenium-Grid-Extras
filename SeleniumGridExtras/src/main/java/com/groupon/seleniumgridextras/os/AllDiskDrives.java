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

import com.groupon.seleniumgridextras.utilities.json.JsonCodec.OS.Hardware.HardDrive;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.groupon.seleniumgridextras.utilities.ValueConverter.bytesToHumanReadable;

public class AllDiskDrives {

  private static Logger logger = Logger.getLogger(AllDiskDrives.class);


  public static List<Map<String, String>> toPreJsonArray() {
    List<Map<String, String>> drivesInfo = new LinkedList<>();

    for (File drive : getHds()) {
      Map<String, String> currentDrive = new HashMap<>();

      currentDrive.put(HardDrive.FREE, bytesToHumanReadable(drive.getFreeSpace(), false));
      currentDrive.put(HardDrive.SIZE, bytesToHumanReadable(drive.getTotalSpace(), false));
      currentDrive.put(HardDrive.USABLE, bytesToHumanReadable(drive.getUsableSpace(), false));
      currentDrive.put(HardDrive.DRIVE, drive.getAbsolutePath());

      logger.debug(currentDrive);

      drivesInfo.add(currentDrive);
    }

    return drivesInfo;
  }

  protected static File[] getHds() {
    return File.listRoots();
  }


}
