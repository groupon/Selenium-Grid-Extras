package com.groupon.seleniumgridextras.utilities;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA. User: dima Date: 7/16/14 Time: 12:17 PM To change this template use
 * File | Settings | File Templates.
 */
public class DoubleToIntConverter {

  public static void convertAllDoublesToInt(Map inputMap) {
    for (String key : (Set<String>) inputMap.keySet()) {
      if (inputMap.get(key) instanceof Double) {
        if ((Double) inputMap.get(key) % 1 == 0) {
          inputMap.put(key, ((Double) inputMap.get(key)).intValue());
        }
      } else if (inputMap.get(key) instanceof List) {
        DoubleToIntConverter.convertAllDoublesToInt((List) inputMap.get(key));
      } else if (inputMap.get(key) instanceof Map) {
        DoubleToIntConverter.convertAllDoublesToInt((Map) inputMap.get(key));
      }
    }
  }

  public static void convertAllDoublesToInt(List inputList) {
    for (int i = 0; i < inputList.size(); i++) {
      if (inputList.get(i) instanceof Double) {
        if ((Double) inputList.get(i) % 1 == 0) {
          inputList.set(i, ((Double) inputList.get(i)).intValue());
        }
      } else if (inputList.get(i) instanceof Map) {
        DoubleToIntConverter.convertAllDoublesToInt((Map) inputList.get(i));
      }
    }
  }

}
