package com.groupon.seleniumgridextras.grid.proxies;


import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.common.exception.RemoteUnregisterException;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.internal.listeners.SelfHealingProxy;
import org.openqa.grid.internal.utils.HtmlRenderer;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;

import java.util.Date;
import java.util.Map;

public class AutoProxy extends DefaultRemoteProxy implements SelfHealingProxy {

  private boolean restarting;
  private boolean available;
  private int numberOfSessionsServed;
  private Date startTime;
  private HtmlRenderer renderer = new ExtrasHtmlRenderer(this);

  public AutoProxy(RegistrationRequest request, Registry registry) {
    super(request, registry);
    NodeManager nodeManager = new NodeManager(this);
    new Thread(nodeManager).start();
    numberOfSessionsServed = 0;
    startTime = new Date();
  }

  @Override
  public HtmlRenderer getHtmlRender() {
    return renderer;
  }

  @Override
  public TestSession getNewSession(Map<String, Object> requestedCapability) {
    synchronized (this) {
      if (isRestarting() || !isAvailable()) {
        return null;
      }
      TestSession session = super.getNewSession(requestedCapability);
      if(session != null){
        numberOfSessionsServed++;
      }
      return session;
    }
  }

  public boolean isRestarting() {
    return restarting;
  }

  public void setRestarting(boolean restarting) {
    this.restarting = restarting;
  }

  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }

  public void unregister() {
    addNewEvent(new RemoteUnregisterException("Unregistering the node."));
  }
}
