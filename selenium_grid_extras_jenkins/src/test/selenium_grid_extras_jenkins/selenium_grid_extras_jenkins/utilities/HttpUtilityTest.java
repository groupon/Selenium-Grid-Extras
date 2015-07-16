package selenium_grid_extras_jenkins.selenium_grid_extras_jenkins.utilities;


import org.junit.Test;

import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.URL;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;

public class HttpUtilityTest {
    @Test(expected = ConnectException.class)
    public void testConnectionRefusedError() throws Exception {
        ServerSocket serverSocket = new ServerSocket(0);
        int port = serverSocket.getLocalPort();
        serverSocket
                .close(); //Find a garanteed open port by taking one and closing. Why doesn't Java allow me to get a list of open ports?
        HttpUtility.getRequest(new URL("http://localhost:" + port)).getResponseCode();
    }

    @Test
    public void test404Page() throws Exception {
        assertEquals(404, HttpUtility.getRequest(new URL("http://xkcd.com/404")).getResponseCode());
    }

    @Test
    public void test200Page() throws Exception {
        assertEquals(200, HttpUtility.getRequest(new URL("http://google.com")).getResponseCode());
    }

    @Test(expected = UnknownHostException.class)
    public void testUnknownHost() throws Exception {
        HttpUtility.getRequest(new URL("http://googasdfasfdkjashfdkjahsfdle.com/")).getResponseCode();
    }

    @Test
    public void testGetAsString() throws Exception {
        assertEquals("", HttpUtility.getRequestAsString(new URL("http://xkcd.com/404")));
    }

    @Test
    public void testCheckIfUrlStatusCode() throws Exception{
        assertEquals(200, HttpUtility.checkIfUrlStatusCode(new URL("http://google.com")));
        assertEquals(404, HttpUtility.checkIfUrlStatusCode(new URL("http://xkcd.com/404")));
        assertEquals(301, HttpUtility.checkIfUrlStatusCode(new URL("http://github.com/groupon/Selenium-Grid-Extras/releases/download/v1.5.0/SeleniumGridExtras-1.5.0-SNAPSHOT-jar-with-dependencies.jar")));
        assertEquals(200, HttpUtility.checkIfUrlStatusCode(new URL("https://github.com/groupon/Selenium-Grid-Extras/releases/download/v1.5.0/SeleniumGridExtras-1.5.0-SNAPSHOT-jar-with-dependencies.jar")));
    }

}
