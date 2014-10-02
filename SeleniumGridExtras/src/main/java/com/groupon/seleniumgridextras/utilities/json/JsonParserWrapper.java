package com.groupon.seleniumgridextras.utilities.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public class JsonParserWrapper {

  public static Map stringToMap(String input) {
    return new Gson().fromJson(input, HashMap.class);
  }

  public static String mapToPrettyPrintJson(Map input) {
    return new GsonBuilder().setPrettyPrinting().create().toJson(input);
  }

}
