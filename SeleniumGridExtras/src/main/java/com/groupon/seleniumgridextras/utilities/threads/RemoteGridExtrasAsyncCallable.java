package com.groupon.seleniumgridextras.utilities.threads;

import com.google.common.base.Throwables;
import com.groupon.seleniumgridextras.utilities.HttpUtility;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.Callable;

public class RemoteGridExtrasAsyncCallable implements Callable {

    private static Logger logger = Logger.getLogger(RemoteGridExtrasAsyncCallable.class);
    protected URI uri;

    public RemoteGridExtrasAsyncCallable(String host, int port, String action, Map<String, String> params) {

        logger.info(String.format("Attempting to notify %s (%s) of action: %s with params %s",
                host, port, action, params.toString()));

        try {
            URIBuilder uri = new URIBuilder();
            uri.setScheme("http");
            uri.setHost(host);
            uri.setPort(port);
            uri.setPath(action);

            for (String p : params.keySet()) {
                uri.addParameter(p, params.get(p));
            }

            this.uri = uri.build();
        } catch (Exception e) {
            logger.error(
                    String.format("Error creating new AsyncCallable instance. Host: %s, port %s, Action/Path %s, Parameters: %s",
                            host, port, action, params.toString()), e);
        }

    }

    @Override
    public String call() throws Exception {
        String response;

        try {
            response = HttpUtility.getRequestAsString(uri);
            logger.debug(String.format("Request against %s, returned \n %s", uri.toString(), response));
        } catch (Exception e) {
            response = String.format("Failed to make Asyc HTTP requests (%s) %s,\n%s",
                    uri,
                    e.getMessage(),
                    Throwables.getStackTraceAsString(e)
            );

            logger.error(response, e);
            return "";
        }
        return response;
    }
}
