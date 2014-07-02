package com.groupon.seleniumgridextras.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created with IntelliJ IDEA. User: dima Date: 7/1/14 Time: 3:27 PM To change this template use
 * File | Settings | File Templates.
 */
public class FileIOUtility {

  public static String getAsString(String file) throws Exception {
    return getAsString(new File(file));
  }


  public static String getAsString(File file) throws Exception {

    String readString = "";
    BufferedReader reader = new BufferedReader(new FileReader(file));
    String line = null;
    while ((line = reader.readLine()) != null) {
      readString = readString + line;
    }

    return readString;
  }

}
