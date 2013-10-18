package com.groupon.seleniumgridextras.config;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class HashMapMerger {

  public static Config merge(Config defaultConfig, Config overwriteConfig){

    Map<String, Object> defaultConfigHash   = new HashMap();
    defaultConfigHash.putAll(convertToHash(convertToString(defaultConfig)));

    Map<String, Object> overwriteConfigHash = convertToHash(convertToString(overwriteConfig));
    Map<String, Object> finalConfig = overwriteMergeStrategy(defaultConfigHash, overwriteConfigHash);

    String finalConfigString = new Gson().toJson(finalConfig);

    return new Gson().fromJson(finalConfigString, Config.class);
  }

  private static Map convertToHash(String json){
    return new Gson().fromJson(json, HashMap.class);
  }

  private static String convertToString(Config config){
    return config.toPrettyJsonString();
  }

  protected static Map overwriteMergeStrategy(Map<String, Object> left, Map<String, Object> right){
    //As desribed in http://grepcode.com/file/repo1.maven.org/maven2/org.codehaus.cargo/cargo-core-api-module/0.9/org/codehaus/cargo/module/merge/strategy/MergeStrategy.java
    return mergeCurrentLevel(left, right);
  }

  private static Map mergeCurrentLevel(Map<String, Object> left, Map<String, Object> right){

    for (String key : right.keySet()){

      if (right.get(key) instanceof Map){
        mergeCurrentLevel((Map<String, Object>)left.get(key), (Map<String, Object>)right.get(key));
      } else {
        left.put(key, right.get(key));
      }
    }
    return left;
  }

}
