package com.groupon.seleniumgridextras.utilities;

import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA. User: dima Date: 7/16/14 Time: 12:25 PM To change this template use
 * File | Settings | File Templates.
 */
public class DoubleToIntConverterTest {

  @Test
  public void testConversion() throws Exception {
    Map input = new HashMap<String, Object>();
    Map nested = new HashMap<String, Object>();
    List nestedArray = new LinkedList();

    nestedArray.add(1);
    nestedArray.add(1.9);
    nestedArray.add(2.0);
    nestedArray.add("Foo");

    nested.put("aa", "a");
    nested.put("bb", 2);
    nested.put("cc", 2.0);
    nested.put("dd", 2.8);

    input.put("a", "a");
    input.put("b", 1);
    input.put("c", 1.0);
    input.put("d", 1.8);
    input.put("e", nested);
    input.put("f", nestedArray);

    Map expected = new HashMap<String, Object>();
    Map expectedNested = new HashMap<String, Object>();
    List expectedNestedArray = new LinkedList();

    expectedNestedArray.add(1);
    expectedNestedArray.add(1.9);
    expectedNestedArray.add(2);
    expectedNestedArray.add("Foo");


    expectedNested.put("aa", "a");
    expectedNested.put("bb", 2);
    expectedNested.put("cc", 2);
    expectedNested.put("dd", 2.8);

    expected.put("a", "a");
    expected.put("b", 1);
    expected.put("c", 1);
    expected.put("d", 1.8);
    expected.put("e", nested);
    expected.put("f", expectedNestedArray);

    DoubleToIntConverter.convertAllDoublesToInt(input);

    for (String key : (Set<String>) expected.keySet()) {

      if (key.equals("e")) {
        for (String nestedKey : (Set<String>) ((Map<String, Object>) expected.get("e")).keySet()) {
          assertEquals(((Map<String, Object>) expected.get("e")).get(nestedKey),
                       ((Map<String, Object>) input.get("e")).get(nestedKey));
        }
      } else {
        assertEquals(expected.get(key), input.get(key));
      }

    }


  }

}
