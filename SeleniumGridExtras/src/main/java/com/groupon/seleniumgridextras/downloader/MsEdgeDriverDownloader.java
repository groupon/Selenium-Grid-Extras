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
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MsEdgeDriverDownloader extends Downloader {

  private String bit;
  private String version;

  private static final String MSEDGEDRIVER_BASE_URL = "https://msedgedriver.azureedge.net/";

  private static Logger logger = Logger.getLogger(MsEdgeDriverDownloader.class);

  public MsEdgeDriverDownloader(String version, String bitVersion) {

    setDestinationDir(RuntimeConfig.getConfig().getMsEdgeDriver().getDirectory());
    setVersion(version);
    setBitVersion(bitVersion);

    setDestinationFile(getVersion() + "_" + getBitVersion() + "bit" + ".zip");

    setSourceURL(MSEDGEDRIVER_BASE_URL + getVersion() + "/edgedriver_"
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

        String msedgedriver = "msedgedriver";
        if (RuntimeConfig.getOS().isWindows()){
          msedgedriver = msedgedriver + ".exe";
        }

        File tempUnzipedExecutable = new File(getDestinationDir(), msedgedriver);
        File finalExecutable =
            new File(RuntimeConfig.getConfig().getMsEdgeDriver().getExecutablePath());

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
    return "mac";
  }

  protected static String getWindowsName() {
    return "win";
  }

  public static String[] getBitArchitecturesForVersion(String edgeDriverVersionNumber) {
    // ArrayList<String> bitArchitecturesAvailable = new ArrayList<String>();
    // try {
    //   String xpathString = "//*[text()[contains(.,'" + edgeDriverVersionNumber + "/edgedriver_" + getOSName() + "')]]";
    //   XPathExpression expression = XPathFactory.newInstance().newXPath().compile(xpathString);

    //   NodeList result = (NodeList) expression.evaluate(getVersionManifest(), XPathConstants.NODESET);

    //   for (int i = 0; i < result.getLength(); i++) {
    //     String nodeValue = result.item(i).getTextContent();
    //     Matcher matcher = Pattern.compile("(\\d{2})(?=.zip)").matcher(nodeValue);
    //     while (matcher.find())
    //     {
    //       bitArchitecturesAvailable.add(matcher.group(1));
    //     }
    //   }
    // }
    // catch (XPathExpressionException e) {
    //   logger.error(e.toString());
    // }
    // finally {
    //   return bitArchitecturesAvailable.toArray(new String[] {});
    // }
    return new String[] { "32", "64" };
  }

  private static Document getVersionManifest() {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try {
      return factory.newDocumentBuilder().parse(new URL(MSEDGEDRIVER_BASE_URL).openStream());
    }
    catch (ParserConfigurationException pce) {
      logger.error(pce.toString());
    }
    catch (SAXException se) {
      logger.error(se.toString());
    }
    catch (IOException ioe) {
      logger.error(ioe.toString());
    }
    return null;
  }
}
