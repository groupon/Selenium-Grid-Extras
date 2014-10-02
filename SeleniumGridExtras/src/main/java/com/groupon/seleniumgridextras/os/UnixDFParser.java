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

import com.groupon.seleniumgridextras.ExecuteCommand;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;

import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

public class UnixDFParser {
  private static Logger logger = Logger.getLogger(UnixDFParser.class);

  public static AllDiskDrives getSystemDrives() {
    LinkedList<String> headers;

    AllDiskDrives disks = new AllDiskDrives();

    String[] dfLines = dfCommand().split("\",\"");

    headers = dfHeaders(dfLines[0]);

    int i = 1;
    while (i < dfLines.length) {
      DiskDrive currentDrive = parseDrive(dfLines[i], headers);
      disks.addDisk(currentDrive);

      i++;
    }

    return disks;

  }

  protected static DiskDrive parseDrive(String lineInput, LinkedList<String> headers) {
    DiskDrive drive;

    String[] splitLine = lineInput.split(" ");
    List<String> refinedLine = new LinkedList<String>();

    for (String item : splitLine) {
      if (item != null && !item.equals("")) {
        refinedLine.add(item);
      }
    }

    int filesytemPostion = findPosition("Filesystem", headers);
    int mountedOnPosition = findPosition("Mounted", headers);

    if (refinedLine.get(filesytemPostion).contains("/dev")) {
      drive = new DiskDrive(refinedLine.get(filesytemPostion));
    } else {
      drive = new DiskDrive(refinedLine.get(mountedOnPosition));
    }

    drive.setSize(refinedLine.get(findPosition("Size", headers)));
    drive.setFreeSpace(refinedLine.get(findPosition("Avail", headers)));

    return drive;
  }


  private static int findPosition(String word, LinkedList<String> list) {
    int i = 0;
    boolean found = false;

    for (String item : list) {

      if (item.equals(word)) {
        found = true;
        break;
      }

      i++;
    }

    if (found) {
      return i;
    } else {
      logger.debug("Not Found: " + word);
      logger.debug(list);
      return -1;
    }
  }

  protected static String dfCommand() {
    return ExecuteCommand.execRuntime("df -hl").get(JsonCodec.OUT).toString();
  }

  protected static LinkedList<String> dfHeaders(String input) {
    LinkedList<String> sections = new LinkedList<String>();

    String[] split = input.split(" ");

    for (String item : split) {
      if (item != null && !item.equals("")) {
        sections.add(item);
      }
    }

    String foo = sections.get(0).toString().replaceAll("\\[\"", "");
    sections.set(0, foo);

    return sections;
  }


}
