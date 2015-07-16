package selenium_grid_extras_jenkins.selenium_grid_extras_jenkins.utilities;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JsonParserWrapperTest {

    private Map expectedParsedHash;

    public static final String SAMPLE_JSON = "{\n" +
            "  \"a\": \"b\",\n" +
            "  \"z\": \"a\"\n" +
            "}";

    @Before
    public void setUp() throws Exception {
        expectedParsedHash = new HashMap();
    }

//    @Test
//    public void testToList() throws Exception {
//
//    }
//
//    @Test
//    public void testToJsonObject() throws Exception {
//
//    }
//
//    @Test
//    public void testToHashMap() throws Exception {
//        Map actual = JsonParserWrapper.toHashMap(SAMPLE_JSON);
//
//        System.out.println(actual.values().toArray()[0].getClass().getCanonicalName());
//
//        assertEquals(expectedParsedHash, actual);
//    }


    @Test
    public void testPrettyPrintString() throws Exception {

        Map map = new HashMap();

        map.put("a", "b");
        map.put("z", "a");

        assertEquals(SAMPLE_JSON, JsonParserWrapper.prettyPrintString(map));
    }
}
