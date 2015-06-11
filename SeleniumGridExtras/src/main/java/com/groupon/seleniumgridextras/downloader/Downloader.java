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

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class Downloader {

  protected String errorMessage = "";
  protected String sourceURL;
  protected String destinationFile;
  protected String destinationDir;

  private static Logger logger = Logger.getLogger(Downloader.class);

  public boolean download(){
    return startDownload();
  }

  public File getDestinationFileFullPath(){
    File dir = new File(getDestinationDir());
    File file = new File(getDestinationFile());
    File combined = new File(dir.getAbsolutePath() + RuntimeConfig.getOS().getFileSeparator() + file.getName());
    return combined;
  }

  public String getDestinationFile(){
    return destinationFile;
  }

  public String getSourceURL(){
    return sourceURL;
  }


  public String getDestinationDir(){
    return destinationDir;
  }

  public String getErrorMessage(){
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  protected boolean startDownload(){
    try {
      URL url = new URL(getSourceURL());
      logger.info("Starting to download from " + url);
      FileUtils.copyURLToFile(url, getDestinationFileFullPath());
      logger.info("Download complete");
      return true;
    } catch (MalformedURLException error){
      logger.error(error.toString());
      setErrorMessage(error.toString());
    } catch (IOException error) {
      logger.error(error.toString());
      setErrorMessage(error.toString());
    }
    logger.error("Download failed");
    return false;
  }

  public abstract void setSourceURL(String source);
  public abstract void setDestinationFile(String destination);
  public abstract void setDestinationDir(String dir);


}
