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

import com.groupon.seleniumgridextras.OSChecker;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

import java.io.File;


public class IEDownloader extends Downloader {

  private String bit;
  private String version;

  public IEDownloader(String version, String bitVersion) {
    setDestinationDir(RuntimeConfig.getConfig().getIEdriver().getDirectory());

    setVersion(version);
    setBitVersion(bitVersion);

    setDestinationFile(getVersion() + getBitVersion() + ".zip");

    setSourceURL(
        "https://selenium.googlecode.com/files/IEDriverServer_" + getBitVersion() + "_"
        + getVersion() + ".zip");
  }

  @Override
  public void setSourceURL(String source) {
    sourceURL = source;
  }

  @Override
  public void setDestinationFile(String destination) {
    destinationFile = destination;
  }

  @Override
  public void setDestinationDir(String dir) {
    destinationDir = dir;
  }

  public void setBitVersion(String bitVersion) {
    this.bit = bitVersion;
  }

  public String getBitVersion() {
    return bit;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getVersion() {
    return version;
  }


  @Override
  public boolean download() {
    Boolean downloaded = startDownload();
    String zipPath = destinationDir + "/" + destinationFile;

    if (downloaded) {
      Boolean unzippied = Unzipper.unzip(zipPath,
                                         RuntimeConfig.getConfig().getIEdriver().getDirectory());

      if (unzippied) {

        File zip = new File(zipPath);
        zip.delete();

        String slash = "\\";

        if (!OSChecker.isWindows()) {
          slash = "/";
        }

        String destinationFilePath = RuntimeConfig.getConfig().getIEdriver().getDirectory();

        destinationFilePath =
            destinationFilePath + slash + getBitVersion() + "_" + getVersion() + ".exe";

        setDestinationFile(destinationFilePath);
        File
            exe =
            new File(
                RuntimeConfig.getConfig().getIEdriver().getDirectory() + "/IEDriverServer.exe");
        exe.renameTo(new File(destinationFilePath));
        return true;
      }
    }
    return false;
  }

}
