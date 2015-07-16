package selenium_grid_extras_jenkins.selenium_grid_extras_jenkins.utilities;

import com.google.common.base.Throwables;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtility {

    public static final int DEFAULT_HTTP_TIMEOUT = 60000;

    public static HttpURLConnection getRequest(URL url, int timeout) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(timeout);
        conn.setReadTimeout(timeout);

        return conn;
    }

    public static String getRequestAsString(URL url, int timeout) throws IOException {
        HttpURLConnection conn = getRequest(url, timeout);

        if (conn.getResponseCode() == 200) {
            return StreamUtility.inputStreamToString(conn.getInputStream());
        } else {
            return "";
        }
    }

    public static String getRequestAsString(URL url) throws IOException {
        return getRequestAsString(url, DEFAULT_HTTP_TIMEOUT);
    }

    public static HttpURLConnection getRequest(URL url) throws IOException {
        return getRequest(url, DEFAULT_HTTP_TIMEOUT);
    }

    public static int checkIfUrlStatusCode(URL u) {

        HttpURLConnection huc = null;
        try {

            huc = (HttpURLConnection) u.openConnection();
            huc.setRequestMethod("GET");
            huc.setInstanceFollowRedirects(true);
            huc.connect();
//            OutputStream os = huc.getOutputStream();
            return huc.getResponseCode();
        } catch (IOException e) {
            String message = String.format("URL: %s, \n %s", u, Throwables.getStackTraceAsString(e));
            System.out.println(message);
        }

        return -1;
    }
}
