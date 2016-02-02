package com.groupon.seleniumgridextras.utilities;

import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xhu on 9/06/2014.
 */
public class Windows extends Env {

    public Windows() {
        commandLib = new WindowsCommandLib();
    }

    private static Logger logger = Logger.getLogger(Windows.class);

    @Override
    public String getJavaExecutable() {
        return getExecutable("java.exe");
    }

    @Override
    public String getNPMExecutable() {
        return getExecutable("npm.cmd");
    }

    @Override
    public String getEmulatorExecutable() {
        return getExecutable("emulator.exe");
    }

    @Override
    public String getClassPath(String... paths) {
        return "\"" + StringUtils.join(paths, getPathSeparator()) + "\"";
    }

    @Override
    public String getAppiumExecutable() {
        return getExecutable("appium.cmd");
    }

    protected File daemonConfigFile;

    protected File destJar;

    @Override
    public void install() throws IOException, InterruptedException {
        uninstall();
        File installDir = current().getDir(getInstallDir());
        destJar = new File(installDir, current().getGridExtJarFile().getName());
        Files.copy(current().getGridExtJarFile(), destJar);
        // Write daemon config file
        daemonConfigFile = getDaemonConfigFile();
        try {
            FileUtils.writeStringToFile(daemonConfigFile, getXml());
        } catch (Exception error) {
            logger.error(
                    "Could not write " + daemonConfigFile.getAbsolutePath() + " for " + current().getDaemonName());
            logger.error(error);
            System.exit(1);
        }

        Config.current().install();

        // add daemon as a scheduled task
        logger.info("Attempting to add daemon as a scheduled task");
        new Command().
                startWith("cmd /C schtasks /create /tn").
                arg(current().getDaemonName()).arg("/xml").arg(daemonConfigFile.getAbsolutePath()).
                outputTo(current().getOutputFile("install.out")).go(true);
    }

    @Override
    public void uninstall() throws IOException, InterruptedException {
        logger.info("Attempting to uninstall daemon as a scheduled task");
        new Command().
                startWith("cmd").arg("/C").arg("schtasks").arg("/delete").arg("/F").arg("/tn").arg(current().getDaemonName()).
                outputTo(current().getOutputFile("uninstall.out")).go(true);
        daemonConfigFile = getDaemonConfigFile();
        if (daemonConfigFile.exists()) {
            daemonConfigFile.delete();
        }
        logger.info(String.format("clean dir %s.", getInstallDir().getAbsolutePath()));
        FileUtils.cleanDirectory(getInstallDir());
    }

    private File getDaemonConfigFile() {
        return new File(getInstallDir(), "windowsDaemonConfig.xml");
    }

    private String getXml() {
        final String userName = getUserName();


        String xml = "<?xml version=\"1.0\" encoding=\"UTF-16\"?>\n"
                + "<Task version=\"1.2\" xmlns=\"http://schemas.microsoft.com/windows/2004/02/mit/task\">\n"
                + "  <RegistrationInfo>\n"
                + "    <Author>" + userName + "</Author>\n"
                + "  </RegistrationInfo>\n"
                + "  <Triggers>\n"
                + "    <TimeTrigger>\n"
                + "      <Repetition>\n"
                + "        <Interval>PT" + current().getDaemonCheckIntervalInMinutes() + "M</Interval>\n"
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
                + "    <MultipleInstancesPolicy>IgnoreNew</MultipleInstancesPolicy>\n"
                + "    <DisallowStartIfOnBatteries>false</DisallowStartIfOnBatteries>\n"
                + "    <StopIfGoingOnBatteries>false</StopIfGoingOnBatteries>\n"
                + "    <AllowHardTerminate>true</AllowHardTerminate>\n"
                + "    <StartWhenAvailable>true</StartWhenAvailable>\n"
                + "    <RunOnlyIfNetworkAvailable>true</RunOnlyIfNetworkAvailable>\n"
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
                + "      <Command>\"" + getJavaExecutable() + "\"</Command>\n"
                + "      <Arguments>-jar \"" + destJar.getAbsolutePath() + "\"</Arguments>\n"
                + "      <WorkingDirectory>" + getInstallDir() + "</WorkingDirectory>\n"
                + "    </Exec>\n"
                + "  </Actions>\n"
                + "</Task>";

        logger.debug(xml);

        return xml;
    }


    @Override
    public File getInstallDir() {
        return new File("C:\\GridExt");
    }

    @Override
    public void killProcessByName(String name) throws IOException {
        logger.info(String.format("Killing process %s.", name));
        Runtime.getRuntime().exec(String.format("taskkill /F /IM %s", name));
    }

    @Override
    public boolean isSoftwareInstalled(String name) {
        // TODO implement me
        return false;
    }
}
