package com.groupon.seleniumgridextras;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.openqa.grid.internal.ProxySet;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.web.servlet.RegistryBasedServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SeleniumGridExtrasServlet extends RegistryBasedServlet {

  private Registry internalRegistry;

  public SeleniumGridExtrasServlet(Registry registry) {
    super(registry);
  }


  public SeleniumGridExtrasServlet() {
    super(null);
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
      s = new String(Files.readAllBytes(Paths.get(file)));

    } catch (Exception error) {
      System.out.println(error);
    }
    return s;
  }

//  private void getApi(RemoteProxy proxy) {
//    System.out.println(proxy.getRemoteHost());
//    System.out.println("http://" + proxy.getRemoteHost().getHost() + ":3000/api");
//    String json = getJSON("http://" + proxy.getRemoteHost().getHost() + ":3000/api", 1000);
//    JSONArray api = (JSONArray) JSONValue.parse(json);
//    for (Object entry : api){
//      System.out.println(entry.toString());
//    }
//    //JSONArray array = (JSONArray) api;
//    System.out.println(api.size() + ", " + api.toString());
//  }
//
//  public String getJSON(String url, int timeout) {
//    try {
//      URL u = new URL(url);
//      HttpURLConnection c = (HttpURLConnection) u.openConnection();
//      c.setRequestMethod("GET");
//      c.setRequestProperty("Content-length", "0");
//      c.setUseCaches(false);
//      c.setAllowUserInteraction(false);
//      c.setConnectTimeout(timeout);
//      c.setReadTimeout(timeout);
//      c.connect();
//      int status = c.getResponseCode();
//
//      switch (status) {
//        case 200:
//        case 201:
//          BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
//          StringBuilder sb = new StringBuilder();
//          String line;
//          while ((line = br.readLine()) != null) {
//            sb.append(line + "\n");
//          }
//          br.close();
//          return sb.toString();
//      }
//
//    } catch (MalformedURLException ex) {
//      System.out.println(ex);
//    } catch (IOException ex) {
//      System.out.println(ex);
//    }
//    return null;
//  }

  protected String getHtml() {

    StringBuilder html = new StringBuilder();
    html.append(readFile("www/body_partial.html"));

    ProxySet proxies = getRegistry().getAllProxies();


    html.append("<script>");
    html.append("var nodes = new Array();");

    for (RemoteProxy p : proxies) {
//      getApi(p);
      html.append("nodes.push(\"" + p.getRemoteHost().getHost() + "\");");
    }

    html.append("</script>");

    html.append(readFile("www/css_partial.html"));
    html.append(readFile("www/js_partial.html"));


    return html.toString();


  }


}
