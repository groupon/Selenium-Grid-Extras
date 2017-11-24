package com.groupon.seleniumgridextras.utilities;

public class VersionCompare {

  /**
  Return 1 if version1 is greater than version2
  Return 0 if version 1 is equal to version2
  Return -1 if version 1 is less than version2
  */
  public static int versionCompare(String version1, String version2) {
    int major1 = (version1.split(".").length > 0) ? Integer.parseInt(version1.split(".")[0]) : 0;
    int minor1 = (version1.split(".").length > 1) ? Integer.parseInt(version1.split(".")[1]) : 0;
    int patch1 = (version1.split(".").length > 2) ? Integer.parseInt(version1.split(".")[2]) : 0;

    int major2 = (version2.split(".").length > 0) ? Integer.parseInt(version2.split(".")[0]) : 0;
    int minor2 = (version2.split(".").length > 1) ? Integer.parseInt(version2.split(".")[1]) : 0;
    int patch2 = (version2.split(".").length > 2) ? Integer.parseInt(version2.split(".")[2]) : 0;

    if (major1 > major2) {
      return 1;
    } else if (major1 < major2) {
      return -1;
    } else { // Majors match
      if (minor1 > minor2) {
        return 1;
      } else if (minor1 < minor2) {
        return -1;
      } else {
        if (patch1 > patch2) {
          return 1;
        } else if (patch1 < patch2) {
          return -1;
        } else {
          return 0;
        }
      }
    }
  }
}
