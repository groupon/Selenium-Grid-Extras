package com.groupon.seleniumgridextras.utilities.threads;

import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.Callable;

public class ExecuteOsTaskCallable implements Callable {
    protected String command;
    protected Process process;
    private static Logger logger = Logger.getLogger(ExecuteOsTaskCallable.class);


    public ExecuteOsTaskCallable(String commandToWatch, Process processToWatch) {
        this.command = commandToWatch;
        this.process = processToWatch;
    }

    @Override
    public String call() throws Exception {
        String message;
        try {
            logger.info("Starting to wait for command to finish in background: " + this.command);

            // Starts a thread to read error stream
            ByteArrayOutputStream errorOutputStream = new ByteArrayOutputStream();
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), errorOutputStream);
            errorGobbler.start();

            // Starts a thread to read output stream and discards it (the output should be in the command log)
            StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), null);
            outputGobbler.start();

            int exitCode = process.waitFor();

            // Waits for threads to die
            errorGobbler.join();
            outputGobbler.join();

            message = String.format("Command finished. \n command: %s \n exit code: %d \n standard error: \n %s",
                    this.command,
                    exitCode,
                    errorOutputStream.toString()
            );

            logger.info(message);
            return message;
        } catch (Exception e) {
            message = String.format("An error occurred while waiting for %s command to finish in background", command);
            logger.error(message, e);
            return message;
        }
    }
}
