package com.groupon.seleniumgridextras.homepage;

import com.google.common.base.Throwables;
import com.google.gson.internal.LinkedTreeMap;
import com.groupon.seleniumgridextras.Version;
import com.groupon.seleniumgridextras.config.GridNode;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.config.capabilities.Capability;
import com.groupon.seleniumgridextras.tasks.SystemInfo;
import com.groupon.seleniumgridextras.tasks.VideoRecorder;
import com.groupon.seleniumgridextras.utilities.ImageUtils;
import com.groupon.seleniumgridextras.utilities.ScreenshotUtility;
import com.groupon.seleniumgridextras.utilities.TimeStampUtility;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import com.groupon.seleniumgridextras.utilities.json.JsonParserWrapper;
import com.groupon.seleniumgridextras.videorecording.ImageProcessor;

import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class HtmlNodeRenderer {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final String CONTAINER_DIV_CLASS = "container";
    public static final String SCREENSHOT_CONTAINER_DIV = "jumbotron";
    public static final String SCREENSHOT_IMAGE_CLASS = "img-responsive";
    public static final String MAIN_INFO_SECTION = "row marketing";
    public static final String COL_LG_6 = "col-lg-6";
    public static final String NO_HD_INFO = "HD Info Not Available";
    public static final String NO_CPU_INFO = "CPU info not avalible";
    private static Logger logger = Logger.getLogger(HtmlNodeRenderer.class);


    public String getFullHtml() {

        StringBuilder pageHtml = new StringBuilder();
        pageHtml.append(buildPageHead());
        pageHtml.append(HtmlRenderer.openDiv(CONTAINER_DIV_CLASS));
        pageHtml.append(buildNavBar());
        pageHtml.append(buildScreenshotBillboard());

        pageHtml.append(buildNodeInfo());

        pageHtml.append(HtmlRenderer.closeDiv(CONTAINER_DIV_CLASS));
        pageHtml.append(buildPageFooter());

        return pageHtml.toString();
    }

    protected String buildNodeInfo() {
        StringBuilder nodeInfo = new StringBuilder();
        nodeInfo.append(HtmlRenderer.openDiv(MAIN_INFO_SECTION));
        nodeInfo.append(placeholder());
        nodeInfo.append(buildHardwareInfo());

        nodeInfo.append(HtmlRenderer.closeDiv(MAIN_INFO_SECTION));
        return nodeInfo.toString();

    }

    protected String buildHardwareInfo() {
        StringBuilder hardwareInfoSnippet = new StringBuilder();

        Map systemInfo = JsonParserWrapper.toHashMap(new SystemInfo().execute());

        hardwareInfoSnippet.append(HtmlRenderer.openDiv(COL_LG_6));

        hardwareInfoSnippet.append(infoSnippet("CPU", buildCPUInfo(systemInfo)));
        hardwareInfoSnippet.append(infoSnippet("RAM", buildMemoryInfo(systemInfo)));
        hardwareInfoSnippet.append(infoSnippet("JVM", buildJvmMemoryInfo(systemInfo)));
        hardwareInfoSnippet.append(infoSnippet("HD", buildHDInfo(systemInfo)));

        hardwareInfoSnippet.append(HtmlRenderer.closeDiv(COL_LG_6));

        return hardwareInfoSnippet.toString();
    }

    protected String buildJvmMemoryInfo(Map input) {
        LinkedTreeMap<String, String> jvmInfoFromMap = (LinkedTreeMap<String, String>) input.get(JsonCodec.OS.JVM.JVM_INFO);
        StringBuilder jvm = new StringBuilder();

        jvm.append("\n<ul>");
        jvm.append("\n\t<li>Available Processors: " + jvmInfoFromMap
                .get(JsonCodec.OS.JVM.AVAILABLE_PROCESSORS_TO_JVM) + "</li>");
        jvm.append(
                "\n\t<li>Free Memory: " + jvmInfoFromMap.get(JsonCodec.OS.JVM.FREE_MEMORY_AVAILABLE_TO_JVM)
                        + "</li>");
        jvm.append("\n\t<li>Max Memory: " + jvmInfoFromMap.get(JsonCodec.OS.JVM.MAX_MEMORY) + "</li>");

        jvm.append("\n</ul>");

        return jvm.toString();

    }

    protected String buildMemoryInfo(Map input) {
        LinkedTreeMap<String, String> ramFromInput = (LinkedTreeMap<String, String>) input.get(JsonCodec.OS.Hardware.Ram.RAM);

        StringBuilder ramInfo = new StringBuilder();

        ramInfo.append("\n<ul>");
        ramInfo.append(
                "\n\t<li>RAM (free/total): " + ramFromInput.get(JsonCodec.OS.Hardware.Ram.FREE) + "/"
                        + ramFromInput.get(JsonCodec.OS.Hardware.Ram.TOTAL) + "</li>");
        ramInfo.append(
                "\n\t<li>SWAP (free/total): " + ramFromInput.get(JsonCodec.OS.Hardware.Ram.FREE_SWAP) + "/"
                        + ramFromInput.get(JsonCodec.OS.Hardware.Ram.TOTAL_SWAP) + "</li>");
        ramInfo.append("\n</ul>");

        return ramInfo.toString();
    }

    protected String buildCPUInfo(Map input) {
        if (input.containsKey(JsonCodec.OS.Hardware.Processor.PROCESSOR)) {
            StringBuilder cpuInfo = new StringBuilder();

            LinkedTreeMap<String, String>
                    cpu =
                    (LinkedTreeMap<String, String>) input.get(JsonCodec.OS.Hardware.Processor.PROCESSOR);

            cpuInfo.append("\n<ul>");
            cpuInfo.append(
                    "\n\t<li>Processor Info: " + cpu.get(JsonCodec.OS.Hardware.Processor.INFO) + "</li>");
            cpuInfo.append("\n\t<li>Cores: " + cpu.get(JsonCodec.OS.Hardware.Processor.CORES) + "</li>");
            cpuInfo.append(
                    "\n\t<li>Average Load (60s): " + cpu.get(JsonCodec.OS.Hardware.Processor.LOAD) + "</li>");
            cpuInfo.append(
                    "\n\t<li>Architecture: " + cpu.get(JsonCodec.OS.Hardware.Processor.ARCHITECTURE)
                            + "</li>");

            if (input.containsKey(JsonCodec.OS.UPTIME)) {
                cpuInfo.append("\n\t<li>Uptime (min): " + input.get(JsonCodec.OS.UPTIME) + "</li>");
            }

            cpuInfo.append("\n</ul>");
            return cpuInfo.toString();
        } else {
            logger.error(NO_CPU_INFO);
            logger.error(input);
            return NO_CPU_INFO;
        }
    }

    protected String buildHDInfo(Map input) {

        if (input.containsKey(JsonCodec.OS.Hardware.HardDrive.DRIVES)) {
            StringBuilder hds = new StringBuilder();
            hds.append("\n<ul>");

            for (LinkedTreeMap<String, String> currentHd : (ArrayList<LinkedTreeMap<String, String>>) input
                    .get(JsonCodec.OS.Hardware.HardDrive.DRIVES)) {

                hds.append("<li>Drive: " + currentHd.get(JsonCodec.OS.Hardware.HardDrive.DRIVE));
                hds.append(getFormattedHdInfo(
                        currentHd.get(JsonCodec.OS.Hardware.HardDrive.SIZE),
                        currentHd.get(JsonCodec.OS.Hardware.HardDrive.FREE),
                        currentHd.get(JsonCodec.OS.Hardware.HardDrive.USABLE))
                        + "</li>");
            }

            hds.append("</ul>");
            return hds.toString();
        } else {
            logger.error(NO_HD_INFO);
            logger.error(input);
            return NO_HD_INFO;
        }
    }

    protected String getFormattedHdInfo(String total, String free, String usable) {
        StringBuilder hd = new StringBuilder();
        hd.append("\n\t<ul>");
        hd.append("\n\t\t<li>Total: " + total + "</li>");
        hd.append("\n\t\t<li>Free: " + free + "</li>");
        hd.append("\n\t\t<li>Total Usable: " + usable + "</li>");
        hd.append("\n\t</ul>");
        return hd.toString();
    }

    protected String placeholder() {
        StringBuilder hardwareInfoSnippet = new StringBuilder();

        hardwareInfoSnippet.append(HtmlRenderer.openDiv(COL_LG_6));

        hardwareInfoSnippet.append(infoSnippet("Declared Capabilities", buildSupportedCapabilities()));
        hardwareInfoSnippet.append(infoSnippet("Available Videos", buildListOfVideos()));
        hardwareInfoSnippet.append(infoSnippet("Supported Browsers", ""));

        hardwareInfoSnippet.append(HtmlRenderer.closeDiv(COL_LG_6));

        return hardwareInfoSnippet.toString();
    }

    private String buildListOfVideos() {

        Map videoInfo = JsonParserWrapper.toHashMap(new VideoRecorder().execute());
        StringBuilder videos = new StringBuilder();
        videos.append("\n<ul class='videos'>");

        try {
			Map<String, Map> allVideoInfos = (Map<String, Map>) videoInfo.get(JsonCodec.Video.AVAILABLE_VIDEOS);

			List<String> sortedSessions = new ArrayList<String>(allVideoInfos.keySet());
			Collections.sort(sortedSessions, new VideoSessionComparator(allVideoInfos));

			for (String session : sortedSessions)
			{
                videos.append("\n\t<li>");
				if ((allVideoInfos.get(session) != null) && (allVideoInfos.get(session).containsKey(JsonCodec.Video.VIDEO_DOWNLOAD_URL)))
				{
					Date lastModified = new Date(((Number) allVideoInfos.get(session).get(JsonCodec.Video.LAST_MODIFIED)).longValue());
                    videos.append(String.format(
						"<a target='new' href='%s'>%s (%tF %tT)</a>",
						allVideoInfos.get(session).get(JsonCodec.Video.VIDEO_DOWNLOAD_URL),
						session, lastModified, lastModified));
                } else {
                    videos.append(session);
                }
                videos.append("</li>");
            }
        } catch (Exception e) {
            logger.error(String.format("Could not render available videos %s,\n%s",
                    e.getMessage(),
                    Throwables.getStackTraceAsString(e)));

            videos.append(String.format("<li>Could not append videos because of %s error</li>", e.getMessage()));

        }


        videos.append("\n</ul> <!-- videos -->");
        return videos.toString();

    }

    public String buildSupportedCapabilities() {
        StringBuilder renderedCapabilities = new StringBuilder();
        renderedCapabilities.append("\n<ul class='capabilities'>");
        for (GridNode node : RuntimeConfig.getConfig().getNodes()) {
            renderedCapabilities.append("\n\t<li class='cap_node'>");
            renderedCapabilities.append("\n\t\tNode: " + node.getConfiguration().getPort());
            renderedCapabilities.append("\n\t\t<ul class='node'>");
            renderedCapabilities.append("\n\t\t\t<li>Config File: " + node.getLoadedFromFile() + "</li>");
            renderedCapabilities.append("\n\t\t\t<li>Max Sessions: " + node.getConfiguration().getMaxSession() + "</li>");
            renderedCapabilities.append("\n\t\t\t<li class='declared_browsers'>Declared Browsers: <br/> (Version / Max Instances / Driver Version)");
            renderedCapabilities.append("\n\t\t\t\t<ul class='browser_list'>");
            for (Capability cap : node.getCapabilities()) {
                renderedCapabilities.append("\n\t\t\t\t\t<li class='browser'>");
                renderedCapabilities.append("<img class='browser_icon' src='data:image/png;base64," + cap.getIcon() + "'>");
                renderedCapabilities.append(" " + cap.getBrowserVersion() + " / " + cap.getMaxInstances() + " / " + getDriverVersion(cap.getBrowser()));
                renderedCapabilities.append("\n\t\t\t\t\t</li> <!-- browser -->");
            }
            renderedCapabilities.append("\n\t\t\t\t</ul> <!-- browser_list -->");
            renderedCapabilities.append("\n\t\t\t</li> <!-- declared_browsers -->");
            renderedCapabilities.append("\n\t\t</ul><!-- node -->");
            renderedCapabilities.append("\n\t</li><!-- cap_node -->");
        }
        renderedCapabilities.append("\n</ul> <!-- capabilities -->");
        return renderedCapabilities.toString();
    }

    private String getDriverVersion(String browserName) {
        if (browserName.equalsIgnoreCase("chrome")) {
            return RuntimeConfig.getConfig().getChromeDriver().getVersion();
        } else if (browserName.equalsIgnoreCase("internet explorer")) {
            return RuntimeConfig.getConfig().getIEdriver().getVersion();
        } else if (browserName.equalsIgnoreCase("firefox")) {
            return RuntimeConfig.getConfig().getGeckoDriver().getVersion();
        } else {
            return RuntimeConfig.getConfig().getWebdriver().getVersion();
        }
    }

    public static String infoSnippet(String title, String contents) {
        return "\n\t<h4>" + title + "</h4>" + "\n\t<p>" + contents + "</p>\n";
    }


    protected String getScreenshot() {
        String returnString = "<img class='" + SCREENSHOT_IMAGE_CLASS + "' src='data:image/png;base64,";

        try {
            returnString = returnString + ScreenshotUtility
                    .getResizedScreenshotAsBase64String(WIDTH, HEIGHT);
        } catch (AWTException e) {
            logger.debug(e);

            BufferedImage
                    errorImage =
                    ImageProcessor
                            .createTitleFrame(new Dimension(WIDTH, HEIGHT), BufferedImage.TYPE_3BYTE_BGR,
                                    "Cannot create screenshot for this node. Is it in Headless mode?",
                                    e.getMessage(),
                                    getMachineInfo() + " " + TimeStampUtility.getTimestampAsString());

            returnString = returnString + ImageUtils.encodeToString(errorImage, "PNG");
        }

        return returnString + "'>";
    }


  protected String getMachineInfo() {
    return RuntimeConfig.getOS().getHostName() +
           " (" + RuntimeConfig.getHostIp() + ")";
  }
  
  protected String getVersionInfo() {
    return "Version : " + Version.getVersion();
  }

    protected String buildScreenshotBillboard() {
        StringBuilder billBoard = new StringBuilder();
        billBoard.append(HtmlRenderer.openDiv(SCREENSHOT_CONTAINER_DIV));
        billBoard.append(getScreenshot());
        billBoard.append(HtmlRenderer.closeDiv(SCREENSHOT_CONTAINER_DIV));
        return billBoard.toString();
    }

  protected String buildNavBar() {
    String navBarHtmlSnippet = HtmlRenderer.getNavBar();
    
    navBarHtmlSnippet = navBarHtmlSnippet.replaceAll("INSERT_TITLE", getMachineInfo());
    navBarHtmlSnippet = navBarHtmlSnippet.replaceAll("INSERT_VERSION", getVersionInfo());
    
    return navBarHtmlSnippet;
  }


    protected String buildPageHead() {

        String headHtmlSnippet = HtmlRenderer.getPageHead();

        headHtmlSnippet = headHtmlSnippet.
                replaceAll("INSERT_PAGE_TITLE", getMachineInfo());

        headHtmlSnippet = headHtmlSnippet.
                replaceAll("INSERT_BOOTSTRAP_CSS", HtmlRenderer.getMainCss());

        headHtmlSnippet = headHtmlSnippet.
                replaceAll("INSERT_TEMPLATE_CSS", HtmlRenderer.getTemplateSource());

        return headHtmlSnippet;

    }

    protected String buildPageFooter() {
        String footHtmlSnippet = HtmlRenderer.getPageFooter();

        footHtmlSnippet = footHtmlSnippet.
                replace("INSERT_JQUERY_SCRIPT", HtmlRenderer.getJquery());

        footHtmlSnippet =
                footHtmlSnippet.replace("INSERT_BOOTSTRAP_SCRIPT", HtmlRenderer.getMainJs());

        return footHtmlSnippet;

    }

	private static class VideoSessionComparator implements Comparator<String>
	{
		private final Map<String, Map> videoInfo;

		private VideoSessionComparator(Map<String, Map> allVideoInfos)
		{
			videoInfo = allVideoInfos;
		}

		public int compare(String o1, String o2)
		{
			Map videoInfo1 = videoInfo.get(o1);
			Map videoInfo2 = videoInfo.get(o2);
			long lastModified1 = ((Number) videoInfo1.get(JsonCodec.Video.LAST_MODIFIED)).longValue();
			long lastModified2 = ((Number) videoInfo2.get(JsonCodec.Video.LAST_MODIFIED)).longValue();
			return (int) (lastModified2 - lastModified1);
		}
	}


}
