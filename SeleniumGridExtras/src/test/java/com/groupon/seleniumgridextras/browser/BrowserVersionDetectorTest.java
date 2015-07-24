package com.groupon.seleniumgridextras.browser;

import org.junit.Test;
import static org.junit.Assert.assertFalse;

import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.ExecuteCommand;


public class BrowserVersionDetectorTest {

    @Test
    public void testDetectChrome() throws Exception {
    	if(System.getProperty("os.name").contains("Mac")) {
	    	String[] cmd = BrowserVersionDetector.chromeMacVersionCommand;
	        JsonObject object = ExecuteCommand.execRuntime(cmd, true);
	        if (object.get("error").getAsJsonArray().size() == 0) { // Passes because it exists
	        	
	        } else { // Maybe the machine doesn't have Chrome installed. Either way we want to make sure the path does not get screwed up.
	            assertFalse(object.get("error").getAsJsonArray().get(1).getAsString().contains("Cannot run program \"/Applications/Google\":"));
	        }
    	}
    }
}
