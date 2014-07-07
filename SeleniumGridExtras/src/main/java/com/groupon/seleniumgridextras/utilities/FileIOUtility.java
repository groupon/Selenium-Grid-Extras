package com.groupon.seleniumgridextras.utilities;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA. User: dima Date: 7/1/14 Time: 3:27 PM To change this template use
 * File | Settings | File Templates.
 */
public class FileIOUtility {

  private static Logger logger = Logger.getLogger(FileIOUtility.class);

  public static String getAsString(String file) throws FileNotFoundException {
    return getAsString(new File(file));
  }


  public static String getAsString(File file) throws FileNotFoundException{

    String readString = "";
    BufferedReader reader = new BufferedReader(new FileReader(file));
    String line = null;

    try{
    while ((line = reader.readLine()) != null) {
      readString = readString + line;
    }
    }catch (IOException error){
      logger.error("IOExcetion reading "+ file.getAbsolutePath());
      logger.error(error);
    }

    logger.debug("Read from" + file.getAbsolutePath() + " following content\n" + readString);

    return readString;
  }

  public static void writeToFile(String filename, String content) throws Exception {
    writeToFile(new File(filename), content);
  }

  public static void writeToFile(File filename, String content) throws Exception {
    logger.debug("Writing to " + filename.getAbsolutePath() + " following content\n" + content);
    FileUtils.writeStringToFile(filename, content);
  }

}
