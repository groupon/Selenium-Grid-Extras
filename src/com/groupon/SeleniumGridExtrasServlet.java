package com.groupon;

import org.openqa.grid.internal.ProxySet;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.web.servlet.RegistryBasedServlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

  protected String getHtml() {

    StringBuilder html = new StringBuilder();
    html.append(readFile("www/body_partial.html"));

    ProxySet proxies = getRegistry().getAllProxies();


    html.append("<script>");
    html.append("var nodes = new Array();");

    for (RemoteProxy p : proxies) {
      html.append("nodes.push(\"" + p.getRemoteHost().getHost() + "\");");
    }

    html.append("</script>");

    html.append(readFile("www/css_partial.html"));
    html.append(readFile("www/js_partial.html"));


    return html.toString();


  }


}
