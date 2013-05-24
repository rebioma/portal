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

import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.rebioma.server.util.ManagedSession;

/**
 * Default implementation of {@link UpdateService}.
 * 
 */
public class UpdateServiceImpl implements UpdateService {

  /**
   * The {@link Logger} for this class.
   */
  private static final Logger log = Logger.getLogger(UpdateServiceImpl.class);

  public UpdateServiceImpl() {
  }

  public Date getLastUpdate() {
    Session session = ManagedSession.createNewSessionAndTransaction();
    try {
      Criteria updateCriteria = session.createCriteria(OccurrenceUpdates.class);
      OccurrenceUpdates instance = (OccurrenceUpdates) updateCriteria
          .uniqueResult();
      Date currentUpdate = instance.getLastupdate();
      // this is needed to clear out the current Transaction
      ManagedSession.commitTransaction(session);
      return currentUpdate;
    } catch (RuntimeException r) {
      ManagedSession.rollbackTransaction(session);
      log.error(r.getMessage(), r);
      return null;
    } finally {
    }
  }

  public void update() {
    Session session = ManagedSession.createNewSessionAndTransaction();
    try {
      Criteria updateCriteria = session.createCriteria(OccurrenceUpdates.class);
      OccurrenceUpdates instance = (OccurrenceUpdates) updateCriteria
          .uniqueResult();
      instance.setLastupdate(new Date(System.currentTimeMillis()));
      session.update(instance);
      ManagedSession.commitTransaction(session);
    } catch (RuntimeException r) {
      ManagedSession.rollbackTransaction(session);
      log.error(r.getMessage(), r);
    } finally {
    }
  }

}
