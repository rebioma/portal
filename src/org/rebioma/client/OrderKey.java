package org.rebioma.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class OrderKey implements IsSerializable {

  private String attributeName;
  private boolean isAsc;

  public OrderKey() {

  }

  public OrderKey(String attributeName, boolean isAsc) {
    this.attributeName = attributeName;
    this.isAsc = isAsc;
  }

  public String getAttributeName() {
    return attributeName;
  }

  public boolean isAsc() {
    return isAsc;
  }

  public void setAsc(boolean isAsc) {
    this.isAsc = isAsc;
  }

  public void setAttributeName(String attributeName) {
    this.attributeName = attributeName;
  }

  public String toString() {
    return attributeName + (isAsc ? " asc" : "desc");
  }

}
