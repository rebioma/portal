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

import static org.hibernate.criterion.Example.create;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.rebioma.client.bean.AscData;
import org.rebioma.server.util.ManagedSession;

/**
 * @author eighty
 * 
 */
public class AscDataDbImpl implements AscDataDb {
  /**
   * The {@link Logger} for this class.
   */
  private static final Logger log = Logger.getLogger(AscDataDbImpl.class);

  public AscDataDbImpl() {
  }

  public void attachClean(AscData instance) {
    log.debug("attaching clean AscData instance");
    try {
      ManagedSession.createNewSession().lock(instance, LockMode.NONE);	
      //HibernateUtil.getCurrentSession().lock(instance, LockMode.NONE);
      log.debug("attach successful");
    } catch (RuntimeException re) {
      log.error("attach failed", re);
      throw re;
    }
  }

  public void attachDirty(AscData instance) {
    log.debug("attaching dirty AscData instance");
    try {
      //Session session = HibernateUtil.getCurrentSession();
      Session session = ManagedSession.createNewSessionAndTransaction();
      //HibernateUtil.getCurrentSession().saveOrUpdate(instance);
      session.saveOrUpdate(instance);
      log.debug("attach successful");
      ManagedSession.commitTransaction(session);
    } catch (RuntimeException re) {
      log.error("attach failed", re);
      //HibernateUtil.rollbackTransaction();
      throw re;
    }
  }

  public void attachDirty(Set<AscData> instances) {
    log.debug("attaching dirty AscData instances");
    AscData ref = null;
    try {
      //Session session = HibernateUtil.getCurrentSession();
      //boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      Session session = ManagedSession.createNewSessionAndTransaction();
      for (AscData instance : instances) {
        ref = instance;
        session.saveOrUpdate(instance);
      }
      //if (isFirstTransaction) {
      //  HibernateUtil.commitCurrentTransaction();
      //}
      ManagedSession.commitTransaction(session);
      log.debug("attach successful");
    } catch (RuntimeException re) {
      log.info("attach failed (" + ref + ") ", re);
      //HibernateUtil.rollbackTransaction();
      throw re;
    }
  }

  public void delete(AscData persistentInstance) {
    log.debug("deleting AscData instance");
    try {
      //Session session = HibernateUtil.getCurrentSession();
      Session session = ManagedSession.createNewSessionAndTransaction();
      //boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      
      session.delete(persistentInstance);
      //if (isFirstTransaction) {
      //  HibernateUtil.commitCurrentTransaction();
      //}
      ManagedSession.commitTransaction(session);
      log.debug("delete successful");
    } catch (RuntimeException re) {
      log.error("delete failed", re);
      throw re;
    }
  }

  public void delete(Set<AscData> persistentInstances) {
    log.debug("deleting AscData instance");
    try {
      //Session session = HibernateUtil.getCurrentSession();
      //boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      Session session = ManagedSession.createNewSessionAndTransaction();
      for (AscData persistentInstance : persistentInstances) {
        session.delete(persistentInstance);
      }
      //if (isFirstTransaction) {
      //  HibernateUtil.commitCurrentTransaction();
      //}
      ManagedSession.commitTransaction(session);
      log.debug("delete successful");
    } catch (RuntimeException re) {
      log.error("delete failed", re);
      //HibernateUtil.rollbackTransaction();
      throw re;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.rebioma.server.services.AscDataDb#findAll()
   */
  public List<AscData> findAll() {
    log.debug("getting all AscData");
    List<AscData> results;
    try {
      //Session session = HibernateUtil.getCurrentSession();
      //boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      Session session = ManagedSession.createNewSessionAndTransaction();
      results = session.createCriteria(AscData.class)
          .list();
      //if (isFirstTransaction) {
      //  HibernateUtil.commitCurrentTransaction();
      //}
      ManagedSession.commitTransaction(session);
      return results;
    } catch (RuntimeException re) {
      log.error("get failed", re);
      //HibernateUtil.rollbackTransaction();
      throw re;
    }
  }

  public List<AscData> findByExample(AscData instance) {
    log.debug("finding AscData instance by example");
    try {
      //Session session = HibernateUtil.getCurrentSession();
      //boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      Session session = ManagedSession.createNewSessionAndTransaction();
      List<AscData> results = session.createCriteria(
          "org.rebioma.client.bean.AscData").add(create(instance)).list();
      log.debug("find by example successful, result size: " + results.size());
      //if (isFirstTransaction) {
      //  HibernateUtil.commitCurrentTransaction();
      //}
      ManagedSession.commitTransaction(session);
      return results;
    } catch (RuntimeException re) {
      log.error("find by example failed", re);
      //HibernateUtil.rollbackTransaction();
      throw re;
    }
  }

  public List<AscData> findByExample(Set<AscData> instances) {
    List<AscData> result = new ArrayList<AscData>();
    for (AscData instance : instances) {
      result.addAll(findByExample(instance));
    }
    return result;
  }

  public AscData findById(java.lang.Integer id) {
    log.debug("getting AscData instance with id: " + id);
    try {
      //Session session = HibernateUtil.getCurrentSession();
      //boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      Session session = ManagedSession.createNewSessionAndTransaction();
      AscData instance = (AscData) session.get(
          "org.rebioma.client.bean.AscData", id.toString());
      if (instance == null) {
        log.debug("get successful, no instance found");
      } else {
        log.debug("get successful, instance found");
      }
      //if (isFirstTransaction) {
      //  HibernateUtil.commitCurrentTransaction();
      //}
      ManagedSession.commitTransaction(session);
      return instance;
    } catch (RuntimeException re) {
      log.error("get failed", re);
      //HibernateUtil.rollbackTransaction();
      throw re;
    }
  }

  public List<AscData> findById(Set<Integer> ids) {
    List<AscData> results = new ArrayList<AscData>();
    for (Integer id : ids) {
      results.add(findById(id));
    }
    return results;
  }

  public AscData merge(AscData detachedInstance) {
    log.debug("merging AscData instance");
    try {
      //Session session = HibernateUtil.getCurrentSession();
      //boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      Session session = ManagedSession.createNewSessionAndTransaction();
      AscData result = (AscData) session.merge(detachedInstance);
      log.debug("merge successful");
      //if (isFirstTransaction) {
      //  HibernateUtil.commitCurrentTransaction();
      //}
      ManagedSession.commitTransaction(session);
      return result;
    } catch (RuntimeException re) {
      log.error("merge failed", re);
      //HibernateUtil.rollbackTransaction();
      throw re;
    }
  }

  public void persist(AscData transientInstance) {
    log.debug("persisting AscData instance");
    try {
       Session session = ManagedSession.createNewSessionAndTransaction();
       session.persist(transientInstance);
       //HibernateUtil.getCurrentSession().persist(transientInstance);
      log.debug("persist successful");
    } catch (RuntimeException re) {
      log.error("persist failed", re);
      throw re;
    }
  }

}
