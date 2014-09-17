package com.groupon.seleniumgridextras.utilities;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class HttpUtility {

  private static Logger logger = Logger.getLogger(HttpUtility.class);
  protected static ExecutorService cachedPool;


  public static String getRequestAsString(URI uri) throws IOException, URISyntaxException {
    return getRequestAsString(uri.toURL());
  }

  public static String getRequestAsString(URL url) throws IOException {

    HttpURLConnection conn = getRequest(url);

    if (conn.getResponseCode() == 200) {
      return StreamUtility.inputStreamToString(conn.getInputStream());
    } else {
      return "";
    }

  }

  public static HttpURLConnection getRequest(URI uri) throws IOException {
    return getRequest(uri.toURL());
  }

  public static HttpURLConnection getRequest(URL url) throws IOException {
    logger.debug("Making GET request to " + url);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    logger.debug("Response code is " + conn.getResponseCode());
    return conn;
  }

  public static Future<String> makeAsyncGetRequest(URI uri) {
    if (cachedPool == null) {
      initializeThreadPool();
    }

    AsyncHttpRequestCallable callable = new AsyncHttpRequestCallable(uri);
    return cachedPool.submit(callable);
  }

  protected static void initializeThreadPool() {
    logger.info("Initializing a new thread for Async Get Requests");
    cachedPool = Executors.newCachedThreadPool();
  }

}
