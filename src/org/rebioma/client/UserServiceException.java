package org.rebioma.client;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserServiceException extends Exception implements IsSerializable {
  public UserServiceException() {
    this(null);
  }

  public UserServiceException(String msg) {
    super(msg);
  }
}
