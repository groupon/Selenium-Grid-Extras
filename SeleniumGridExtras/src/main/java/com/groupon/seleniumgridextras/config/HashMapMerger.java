package com.groupon.seleniumgridextras.config;

import java.util.Map;

public class HashMapMerger {

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
