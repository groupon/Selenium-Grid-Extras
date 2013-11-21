
package com.groupon.seleniumgridextras.daemons;

import com.groupon.seleniumgridextras.config.RuntimeConfig;

import org.apache.commons.io.FileUtils;

import java.io.File;

public class OsXDaemon extends DaemonWrapper {

  public OsXDaemon(){

  }

  @Override
  public void installDaemon() {
    File file = new File(getInitDExecutablePath());

    try {
      FileUtils.writeStringToFile(file, getXml());
    } catch (Exception error) {
      System.out.println("Could not write launchd plist to " + getDaemonName());
      error.printStackTrace();
      System.exit(1);

    }
  }

  @Override
  public void uninstallDaemon() {
    File file = new File(getInitDExecutablePath());

    if(file.exists()){
      file.delete();
      System.out.println("Deleted the " + getDaemonName());
    } else {
      System.out.println(getDaemonName() + " didn't exist so no need to delete it");
    }

  }

  protected String getInitDExecutablePath(){
    return  RuntimeConfig.getOS().getUserHome() + "/Library/LaunchAgents/" + getDaemonName();
  }

  @Override
  public void setDaemonName(String name) {
    this.daemonName = "com.groupon.seleniumgridextras."  + name + ".plist";
  }

  protected int getCheckInterval(){
    return this.interval * 60;
  }



  protected String getXml() {
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
           + "<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n"
           + "<plist version=\"1.0\">\n"
           + "  <dict>\n"
           + "     <key>KeepAlive</key>\n"
           + "       <true />\n"
           + "     <key>Label</key>\n"
           + "     <string>" + getDaemonName() + "</string>\n"
           + "     <key>RunAtLoad</key>\n"
           + "       <true/>      \n"
           + "     <key>RootDirectory</key>\n"
           + "       <string>" + getWorkingDirectory() + "</string>\n"
           + "     <key>WorkingDirectory</key>\n"
           + "       <string>" + getWorkingDirectory() + "</string>\n"
           + "     <key>ProgramArguments</key>\n"
           + "       <array>\n"
           + "         <string>" + getJavaExecutable() + "</string>\n"
           + "         <string>-jar</string>\n"
           + "         <string>" + getJarPath() + "</string>\n"
           + "       </array>\n"
           + "     <key>StandardErrorPath</key>\n"
           + "       <string>" + getLogDirectory() + "/seleniung_grid_extras_err.log</string>\n"
           + "     <key>StandardOutPath</key>\n"
           + "       <string>" + getLogDirectory() + "/seleniung_grid_extras_out.log</string>\n"
           + "     <key>StartInterval</key>\n"
           + "       <integer>" + getCheckInterval() + "</integer>\n"
           + "  </dict>\n"
           + "</plist>";

  }

}
