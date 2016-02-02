package com.groupon.seleniumgridextras.utilities.json;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by xhu on 6/06/2014.
 */

//TODO Remove

public class JsonFileReader {

    public static String getContent(String filePath) throws IOException {
        FileInputStream is = new FileInputStream(filePath);
        try {
            return IOUtils.toString(is);
        } finally {
            is.close();
        }
    }

    public static JsonObject getJsonObject(File file) throws IOException {
        return new JsonParser().parse(getContent(file.getAbsolutePath())).getAsJsonObject();
    }
}
