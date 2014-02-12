package com.groupon.seleniumgridextras.config;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class JvmParamsTest {
    private Config config;

    @Before
    public void setUp() throws Exception {

        config = new Config();

        config.addGridExtrasJvmOptions("http.proxyHost", "www.google.com");
        config.addGridExtrasJvmOptions("fakeBool", true);
        config.addGridExtrasJvmOptions("http.proxyPort", 5555);

        config.addGridJvmOptions("http.proxyHost", "www.bing.com");
        config.addGridJvmOptions("fakeBool", false);
        config.addGridJvmOptions("http.proxyPort", 9999);
    }

    @Test
    public void testGridExtrasJvmParams() throws Exception {
        assertEquals("-Dhttp.proxyPort=5555 -DfakeBool=true -Dhttp.proxyHost=www.google.com ", config.getGridExtrasJvmOptions());
    }


    @Test
    public void testGridJvmParams() throws Exception {
        assertEquals("-Dhttp.proxyPort=9999 -DfakeBool=false -Dhttp.proxyHost=www.bing.com ", config.getGridJvmOptions());
    }

    @Test
    public void testEmptyJvmParams() throws Exception {
        Config emptyConfig  = new Config();

        assertEquals("", emptyConfig.getGridExtrasJvmOptions());
        assertEquals("", emptyConfig.getGridJvmOptions());

    }
}
