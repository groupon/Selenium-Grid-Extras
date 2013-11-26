package com.groupon.seleniumgridextras.logger;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


public class GridLogger {

  public static void load(){
    final String filename = "log4j.properties";

    PropertyConfigurator
        .configure(GridLogger.class.getClassLoader().getResource(filename));
    info("Loaded Grid Logger from " + filename);
  }

  public static void info(Class klass, Object message){
    getLoggerForClass(klass).info(message);
  }

  public static void debug(Class klass, Object message){
    getLoggerForClass(klass).debug(message);
  }

  public static void warn(Class klass, Object message){
    getLoggerForClass(klass).warn(message);
  }

  public static void info(Object message){
    getLoggerForClass(GridLogger.class).info(message);
  }

  public static void debug(Object message){
    getLoggerForClass(GridLogger.class).debug(message);
  }

  public static void warn(Object message){
    getLoggerForClass(GridLogger.class).warn(message);
  }

  protected static Logger getLoggerForClass(Class klass){
    return Logger.getLogger(klass);
  }



}
