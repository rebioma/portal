/*
 * Copyright 2008 University of California at Berkeley.
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
package org.rebioma.server.util;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
  private static SessionFactory sessionFactory;

  private static final Logger log = Logger.getLogger(HibernateUtil.class);

  private static Transaction currentTransaction = null;

  private static Session currentSession = null;
  
  private static Configuration configuration = null;
  static {
    init();
  }

  public static boolean beginTransaction(Session session) {
    boolean first = false;
    // System.out.println("session is open ? " + session.isOpen());
    boolean wasNotActive = !session.getTransaction().isActive();
    if (log.isDebugEnabled())
      log.debug("session " + session.hashCode()
          + ": beginn Transaction. New transaction was opened is " + wasNotActive);
    // if (wasNotActive) {
    currentTransaction = session.beginTransaction();
    // }
    if (!currentTransaction.isActive()) {
      currentTransaction.begin();
    }
    first = wasNotActive;
    return first;
  }

  public static void commitCurrentTransaction() {
    Session s = getCurrentSession();
    if (log.isDebugEnabled())
      log.debug("session " + s.hashCode() + ": committing Transaction.");
    try {
      s.getTransaction().commit();
    } catch (Exception ex) {
      log.error("session " + s.hashCode() + ": commit failed.", ex);
      rollbackTransaction();
      throw new RuntimeException(ex);
    } finally {
      if (s != null && s.isOpen()) {
        s.close();
      }
      currentSession = null;
      if (log.isDebugEnabled()) {
        if (s != null)
          log.debug("session " + s.hashCode() + ": closed.");
        else
          log.debug("session closed.");
      }
    }
  }

  // public static void clear() {
  // Session session = getCurrentSession();
  // session.beginTransaction();
  // session.clear();
  // commitCurrentTransaction();
  // }

  public static Session getCurrentSession() {
    // if (currentSession == null || !currentSession.isOpen()) {
    currentSession = sessionFactory.getCurrentSession();
    // }
    // }
    // Session s = sessionFactory.getCurrentSession();
    if (log.isDebugEnabled())
      log.debug("session " + currentSession.hashCode() + ": getCurrentSession");
    return currentSession;
  }

  public static void main(String args[]) {

  }

  public static void rollbackTransaction() {
    Session s = getCurrentSession();
    if (log.isDebugEnabled())
      log.debug("session " + s.hashCode() + ": rolling back Transaction.");
    try {
      if (s.getTransaction().isActive())
        s.getTransaction().rollback();
    } catch (Exception ex) {
      log.error("unable to rollback trying to re-initialize hibernate session factory");
      init();
      return;
    } finally {
      if (s != null && s.isOpen()) {
        s.close();
      }
      currentSession = null;
      if (log.isDebugEnabled()) {
        if (s != null)
          log.debug("session " + s.hashCode() + ": closed.");
        else
          log.debug("session closed.");
      }
    }

  }

  public static SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  private synchronized static void init() {
    try {
      configuration = new Configuration().configure();	
      // Create the SessionFactory from hibernate.cfg.xml
      sessionFactory = configuration.buildSessionFactory();
      currentSession = null;
      currentTransaction = null;
    } catch (Throwable ex) {
      // Make sure you log the exception, as it might be swallowed
      log.error("Initial SessionFactory creation failed", ex);
      throw new ExceptionInInitializerError(ex);
    }
  }

  public static Configuration getConfiguration() {
	  return configuration;
  }
}
