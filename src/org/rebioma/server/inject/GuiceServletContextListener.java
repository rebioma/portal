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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.ServletModule;

/**
 * @author eighty
 * 
 */
public class GuiceServletContextListener implements ServletContextListener {

  public void contextDestroyed(ServletContextEvent sce) {
    ServletContext servletContext = sce.getServletContext();
    servletContext.removeAttribute(Injector.class.getName());
  }

  public void contextInitialized(ServletContextEvent sce) {
    Injector injector = Guice.createInjector(new Module[] {
        new ServiceModule(), new ServletModule() });
    ServletContext servletContext = sce.getServletContext();
    servletContext.setAttribute(Injector.class.getName(), injector);
  }

}
