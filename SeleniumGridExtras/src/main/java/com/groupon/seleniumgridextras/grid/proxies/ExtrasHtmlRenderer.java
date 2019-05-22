package com.groupon.seleniumgridextras.grid.proxies;

import com.google.common.base.Strings;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.internal.TestSlot;
import org.openqa.grid.internal.utils.HtmlRenderer;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.InputStream;
import java.util.Map;

public class ExtrasHtmlRenderer implements HtmlRenderer {

  private AutoProxy proxy;

  public ExtrasHtmlRenderer(AutoProxy proxy) {
    this.proxy = proxy;
  }

  public String renderSummary() {
    StringBuilder builder = new StringBuilder();
    builder.append("<fieldset>");
    builder.append("<legend>").append(proxy.getClass().getSimpleName()).append("</legend>");
    builder.append("listening on ").append(proxy.getRemoteHost());

    if (((DefaultRemoteProxy) proxy).isDown()) {
      builder.append("(cannot be reached at the moment)");
    }
    builder.append("<br />");
    if (proxy.getTimeOut() > 0) {
      int inSec = proxy.getTimeOut() / 1000;
      builder.append("test session time out after ").append(inSec).append(" sec.<br />");
    }

    builder.append("Supports up to <b>").append(proxy.getMaxNumberOfConcurrentTestSessions())
        .append("</b> concurrent tests from: <br />");

    builder.append("");
    for (TestSlot slot : proxy.getTestSlots()) {
      TestSession session = slot.getSession();

      String icon = getConsoleIconPath(new DesiredCapabilities(slot.getCapabilities()));
      if (icon != null) {
        builder.append("<img ");
        builder.append("src='").append(icon).append("' ");
      } else {
        builder.append("<a href='#' ");
      }

      if (session != null) {
        builder.append(" class='busy' ");
        builder.append(" title='").append(session.get("lastCommand")).append("' ");
      } else {
        builder.append(" title='")
            .append(slot.getCapabilities())
            .append("type=" + slot.getProtocol())
            .append("' ");
      }

      if (icon != null) {
        builder.append("/>");
      } else {
        builder.append(">");
        builder.append(slot.getCapabilities().get(CapabilityType.BROWSER_NAME));
        builder.append("</a>");
      }

    }
    builder.append("</fieldset>");

    return builder.toString();
  }

  private String getConsoleIconPath(DesiredCapabilities cap) {
    String name = this.consoleIconName(cap);
    String path = "org/openqa/grid/images/";
    InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path + name + ".png");
    return in == null ? null : "/grid/resources/" + path + name + ".png";
  }

  private String consoleIconName(DesiredCapabilities cap) {
    String browserString = cap.getBrowserName();
    if (Strings.isNullOrEmpty(browserString)) {
      return "missingBrowserName";
    } else {
      String ret = browserString;
      if (!browserString.contains("iexplore") && !browserString.startsWith("*iehta")) {
        if (!browserString.contains("firefox") && !browserString.startsWith("*chrome")) {
          if (browserString.toLowerCase().contains("safari")) {
            if (browserString.toLowerCase().contains("technology")) {
              ret = "safari_technology_preview";
            } else {
              ret = "safari";
            }
          } else if (browserString.startsWith("*googlechrome")) {
            ret = "chrome";
          } else if (browserString.startsWith("opera")) {
            ret = "opera";
          } else if (browserString.toLowerCase().contains("edge")) {
            ret = "MicrosoftEdge";
          }
        } else if ((cap.getVersion() == null || !cap.getVersion().toLowerCase().equals("beta")) && !cap.getBrowserName().toLowerCase().contains("beta")) {
          if ((cap.getVersion() == null || !cap.getVersion().toLowerCase().equals("aurora")) && !cap.getBrowserName().toLowerCase().contains("aurora")) {
            if ((cap.getVersion() == null || !cap.getVersion().toLowerCase().equals("nightly")) && !cap.getBrowserName().toLowerCase().contains("nightly")) {
              ret = "firefox";
            } else {
              ret = "nightly";
            }
          } else {
            ret = "aurora";
          }
        } else {
          ret = "firefoxbeta";
        }
      } else {
        ret = "internet explorer";
      }

      return ret.replace(" ", "_");
    }
  }
}
