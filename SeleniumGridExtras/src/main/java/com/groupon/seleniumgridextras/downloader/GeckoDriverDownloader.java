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

import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeckoDriverDownloader extends Downloader {

  private String bit;
  private String version;

  private static Logger logger = Logger.getLogger(GeckoDriverDownloader.class);
  private static final String GECKODRIVER_RELEASES_URL = "https://api.github.com/repos/mozilla/geckodriver/releases";

  public GeckoDriverDownloader(String version, String bitVersion) {

    setDestinationDir(RuntimeConfig.getConfig().getGeckoDriver().getDirectory());
    setVersion(version);
    setBitVersion(bitVersion);

    setDestinationFile("geckodriver_" + getVersion() + "." + getExtension());

    setSourceURL(buildSourceURL());
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

        String geckodriver = "geckodriver";
        if (RuntimeConfig.getOS().isWindows()){
          geckodriver = geckodriver + ".exe";
        }


        File tempUnzipedExecutable = new File(getDestinationDir(), geckodriver);
        File finalExecutable =
            new File(RuntimeConfig.getConfig().getGeckoDriver().getExecutablePath());

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

  private String getExtension() {
    String ext;

    if (RuntimeConfig.getOS().isWindows()) {
      ext = "zip";
    } else if (RuntimeConfig.getOS().isMac()) {
      ext = "tar.gz";
    } else {
      ext = "tar.gz";
    }
    return ext;
  }

  protected static String getOSName() {
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

  protected static String getLinuxName() {
    return "linux";
  }

  protected static String getMacName() {
    return "macos";
  }

  protected static String getWindowsName() {
    return "win";
  }

  private String buildSourceURL()
  {
    final String base_url = "https://github.com/mozilla/geckodriver/releases/download/v";

    String firstPart = base_url + getVersion() + "/geckodriver-v" + getVersion() + "-" + getOSName();

    if (getOSName() == getMacName())
    {
      return firstPart + "." + getExtension();
    }
    else
    {
      return firstPart  + getBitVersion() + "." + getExtension();
    }
  }

  public static String[] getBitArchitecturesForVersion(String geckoDriverVersionNumber)
  {
    ArrayList<String> bitArchitecturesAvailable = new ArrayList<String>();
    String[] versions = getVersionManifest();
    for (String version : versions)
    {
      Matcher versionMatcher = Pattern.compile("geckodriver-v" + geckoDriverVersionNumber + "-" + getOSName()).matcher(version);
      if (versionMatcher.find())
      {
        if(Pattern.compile(getOSName() + JsonCodec.WebDriver.Downloader.BIT_64).matcher(version).find())
        {
          bitArchitecturesAvailable.add(JsonCodec.WebDriver.Downloader.BIT_64);
        }
        else if (Pattern.compile(getOSName() + JsonCodec.WebDriver.Downloader.BIT_32).matcher(version).find())
        {
          bitArchitecturesAvailable.add(JsonCodec.WebDriver.Downloader.BIT_32);
        }
      }
    }
    return  bitArchitecturesAvailable.toArray(new String[] {});
  }

  private static String[] getVersionManifest()
  {
    List<String> versions = new ArrayList<String>();
    GitHubDownloader gitHubDownloader = new GitHubDownloader(GECKODRIVER_RELEASES_URL);
    try
    {
      List<Map<String, String>> results = gitHubDownloader.getAllDownloadableAssets();
      for (Map<String, String> kv : results) {
        for (String key : kv.keySet()) {
          versions.add(key);
        }
      }
    } catch (IOException e)
    {
      e.printStackTrace();
    } catch (URISyntaxException e)
    {
      e.printStackTrace();
    }
    finally{
      return versions.toArray(new String[] {});
    }
  }
}
