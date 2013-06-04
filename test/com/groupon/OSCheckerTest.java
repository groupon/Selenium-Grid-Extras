package com.groupon;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA. User: dima Date: 6/4/13 Time: 5:03 PM To change this template use
 * File | Settings | File Templates.
 */
public class OSCheckerTest {


  @Test
  public void testGetOSName() throws Exception {
    assertEquals(OSChecker.getOSName(), System.getProperty("os.name"));
  }
}
