package com.groupon.seleniumgridextras.grid.servlets;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.grid.common.exception.GridException;
import org.openqa.grid.internal.ProxySet;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.web.servlet.RegistryBasedServlet;

public class NodeInformation extends RegistryBasedServlet {

    /**
     *
     */
    private static final long serialVersionUID = -1975392591408983229L;

    public NodeInformation() {
        this(null);
    }

    public NodeInformation(Registry registry) {
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
        response.setContentType("text/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(200);
        JSONObject res;
        try {
            res = getResponse();
            response.getWriter().print(res);
            response.getWriter().close();
        } catch (JSONException e) {
            throw new GridException(e.getMessage());
        }
    }

    private JSONObject getResponse() throws IOException, JSONException {
        JSONObject requestJSON = new JSONObject();
        ProxySet proxies = this.getRegistry().getAllProxies();
        Iterator<RemoteProxy> iterator = proxies.iterator();
        JSONArray busyProxies = new JSONArray();
        JSONArray freeProxies = new JSONArray();
        JSONArray allProxies = new JSONArray();
        while (iterator.hasNext()) {
            RemoteProxy eachProxy = iterator.next();
            allProxies.put(eachProxy.getOriginalRegistrationRequest().getAssociatedJSON());
            //if (eachProxy.isBusy()) {
            //    busyProxies.put(eachProxy.getOriginalRegistrationRequest().getAssociatedJSON());
            //} else {
            //    freeProxies.put(eachProxy.getOriginalRegistrationRequest().getAssociatedJSON());
            //}
        }
        requestJSON.put("AllProxies", allProxies);

        //requestJSON.put("BusyProxies", busyProxies);
        //requestJSON.put("FreeProxies", freeProxies);

        return requestJSON;

        /*for (TestSlot slot : proxy.getTestSlots()) {
      builder.append(slot.getCapabilities().containsKey(BROWSER) ? slot.getCapabilities().get(
          BROWSER) : slot.getCapabilities().get(APP));
      TestSession session = slot.getSession();
      builder.append(session == null ? "(free)" : "(busy, session " + session + ")");
      builder.append("<br>");
    }*/
    }

}