package com.groupon.seleniumgridextras.utilities;


import org.apache.log4j.Logger;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.remote.http.HttpMethod;
import org.openqa.selenium.remote.http.HttpRequest;
import org.openqa.selenium.remote.http.JsonHttpCommandCodec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;

public class JsonWireCommandTranslator {
  private static Logger logger = Logger.getLogger(JsonWireCommandTranslator.class);
  private String body;
  private String method;
  private String url;

  public JsonWireCommandTranslator(String method, String url, String body) {
    this.method = method;
    this.body = body;
    this.url = url.replaceAll("/wd/hub", "");
  }

  public String toString() {
    return getCommandName() + ": " + getBody();
  }

  public String getCommandName(){
    JsonHttpCommandCodec codec = new JsonHttpCommandCodec();
    Command translatedCommand;
    try{
      translatedCommand = codec.decode(getRequest());
    } catch (UnsupportedCommandException e){
      translatedCommand = new Command(new SessionId(""), this.url);
    }

    return translatedCommand.getName();
  }

  public HttpRequest getRequest(){
    return new HttpRequest(getMethod(), url);
  }

  public String getBody(){
    return this.body;
  }

  public HttpMethod getMethod() {
    if (this.method.equals("POST")) {
      return HttpMethod.POST;
    } else if (this.method.equals("GET")) {
      return HttpMethod.GET;
    } else {
      return HttpMethod.DELETE;
    }
  }

  public static String getBodyAsString(HttpServletRequest request){
    StringBuilder stringBuilder = new StringBuilder();
    BufferedReader bufferedReader = null;

    try {
      InputStream inputStream = request.getInputStream();
      if (inputStream != null) {
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        char[] charBuffer = new char[128];
        int bytesRead = -1;
        while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
          stringBuilder.append(charBuffer, 0, bytesRead);
        }
      } else {
        stringBuilder.append("");
      }
    } catch (IOException ex) {
      stringBuilder.append("Error: Could not parse body anymore");
      logger.warn("Error parsing body of request");
      logger.warn(stringBuilder.toString());
      logger.warn(ex);
    } finally {
      if (bufferedReader != null) {
        try {
          bufferedReader.close();
        } catch (IOException ex) {
          stringBuilder.append("Error closing body reader");
          logger.warn("Error closing body reader");
          logger.warn(ex);
        }
      }
    }

    return stringBuilder.toString();

  }

}
