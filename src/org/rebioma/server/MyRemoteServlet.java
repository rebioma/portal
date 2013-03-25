package org.rebioma.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class MyRemoteServlet extends RemoteServiceServlet {
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    MySqlPing.startPingTimer();
  }
}
