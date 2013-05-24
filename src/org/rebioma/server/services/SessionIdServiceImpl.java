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
package org.rebioma.server.services;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.rebioma.client.bean.User;
import org.rebioma.server.util.ManagedSession;

/**
 * The default service implementation of {@link SessionIdService}.
 * 
 */
// @Singleton
public class SessionIdServiceImpl implements SessionIdService {

  /**
   * The {@link Logger} used for logging events.
   */
  Logger log = Logger.getLogger(SessionIdServiceImpl.class);

  /**
   * Checks the user table for a matching session id. If a user is found,
   * returns the user id.
   * 
   * @see org.rebioma.server.services.SessionIdService#getUserBySessionId(java.lang.String)
   */
  public User getUserBySessionId(String sid) {
    Session session = ManagedSession.createNewSessionAndTransaction();
    try {
      System.out.println("session before calling getUserBySessionId is "
          + session.isOpen());
      User user = (User) session.createCriteria(User.class).add(
          Restrictions.eq("sessionId", sid)).uniqueResult();
      ManagedSession.commitTransaction(session);
      // user.setRoles(new HashSet<Role>(roleD));
      return user;
    } catch (HibernateException e) {
      ManagedSession.rollbackTransaction(session);
      log.error("Error: " + e.getMessage(), e);
      return null;
    } finally {
      // session.close();
    }
  }

  /**
   * Checks the user table for a matching session id and returns true if there
   * is a match.
   * 
   * @see org.rebioma.server.services.SessionIdService#isSessionIdValid(java.lang
   *      .String)
   */
  public boolean isSessionIdValid(String sid) {
    User user = getUserBySessionId(sid);
    return user != null;
  }

}
