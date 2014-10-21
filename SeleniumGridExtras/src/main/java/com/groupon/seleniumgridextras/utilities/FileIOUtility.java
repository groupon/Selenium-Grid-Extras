package com.groupon.seleniumgridextras.utilities;

import com.groupon.seleniumgridextras.utilities.json.JsonParserWrapper;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FileIOUtility {

    private static Logger logger = Logger.getLogger(FileIOUtility.class);

    public static String getAsString(String file) throws FileNotFoundException {
        return getAsString(new File(file));
    }


    public static String getAsString(File file) throws FileNotFoundException {

        String readString = "";
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;

        try {
            while ((line = reader.readLine()) != null) {
                readString = readString + line;
            }
        } catch (IOException error) {
            logger.error("IOExcetion reading " + file.getAbsolutePath());
            logger.error(error);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                logger.error("Error closing the file reader");
                logger.equals(e);
                e.printStackTrace();
            }
        }

        logger.debug("Read from" + file.getAbsolutePath() + " following content\n" + readString);

        return readString;
    }

    public static void writeToFile(File filename, String content, boolean append) throws IOException {
        logger.debug("Writing to " + filename.getAbsolutePath() + " following content\n" + content);
        FileUtils.writeStringToFile(filename, content, append);
    }

    public static void writeToFile(File filename, String content) throws IOException {
        FileIOUtility.writeToFile(filename, content, false);
    }

    public static void writePrettyJsonToFile(File filename, String content) throws IOException {
        Map parsedJson = JsonParserWrapper.toHashMap(content);
        DoubleToIntConverter.convertAllDoublesToInt(parsedJson);
        writePrettyJsonToFile(filename, parsedJson);
    }

    public static void writePrettyJsonToFile(File filename, Map content) throws IOException {
        writeToFile(filename, JsonParserWrapper.prettyPrintString(content));
    }

    public static void writePrettyJsonToFile(File filename, List content) throws IOException {
        writeToFile(filename, JsonParserWrapper.prettyPrintString(content));
    }

    public static void writePrettyJsonToFile(File filename, List content, boolean append) throws IOException {
        writeToFile(filename, JsonParserWrapper.prettyPrintString(content), append);
    }

}
