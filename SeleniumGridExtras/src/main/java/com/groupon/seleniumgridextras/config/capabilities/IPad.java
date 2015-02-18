package com.groupon.seleniumgridextras.config.capabilities;

public class IPad extends Capability {

  @Override
  public String getWebDriverClass() {
    return "io.appium.java_client.ios.IOSDriver";
  }

  public String getIcon(){
    return "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAALGPC/xhBQAAAAlwSFlzAAAOwwAADsMBx2+oZAAAAOtJREFUOE9joCpIS0urSk1NbSCEQeqgWlABUOJ/cnLyS0IYpA6qBRUATd8OZeIFONXhkqivr2eCMsGAJANSk5L8MrIy2qBcMCDKgNTUpPyUlJSzmenpPTDa3t6eAyJHwACgZkdQQCHjlJTkNSQYkBqBbgDIRWBFQEDQgLi4FG10A0AY6BVtZHUYAFkC5GdkzcVFRVdgsUGUASCQmBitZWzswp+enq4F8z8IEG0ALoBTHdCp+zMzM2UJYZA6qBZUgOxnEAba9AWU9kE0uhxUCyoAKvwFUwD097G0tNQrQLEmEA3hww3+BdHBwAAAUYXTGfyLOn8AAAAASUVORK5CYII=";
  }
}
