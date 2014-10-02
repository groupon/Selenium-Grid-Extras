package com.groupon.seleniumgridextras.utilities.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class JsonParserWrapper {

  public static Map toHashMap(String input) {
    return new Gson().fromJson(input, HashMap.class);
  }

  public static Map toHashMap(JsonObject input) {
    return new Gson().fromJson(input, HashMap.class);
  }

  public static String prettyPrintString(Object input) {
    return new GsonBuilder().setPrettyPrinting().create().toJson(input);
  }
}
