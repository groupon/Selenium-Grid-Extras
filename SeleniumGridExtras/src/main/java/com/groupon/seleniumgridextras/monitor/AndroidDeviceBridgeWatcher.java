package com.groupon.seleniumgridextras.monitor;

import com.groupon.seleniumgridextras.grid.CreateAndroidAppiumNode;
import com.groupon.seleniumgridextras.utilities.Env;
import com.groupon.seleniumgridextras.utilities.json.JsonFileReader;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.tasks.KillPort;
import com.groupon.seleniumgridextras.utilities.json.JsonCodec;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by xhu on 5/06/2014.
 */
public class AndroidDeviceBridgeWatcher extends DaemonCallable {
    private static Logger logger = Logger.getLogger(AndroidDeviceBridgeWatcher.class);
    public static List<String> deviceOptions = Arrays.asList("deviceName", "udid", "port", "bootstrap-port", "selendroid-port", "version", "platform-version", "url", "deviceType", "deviceCategory");
    public static List<String> appiumProperties = Arrays.asList("udid", "port", "bootstrap-port", "selendroid-port", "platform-version");
    public static List<String> capProperties = Arrays.asList( "deviceName", "udid", "version", "deviceType", "deviceCategory");
    public static List<String> configProperties = Arrays.asList( "port", "url");

    public static AndroidDeviceBridgeWatcher watch(File folder) {
        return (AndroidDeviceBridgeWatcher) new AndroidDeviceBridgeWatcher(folder).start();
    }


    private volatile File androidDeviceConfig;

    public AndroidDeviceBridgeWatcher(File file) {
        androidDeviceConfig = file;

    }

    private ConcurrentHashMap<String, String> nodes;

    @Override
    protected void run() {
        //TODO
        if(nodes==null)
            nodes = new ConcurrentHashMap<String, String>();
        try {
            logger.debug("starting to monitor adb");
            File file;
            JsonObject config = null;
            try {
                logger.debug("about to read config file " + androidDeviceConfig.getPath());
                config = JsonFileReader.getJsonObject(androidDeviceConfig);
                logger.debug("read config file");
            } catch (IOException e) {
                e.printStackTrace();
            }

            //check adb
            ArrayList<String> attachedDevices = getAttachedDevices();
            logger.debug("number of devices attacehed = " + attachedDevices.size());

            //are any devices not online
            for (Map.Entry<String, String> node : nodes.entrySet()) {
                if (!attachedDevices.contains(node.getKey())) {
                    //stop nodes where the devices aren't running anymore
                    System.out.println("Stopping port for device " + node.getKey());
                    System.out.println("Stopping port " + node.getValue());
                    try{
                        stopNodeByPort(node.getValue());
                        nodes.remove(node.getKey());
                    }catch(Exception e){

                    }
                }
            }

            //logger.debug("Checked devices are not online");
            //if devices are offline try to send reboot? //TODO stop nodes in bad state/restart adb?

            //if new device exists then add
            for (String attachedDevice : attachedDevices) {
                boolean alreadyCreated = false;
                if (!nodes.containsKey(attachedDevice)) {
                    System.out.println("Attaching device " + attachedDevice);
                    createDevice(attachedDevice, config, androidDeviceConfig);
                }

            }
        }catch(Exception e){e.printStackTrace();logger.debug(e.getMessage());}

        logger.debug("Added new devices");
        try {
            Thread.sleep(20000);
        }catch (Exception e){}
    }

    private void stopNodeByPort(String port) {
        //Run task KillPort
        Map<String, String> parameter = new HashMap<String, String>();
        parameter.put(JsonCodec.OS.KillCommands.PORT, port);
        logger.debug("killing port " + parameter);
        new KillPort().execute(parameter);
    }

    private void createNodeForDevice(File configFile, Map<String, String> deviceInfoToSet, String attachedDevice) throws Exception {

        final JsonObject config = editNodeConfig(JsonFileReader.getJsonObject(configFile), deviceInfoToSet);

        logger.debug(config.get("nodeconfig").toString());
        String name = configFile.getName();
        String nameForConfig = attachedDevice;
        if(nameForConfig!=null)
            name = nameForConfig+".json";
        File nodeConfigFile = Env.current().dumpTmpFile(name, config.get("nodeconfig").toString());
        new CreateAndroidAppiumNode(configFile, deviceInfoToSet,  name, nodeConfigFile, config).start();
    }

    private JsonObject editNodeConfig(JsonObject config, Map<String, String> deviceInfo) {
        if(deviceInfo!=null){
            //change Appium Options
            //need to do something smarter with AVD vs udid
            JsonObject cap = config.getAsJsonObject("appiumOptions");
            for(String prop:appiumProperties){
                if(deviceInfo.containsKey(prop)){
                    cap.addProperty(prop, deviceInfo.get(prop));
                }
            }
            //change nodeconfig options


            cap = config.getAsJsonObject("nodeconfig").getAsJsonObject("configuration");
            for(String prop:configProperties){
                if(deviceInfo.containsKey(prop)){
                    cap.addProperty(prop, deviceInfo.get(prop));
                }else if(prop.equals("url")){
                    //System.out.println("contains url");
                    if(deviceInfo.containsKey("port")){
                        //System.out.println("has port");
                        String url = "";
                        try{
                            String host = cap.get("host").getAsString();
                            //System.out.println("host = " + host);
                            url = "http://"+host+":"+deviceInfo.get("port")+"/wd/hub";
                            //System.out.println("url =" + url);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        if(url.equals("")){
                            try{
                                url = cap.get("url").getAsString();
                                url.replaceAll(":[0-9][0-9][0-9][0-9]",":"+deviceInfo.get("port"));
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                        if(!url.equals(""))
                            cap.addProperty(prop, url);
                    }
                }
            }

            JsonArray capabilities =  config.getAsJsonObject("nodeconfig").getAsJsonArray("capabilities");
            for(JsonElement capability : capabilities) {
                cap = capability.getAsJsonObject();
                for (String prop : capProperties) {
                    if (deviceInfo.containsKey(prop)) {
                        cap.addProperty(prop, deviceInfo.get(prop));
                    }
                }
            }
        }
        return config;
    }

    private void createDevice(String attachedDevice, JsonObject config, File file) {
        //does the device exist in the config file
        JsonArray devices = config.getAsJsonArray("devices");

        Map<String, String> deviceInfoToSet = new HashMap<String, String>();

        for(JsonElement device : devices){
            JsonObject deviceInfo = device.getAsJsonObject();
            //check udid

            if(deviceInfo.has("udid")){
                String udid = deviceInfo.get("udid").getAsString();
                if (udid.equals(attachedDevice)){
                    for(String option: deviceOptions) {
                        if (deviceInfo.has(option)) {
                            deviceInfoToSet.put(option, deviceInfo.get(option).getAsString());

                        }
                    }
                    //we have the device information now create a config for it and start it...
                    logger.info("Creating node for " + attachedDevice);
                    try{
                        createNodeForDevice(file, deviceInfoToSet, attachedDevice);
                        nodes.put(attachedDevice, deviceInfo.get("port").getAsString());
                    }catch(Exception e){

                    }

                    return;
                }
            }else{
            }
        }
    }




    private ArrayList<String> getAttachedDevices() {
        ArrayList<String> attachedDevices = new ArrayList();
        Process cmdProc = null;
        try {
            cmdProc = Runtime.getRuntime().exec("adb devices");
            Thread.sleep(10000);
        } catch (IOException e) {
            logger.debug(e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        BufferedReader stdoutReader = new BufferedReader(
                new InputStreamReader(cmdProc.getInputStream()));
        String line;
        //TODO add additional lines for adb.  when it ends in device then the device is online and operational.
        try {
            while ((line = stdoutReader.readLine()) != null) {
                if(line.contains("\tdevice")){
                    attachedDevices.add(line.substring(0,line.indexOf("\tdevice")));
                }
            }
        } catch (IOException e) {
            logger.debug(e.getMessage());
            e.printStackTrace();
        }

        BufferedReader stderrReader = new BufferedReader(
                new InputStreamReader(cmdProc.getErrorStream()));
        try {
            while ((line = stderrReader.readLine()) != null) {
                logger.debug(line);
            }
        } catch (IOException e) {
            logger.debug(e.getMessage());
            e.printStackTrace();
        }

        try {
            stderrReader.close();
            stdoutReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int retValue = cmdProc.exitValue();

        return attachedDevices;
    }




    @Override
    protected void afterRun() {

    }
}