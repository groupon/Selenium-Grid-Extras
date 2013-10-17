//package com.groupon.seleniumgridextras.config;
//
//import org.junit.Test;
//import org.junit.Before;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.Assert.assertEquals;
//
//public class ConfigMergerTest {
//
//  private String emptyConfigJson;
//  private Map leftHash;
//
//  @Before
//  public void setUp() throws Exception {
//
//    leftHash = new HashMap();
//    leftHash.put("a", 1);
//    leftHash.put("b", "string");
//
//    String[] array1 = new String[2];
//    array1[0] = "a";
//    array1[1] = "b";
//    leftHash.put("c", array1);
//
//    Map hash1 = new HashMap();
//    hash1.put("aa", 2);
//    hash1.put("bb", "string bb");
//
//    Map hash2 = new HashMap();
//    hash2.put("aaa", 3);
//    hash2.put("bbb", "string bbb");
//    hash1.put("cc", hash2);
//
//
//    leftHash.put("d", hash1);
//
//  }
//
//  @Test
//  public void testMergeNestedMap() throws Exception {
//
//    Map right = new HashMap();
//
//    Map nested1 = new HashMap();
//    Map nested2 = new HashMap();
//
//    nested2.put("aaa", "aaa");
//    nested2.put("bbb", 10);
//
//    String[] nestedStringArray = new String[2];
//    nestedStringArray[0] = "nested";
//    nestedStringArray[1] = "array";
//
//    nested2.put("ccc", nestedStringArray);
//
//
//    nested1.put("cc", nested2);
//
//    right.put("d", nested1);
//
//    Map actual = ConfigMerger.overwriteMergeStrategy(leftHash, right);
//
//
//
//    assertEquals("", actual);
//  }
//
//  @Test
//  public void testMergeInteger() throws Exception {
//    Map right = new HashMap();
//    right.put("a", 2);
//
//    assertEquals(2, ConfigMerger.overwriteMergeStrategy(leftHash, right).get("a"));
//  }
//
//  @Test
//  public void testMergeString() throws Exception {
//    Map right = new HashMap();
//    right.put("b", "new string");
//
//    assertEquals("new string", ConfigMerger.overwriteMergeStrategy(leftHash, right).get("b"));
//  }
//
//  @Test
//  public void testMergeArray() throws Exception {
//    int[] array = new int[3];
//    array[0] = 0;
//    array[1] = 1;
//    array[2] = 3;
//    Map right = new HashMap();
//
//    right.put("c", array);
//
//    assertEquals(array, ConfigMerger.overwriteMergeStrategy(leftHash, right).get("c"));
//  }
//
//  @Test
//  public void testMergeNonExistingOnLeft() throws Exception {
//    Map right = new HashMap();
//    right.put("zzz", "new string");
//
//    assertEquals("new string", ConfigMerger.overwriteMergeStrategy(leftHash, right).get("zzz"));
//  }
//
//  @Test
//  public void testMergeEmptyMap() throws Exception {
//    Map right = new HashMap();
//
//    assertEquals(leftHash, ConfigMerger.overwriteMergeStrategy(leftHash, right));
//  }
//
//
//
//}
