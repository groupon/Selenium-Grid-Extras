package com.groupon.seleniumgridextras.utilities;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA. User: dima Date: 7/16/14 Time: 12:17 PM To change this template use
 * File | Settings | File Templates.
 */
public class DoubleToIntConverter {

  public static void convertAllDoublesToInt(Map foo){
    for (String key : (Set<String>) foo.keySet()){
      if(foo.get(key) instanceof Double){
        if ((Double)foo.get(key) % 1 == 0){
          foo.put(key, ((Double) foo.get(key)).intValue());
        }
      } else if (foo.get(key) instanceof Map){
        DoubleToIntConverter.convertAllDoublesToInt((Map)foo.get(key));
      }
    }

  }

}
