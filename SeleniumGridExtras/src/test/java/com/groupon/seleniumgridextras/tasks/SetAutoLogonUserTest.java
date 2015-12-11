package com.groupon.seleniumgridextras.tasks;

import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.utilities.json.JsonParserWrapper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


public class SetAutoLogonUserTest {


    @Test
    public void testRegistryKeys() throws Exception {
        assertEquals("Software\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon", SetAutoLogonUser.baseRegLocation);
        assertEquals("AutoAdminLogon", SetAutoLogonUser.AutoAdminLogon);
        assertEquals("DefaultDomainName", SetAutoLogonUser.DefaultDomainName);
        assertEquals("DefaultUserName", SetAutoLogonUser.DefaultUserName);
        assertEquals("DefaultPassword", SetAutoLogonUser.DefaultPassword);
    }

    @Test
    public void testBlankExecute() throws Exception {



        if (RuntimeConfig.getOS().isWindows()) {

        } else {
            ArrayList expectedArray = new ArrayList();
            expectedArray.add("");

            ArrayList errorArray = new ArrayList();
            errorArray.add("Not implemented for current OS/Platform");

            Map response = JsonParserWrapper.toHashMap(new SetAutoLogonUser().execute());
                //{exit_code=1.0, error=[Not implemented for current OS/Platform], current_user=[], out=[]}
            assertEquals(1.0, response.get("exit_code"));
            assertArrayEquals(expectedArray.toArray(), ((ArrayList) response.get("current_user")).toArray());
            assertArrayEquals(new ArrayList().toArray(), ((ArrayList) response.get("out")).toArray());
            assertArrayEquals(errorArray.toArray(), ((ArrayList) response.get("error")).toArray());
        }

    }

    @Test
    public void testEmptyStringExecute() throws Exception {

        if (RuntimeConfig.getOS().isWindows()) {

        } else {
            ArrayList expectedArray = new ArrayList();
            expectedArray.add("");

            ArrayList errorArray = new ArrayList();
            errorArray.add("Not implemented for current OS/Platform");

            Map response = JsonParserWrapper.toHashMap(new SetAutoLogonUser().execute(""));
            //{exit_code=1.0, error=[Not implemented for current OS/Platform], current_user=[], out=[]}
            assertEquals(1.0, response.get("exit_code"));
            assertArrayEquals(expectedArray.toArray(), ((ArrayList) response.get("current_user")).toArray());
            assertArrayEquals(new ArrayList().toArray(), ((ArrayList) response.get("out")).toArray());
            assertArrayEquals(errorArray.toArray(), ((ArrayList) response.get("error")).toArray());
        }

    }

    @Test
    public void testReplaceNull() throws Exception{
        assertEquals("", SetAutoLogonUser.replaceNull(null));
        assertEquals("", SetAutoLogonUser.replaceNull(""));
        assertEquals("test string", SetAutoLogonUser.replaceNull("test string"));
    }
}
