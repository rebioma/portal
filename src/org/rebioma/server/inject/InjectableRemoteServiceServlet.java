/*
 * Copyright 2008 University of California at Berkeley
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.rebioma.server.inject;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Injector;

/**
 * @author eighty
 * 
 */
public abstract class InjectableRemoteServiceServlet extends
    RemoteServiceServlet {

  private static final Logger log = Logger
      .getLogger(InjectableRemoteServiceServlet.class);

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    ServletContext context = config.getServletContext();
    Injector injector;
    injector = (Injector) context.getAttribute(Injector.class.getName());
    if (injector == null) {
      log.info("Guice injector not in servlet context!");
      throw new ServletException("Guice Injector not in ServletContext");
    }
    injector.injectMembers(this);
    // MySqlPing.startPingTimer();
  }

  @Override
  protected void doUnexpectedFailure(Throwable e) {
    log.info("InjectableRemoteSerivceServlet failed: " + e.getMessage());
  }
}
