package com.groupon.seleniumgridextras.utilities.threads;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class StreamGobbler extends Thread {
    private InputStream inputStream;
    private PrintStream outputStream;

    public StreamGobbler(InputStream inputStream, PrintStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public StreamGobbler(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = new PrintStream(outputStream);
    }

    public void run() {
        try {
            int c;
            while ((c = inputStream.read()) != -1) {
                if(outputStream != null) {
                    outputStream.print((char) c);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
