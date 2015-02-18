package com.groupon.seleniumgridextras.config.capabilities;

public class IPhone extends Capability {

  @Override
  public String getWebDriverClass() {
    return "io.appium.java_client.ios.IOSDriver";
  }

  public String getIcon(){
    return "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAALGPC/xhBQAAAAlwSFlzAAAOwwAADsMBx2+oZAAAAN1JREFUOE9joCpITk62Tk1NbcCHU1JSSqHKMQFIQVpa2n+gopfYMFD+C0geqhwTgAzITE/vgXIxQGZmphvJBmRnZ1sCxSNAbKINsLe3ZwE62SItLdEmPT39GFBTtrGxCz/RBgA1l4MUImNQGGRlpXkQZQCQ3oBuADCGVIh2QWpq8kp0A0DiJBiQGotuAFDsBjA8ookyAMJOygdqmAejXYyNiQ9EKBcDEG0AyDZsODUpyY+gASAFhDBUOSaAGQCkv4D4KSlJ85KSYoKBaaAUlA6IMaAapggXhhkOAQwMAAoA8JzOi9pLAAAAAElFTkSuQmCC";
  }
}
