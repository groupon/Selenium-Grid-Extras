package com.groupon.seleniumgridextras.tasks;


import com.google.common.base.Throwables;
import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.tasks.config.TaskDescriptions;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import com.groupon.seleniumgridextras.windows.WinRegistry;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SetAutoLogonUser extends ExecuteOSTask {
    public static final String AutoAdminLogonTrue = "1";
    public static final String FAILED_TO_SET_DEFAULT_USER_MESSAGE = "Setting default logon user failed. Is the script running with elevated privileges?";
    private static Logger logger = Logger.getLogger(GetNodeConfig.class);

    //    All info found here
//    http://www.computerperformance.co.uk/windows7/windows7_auto_logon.htm
    public static final String baseRegLocation = "Software\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon";
    public static final String AutoAdminLogon = "AutoAdminLogon";
    public static final String DefaultDomainName = "DefaultDomainName";
    public static final String DefaultUserName = "DefaultUserName";
    public static final String DefaultPassword = "DefaultPassword";

    //TODO: need ctrl alt delte


    public SetAutoLogonUser() {
        setEndpoint(TaskDescriptions.Endpoints.USER_AUTO_LOGON);
        setDescription(TaskDescriptions.Description.SETS_A_USER_TO_AUTOMATICALLY_LOGON_INTO_SYSTEM_POST_REBOOT);
        JsonObject params = new JsonObject();


        params.addProperty(JsonCodec.OS.USERNAME, "Username of auto logon user");
        params.addProperty(JsonCodec.OS.PASSWORD, "Password of auto logon user");
        params.addProperty(JsonCodec.OS.DOMAIN, "(blank for localhost) - Domain of the user to use");

        setAcceptedParams(params);
        setRequestType(TaskDescriptions.HTTP.GET);
        setResponseType(TaskDescriptions.HTTP.JSON);
        setClassname(this.getClass().getCanonicalName().toString());
        setCssClass(TaskDescriptions.UI.BTN_DANGER);
        setButtonText(TaskDescriptions.UI.AUTO_LOGON_USER);
        setEnabledInGui(true);

        getJsonResponse().addKeyDescriptions(JsonCodec.OS.CURRENT_USER, "");
        getJsonResponse().addKeyDescriptions(JsonCodec.OS.CURRENT_DOMAIN, "");
        getJsonResponse().addKeyDescriptions(JsonCodec.OS.AUTO_LOGON_ENABLED, "");

    }


    @Override
    public JsonObject execute(Map<String, String> parameter) {
        if (!parameter.isEmpty() && parameter.containsKey(JsonCodec.OS.USERNAME) && parameter.containsKey(JsonCodec.OS.PASSWORD)) {
            try {
                String domain = RuntimeConfig.getOS().getHostName();
                String username = parameter.get(JsonCodec.OS.USERNAME).toString();
                String password = parameter.get(JsonCodec.OS.PASSWORD).toString();

                if (parameter.containsKey(JsonCodec.OS.DOMAIN) && parameter.get(JsonCodec.OS.DOMAIN).toString() != "") {
                    domain = parameter.get(JsonCodec.OS.DOMAIN).toString();
                }

                WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE, baseRegLocation, AutoAdminLogon, AutoAdminLogonTrue, getArchitecture());
                WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE, baseRegLocation, DefaultDomainName, domain, getArchitecture());
                WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE, baseRegLocation, DefaultUserName, username, getArchitecture());
                WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE, baseRegLocation, DefaultPassword, password, getArchitecture());

                List<String> mismatches = findUserSettingsThatDoNotMatch(AutoAdminLogonTrue, domain, username, password);

                if(mismatches.size() != 0){
                    getJsonResponse().addKeyValues(JsonCodec.OUT, FAILED_TO_SET_DEFAULT_USER_MESSAGE);
                    getJsonResponse().addKeyValues(JsonCodec.ERROR, mismatches);
                    getJsonResponse().addKeyValues(JsonCodec.EXIT_CODE, 1);
                    return getJsonResponse().getJson();
                }

            } catch (Exception e) {
                logger.error(Throwables.getStackTraceAsString(e));
                getJsonResponse().addKeyValues(JsonCodec.ERROR, Throwables.getStackTraceAsString(e));
                return getJsonResponse().getJson();
            }
        }

        return execute();
    }

    @Override
    public JsonObject execute() {
        return execute("");
    }


    @Override
    public JsonObject execute(String status) {
        try {
            if (RuntimeConfig.getOS().isWindows()) {

                String current_enabled = getAutoAdminLogonValue();
                String current_user = getDefaultUserName();
                String current_domain = getDefaultDomainName();

                getJsonResponse().addKeyValues(JsonCodec.OS.AUTO_LOGON_ENABLED, current_enabled);
                getJsonResponse().addKeyValues(JsonCodec.OS.CURRENT_USER, current_user);
                getJsonResponse().addKeyValues(JsonCodec.OS.CURRENT_DOMAIN, current_domain);

            } else {
                //TODO: Implement OSX and possibly linux auto logon
                getJsonResponse().addKeyValues(JsonCodec.ERROR, "Not implemented for current OS/Platform");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(Throwables.getStackTraceAsString(e));
            getJsonResponse().addKeyValues(JsonCodec.ERROR, Throwables.getStackTraceAsString(e));
        }

        return getJsonResponse().getJson();
    }

    public static List<String> findUserSettingsThatDoNotMatch(String autoAdminLogon, String defaultDomainName, String defaultUserName, String defaultPassword) throws InvocationTargetException, IllegalAccessException {
        ArrayList<String> notMatching = new ArrayList<String>();

        if (!autoAdminLogon.equals(getAutoAdminLogonValue())) {
            notMatching.add(String.format(
                    "Registry key mismatch. Key: '%s', Path: '%s', Expected Value: '%s', Actual Value: '%s'",
                    AutoAdminLogon,
                    baseRegLocation,
                    autoAdminLogon,
                    getAutoAdminLogonValue()
            )
            );
        }

        if (!defaultDomainName.equals(getDefaultDomainName())) {
            notMatching.add(String.format(
                    "Registry key mismatch. Key: '%s', Path: '%s', Expected Value: '%s', Actual Value: '%s'",
                    DefaultDomainName,
                    baseRegLocation,
                    defaultDomainName,
                    getDefaultDomainName()
            )
            );
        }

        if (!defaultUserName.equals(getDefaultUserName())) {
            notMatching.add(String.format(
                    "Registry key mismatch. Key: '%s', Path: '%s', Expected Value: '%s', Actual Value: '%s'",
                    DefaultUserName,
                    baseRegLocation,
                    defaultUserName,
                    getDefaultUserName()
            )
            );
        }

        if (!defaultPassword.equals(getDefaultPassword())) {
            notMatching.add(String.format(
                    "Registry key mismatch. Key: '%s', Path: '%s', Expected Value: '********', Actual Value: '********'",
                    DefaultPassword,
                    baseRegLocation
            )
            );
        }


        return notMatching;
    }

    protected static String getDefaultDomainName() throws IllegalAccessException, InvocationTargetException {
        return replaceNull(WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE, baseRegLocation, DefaultDomainName, getArchitecture()));
    }

    protected static String getDefaultUserName() throws IllegalAccessException, InvocationTargetException {
        return replaceNull(WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE, baseRegLocation, DefaultUserName, getArchitecture()));
    }

    protected static String getAutoAdminLogonValue() throws IllegalAccessException, InvocationTargetException {
        return replaceNull(WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE, baseRegLocation, AutoAdminLogon, getArchitecture()));

    }

    protected static String getDefaultPassword() throws IllegalAccessException, InvocationTargetException {
        return replaceNull(WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE, baseRegLocation, DefaultPassword, getArchitecture()));
    }

    protected static String replaceNull(String input) {
        return input == null ? "" : input;
    }

    protected static int getArchitecture() {
        if (RuntimeConfig.getOS().getWindowsRealArchitecture().equals("64")) {
            return WinRegistry.KEY_WOW64_64KEY;
        } else {
            return WinRegistry.KEY_WOW64_32KEY;
        }
    }

}
