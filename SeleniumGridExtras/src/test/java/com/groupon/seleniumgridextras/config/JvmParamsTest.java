package com.groupon.seleniumgridextras.config;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;


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
        String gridExtrasJvmOptions = config.getGridExtrasJvmOptions();
        assertThat(gridExtrasJvmOptions, containsString("-Dhttp.proxyPort=5555"));
        assertThat(gridExtrasJvmOptions, containsString("-DfakeBool=true"));
        assertThat(gridExtrasJvmOptions, containsString("-Dhttp.proxyHost=www.google.com"));
    }


    @Test
    public void testGridJvmParams() throws Exception {
        String gridJvmOptions = config.getGridJvmOptions();
        assertThat(gridJvmOptions, containsString("-Dhttp.proxyPort=9999"));
        assertThat(gridJvmOptions, containsString("-DfakeBool=false"));
        assertThat(gridJvmOptions, containsString("-Dhttp.proxyHost=www.bing.com"));
    }

    @Test
    public void testEmptyJvmParams() throws Exception {
        Config emptyConfig  = new Config();

        assertEquals("", emptyConfig.getGridExtrasJvmOptions());
        assertEquals("", emptyConfig.getGridJvmOptions());

    }
}
