package com.groupon.seleniumgridextras.daemons;

import com.google.gson.JsonObject;

import com.groupon.seleniumgridextras.ExecuteCommand;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

import org.apache.commons.io.FileUtils;

import java.io.File;

public class WindowsDaemon extends DaemonWrapper {

  protected File daemonConfigFile;

  @Override
  public void installDaemon() {
    uninstallDaemon();

    daemonConfigFile = getDaemonConfigFile();

    writeDaemonConfigFile(daemonConfigFile);

    System.out.println("Attempting to add daemon as a scheduled task");
    JsonObject
        result =
        ExecuteCommand
            .execRuntime(createDaemonCommand(getDaemonName(), daemonConfigFile.getAbsolutePath()));

    System.out.println(result.get("out"));
    System.out.println(result.get("error"));


  }

  protected File getDaemonConfigFile() {
    return new File(getWorkingDirectory() + RuntimeConfig.getOS().getFileSeparator()
                    + "windowsDaemonConfig.xml");
  }

  protected void writeDaemonConfigFile(File configFilePath) {
    try {
      FileUtils.writeStringToFile(configFilePath, getXml());
    } catch (Exception error) {
      System.out.println(
          "Could not write " + configFilePath.getAbsolutePath() + " for " + getDaemonName());
      error.printStackTrace();
      System.exit(1);
    }
  }

  @Override
  public void uninstallDaemon() {
    System.out.println("Attempting to uninstall daemon as a scheduled task");
    JsonObject result = ExecuteCommand.execRuntime(deleteDaemonCommand(getDaemonName()));

    daemonConfigFile = getDaemonConfigFile();
    if (daemonConfigFile.exists()){
      daemonConfigFile.delete();
    }

    System.out.println(result.get("out"));
    System.out.println(result.get("error"));

  }

  protected String createDaemonCommand(String daemonName, String xmlLocation) {
    return "schtasks /create /tn " + daemonName + " /xml " + xmlLocation;
  }

  protected String deleteDaemonCommand(String daemonName) {
    return "schtasks /delete /F /tn " + daemonName;
  }

  protected String getXml() {
    final String userName = RuntimeConfig.getOS().getUserName();

    return "<?xml version=\"1.0\" encoding=\"UTF-16\"?>\n"
           + "<Task version=\"1.2\" xmlns=\"http://schemas.microsoft.com/windows/2004/02/mit/task\">\n"
           + "  <RegistrationInfo>\n"
           + "    <Author>" + userName + "</Author>\n"
           + "  </RegistrationInfo>\n"
           + "  <Triggers>\n"
           + "    <LogonTrigger>\n"
           + "      <Repetition>\n"
           + "        <Interval>PT" + getCheckInterval() + "M</Interval>\n"
           + "        <StopAtDurationEnd>false</StopAtDurationEnd>\n"
           + "      </Repetition>\n"
           + "      <Enabled>true</Enabled>\n"
           + "      <UserId>" + userName + "</UserId>\n"
           + "    </LogonTrigger>\n"
           + "  </Triggers>\n"
           + "  <Principals>\n"
           + "    <Principal id=\"Author\">\n"
           + "      <UserId>" + userName + "</UserId>\n"
           + "      <LogonType>InteractiveToken</LogonType>\n"
           + "      <RunLevel>HighestAvailable</RunLevel>\n"
           + "    </Principal>\n"
           + "  </Principals>\n"
           + "  <Settings>\n"
           + "    <MultipleInstancesPolicy>Parallel</MultipleInstancesPolicy>\n"
           + "    <DisallowStartIfOnBatteries>false</DisallowStartIfOnBatteries>\n"
           + "    <StopIfGoingOnBatteries>true</StopIfGoingOnBatteries>\n"
           + "    <AllowHardTerminate>true</AllowHardTerminate>\n"
           + "    <StartWhenAvailable>true</StartWhenAvailable>\n"
           + "    <RunOnlyIfNetworkAvailable>false</RunOnlyIfNetworkAvailable>\n"
           + "    <IdleSettings>\n"
           + "      <StopOnIdleEnd>true</StopOnIdleEnd>\n"
           + "      <RestartOnIdle>false</RestartOnIdle>\n"
           + "    </IdleSettings>\n"
           + "    <AllowStartOnDemand>true</AllowStartOnDemand>\n"
           + "    <Enabled>true</Enabled>\n"
           + "    <Hidden>false</Hidden>\n"
           + "    <RunOnlyIfIdle>false</RunOnlyIfIdle>\n"
           + "    <WakeToRun>true</WakeToRun>\n"
           + "    <ExecutionTimeLimit>P3D</ExecutionTimeLimit>\n"
           + "    <Priority>7</Priority>\n"
           + "  </Settings>\n"
           + "  <Actions Context=\"Author\">\n"
           + "    <Exec>\n"
           + "      <Command>\"" + getJavaExecutable() + ".exe\"</Command>\n"
           + "      <Arguments>-jar \"" + getJarPath() + "\"</Arguments>\n"
           + "      <WorkingDirectory>" + getWorkingDirectory() + "</WorkingDirectory>\n"
           + "    </Exec>\n"
           + "  </Actions>\n"
           + "</Task>";


  }

}
