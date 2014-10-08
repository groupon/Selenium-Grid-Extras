package com.groupon.seleniumgridextras.utilities.json;

import com.groupon.seleniumgridextras.tasks.ExposeDirectory;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JsonParserWrapperTest {


    private static final String JSON_BUILDER_PRETTY_STRING =
            "{\n" +
            "  \"exit_code\": 0,\n" +
            "  \"out\": [],\n" +
            "  \"error\": [],\n" +
            "  \"foo\": [\n" +
            "    \"hello\"\n" +
            "  ]\n" +
            "}";

    private JsonResponseBuilder responseBuilder;
    private Map expectedParsedHash;

    @Before
    public void setUp() throws Exception {
        responseBuilder = new JsonResponseBuilder();
        responseBuilder.addKeyDescriptions("foo", "bar");
        responseBuilder.addKeyValues("foo", "hello");

        expectedParsedHash = new HashMap();
        expectedParsedHash.put("exit_code", 0.0);
        expectedParsedHash.put("error", new LinkedList());
        expectedParsedHash.put("out", new LinkedList());

        List foo = new LinkedList();
        foo.add("hello");
        expectedParsedHash.put("foo", foo);
    }

    @Test
    public void testToHashMapFromString() throws Exception {
        assertEquals(expectedParsedHash, JsonParserWrapper.toHashMap(JSON_BUILDER_PRETTY_STRING));
    }

    @Test
    public void testToHashMapFromJsonObject() throws Exception {
        assertEquals(expectedParsedHash, JsonParserWrapper.toHashMap(responseBuilder.getJson()));
    }

    @Test
    public void testPrettyPrintStringNormalObject() throws Exception {
        String expected =
                "{\n" +
                "  \"a\": \"b\",\n" +
                "  \"z\": \"a\"\n" +
                "}";

        Map map = new HashMap();

        map.put("a", "b");
        map.put("z", "a");

        assertEquals(expected, JsonParserWrapper.prettyPrintString(map));
    }

    @Test
    public void testPrettyPrintStringJsonObject() throws Exception {
        assertEquals(JSON_BUILDER_PRETTY_STRING, JsonParserWrapper.prettyPrintString(responseBuilder.getJson()));
    }
}
