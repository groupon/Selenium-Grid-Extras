package com.groupon.seleniumgridextras.grid.matcher;

import org.openqa.grid.internal.utils.CapabilityMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Created by xhu on 26/09/14.
 */

public class AppiumCapabilityMatcher implements CapabilityMatcher {

    public static final String PLATFORM_NAME = "platformName";
    public static final String DEVICE_NAME = "deviceName";
    public static final String DEVICE_TYPE = "deviceType";
    public static final String DEVICE_CATEGORY = "deviceCategory";

    private final List<String> toConsider = new ArrayList<String>();
    private static Logger logger = Logger.getLogger(AppiumCapabilityMatcher.class);

    public AppiumCapabilityMatcher() {
        toConsider.add(PLATFORM_NAME);
        toConsider.add(DEVICE_NAME);
        toConsider.add(DEVICE_TYPE);
        toConsider.add(DEVICE_CATEGORY);
    }

    @Override
    public boolean matches(Map<String, Object> nodeCapability, Map<String, Object> requestedCapability) {
        if (nodeCapability == null || requestedCapability == null)
            return false;
        try {
            logger.info("Starting matching for node ");
            for (String key : requestedCapability.keySet()) {
                if (toConsider.contains(key)) {
                    if (requestedCapability.get(key) != null) {
                        String value = requestedCapability.get(key).toString();
                        if (value != null) {
                            if (!value.equals(nodeCapability.get(key))) {
                                logger.info("Invalid match for key " + key + " nodes value = " + nodeCapability.get(key) + " requested value = " + value);
                                return false;
                            } else {
                                logger.info("Valid match for key " + key + " nodes value = " + nodeCapability.get(key) + " requested value = " + value);
                            }
                        } else {
                            logger.info("requested capability for " + key + " and value is null ");
                            // null value matches anything.
                        }
                        logger.info("no requested capability for " + key);
                    }
                }
            }
            /*
            //should this be real?
            String deviceType = "physical";
            if (requestedCapability.containsKey(DEVICE_TYPE)) {
                deviceType = requestedCapability.get(DEVICE_TYPE).toString();
                logger.info("device type = " + deviceType);
            }
            if (requestedCapability.containsKey(PLATFORM_NAME) && deviceType.equals("physical")) {
                logger.info("setting udid");
                if (nodeCapability.containsKey("udid")) {
                    requestedCapability.put("udid", nodeCapability.get("udid"));
                }
            }*/
            logger.info("Match found ");
        }catch(Exception e){
            System.out.println(e.getMessage());
            logger.error(e);
            return false;
        }
        return true;
    }
}
