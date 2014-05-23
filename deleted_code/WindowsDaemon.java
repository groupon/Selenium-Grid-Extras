package com.groupon.seleniumgridextras.daemons;

import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.ExecuteCommand;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WindowsDaemon extends DaemonWrapper {

  private static Logger logger = Logger.getLogger(WindowsDaemon.class);

  protected File daemonConfigFile;

  @Override
  public void installDaemon() {
    uninstallDaemon();

    daemonConfigFile = getDaemonConfigFile();

    writeDaemonConfigFile(daemonConfigFile);

    logger.info("Attempting to add daemon as a scheduled task");
    JsonObject
        result =
        ExecuteCommand
            .execRuntime(createDaemonCommand(getDaemonName(), daemonConfigFile.getAbsolutePath()));

    logger.debug(result);
  }

  protected File getDaemonConfigFile() {
    return new File(getWorkingDirectory() + RuntimeConfig.getOS().getFileSeparator()
                    + "windowsDaemonConfig.xml");
  }

  protected void writeDaemonConfigFile(File configFilePath) {
    try {
      FileUtils.writeStringToFile(configFilePath, getXml());
    } catch (Exception error) {
      logger.error(
          "Could not write " + configFilePath.getAbsolutePath() + " for " + getDaemonName());
      logger.error(error);
      System.exit(1);
    }
  }

  @Override
  public void uninstallDaemon() {
    logger.info("Attempting to uninstall daemon as a scheduled task");
    logger.debug(ExecuteCommand.execRuntime(deleteDaemonCommand(getDaemonName())));
    daemonConfigFile = getDaemonConfigFile();
    if (daemonConfigFile.exists()) {
      daemonConfigFile.delete();
    }

  }

  protected String createDaemonCommand(String daemonName, String xmlLocation) {
    return "schtasks /create /tn " + daemonName + " /xml \"" + xmlLocation + "\"";
  }

  protected String deleteDaemonCommand(String daemonName) {
    return "schtasks /delete /F /tn " + daemonName;
  }

  protected String getXml() {
    final String userName = RuntimeConfig.getOS().getUserName();


    String xml = "<?xml version=\"1.0\" encoding=\"UTF-16\"?>\n"
                  + "<Task version=\"1.2\" xmlns=\"http://schemas.microsoft.com/windows/2004/02/mit/task\">\n"
                  + "  <RegistrationInfo>\n"
                  + "    <Author>" + userName + "</Author>\n"
                  + "  </RegistrationInfo>\n"
                  + "  <Triggers>\n"
                  + "    <TimeTrigger>\n"
                  + "      <Repetition>\n"
                  + "        <Interval>PT" + getCheckInterval() + "M</Interval>\n"
                  + "        <StopAtDurationEnd>false</StopAtDurationEnd>\n"
                  + "      </Repetition>\n"
                  + "      <StartBoundary>" + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()) + "</StartBoundary>\n"
                  + "      <Enabled>true</Enabled>\n"
                  + "    </TimeTrigger>\n"
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



    logger.debug(xml);

    return xml;
  }

}
