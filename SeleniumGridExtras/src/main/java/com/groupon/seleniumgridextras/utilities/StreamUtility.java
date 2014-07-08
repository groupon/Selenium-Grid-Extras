package com.groupon.seleniumgridextras.utilities;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA. User: dima Date: 7/8/14 Time: 3:49 PM To change this template use
 * File | Settings | File Templates.
 */
public class StreamUtility {

  public static String inputStreamToString(InputStream is) throws IOException {
    StringBuilder result = new StringBuilder();
    int in;
    while ((in = is.read()) != -1) {
      result.append((char) in);
    }
    is.close();
    return result.toString();
  }

}
