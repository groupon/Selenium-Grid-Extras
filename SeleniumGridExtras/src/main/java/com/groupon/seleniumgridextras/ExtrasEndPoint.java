package com.groupon.seleniumgridextras;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class ExtrasEndPoint {

  private String response_type;
  private String request_type;
  private Map<String, String> accepted_params;
  private String description;
  private Map<String, String> response_description;
  @SerializedName("class")
  private String klass;
  private String http_type;
  private String endpoint;
  private String button_text;
  private String css_class;
  private Boolean enabled_in_gui;

  public String getResponseType() {
    return response_type;
  }

  public void setResponseType(String response_type) {
    this.response_type = response_type;
  }

  public Map<String, String> getAcceptedParams() {
    return accepted_params;
  }

  public void setAcceptedParams(Map<String, String> accepted_params) {
    this.accepted_params = accepted_params;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Map<String, String> getResponseDescription() {
    return response_description;
  }

  public void setResponseDescription(Map<String, String> response_description) {
    this.response_description = response_description;
  }

  public String getClassname() {
    return klass;
  }

  public void setClassname(String klass) {
    this.klass = klass;
  }

  public String getHttpType() {
    return http_type;
  }

  public void setHttpType(String http_type) {
    this.http_type = http_type;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  public String getButtonText() {
    return button_text;
  }

  public void setButtonText(String button_text) {
    this.button_text = button_text;
  }

  public String getCssClass() {
    return css_class;
  }

  public void setCssClass(String css_class) {
    this.css_class = css_class;
  }

  public Boolean getEnabledInGui() {
    return enabled_in_gui;
  }

  public void setEnabledInGui(Boolean enabled_in_gui) {
    this.enabled_in_gui = enabled_in_gui;
  }

  public String getRequestType() {
    return request_type;
  }

  public void setRequestType(String request_type) {
    this.request_type = request_type;
  }

  public void registerApi() {
    Map apiDescription = new HashMap();
    apiDescription.put("endpoint", getEndpoint());
    apiDescription.put("description", getDescription());
    apiDescription.put("class", getClassname());
    apiDescription.put("accepted_params", getAcceptedParams());
    apiDescription.put("http_type", getRequestType());
    apiDescription.put("response_type", getResponseType());
    apiDescription.put("response_description", getResponseDescription());
    apiDescription.put("css_class", getCssClass());
    apiDescription.put("enabled_in_gui", getEnabledInGui());
    apiDescription.put("button_text", getButtonText());

    ApiDocumentation.registerApiEndPoint(apiDescription);
  }
}


