package org.rebioma.server.services;

// Generated Feb 18, 2009 10:17:00 AM by Hibernate Tools 3.2.4.CR1

import static org.hibernate.criterion.Example.create;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.rebioma.server.util.ManagedSession;

/**
 * Home object for domain model class Collaborators.
 * 
 * @see org.rebioma.server.services.Collaborators
 * @author Hibernate Tools
 */
public class CollaboratorsDbImpl implements CollaboratorsDb {

  /**
   * The {@link Logger} for this class.
   */
  private static final Logger log = Logger.getLogger(CollaboratorsDbImpl.class);

  public void attachClean(Collaborators instance) {
    log.debug("attaching clean Collaborators instance");
    try {
      //HibernateUtil.getCurrentSession().lock(instance, LockMode.NONE);
      ManagedSession.createNewSession().lock(instance, LockMode.NONE);	
      log.debug("attach successful");
    } catch (RuntimeException re) {
      log.error("attach failed", re);
      throw re;
    }
  }

  public void attachDirty(Collaborators instance) {
    log.debug("attaching dirty Collaborators instance");
    try {
      ManagedSession.createNewSession().saveOrUpdate(instance);
      //HibernateUtil.getCurrentSession().saveOrUpdate(instance);
      log.debug("attach successful");
    } catch (RuntimeException re) {
      log.error("attach failed", re);
      throw re;
    }
  }

  public void attachDirty(Set<Collaborators> addedFriends) {
    Session session = ManagedSession.createNewSessionAndTransaction();
    //Session session = HibernateUtil.getCurrentSession();
    //boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
    try {
      for (Collaborators collaborators : addedFriends) {
        session.saveOrUpdate(collaborators);
      }
      //if (isFirstTransaction) {
      //  HibernateUtil.commitCurrentTransaction();
      //}
      ManagedSession.commitTransaction(session);
    } catch (RuntimeException e) {
      //HibernateUtil.rollbackTransaction();
      throw e;
    }
  }

  public void delete(Collaborators persistentInstance) {
    log.debug("deleting Collaborators instance");
    try {
      ManagedSession.createNewSession().delete(persistentInstance);
      //HibernateUtil.getCurrentSession().delete(persistentInstance);
      log.debug("delete successful");
    } catch (RuntimeException re) {
      log.error("delete failed", re);
      throw re;
    }
  }

  public void delete(Integer userId, Set<Integer> removedFriends) {
    log.debug("deleting Collaborators by ids");
    if (removedFriends.isEmpty()) {
      return;
    }
    Session session = ManagedSession.createNewSessionAndTransaction();
    //Session session = HibernateUtil.getCurrentSession();
    //boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
    try {
      StringBuilder sb = new StringBuilder(
          "delete from Collaborators where userId = " + userId + " and (");
      for (Integer friendId : removedFriends) {
        sb.append("friendId = " + friendId + " or ");
      }
      sb.delete(sb.length() - 4, sb.length());
      sb.append(")");
      session.createQuery(sb.toString()).executeUpdate();
      //if (isFirstTransaction) {
      //  HibernateUtil.commitCurrentTransaction();
      //}
      ManagedSession.commitTransaction(session);
    } catch (RuntimeException re) {
      log.error("deleteCollaborators by ids  failed", re);
      //HibernateUtil.rollbackTransaction();
      throw re;
    }

  }

  public List<Collaborators> findByExample(Collaborators instance) {
    log.debug("finding Collaborators instance by example");
    try {
      Session session =	ManagedSession.createNewSessionAndTransaction();
      List<Collaborators> results = session//HibernateUtil.getCurrentSession()
          .createCriteria("org.rebioma.server.services.Collaborators").add(
              create(instance)).list();
      log.debug("find by example successful, result size: " + results.size());
      ManagedSession.commitTransaction(session);
      return results;
    } catch (RuntimeException re) {
      log.error("find by example failed", re);
      throw re;
    }
  }

  public Collaborators findById(int id) {
    log.debug("getting Collaborators instance with id: " + id);
    //Session session = HibernateUtil.getCurrentSession();
    //boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
    Session session = ManagedSession.createNewSessionAndTransaction();
    try {
      Collaborators instance = (Collaborators) session.get(
          "org.rebioma.server.services.Collaborators", id);
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

  public Set<Integer> getAllCollaboratorIds(Integer userId) throws Exception {
    //Session session = HibernateUtil.getCurrentSession();
    //boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
	Session session = ManagedSession.createNewSessionAndTransaction();  
    try {
      Set<Integer> users = new HashSet<Integer>(session.createQuery(
          "select friendId from Collaborators where userId =:id").setInteger(
          "id", userId).list());
      //if (isFirstTransaction) {
      //  HibernateUtil.commitCurrentTransaction();
      //}
      ManagedSession.commitTransaction(session);
      return users;
    } catch (Exception e) {
      //HibernateUtil.rollbackTransaction();
      throw e;
    }
  }

  public Collaborators merge(Collaborators detachedInstance) {
    log.debug("merging Collaborators instance");
    try {
      Session session = ManagedSession.createNewSessionAndTransaction();	
      Collaborators result = (Collaborators) session
          .merge(detachedInstance);
      log.debug("merge successful");
      ManagedSession.createNewSessionAndTransaction();
      return result;
    } catch (RuntimeException re) {
      log.error("merge failed", re);
      throw re;
    }
  }

  public void persist(Collaborators transientInstance) {
    log.debug("persisting Collaborators instance");
    try {
      ManagedSession.createNewSessionAndTransaction().persist(transientInstance);	
      //HibernateUtil.getCurrentSession().persist(transientInstance);
      log.debug("persist successful");
    } catch (RuntimeException re) {
      log.error("persist failed", re);
      throw re;
    }
  }

}
