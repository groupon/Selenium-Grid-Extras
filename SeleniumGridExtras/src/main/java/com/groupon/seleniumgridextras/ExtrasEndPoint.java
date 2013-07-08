package com.groupon.seleniumgridextras;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class ExtrasEndPoint {

  private String response_type;
  private String request_type;
  private JsonObject accepted_params;
  private String description;
  @SerializedName("class")
  private String klass;
  private String http_type;
  private String endpoint;
  private String button_text;
  private String css_class;
  private Boolean enabled_in_gui;
  protected JsonResponseBuilder jsonResponse = new JsonResponseBuilder();

  public String getResponseType() {
    return response_type;
  }

  public void setResponseType(String response_type) {
    this.response_type = response_type;
  }

  public JsonObject getAcceptedParams() {
    return accepted_params;
  }

  public void setAcceptedParams(JsonObject accepted_params) {
    this.accepted_params = accepted_params;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public JsonObject getResponseDescription() {
    return getJsonResponse().getKeyDescriptions();
  }

  public void addResponseDescription(String key, String description) {
    getJsonResponse().addKeyDescriptions(key, description);
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

  public JsonResponseBuilder getJsonResponse() {

    if (this.jsonResponse == null) {
      this.jsonResponse = new JsonResponseBuilder();
    }
    return jsonResponse;
  }

  public void registerApi() {

    this.getJsonResponse();

    JsonObject apiDescription = new JsonObject();
    apiDescription.addProperty("endpoint", getEndpoint());
    apiDescription.addProperty("description", getDescription());
    apiDescription.addProperty("class", getClassname());
    apiDescription.add("accepted_params", getAcceptedParams());
    apiDescription.addProperty("http_type", getRequestType());
    apiDescription.addProperty("response_type", getResponseType());
    apiDescription.add("response_description", getResponseDescription());
    apiDescription.addProperty("css_class", getCssClass());
    apiDescription.addProperty("enabled_in_gui", getEnabledInGui());
    apiDescription.addProperty("button_text", getButtonText());

    ApiDocumentation.registerApiEndPoint(apiDescription);
  }
}


