package com.groupon.seleniumgridextras.utilities;


import com.groupon.seleniumgridextras.SeleniumGridExtras;

import java.io.IOException;
import java.io.InputStream;


public class ResourceRetriever {

  public String getAsString(String resource) throws IOException {
    InputStream in = SeleniumGridExtras.class.getResourceAsStream(resource);
    return StreamUtility.inputStreamToString(in);
  }

}
