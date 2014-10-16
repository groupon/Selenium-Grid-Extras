package com.groupon.seleniumgridextras.grid.servlets;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.openqa.grid.internal.ProxySet;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.internal.TestSlot;
import org.openqa.grid.web.servlet.RegistryBasedServlet;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Deprecated
public class ProxyStatusJsonServlet extends RegistryBasedServlet {

    private static final long serialVersionUID = -1975392591408983229L;

    public ProxyStatusJsonServlet() {
        this(null);
    }

    public ProxyStatusJsonServlet(Registry registry) {
        super(registry);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        process(req, resp);

    }

    protected void process(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(200);
        response.getWriter().print(new GsonBuilder().setPrettyPrinting().create()
                .toJson(getResponse()));
        response.getWriter().close();


    }

    private Map getResponse() {
        Map error = new HashMap();

        error.put("message", "This servlet is currently disabled and will be deprecated soon.");

        return error;

//        ProxySet proxies = this.getRegistry().getAllProxies();
//        Iterator<RemoteProxy> iterator = proxies.iterator();
//
//        while (iterator.hasNext()) {
//            RemoteProxy currentProxy = iterator.next();
//            Iterator<TestSlot> proxyIterator = currentProxy.getTestSlots().iterator();
//            while (proxyIterator.hasNext()) {
//                Map testMachine = new HashMap();
//                TestSlot currentTestSlot = proxyIterator.next();
//                testMachine.put("browserName", currentTestSlot.getCapabilities().get("browserName").toString());
//
//                String version = "";
//                if (currentTestSlot.getCapabilities().containsKey("version")) {
//                    version = currentTestSlot.getCapabilities().get("version").toString();
//                }
//
//                testMachine.put("version", version);
//
//                testMachine.put("session", "");
//
//                if (currentTestSlot.getSession() != null) {
//                    testMachine.put("session", currentTestSlot.getSession().getExternalKey().getKey());
//                }
//
//
//                testMachine.put("host", currentProxy.getRemoteHost().getHost());
//
//
//            }
//
//            Proxies.put(currentProxy.getOriginalRegistrationRequest().getAssociatedJSON());
//        }
//        requestJSON.put("Proxies", ProxyStatus);
//        requestJSON.put("TotalProxies", Proxies);
//
//        return requestJSON;
    }

}

