package com.groupon.seleniumgridextras.utilities;

import com.google.gson.Gson;
import org.apache.log4j.Logger;

import java.io.*;
import java.time.LocalTime;
import java.util.Map;

/**
 * Created by xhu on 18/06/2014.
 */
public class Config {
    private static Logger logger = Logger.getLogger(Config.class);
    private volatile int max_session = -1;
    private volatile LocalTime start = LocalTime.MIN;
    private volatile LocalTime end = LocalTime.MAX;


    public int getMax_session() {
        return max_session;
    }

    public boolean WorkingTime() {
        if(start.equals(LocalTime.MIN)&&end.equals(LocalTime.MAX))
            return true;
        return LocalTime.now().isAfter(start) && LocalTime.now().isBefore(end);
    }

    private static volatile Config config = null;

    private static Gson gson = new Gson();

    public static void load(File configFile) {
        if (configFile == null || !configFile.exists()) {
            logger.info(String.format("Config file does not exist or not specified, try to use default one (config.json)."));
            configFile = new File(Env.current().getHomeDir(), "config.json");
        }

        if (configFile.exists()) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(configFile));
            } catch (FileNotFoundException e) {
                // never here
                e.printStackTrace();
            }
            config = gson.fromJson(br, Config.class);
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            logger.info(String.format("Config file [%s] does not exist. Get config from env. Apply default value if not defined.\n", configFile.getAbsolutePath()));
            config = fromEnv();
        }
    }

    private static Config fromEnv() {
        config = new Config();
        String max_session = System.getenv("grid_max_session");
        if (max_session != null) {
            config.max_session = Integer.parseInt(max_session);
        }
        return config;
    }

    public void applyToProcess(ProcessBuilder builder) {
        Map<String, String> env = builder.environment();
        logger.info(String.format("apply config to process: %s", builder.command()));
        env.put("grid_max_session", String.valueOf(max_session));
    }

    public static Config current() {
        if (config == null) {
            config = fromEnv();
        }
        return config;
    }

    public File toFile(File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw =new BufferedWriter(fw);
        bw.write(gson.toJson(this));
        bw.close();
        return file;
    }

    public void pp() {
        System.out.println("Grid Config");
        System.out.format(" - max_session: %d\n", max_session);
        System.out.format(" - start: %s\n", start.toString());
        System.out.format(" - end: %s\n", end.toString());
    }

    public void install() throws IOException {
        toFile(new File(Env.current().getInstallDir(), "config.json"));
    }
}
