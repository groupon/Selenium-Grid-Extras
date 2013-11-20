
package com.groupon.seleniumgridextras.daemons;

import com.groupon.seleniumgridextras.config.RuntimeConfig;

import org.apache.commons.io.FileUtils;

import java.io.File;

public class OsXDaemon implements Daemon {

  private String labelName = "com.groupon.seleniumgridextras.plist";


  @Override
  public void installDaemon() {
    File file = new File(getLabelName());

    try {
      FileUtils.writeStringToFile(file, getXml());
    } catch (Exception error) {
      System.out.println("Could not write launchd plist to " + getLabelName());
      error.printStackTrace();
      System.exit(1);

    }
  }

  @Override
  public void uninstallDaemon() {
    File file = new File(getLabelName());

    if(file.exists()){
      file.delete();
      System.out.println("Deleted the " + getLabelName());
    } else {
      System.out.println(getLabelName() + " didn't exist so no need to delete it");
    }

  }


  public String getLabelName() {
    return labelName;
  }

  public String getInitDExecutablePath(){
    return  RuntimeConfig.getOS().getUserHome() + "/Library/LaunchAgents/" + getLabelName();
  }

  protected String getDaemonName() {
    return getLabelName();
  }


  protected String getWorkingDir() {
    return RuntimeConfig.getSeleniungGridExtrasHomePath();
  }


  protected String getLogDir() {
    return RuntimeConfig.getSeleniungGridExtrasHomePath() + RuntimeConfig.getConfig().getSharedDirectory();
  }

  protected String getExecutableFilePath() {
    return RuntimeConfig.getSeleniumGridExtrasJarFile().getAbsolutePath();
  }

  protected String getJava() {
    return System.getProperty("java.home") + "/java";
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
           + "       <string>" + getWorkingDir() + "</string>\n"
           + "     <key>WorkingDirectory</key>\n"
           + "       <string>" + getWorkingDir() + "</string>\n"
           + "     <key>ProgramArguments</key>\n"
           + "       <array>\n"
           + "         <string>" + getJava() + "</string>\n"
           + "         <string>-jar</string>\n"
           + "         <string>" + getExecutableFilePath() + "</string>\n"
           + "       </array>\n"
           + "     <key>StandardErrorPath</key>\n"
           + "       <string>" + getLogDir() + "/seleniung_grid_extras_err.log</string>\n"
           + "     <key>StandardOutPath</key>\n"
           + "       <string>" + getLogDir() + "/seleniung_grid_extras_out.log</string>\n"
           + "  </dict>\n"
           + "</plist>";

  }

}
