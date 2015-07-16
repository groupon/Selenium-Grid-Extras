package selenium_grid_extras_jenkins.selenium_grid_extras_jenkins.utilities;


import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class StreamUtility {
    public static String inputStreamToString(InputStream is) throws IOException {
        return IOUtils.toString(is, "UTF-8");
    }
}
