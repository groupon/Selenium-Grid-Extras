package com.groupon.seleniumgridextras.utilities;

import org.apache.log4j.Logger;

import java.net.URI;
import java.util.concurrent.Callable;

public class AsyncHttpRequestCallable implements Callable {

  private static Logger logger = Logger.getLogger(AsyncHttpRequestCallable.class);
  private URI targetURI;

  public AsyncHttpRequestCallable(URI uri){
    this.targetURI = uri;
  }

  @Override
  public Object call() throws Exception {
    logger.info("Making Async Requests against " + this.targetURI.toString());
    String response = HttpUtility.getRequestAsString(this.targetURI);
    logger.debug("Response from" + response);
    return response;
  }
}
