package org.rebioma.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserExistedException extends Exception implements IsSerializable {

  public UserExistedException() {
    this(null);
  }

  public UserExistedException(String msg) {
    super(msg);
  }
}