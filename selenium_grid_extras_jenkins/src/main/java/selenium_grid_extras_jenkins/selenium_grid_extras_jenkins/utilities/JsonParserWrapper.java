package selenium_grid_extras_jenkins.selenium_grid_extras_jenkins.utilities;

import com.google.gson.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JsonParserWrapper {
    public static List toList(String input) throws JsonSyntaxException {
        return new Gson().fromJson(input, LinkedList.class);
    }


    public static JsonObject toJsonObject(Map input) {
        return new Gson().toJsonTree(input).getAsJsonObject();
    }

    public static Map toHashMap(String input) {
        return new Gson().fromJson(input, HashMap.class);
    }

    public static Map toHashMap(JsonObject input) {
        return new Gson().fromJson(input, HashMap.class);
    }

    public static String prettyPrintString(Object input) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(input);
    }

    public static String prettyPrintString(JsonObject input) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(input);
    }

}
