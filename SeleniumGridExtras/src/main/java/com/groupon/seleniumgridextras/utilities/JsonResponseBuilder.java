package com.groupon.seleniumgridextras.utilities;

import com.google.gson.JsonObject;

/**
 * Created by xhu on 11/04/2014.
 */
public class JsonResponseBuilder {
    public enum ResponseCode {
        SUCCESS,
        ERROR
    }

    private ResponseCode responseCode;
    private String message = null;
    private JsonObject content = null;

    private JsonResponseBuilder(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public static JsonResponseBuilder newResponse(ResponseCode responseCode) {
        return new JsonResponseBuilder(responseCode);
    }

    public JsonResponseBuilder withError(String message) {
        responseCode = ResponseCode.ERROR;
        withMessage(message);
        return this;
    }

    public JsonResponseBuilder withMessage(String message) {
        this.message = message;
        return this;
    }

    public JsonResponseBuilder withProperty(String key, String value) {
        if (content == null) {
            content = new JsonObject();
        }
        content.addProperty(key, value);
        return this;
    }

    public JsonResponseBuilder withProperty(String key, int value) {
        withProperty(key, Integer.toString(value));
        return this;
    }

    public JsonObject build() {
        JsonObject jo = new JsonObject();
        jo.addProperty("result", responseCode.toString());
        if (message != null) {
            jo.addProperty("message", message);
        }
        if (content != null) {
            jo.add("content", content);
        }
        return jo;
    }
}
