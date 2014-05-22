package org.rebioma.server.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.rebioma.client.bean.RecordReview;
import org.rebioma.server.util.ManagedSession;

public class TaxonomicReviewerDbImpl implements TaxonomicReviewerDb {
  /**
   * The {@link Logger} for this class.
   */
  private static final Logger log = Logger
      .getLogger(TaxonomicReviewerDbImpl.class);

  public static final int UNLIMITED = -1;

  public void clearExistenceAssignments() {
    try {
      Session session = ManagedSession.createNewSessionAndTransaction();
      for (TaxonomicReviewer taxonomicReviewer : findAll()) {
        session.delete(taxonomicReviewer);
      }
      ManagedSession.commitTransaction(session);

    } catch (RuntimeException re) {
      log.error(
          "error :" + re.getMessage() + " on clearExistenceAssignments()", re);
      throw re;
    }

  }

  public boolean delete(TaxonomicReviewer TaxonomicReviewer) {
    try {
      Session session = ManagedSession.createNewSessionAndTransaction();session.delete(TaxonomicReviewer);
      ManagedSession.commitTransaction(session);
      return true;
    } catch (RuntimeException re) {
      log.error("error :" + re.getMessage() + " on delete(TaxonomicReviewer)",
          re);
      return false;
    }
  }

  public List<TaxonomicReviewer> findAll() {

    try {
      Session session = ManagedSession.createNewSessionAndTransaction();List<TaxonomicReviewer> reviewers = session.createCriteria(
          TaxonomicReviewer.class).list();
      ManagedSession.commitTransaction(session);
      return reviewers;
    } catch (RuntimeException re) {
      log.error("error :" + re.getMessage() + " on findAll()",
          re);
      throw re;
    }
  }

  public List<TaxonomicReviewer> getTaxonomicReviewers(int userId) {
    return findByProperty("userId", userId);
  }

  public boolean isAssignmentExisted(int userId, String fieldName,
      String fieldValue) {
    Session session = ManagedSession.createNewSessionAndTransaction();
    boolean isExisted = false;
    try {
      Criteria criteria = session.createCriteria(TaxonomicReviewer.class);
      criteria.add(Restrictions.eq("userId", userId));
      criteria.add(Restrictions.eq("taxonomicField", fieldName));
      criteria.add(Restrictions.eq("taxonomicValue", fieldValue));
      isExisted = criteria.uniqueResult() != null;
      ManagedSession.commitTransaction(session);
    } catch (RuntimeException re) {
      ManagedSession.rollbackTransaction(session);
      log.error("error :" + re.getMessage()
          + " on isAssignmentExisted(userId, fieldName, fieldValue)", re);
    }
    return isExisted;
  }

  public TaxonomicReviewer save(TaxonomicReviewer TaxonomicReviewer) {
    Session session = ManagedSession.createNewSessionAndTransaction();

    try {
      Integer id = (Integer) session.save(TaxonomicReviewer);
      ManagedSession.commitTransaction(session);
      TaxonomicReviewer.setId(id);
      return TaxonomicReviewer;
    } catch (RuntimeException re) {
      ManagedSession.rollbackTransaction(session);
      log
          .error("error :" + re.getMessage() + " on save(TaxonomicReviewer)",
              re);
      return null;
    }
  }

  public TaxonomicReviewer update(TaxonomicReviewer TaxonomicReviewer) {
    Session session = ManagedSession.createNewSessionAndTransaction();

    try {
      session.update(TaxonomicReviewer);
      ManagedSession.commitTransaction(session);
      return TaxonomicReviewer;
    } catch (RuntimeException re) {
      ManagedSession.rollbackTransaction(session);
      log.error("error :" + re.getMessage() + " on update(TaxonomicReviewer)",
          re);
      return null;
    }
  }

  protected List<TaxonomicReviewer> findByProperty(String property, Object value) {
    Session session = ManagedSession.createNewSessionAndTransaction();
    List<TaxonomicReviewer> TaxonomicReviewers = null;
    try {
      Criteria criteria = session.createCriteria(TaxonomicReviewer.class).add(
          Restrictions.eq(property, value));
      TaxonomicReviewers = criteria.list();
      ManagedSession.commitTransaction(session);
    } catch (RuntimeException re) {
      ManagedSession.rollbackTransaction(session);
      log.error("error :" + re.getMessage()
          + " on findByProperty(property, value)", re);
    }
    return TaxonomicReviewers;
  }
  
  public List<TaxonomicReviewer> findByProperty() {
	  Session session = ManagedSession.createNewSessionAndTransaction();
	    List<TaxonomicReviewer> TaxonomicReviewers = null;
	    try {
	      Criteria criteria = session.createCriteria(TaxonomicReviewer.class);
	      TaxonomicReviewers = criteria.list();
	      ManagedSession.commitTransaction(session);
	    } catch (RuntimeException re) {
	      ManagedSession.rollbackTransaction(session);
	      log.error("error :" + re.getMessage()
	          + " on findByProperty(property, value)", re);
	    }
	    return TaxonomicReviewers;
	  }
  public List<TaxonomicReviewer> findByProperty(Object value,List<TaxonomicReviewer> TaxonomicReviewers) {
	  	List<TaxonomicReviewer> recordReviews =new ArrayList<TaxonomicReviewer>();
	    for(TaxonomicReviewer rv : TaxonomicReviewers){
	    	if(rv.getId()==value)recordReviews.add(rv);
	    }
	    return recordReviews;
  }

}
