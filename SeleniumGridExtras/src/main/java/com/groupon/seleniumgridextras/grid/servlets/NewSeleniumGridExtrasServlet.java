package com.groupon.seleniumgridextras.grid.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.groupon.seleniumgridextras.ExtrasEndPoint;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpRequest;
import org.openqa.grid.internal.ProxySet;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.internal.TestSlot;
import org.openqa.grid.web.servlet.RegistryBasedServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class NewSeleniumGridExtrasServlet extends RegistryBasedServlet {

  private static final long serialVersionUID = 8484071790930378855L;
  private static final Logger log = Logger.getLogger(NewSeleniumGridExtrasServlet.class.getName());
  private static String coreVersion;
  private static String coreRevision;
  private Registry internalRegistry;

  public NewSeleniumGridExtrasServlet(Registry registry) {
    super(registry);
    getVersion();
  }


  public NewSeleniumGridExtrasServlet() {
    super(null);
  }

  private static String extractMessage(HttpResponse resp) throws IOException {
    BufferedReader rd = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
    StringBuilder s = new StringBuilder();
    String line;
    while ((line = rd.readLine()) != null) {
      s.append(line);
    }
    rd.close();
    return s.toString();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    response.setStatus(200);

    response.getWriter().write(getHtml());
    response.getWriter().close();

  }

  private String readFile(String file) {
    String s = "";

    try {
      InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
      s = IOUtils.toString(is, "UTF-8");
    } catch (Exception error) {
      System.out.println("Problem reading: " + file);
      error.printStackTrace();
    }
    return s;
  }

  private List<ExtrasEndPoint> getAvailableEndpoints(RemoteProxy proxy) throws IOException {
    URL apiURL = new URL("http://" + proxy.getRemoteHost().getHost() + ":3000/api");
    String json = getJSON(apiURL);
    if (json == "")
      return new ArrayList<ExtrasEndPoint>();
    Gson gson = new Gson();
    Type listType = new TypeToken<List<ExtrasEndPoint>>() {
    }.getType();
    List<ExtrasEndPoint> endpoints = gson.fromJson(json, listType);
    return endpoints;
  }

  private String getJSON(URL url) throws IOException {
    try {
      HttpClient client = new DefaultHttpClient();

      BasicHttpRequest r = new BasicHttpRequest("GET", url.toString());

      HttpResponse response = client.execute(new HttpHost(url.getHost(), url.getPort()), r);
      return extractMessage(response);
    } catch (Exception e) {
      System.out.println("Problem loading from : " + url.toString() + ", error:" + e.toString());
      return "";
    }
  }

  protected String getHtml() throws IOException {

    StringBuilder html = new StringBuilder();
    String spinnerBase64 = getSpinnerBase64String();

    html.append(readFile("www/body_partial.html"));

    ProxySet proxies = getRegistry().getAllProxies();


    html.append("<script>");
    html.append("var spinnerBase64 ='" + spinnerBase64 + "';");
    html.append("var nodes = new Array();");

    JsonArray nodes = new JsonArray();
    for (RemoteProxy p : proxies) {
      JsonObject node = new JsonObject();
      node.addProperty("host", p.getRemoteHost().getHost());
      String platform = p.getOriginalRegistrationRequest().getCapabilities().get(0).getPlatform() != null ?
          p.getOriginalRegistrationRequest().getCapabilities().get(0).getPlatform().toString() : "";
      node.addProperty("platform", platform);
      node.addProperty("status", getTestSlots(p));


      List<ExtrasEndPoint> availableEndpoints = getAvailableEndpoints(p);
      JsonArray endpoints = new JsonArray();
      for (ExtrasEndPoint e : availableEndpoints) {
        if (!e.getEnabledInGui())
          continue;
        JsonObject endpoint = new JsonObject();
        endpoint.addProperty("endpoint", e.getEndpoint());
        endpoint.addProperty("css", e.getCssClass());
        endpoint.addProperty("button_text", e.getButtonText());
        endpoint.addProperty("endpoint", e.getEndpoint());
        endpoint.addProperty("description", e.getDescription());
        endpoints.add(endpoint);
      }
      if (!endpoints.isJsonNull()) {
        node.add("endpoints", endpoints);
        nodes.add(node);
        html.append("nodes.push(\"" + p.getRemoteHost().getHost() + "\");");
      }
    }
    //System.out.println(nodes.toString());
    html.append("var nodesJson = '" + nodes.toString() + "';");
    html.append("</script>");
    html.append(readFile("www/css_partial.html"));
    html.append(readFile("www/js_partial.html"));

    return html.toString();
  }

  private String getTestSlots(RemoteProxy p) {
    int busy = 0;
    int free = 0;
    for (TestSlot slot : p.getTestSlots()) {
      if (slot.getSession() == null) {
        free++;
      } else {
        busy++;
      }
    }
    return "free: " + free + ", busy: " + busy;
  }

  private String getSpinnerBase64String() {
    return "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";
  }

  private void getVersion() {
    final Properties p = new Properties();

    InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("VERSION.txt");
    if (stream == null) {
      log.severe("Couldn't determine version number");
      return;
    }
    try {
      p.load(stream);
    } catch (IOException e) {
      log.severe("Cannot load version from VERSION.txt" + e.getMessage());
    }
    coreVersion = p.getProperty("selenium.core.version");
    coreRevision = p.getProperty("selenium.core.revision");
    if (coreVersion == null) {
      log.severe("Cannot load selenium.core.version from VERSION.txt");
    }
  }

}
