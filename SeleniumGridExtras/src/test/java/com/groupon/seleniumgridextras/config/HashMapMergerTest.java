package com.groupon.seleniumgridextras.config;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class HashMapMergerTest {

  private Map leftHash;

  @Before
  public void setUp() throws Exception {

    leftHash = new HashMap();
    leftHash.put("a", 1);
    leftHash.put("b", "string");

    String[] array1 = new String[2];
    array1[0] = "a";
    array1[1] = "b";
    leftHash.put("c", array1);

    Map hash1 = new HashMap();
    hash1.put("aa", 2);
    hash1.put("bb", "string bb");

    Map hash2 = new HashMap();
    hash2.put("aaa", 3);
    hash2.put("bbb", "string bbb");
    hash1.put("cc", hash2);


    leftHash.put("d", hash1);

  }

  @Test
  public void testMergeNestedMap() throws Exception {
    //By far the most complicated test in here, i wish assertEquals would not freak out
    //About having a nested array and just compared the values of the array and not the objects
    //Don't try to understand this part unless you had 8 hour of sleep
    //And feel free to fix it with something better!


    Map right = new HashMap();

    Map nested1 = new HashMap();
    Map nested2 = new HashMap();

    nested2.put("aaa", "aaa");
    nested2.put("bbb", 10);

    String[] nestedStringArray = new String[2];
    nestedStringArray[0] = "nested";
    nestedStringArray[1] = "array";

    nested2.put("ccc", nestedStringArray);


    nested1.put("cc", nested2);

    right.put("d", nested1);

    Map actual = HashMapMerger.overwriteMergeStrategy(leftHash, right);


    Map expected = new HashMap();

    expected.put("a", 1);
    expected.put("b", "string");

    String[] array1 = new String[2];
    array1[0] = "a";
    array1[1] = "b";
    expected.put("c", array1);

    Map expectedHashLevel1 = new HashMap();
    expectedHashLevel1.put("aa", 2);
    expectedHashLevel1.put("bb", "string bb");

    Map expectedHashLevel2 = new HashMap();
    expectedHashLevel2.put("aaa", "aaa");
    expectedHashLevel2.put("bbb", 10);

    String[] expectedStringArray = new String[2];
    expectedStringArray[0] = "nested";
    expectedStringArray[1] = "array";
    expectedHashLevel2.put("ccc", expectedStringArray);

    expectedHashLevel1.put("cc", expectedHashLevel2);


    expected.put("d", expectedHashLevel1);

    assertEquals(expected.keySet(), actual.keySet());
    assertEquals(expected.get("a"), actual.get("a"));
    assertEquals(expected.get("b"), actual.get("b"));
    assertArrayEquals((String[]) expected.get("c"), (String[]) actual.get("c"));

    Map expectedD = (HashMap) expected.get("d");
    Map actualD   = (HashMap) actual.get("d");

    assertEquals(expectedD.keySet(), actualD.keySet());
    assertEquals(expectedD.get("aa"), actualD.get("aa"));
    assertEquals(expectedD.get("bb"), actualD.get("bb"));

    Map expectedCC = (HashMap) expectedD.get("cc");
    Map actualCC   = (HashMap) actualD.get("cc");

    assertEquals(expectedCC.keySet(), actualCC.keySet());
    assertEquals(expectedCC.get("aaa"), actualCC.get("aaa"));
    assertEquals(expectedCC.get("bbb"), actualCC.get("bbb"));
    assertArrayEquals((String[]) expectedCC.get("ccc"), (String[]) actualCC.get("ccc"));
  }

  @Test
  public void testMergeInteger() throws Exception {
    Map right = new HashMap();
    right.put("a", 2);

    assertEquals(2, HashMapMerger.overwriteMergeStrategy(leftHash, right).get("a"));
  }

  @Test
  public void testMergeString() throws Exception {
    Map right = new HashMap();
    right.put("b", "new string");

    assertEquals("new string", HashMapMerger.overwriteMergeStrategy(leftHash, right).get("b"));
  }

  @Test
  public void testMergeArray() throws Exception {
    int[] array = new int[3];
    array[0] = 0;
    array[1] = 1;
    array[2] = 3;
    Map right = new HashMap();

    right.put("c", array);

    assertEquals(array, HashMapMerger.overwriteMergeStrategy(leftHash, right).get("c"));
  }

  @Test
  public void testMergeNonExistingOnLeft() throws Exception {
    Map right = new HashMap();
    right.put("zzz", "new string");

    assertEquals("new string", HashMapMerger.overwriteMergeStrategy(leftHash, right).get("zzz"));
  }

  @Test
  public void testMergeEmptyMap() throws Exception {
    Map right = new HashMap();

    assertEquals(leftHash, HashMapMerger.overwriteMergeStrategy(leftHash, right));
  }



}
