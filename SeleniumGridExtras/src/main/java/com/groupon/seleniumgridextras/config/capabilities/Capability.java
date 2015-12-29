package com.groupon.seleniumgridextras.config.capabilities;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public abstract class Capability extends HashMap {

    private static final String MAX_INSTANCES = "maxInstances";
    private static final String SELENIUM_PROTOCOL = "seleniumProtocol";
    private static final String VERSION = "version";
    private static final String PLATFORM = "platform";
    private static final String BROWSER_NAME = "browserName";
    private static Logger logger = Logger.getLogger(Capability.class);

    public abstract String getWebDriverClass();

    public Capability() {
        this.put(MAX_INSTANCES, 3);
        this.put(SELENIUM_PROTOCOL, "WebDriver");
        setBrowser(getWDStyleName());
    }

    public String getBrowserVersion() {
        return String.valueOf(this.get(VERSION));
    }

    public void setBrowserVersion(String browserVersion) {
        this.put(VERSION, browserVersion);
    }


    public String getMaxInstances() {
        return String.valueOf(this.get(MAX_INSTANCES));
    }

    public void setMaxInstances(int maxInstances) {
        this.put(MAX_INSTANCES, maxInstances);
    }

    public void setPlatform(String platform) {
        this.put(PLATFORM, platform);
    }

    protected void setBrowser(String browser) {
        this.put(BROWSER_NAME, browser);
    }

    public String getBrowser() {
        return String.valueOf(this.get(BROWSER_NAME));
    }

    public static Capability getCapabilityFor(String browserName, Map capabilityMap) {
        Capability cap = getCapabilityFor(browserName);
        for (Object capabilityKey : capabilityMap.keySet()) {

            Object value = capabilityMap.get(capabilityKey);
            if (value instanceof Number) {
                //GSON library always converts ints into doubles :(
                value = ((Number) value).intValue();
            }

            if (capabilityKey.equals(VERSION)) {
                //Explicitly convert the Version of the browser into a string instead of an int or double.
                //If we don't do that, then the grid cannot find the provided version :(
                value = String.valueOf(value);
            }

            cap.put(capabilityKey, value);
        }


        return cap;
    }


    public static Capability getCapabilityFor(String browserName) {

        for (Map.Entry<Class, String> entry : Capability.getSupportedCapabilities().entrySet()) {

            Class<Capability> key = entry.getKey();
            String value = entry.getValue();

            if (value.equals(browserName)) {
                try {
                    return key.newInstance();
                } catch (Exception e) {
                    logger.error("Can't load capability from file, exiting with 1");
                    logger.equals(e);
                    e.printStackTrace();
                    System.exit(1);
                }
            }

        }

        return null;
    }

    public static Map<Class, String> getSupportedWebCapabilities() {
        Map<Class, String> capabilityHash = new LinkedHashMap<Class, String>();

        capabilityHash.put(Firefox.class, BrowserType.FIREFOX);
        capabilityHash.put(InternetExplorer.class, BrowserType.IE);
        capabilityHash.put(Edge.class, BrowserType.EDGE);
        capabilityHash.put(Chrome.class, BrowserType.CHROME);
        capabilityHash.put(Safari.class, BrowserType.SAFARI);
        capabilityHash.put(PhantomJs.class, BrowserType.PHANTOMJS);

        return capabilityHash;
    }

    public static Map<Class, String> getSupportedAppiumCapabilities() {
        Map<Class, String> capabilityHash = new LinkedHashMap<Class, String>();

        capabilityHash.put(Android.class, BrowserType.ANDROID);
        capabilityHash.put(Chrome.class, BrowserType.CHROME);
        capabilityHash.put(Chromium.class, BrowserType.CHROMIUM);
        capabilityHash.put(Browser.class, BrowserType.BROWSER);
        capabilityHash.put(IPhone.class, BrowserType.IPHONE);
        capabilityHash.put(IPad.class, BrowserType.IPAD);
        capabilityHash.put(Safari.class, BrowserType.SAFARI);

        return capabilityHash;
    }

    public static Map<Class, String> getSupportedCapabilities() {
        Map<Class, String> capabilityHash = new HashMap<Class, String>();

        capabilityHash.putAll(getSupportedWebCapabilities());
        capabilityHash.putAll(getSupportedAppiumCapabilities());

        return capabilityHash;
    }

    public String getWDStyleName() {
        return Capability.getSupportedCapabilities().get(this.getClass());
    }

    public abstract String getIcon();
}


