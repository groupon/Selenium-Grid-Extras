package com.groupon.seleniumgridextras.grid;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.groupon.seleniumgridextras.config.RuntimeConfig;
import com.groupon.seleniumgridextras.utilities.Command;
import com.groupon.seleniumgridextras.utilities.threads.CommonThreadPool;
import com.groupon.seleniumgridextras.monitor.DaemonCallable;
import com.groupon.seleniumgridextras.tasks.AppiumNodeTasks;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import com.groupon.seleniumgridextras.utilities.Env;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Future;

/**
 * Created by jfarrier on 13/11/2015.
 */
public class CreateAndroidAppiumNode extends DaemonCallable {

    private static Logger logger = Logger.getLogger(CreateAndroidAppiumNode.class);
    private static ArrayList<String> devices = new ArrayList();
    private Map<String, String> deviceInfo;
    public static List<String> appiumProperties = Arrays.asList("udid", "port", "bootstrap-port", "selendroid-port", "platform-version");
    public static List<String> capProperties = Arrays.asList( "deviceName", "udid", "version", "deviceType");
    public static List<String> configProperties = Arrays.asList( "port");
    public String name;
    private Command command;

    File configFile;
    File nodeConfigFile;
    JsonObject config;

    //public CreateAndroidAppiumNode(File configFile) {
        //this.configFile = configFile;
    //}
    public CreateAndroidAppiumNode(File configFile, Map<String, String> deviceInfo, String attachedDevice, File nodeConfigFile, JsonObject config) {
        this.configFile = configFile;
        this.deviceInfo = deviceInfo;
        name = attachedDevice;
        this.nodeConfigFile = nodeConfigFile;
        this.config = config;
    }

    @Override
    protected void run() {
        try {


            if (!isRunning()) {
                restart();
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean isRunning() {
        if (command == null) return false;
        return !command.isComplete();
    }

    private void restart() throws Exception {
        if (command != null) {
            command.kill();
        }

//        command = new Command(FilenameUtils.removeExtension(configFile.getName()), createCommand()).outputTo(new File(Env.current().getOutputDir(), configFile.getName())).go(false);
        command = createCommand().go(true);
    }


    protected Command createCommand() throws Exception {
        // get config


        //Should we kill all ports?, i.e. selendroid port and bootstrap port
        Command cmd = new Command() {
            @Override
            protected void beforeRun() {
                super.beforeRun();
                //TODO use new kill ports
                /*try {
                    int port = config.getAsJsonObject("appiumOptions").get("port").getAsInt();
                    Command kill = Env.current().getCommand().killProcessByPort(port).go(false);
                    logger.debug(kill.getResult());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }
        };

        String appiumExecutable = "appium.exe";
        try{
            String appiumExe = Env.current().getAppiumExecutable();
            if(appiumExe!=null)
                appiumExecutable = appiumExe;
        }catch(Exception e){

        }
        String logFile = name.replace(".json", ".log");

        String workingDirectory = System.getProperty("user.dir");
        String logFileFullPath = workingDirectory + RuntimeConfig.getOS().getFileSeparator() + "log" +
                RuntimeConfig.getOS().getFileSeparator() + logFile;

        logger.debug("Creating command " + cmd.startWith(appiumExecutable).
                arg("--nodeconfig").arg(nodeConfigFile.getAbsolutePath()).
                arg(extractAppiumOptions(config)).arg("--log").arg(logFileFullPath).
                outputTo(new File(Env.current().getOutputDir(), FilenameUtils.removeExtension(configFile.getName()) + ".out")).asString());

        String command = cmd.startWith(appiumExecutable).
                arg("--nodeconfig").arg(nodeConfigFile.getAbsolutePath()).
                arg(extractAppiumOptions(config)).arg("--log").arg(logFileFullPath).
                outputTo(new File(Env.current().getOutputDir(), FilenameUtils.removeExtension(configFile.getName()) + ".out")).asString();

        //String command = "appium.exe" + " --nodeconfig " + nodeConfigFile.getAbsolutePath()+ " " + extractAppiumOptions(config)
        logger.debug("command = " + command.toString());
        Map<String, String> appiumNode = new HashMap<String, String>();
        appiumNode.put("action", "add");
        appiumNode.put("command", command);
        appiumNode.put("port", String.valueOf(getPort(config)));
        new AppiumNodeTasks().execute(appiumNode);

        //example command
        //command = appium.exe --nodeconfig C:\TFS\Workspace\Automation\Trunk\Tools\Selenium-Grid-Extras-master\SeleniumGridExtras\target\tmp\02157df2b317393b.json --platform-name Android --platform-version 19 --automation-name Selendroid --port 4562 --bootstrap-port 4563 --selendroid-port 4564 --log-level warn:debug --udid 02157df2b317393b --log C:\TFS\Workspace\Automation\Trunk\Tools\Selenium-Grid-Extras-master\log\02157df2b317393b.log
        //don't think we need to output anymore???
        //should I change this just to using String?

        //TODO should we just use services ala http://aksahu.blogspot.in/2015/10/start-and-stop-appium-server.html
        return cmd.startWith(appiumExecutable).
                arg("--nodeconfig").arg(nodeConfigFile.getAbsolutePath()).
                arg(extractAppiumOptions(config)).arg("--log").arg(logFileFullPath);//.
//                arg("--quiet").
        //outputTo(new File(Env.current().getOutputDir(), FilenameUtils.removeExtension(configFile.getName()) + ".out"));
    }



    public String getPort(JsonObject config) throws IOException {
        JsonObject cap = config.getAsJsonObject("appiumOptions");

        for (Map.Entry<String, JsonElement> entry : cap.entrySet()) {
            JsonPrimitive value = entry.getValue().getAsJsonPrimitive();
            if (value.isBoolean()) {

            } else {
                String keyValue = entry.getKey();
                if(keyValue.toLowerCase().equals("port"))
                    return entry.getValue().getAsString();
            }
        }
        return null;
    }

    public List<String> extractAppiumOptions(JsonObject config) throws IOException {
        JsonObject cap = config.getAsJsonObject("appiumOptions");
        List<String> args = new ArrayList();
        for (Map.Entry<String, JsonElement> entry : cap.entrySet()) {
            JsonPrimitive value = entry.getValue().getAsJsonPrimitive();
            if (value.isBoolean()) {
                boolean b = value.getAsBoolean();
                if (b) {
                    args.add("--" + entry.getKey());
                }
            } else {
                args.add("--" + entry.getKey());
                args.add(entry.getValue().getAsString());
            }
        }
        return args;
    }

    @Override
    public Object call() throws Exception {
        beforeRun();
        try {
            run();
        } catch (Exception e) {
            throw e;
        } finally {
            afterRun();
            return null;
        }
    }

    public Future getFuture() {
        return future;
    }

    private Future future;

    @Override
    public DaemonCallable start() {
        future = CommonThreadPool.startCallable(this);
        return this;
    }
}
