package org.rebioma.server.services;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.rebioma.client.OccurrenceCommentQuery;
import org.rebioma.client.bean.OccurrenceComments;
import org.rebioma.server.util.ManagedSession;

public class OccurrenceCommentsServiceImpl implements OccurrenceCommentsService {

  private static class OccurrenceCommentFilter extends QueryFilter {
    public OccurrenceCommentFilter(String filter) throws InvalidFilter {
      super(filter, OccurrenceComments.class);
    }

    public String getPropertyName(String property) {
      property = property.toLowerCase();
      if (property.equals("oid")) {
        property = "occurrenceId";
      } else if (property.equals("uid")) {
        property = "userId";
      } else if (property.equals("usercomment")) {
        property = "userComment";
      } else if (property.equals("datecommented")) {
        property = "dateCommented";
      } else {
        return null;
      }
      return property;
    }

  }

  private static final Logger log = Logger
      .getLogger(OccurrenceCommentsServiceImpl.class);

  private static String getOccurrencePropertyName(String property) {
    property = property.toLowerCase();
    if (property.equals("oid")) {
      property = "occurrenceId";
    } else if (property.equals("uid")) {
      property = "userId";
    } else if (property.equals("usercomment")) {
      property = "userComment";
    } else if (property.equals("datecommented")) {
      property = "dateCommented";
    } else {
      return null;
    }
    return property;
  }

  public void attachClean(OccurrenceComments instance) {
    log.debug("attaching clean OccurrenceComments instance");
    try {
      ManagedSession.createNewSessionAndTransaction().lock(instance, LockMode.NONE);
      //HibernateUtil.getCurrentSession().lock(instance, LockMode.NONE);
      log.debug("attach successful");
    } catch (RuntimeException re) {
      log.error("attach failed", re);
      throw re;
    }
  }

  public void attachDirty(OccurrenceComments instance) {
    log.debug("attaching dirty OccurrenceComments instance");
    //System.out.println("######## review comments");
    //Session session = HibernateUtil.getCurrentSession();
    //boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
    Session session = ManagedSession.createNewSessionAndTransaction();
    try {
      session.saveOrUpdate(instance);
      //if (isFirstTransaction) {
      //  HibernateUtil.commitCurrentTransaction();
      //}
      ManagedSession.commitTransaction(session);
      log.debug("attach successful");
    } catch (RuntimeException re) {
      ManagedSession.rollbackTransaction(session);
      //HibernateUtil.rollbackTransaction();
      log.error("attach failed", re);
      throw re;
    }
  }

  public void attachDirty(Set<OccurrenceComments> instances) {
    log.debug("attaching dirty Set of OccurrenceComments");
    Session session = ManagedSession.createNewSessionAndTransaction();try {
      
    	attachDirty(session, instances);
    	
      ManagedSession.commitTransaction(session);
      log.debug("attach successful");
    } catch (RuntimeException re) {
      ManagedSession.rollbackTransaction(session);
      log.error("attach failed", re);
      throw re;
    }

  }
  
  public void attachDirty(Session session, Set<OccurrenceComments> instances) {
	    log.debug("attaching dirty Set of OccurrenceComments");
	   
	    try {
	      for (OccurrenceComments instance : instances) {
	        session.saveOrUpdate(instance);
	      }
	      
	      log.debug("attach successful");
	    } catch (RuntimeException re) {	     
	      log.error("attach failed", re);
	      throw re;
	    }

	  }

  public void delete(OccurrenceComments persistentInstance) {
    log.debug("deleting OccurrenceComments instance");
    Session session = ManagedSession.createNewSessionAndTransaction();try {
      session.delete(persistentInstance);
      ManagedSession.commitTransaction(session);
      log.debug("delete successful");
    } catch (RuntimeException re) {
      log.error("delete failed", re);
      ManagedSession.rollbackTransaction(session);
      throw re;
    }
  }

  public void delete(Set<OccurrenceComments> persistentInstances) {
    log.debug("deleting Set of OccurrenceComments");
    Session session = ManagedSession.createNewSessionAndTransaction();try {
      for (OccurrenceComments persistentInstance : persistentInstances) {
        session.delete(persistentInstance);
      }
      ManagedSession.commitTransaction(session);
      log.debug("delete successful");
    } catch (RuntimeException re) {
      ManagedSession.rollbackTransaction(session);
      log.error("delete failed", re);
      throw re;
    }

  }

  public List<OccurrenceComments> findByExample(OccurrenceComments instance) {
    log.debug("finding OccurrenceComments instance by example");
    try {
    	Session session = ManagedSession.createNewSessionAndTransaction();
      List<OccurrenceComments> results = session//HibernateUtil.getCurrentSession()
          .createCriteria("OccurrenceComments").add(Example.create(instance))
          .list();
      log.debug("find by example successful, result size: " + results.size());
      ManagedSession.commitTransaction(session);
      return results;
    } catch (RuntimeException re) {
      log.error("find by example failed", re);
      throw re;
    }
  }

  public OccurrenceComments findById(int id) {
    log.debug("getting OccurrenceComments instance with id: " + id);
    Session session = ManagedSession.createNewSessionAndTransaction();try {
      OccurrenceComments instance = (OccurrenceComments) session.get(
          "OccurrenceComments", id);
      ManagedSession.commitTransaction(session);
      if (instance == null) {
        log.debug("get successful, no instance found");
      } else {
        log.debug("get successful, instance found");
      }
      return instance;
    } catch (RuntimeException re) {
      ManagedSession.rollbackTransaction(session);
      log.error("get failed", re);
      throw re;
    }
  }

  public List<OccurrenceComments> findByQuery(OccurrenceCommentQuery query) {
    log.debug("finding OccurrenceComments instances by query.");
    Session session = ManagedSession.createNewSessionAndTransaction();try {

      List<OccurrenceComments> results = null;
      Criteria criteria = session.createCriteria(OccurrenceComments.class);

      // Sets the start, limit, and order by accepted species:
      criteria.setFirstResult(query.getStart());
      criteria.setMaxResults(query.getLimit());
      criteria.addOrder(Order.asc(getOccurrencePropertyName("dateCommented")));
      Set<OccurrenceCommentFilter> filters = QueryFilter.getFilters(query
          .getFilters(), OccurrenceCommentFilter.class);
      if (filters != null) {
        for (OccurrenceCommentFilter filter : filters) {
          switch (filter.getOperator()) {
          case CONTAIN:
            criteria.add(Restrictions.ilike(filter.column, filter.value));
            break;
          case EQUAL:
            criteria.add(Restrictions.eq(filter.column, filter.value));
            break;
          }
        }
      }
      log.debug(criteria.toString());
      results = criteria.list();
      log.debug("find by Occurrence comment query successful, result size: "
          + results.size());
      ManagedSession.commitTransaction(session);
      return results;
    } catch (RuntimeException re) {
      ManagedSession.rollbackTransaction(session);
      log.error("find by Occurrence comment query failed", re);
      throw re;
    }
  }

  public OccurrenceComments merge(OccurrenceComments detachedInstance) {
    log.debug("merging OccurrenceComments instance");
    Session session = ManagedSession.createNewSessionAndTransaction();try {
      OccurrenceComments result = (OccurrenceComments) session
          .merge(detachedInstance);
      ManagedSession.commitTransaction(session);
      log.debug("merge successful");
      return result;
    } catch (RuntimeException re) {
      log.error("merge failed", re);
      ManagedSession.rollbackTransaction(session);
      throw re;
    }
  }

  public void persist(OccurrenceComments transientInstance) {
    log.debug("persisting OccurrenceComments instance");
    Session session = ManagedSession.createNewSessionAndTransaction();try {
      session.persist(transientInstance);
      ManagedSession.commitTransaction(session);
      log.debug("persist successful");
    } catch (RuntimeException re) {
      log.error("persist failed", re);
      ManagedSession.rollbackTransaction(session);
      throw re;
    }
  }

  public void save(OccurrenceComments comment) {
    Session session = ManagedSession.createNewSessionAndTransaction();
    try {
      session.saveOrUpdate(comment);
      ManagedSession.commitTransaction(session);
      log.debug("attach successful");
    } catch (RuntimeException re) {
      ManagedSession.rollbackTransaction(session);
      log.error("attach failed", re);
      throw re;
    }
  }

}
