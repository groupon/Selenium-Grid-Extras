package com.groupon.seleniumgridextras.utilities.threads;

import com.groupon.seleniumgridextras.utilities.ProcessOutputReader;
import org.apache.log4j.Logger;

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

            int exitCode = process.waitFor();

            message = String.format("Command finished. \n command: %s \n exit code: %s \n standard out: \n %s \n %standard error: \n %s",
                    this.command,
                    exitCode,
                    ProcessOutputReader.getStandardOut(process),
                    ProcessOutputReader.getErrorOut(process)

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
