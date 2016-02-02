package com.groupon.seleniumgridextras.utilities;

import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import com.groupon.seleniumgridextras.utilities.threads.CommonThreadPool;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by xhu on 6/06/2014.
 */
public class Command {

    public class Result {
        public String command;
        public int exitCode = 999;
        public List<String> output = new ArrayList();
        public List<String> error = new ArrayList();

        @Override
        public String toString() {
            return String.format("command: %s\nexit code: %d\noutput: %s\nerror: %s", command, exitCode, output, error);
        }
    }

    private static Logger logger = Logger.getLogger(Command.class);

    private List<String> cmd = new ArrayList();

    protected Process process;
    private long lastStartTime;
    private volatile boolean isComplete;
    private File outputFile;
    private volatile List<String> output;
//    private static List<Command> history = new ArrayList<>();
//    private static final int HISTORY_SIZE = 20;

    public Result getResult() {
        return result;
    }

    private Result result = new Result();

    public Command() {
        isComplete = true;
        outputFile = null;
        output = null;
        lastStartTime = 0;
    }

    public Command(String[] cmd) {
        this();
        this.cmd = Arrays.asList(cmd);
    }

    public Command startWith(String command) {
        cmd.clear();
        arg(command);
        return this;
    }

    public Command arg(String arg) {
        if (arg != null && !arg.isEmpty()) {
//            for (String a : StringUtils.split(arg, " ")) {
//                cmd.add(a);
//            }
            cmd.add(arg);
        }
        return this;
    }

    public Command arg(List<String> args) {
        if (args != null && !args.isEmpty()) {
            cmd.addAll(args);
        }
        return this;
    }

    public Command outputTo(File to) throws IOException {
        Env.current().touch(to);
        outputFile = to;
        return this;
    }

    public String asString() {
        return StringUtils.join(cmd, ' ');
    }

    protected void beforeRun() {
    }

    public Command go(boolean async) throws IOException, InterruptedException {
        beforeRun();
        ProcessBuilder pb = new ProcessBuilder(cmd.toArray(new String[cmd.size()])).redirectErrorStream(true);
        Config.current().applyToProcess(pb);
        if (outputFile != null) {
            logger.debug(String.format("Redirect output to file %s", outputFile.getAbsolutePath()));
            pb.redirectOutput(ProcessBuilder.Redirect.appendTo(outputFile));
        }

        process = pb.start();
        result.command = Joiner.on(' ').join(cmd);
//        history.add(this);

        isComplete = false;
        logger.info(String.format("Command started. [CMD: %s]", StringUtils.join(cmd, " ")));
        lastStartTime = System.currentTimeMillis();
        if (async) {
            CommonThreadPool.startCallable(new Callable() {
                @Override
                public Object call() throws Exception {
                    int exit = process.waitFor();
                    afterRun();
                    return exit;
                }
            });
        } else {
            process.waitFor();
            afterRun();
        }

        return this;
    }

    protected void afterRun() throws IOException {
        result.output = readStream(process.getInputStream());
        result.error = readStream(process.getErrorStream());
        result.exitCode = process.exitValue();
        isComplete = true;
        if (outputFile != null) {
            logger.info(String.format("Command completed. (outputFile -> %s)", outputFile.getAbsolutePath()));
        }
    }

    private List<String> readStream(InputStream stream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        List<String> lines = new ArrayList();
        String line = null;
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        br.close();
        return lines;
    }

    public void kill() {
        if (process == null || isComplete()) {
            return;
        }

        process.destroy();
        try {
            while(isComplete()) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isComplete() {
        return isComplete;
    }

    public long getLastStartTime() {
        return lastStartTime;
    }

//    public List<String> getOutput() {
//        if (outputFile != null) {
//            try {
//                output = Files.readAllLines(outputFile.toPath(), Charset.defaultCharset());
//            } catch (IOException e) {
//                // never going to happen
//                e.printStackTrace();
//            }
//        }
//        return output;
//    }
}
