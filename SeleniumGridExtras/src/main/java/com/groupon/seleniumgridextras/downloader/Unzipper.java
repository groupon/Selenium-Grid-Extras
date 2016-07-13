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

package com.groupon.seleniumgridextras.downloader;

import org.apache.ant.compress.taskdefs.Unzip;
import org.apache.log4j.Logger;

import com.groupon.seleniumgridextras.config.RuntimeConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class Unzipper {
  private static Logger logger = Logger.getLogger(Unzipper.class);

  public static boolean unzip(String source, String destination) {
    if(source.endsWith(".tar.gz")) {
      return decompressTarGunzip(source, destination);
    } else if(source.endsWith(".gz")) {
      return decompressGunzip(source, destination);
    }
    try {
      Unzip unzipper = new Unzip();
      unzipper.setSrc(new File(source));
      unzipper.setDest(new File(destination));
      unzipper.execute();
    } catch (Exception e) {
      logger.error(e.toString());
      return false;
    }
    return true;
  }

  private static boolean decompressGunzip(String source, String destination) {
    String sourceFileName = new File(source).getName();
    destination = destination + RuntimeConfig.getOS().getFileSeparator() + sourceFileName.substring(0, sourceFileName.lastIndexOf("."));
    if (RuntimeConfig.getOS().isWindows()) {
      destination = destination + ".exe";
    }
    try {
      FileInputStream fis = new FileInputStream(source);
      GZIPInputStream gis = new GZIPInputStream(fis);
      FileOutputStream fos = new FileOutputStream(destination);
      byte[] buffer = new byte[1024];
      int len;
      while((len = gis.read(buffer)) != -1){
        fos.write(buffer, 0, len);
      }
      fos.close();
      gis.close();
    } catch (IOException e) {
      logger.error(e.toString());
    }
    return true;
  }

  private static boolean decompressTarGunzip(String source, String destination) {
    try {
      org.apache.ant.compress.taskdefs.GUnzip gunzip = new org.apache.ant.compress.taskdefs.GUnzip();
      gunzip.setSrc(new File(source));
      gunzip.setDest(new File(destination));
      gunzip.execute();
      org.apache.ant.compress.taskdefs.Untar untar = new org.apache.ant.compress.taskdefs.Untar();
      untar.setSrc(new File(source.replace(".gz", "")));
      untar.setDest(new File(destination)); // Destination doesn't matter?
      untar.execute();
    } catch (Exception e) {
      logger.error(e.toString());
      return false;
    }
    return true;
  }
}
