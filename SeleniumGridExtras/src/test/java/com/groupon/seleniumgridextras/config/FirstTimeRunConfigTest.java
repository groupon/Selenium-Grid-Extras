package com.groupon.seleniumgridextras.config;


import com.groupon.seleniumgridextras.browser.BrowserVersionDetector;
import org.junit.Test;

public class FirstTimeRunConfigTest {

    @Test
    public void testGuessBrowserVersion() throws Exception {

        Config userInput = Config.initilizedFromUserInput();

        FirstTimeRunConfig.customiseConfig(userInput);

        BrowserVersionDetector.guessBrowserVersion("firefox");
        BrowserVersionDetector.guessBrowserVersion("chrome");
        BrowserVersionDetector.guessBrowserVersion("Edge");
        BrowserVersionDetector.guessBrowserVersion("MicrosoftEdge");
        BrowserVersionDetector.guessBrowserVersion("internetexplorer");
        BrowserVersionDetector.guessBrowserVersion("internet explorer");

    }
}
