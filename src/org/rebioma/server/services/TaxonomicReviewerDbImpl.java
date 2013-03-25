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
import org.rebioma.server.util.HibernateUtil;

public class TaxonomicReviewerDbImpl implements TaxonomicReviewerDb {
  /**
   * The {@link Logger} for this class.
   */
  private static final Logger log = Logger
      .getLogger(TaxonomicReviewerDbImpl.class);

  public static final int UNLIMITED = -1;

  public void clearExistenceAssignments() {
    try {
      Session session = HibernateUtil.getCurrentSession();
      boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      for (TaxonomicReviewer taxonomicReviewer : findAll()) {
        session.delete(taxonomicReviewer);
      }
      if (isFirstTransaction) {
        HibernateUtil.commitCurrentTransaction();
      }

    } catch (RuntimeException re) {
      HibernateUtil.rollbackTransaction();
      log.error(
          "error :" + re.getMessage() + " on clearExistenceAssignments()", re);
      throw re;
    }

  }

  public boolean delete(TaxonomicReviewer TaxonomicReviewer) {
    try {
      Session session = HibernateUtil.getCurrentSession();
      boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      session.delete(TaxonomicReviewer);
      if (isFirstTransaction) {
        HibernateUtil.commitCurrentTransaction();
      }
      return true;
    } catch (RuntimeException re) {
      HibernateUtil.rollbackTransaction();
      log.error("error :" + re.getMessage() + " on delete(TaxonomicReviewer)",
          re);
      return false;
    }
  }

  public List<TaxonomicReviewer> findAll() {

    try {
      Session session = HibernateUtil.getCurrentSession();
      boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      List<TaxonomicReviewer> reviewers = session.createCriteria(
          TaxonomicReviewer.class).list();
      if (isFirstTransaction) {
        HibernateUtil.commitCurrentTransaction();
      }
      return reviewers;
    } catch (RuntimeException re) {
      HibernateUtil.rollbackTransaction();
      log.error("error :" + re.getMessage() + " on delete(TaxonomicReviewer)",
          re);
      throw re;
    }
  }

  public List<TaxonomicReviewer> getTaxonomicReviewers(int userId) {
    return findByProperty("userId", userId);
  }

  public boolean isAssignmentExisted(int userId, String fieldName,
      String fieldValue) {
    Session session = HibernateUtil.getCurrentSession();
    boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
    boolean isExisted = false;
    try {
      Criteria criteria = session.createCriteria(TaxonomicReviewer.class);
      criteria.add(Restrictions.eq("userId", userId));
      criteria.add(Restrictions.eq("taxonomicField", fieldName));
      criteria.add(Restrictions.eq("taxonomicValue", fieldValue));
      isExisted = criteria.uniqueResult() != null;
      if (isFirstTransaction) {
        HibernateUtil.commitCurrentTransaction();
      }
    } catch (RuntimeException re) {
      HibernateUtil.rollbackTransaction();
      log.error("error :" + re.getMessage()
          + " on isAssignmentExisted(userId, fieldName, fieldValue)", re);
    }
    return isExisted;
  }

  public TaxonomicReviewer save(TaxonomicReviewer TaxonomicReviewer) {
    Session session = HibernateUtil.getCurrentSession();
    boolean isFirstTransaction = HibernateUtil.beginTransaction(session);

    try {
      Integer id = (Integer) session.save(TaxonomicReviewer);
      if (isFirstTransaction) {
        HibernateUtil.commitCurrentTransaction();
      }
      TaxonomicReviewer.setId(id);
      return TaxonomicReviewer;
    } catch (RuntimeException re) {
      HibernateUtil.rollbackTransaction();
      log
          .error("error :" + re.getMessage() + " on save(TaxonomicReviewer)",
              re);
      return null;
    }
  }

  public TaxonomicReviewer update(TaxonomicReviewer TaxonomicReviewer) {
    Session session = HibernateUtil.getCurrentSession();
    boolean isFirstTransaction = HibernateUtil.beginTransaction(session);

    try {
      session.update(TaxonomicReviewer);
      if (isFirstTransaction) {
        HibernateUtil.commitCurrentTransaction();
      }
      return TaxonomicReviewer;
    } catch (RuntimeException re) {
      HibernateUtil.rollbackTransaction();
      log.error("error :" + re.getMessage() + " on update(TaxonomicReviewer)",
          re);
      return null;
    }
  }

  protected List<TaxonomicReviewer> findByProperty(String property, Object value) {
    Session session = HibernateUtil.getCurrentSession();
    boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
    List<TaxonomicReviewer> TaxonomicReviewers = null;
    try {
      Criteria criteria = session.createCriteria(TaxonomicReviewer.class).add(
          Restrictions.eq(property, value));
      TaxonomicReviewers = criteria.list();
      if (isFirstTransaction) {
        HibernateUtil.commitCurrentTransaction();
      }
    } catch (RuntimeException re) {
      HibernateUtil.rollbackTransaction();
      log.error("error :" + re.getMessage()
          + " on findByProperty(property, value)", re);
    }
    return TaxonomicReviewers;
  }
  
  public List<TaxonomicReviewer> findByProperty() {
	  Session session = HibernateUtil.getCurrentSession();
	    boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
	    List<TaxonomicReviewer> TaxonomicReviewers = null;
	    try {
	      Criteria criteria = session.createCriteria(TaxonomicReviewer.class);
	      TaxonomicReviewers = criteria.list();
	      if (isFirstTransaction) {
	        HibernateUtil.commitCurrentTransaction();
	      }
	    } catch (RuntimeException re) {
	      HibernateUtil.rollbackTransaction();
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
