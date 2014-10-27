package com.groupon.seleniumgridextras.utilities.threads;

import com.groupon.seleniumgridextras.utilities.HttpUtility;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.Callable;

public class RemoteGridExtrasAsyncCallable implements Callable {

    private static Logger logger = Logger.getLogger(RemoteGridExtrasAsyncCallable.class);
    protected URIBuilder uriBuilder;
    protected Map<String, String> parameters;

    public RemoteGridExtrasAsyncCallable(String host, int port, String action, Map<String, String> params) {

        URIBuilder uri = new URIBuilder();
        uri.setHost(host);
        uri.setPort(port);
        uri.setPath(action);

        for (String p : params.keySet()) {
            uri.addParameter(p, params.get(p));
        }

        parameters = params; //Saved for later use in case there is an exception in the call.


    }

    @Override
    public Object call() throws Exception {
        logger.info(String.format(""));
        try {
            URI finalUri = uriBuilder.build();
            String response = HttpUtility.getRequestAsString(finalUri);
            logger.info(String.format("Request against %s, returned \n %", finalUri.toString(), response));
        } catch (Exception e) {

            logger.error(
                    String.format("Failed to make Asyc HTTP requests (%s), \n host: %s, action: %s, parameters: \n%s",
                            e.getMessage(),
                            uriBuilder.getHost(),
                            uriBuilder.getPath(),
                            parameters.toString()
                    ), e);
        }


        return null; //everything goes to log, on one is paying attention to these
    }
}
