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

import com.groupon.seleniumgridextras.config.RuntimeConfig;

import org.apache.log4j.Logger;

import java.io.File;

public class ChromeDriverDownloader extends Downloader {

  private String bit;
  private String version;

  private static Logger logger = Logger.getLogger(ChromeDriverDownloader.class);

  public ChromeDriverDownloader(String version, String bitVersion) {

    setDestinationDir(RuntimeConfig.getConfig().getChromeDriver().getDirectory());
    setVersion(version);
    setBitVersion(bitVersion);

    setDestinationFile(getVersion() + "_" + getBitVersion() + "bit" + ".zip");

    setSourceURL("http://chromedriver.storage.googleapis.com/" + getVersion() + "/chromedriver_"
                 + getOSName() + getBitVersion() + ".zip");

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

  @Override
  public boolean download() {

    logger.info("Downloading from " + getSourceURL());


    if (startDownload()) {

      if (Unzipper.unzip(getDestinationFileFullPath().getAbsolutePath(), getDestinationDir())) {

        String chromedriver = "chromedriver";
        if (RuntimeConfig.getOS().isWindows()){
          chromedriver = chromedriver + ".exe";
        }


        File tempUnzipedExecutable = new File(getDestinationDir(), chromedriver);
        File finalExecutable =
            new File(RuntimeConfig.getConfig().getChromeDriver().getExecutablePath());

        if (tempUnzipedExecutable.exists()){
          logger.debug(tempUnzipedExecutable.getAbsolutePath());
          logger.debug("It does exist");
          logger.debug(finalExecutable.getAbsolutePath());
        } else {
          logger.debug(tempUnzipedExecutable.getAbsolutePath());
          logger.debug("NO exist");
          logger.debug(finalExecutable.getAbsolutePath());
        }

        tempUnzipedExecutable.renameTo(finalExecutable);

        setDestinationFile(finalExecutable.getAbsolutePath());

        finalExecutable.setExecutable(true, false);
        finalExecutable.setReadable(true, false);

        return true;
      }
    }
    return false;
  }


  public String getBitVersion() {
    return bit;
  }

  public void setBitVersion(String bit) {
    this.bit = bit;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }


  protected String getOSName() {
    String os;

    if (RuntimeConfig.getOS().isWindows()) {
      os = getWindowsName();
    } else if (RuntimeConfig.getOS().isMac()) {
      os = getMacName();
    } else {
      os = getLinuxName();
    }

    return os;
  }

  protected String getLinuxName() {
    return "linux";
  }

  protected String getMacName() {
    return "mac";
  }

  protected String getWindowsName() {
    return "win";
  }


}
