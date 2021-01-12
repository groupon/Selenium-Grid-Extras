package com.groupon.seleniumgridextras.grid.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.ProxySet;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.web.servlet.RegistryBasedServlet;

import com.google.gson.GsonBuilder;

public class ListNodesServlet extends RegistryBasedServlet {

    private static final long serialVersionUID = 1L;

    public ListNodesServlet() {
        this(null);
    }

    public ListNodesServlet(Registry registry) {
        super(registry);
    }

     @Override
     protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         doPost(req, resp);
     }

     @Override
     protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         process(req, resp);
     }

     protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
         response.setContentType("application/json");
         response.setCharacterEncoding("UTF-8");
         response.setStatus(200);

         response.getWriter().print(new GsonBuilder().setPrettyPrinting().create().toJson(getResponse()));
         response.getWriter().close();
     }

     private Map<String, List<String>> getResponse() throws IOException {
         Map<String, List<String>> res = new HashMap<>();
         ArrayList<String> nodeList = new ArrayList<>();

         ProxySet proxies = this.getRegistry().getAllProxies();
         Iterator<RemoteProxy> iterator = proxies.iterator();
         while (iterator.hasNext()) {
             RemoteProxy eachProxy = iterator.next();
             nodeList.add(String.format("%s:%s", eachProxy.getConfig().host, eachProxy.getConfig().custom.get("grid_extras_port")));
         }

         res.put("nodeList", nodeList);
         return res;
     }
}
