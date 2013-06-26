package org.rebioma.server.services;

import java.util.ArrayList;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.rebioma.client.bean.RecordReview;
import org.rebioma.server.util.ManagedSession;

public class RecordReviewDbImpl implements RecordReviewDb {
  /**
   * The {@link Logger} for this class.
   */
  private static final Logger log = Logger.getLogger(RecordReviewDbImpl.class);

  public static final int UNLIMITED = -1;

  public void clear() {
    // Set<String> occIds = new HashSet<String>();
    try {
      Session session = ManagedSession.createNewSessionAndTransaction();
      session.createQuery("delete RecordReview").executeUpdate();
      // Criteria criteria = session.createCriteria(RecordReview.class);
      // criteria.add(Restrictions.isNull("reviewedDate"));
      // for (Object obj : criteria.list()) {
      // RecordReview recordReview = (RecordReview) obj;
      // session.delete(recordReview);
      // occIds.add(recordReview.getOccurrenceId() + "");
      // }
      ManagedSession.commitTransaction(session);
    } catch (RuntimeException re) {
      log.error("error :" + re.getMessage() + " on clearWaitingReviews()", re);
      throw re;
    }
  }

  public boolean delete(RecordReview recordReview) {
    Session session = ManagedSession.createNewSessionAndTransaction();

    try {
      //session.delete(recordReview);
      delete(session,recordReview);
      ManagedSession.commitTransaction(session);
      return true;
    } catch (RuntimeException re) {
      ManagedSession.rollbackTransaction(session);
      log.error("error :" + re.getMessage() + " on delete(RecordReview)", re);
      return false;
    }
  }
  
  public boolean delete(Session session,RecordReview recordReview) {	   
	    try {
	      session.delete(recordReview);
	      
	      return true;
	    } catch (RuntimeException re) {	      
	      log.error("error :" + re.getMessage() + " on delete(RecordReview)", re);
	      return false;
	    }
	  }

  public RecordReview getRecordReview(int userId, int occurrenceId) {
    Session session = ManagedSession.createNewSessionAndTransaction();
    RecordReview recordReview = null;
    try {
      Criteria criteria = session.createCriteria(RecordReview.class);
      criteria.add(Restrictions.eq("userId", userId));
      criteria.add(Restrictions.eq("occurrenceId", occurrenceId));
      recordReview = (RecordReview) criteria.uniqueResult();
      ManagedSession.commitTransaction(session);
    } catch (RuntimeException re) {
      if(session!=null)
      ManagedSession.rollbackTransaction(session);
      log.error("error :" + re.getMessage()
          + " on getRecordReview(userId, occurrenceId)", re);
      throw re;
    }
    return recordReview;
  }

  public List<Integer> getRecordReviewOccIds(int userId, Boolean reviewed) {
    Session session = ManagedSession.createNewSessionAndTransaction();
    RecordReview recordReview = null;
    try {
      String queryString = "select occurrenceId from RecordReview r where r.userId="
          + userId + " and r.reviewed";
      if (reviewed == null) {
        queryString += " is null";
      } else {
        queryString += "=" + reviewed;
      }
      Query query = session.createQuery(queryString);
      List<Integer> result = query.list();
      ManagedSession.commitTransaction(session);
      return result;
    } catch (RuntimeException re) {
      ManagedSession.rollbackTransaction(session);
      log.error("error :" + re.getMessage()
          + " on getRecordReview(userId, occurrenceId)", re);
      throw re;
    }
  }

  public List<RecordReview> getRecordReviewsByOcc(int occurrenceId) {
    return findByProperty("occurrenceId", occurrenceId);
  }
  public List<RecordReview> getRecordReviewsByOcc(Session session,int occurrenceId) {
	    return findByProperty(session,"occurrenceId", occurrenceId);
	  }

  public List<RecordReview> getRecordReviewsByUser(int userId) {
    return findByProperty("userId", userId);
  }

  public RecordReview reviewedRecord(int userId, int occurrenceId,
      boolean reviewed) {
    Session session = ManagedSession.createNewSessionAndTransaction();

    try {
      RecordReview recordReview = getRecordReview(userId, occurrenceId);
      recordReview.setReviewed(reviewed);
      recordReview.setReviewedDate(new Date());
      session.update(recordReview);
      // List<RecordReview> recordReviews = getRecordReviewsByOcc(occurrenceId);
      ManagedSession.commitTransaction(session);
      OccurrenceDb occurrenceDb = DBFactory.getOccurrenceDb();
      boolean isChanged = occurrenceDb.checkForReviewedChanged(occurrenceId);
      
      if (isChanged) {
        return recordReview;
      } else {
        return null;
      }
    } catch (RuntimeException re) {
      ManagedSession.rollbackTransaction(session);
      log.error("error :" + re.getMessage() + " on update(RecordReview)", re);
      throw re;
    } catch (Exception e) {
      ManagedSession.rollbackTransaction(session);
      log.error("error :" + e.getMessage() + " on update(RecordReview)", e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public RecordReview reviewedRecord(int userId, int occurrenceId,
	      boolean reviewed, Date date) {
	    Session session = ManagedSession.createNewSessionAndTransaction();

	    try {
	      RecordReview recordReview = getRecordReview(userId, occurrenceId);
	      recordReview.setReviewed(reviewed);
	      recordReview.setReviewedDate(date);
	      session.update(recordReview);
	      // List<RecordReview> recordReviews = getRecordReviewsByOcc(occurrenceId);
	      ManagedSession.commitTransaction(session);
	      OccurrenceDb occurrenceDb = DBFactory.getOccurrenceDb();
	      boolean isChanged = occurrenceDb.checkForReviewedChanged(occurrenceId);
	      
	      if (isChanged) {
	        return recordReview;
	      } else {
	        return null;
	      }
	    } catch (RuntimeException re) {
	      ManagedSession.rollbackTransaction(session);
	      log.error("error :" + re.getMessage() + " on update(RecordReview)", re);
	      throw re;
	    } catch (Exception e) {
	      ManagedSession.rollbackTransaction(session);
	      log.error("error :" + e.getMessage() + " on update(RecordReview)", e);
	      throw new RuntimeException(e.getMessage(), e);
	    }
	  }

  public RecordReview save(RecordReview recordReview) {
    Session session = ManagedSession.createNewSessionAndTransaction();

    try {
      RecordReview existenceRecordReview = getRecordReview(recordReview
          .getUserId(), recordReview.getOccurrenceId());
      if (existenceRecordReview == null) {
        session.save(recordReview);
      } else {
        recordReview = null;
      }
      ManagedSession.commitTransaction(session);
      return recordReview;
    } catch (RuntimeException re) {
      ManagedSession.rollbackTransaction(session);
      log.error("error :" + re.getMessage() + " on save(RecordReview)", re);
      throw re;
    }
  }

  public RecordReview update(RecordReview recordReview) {
    Session session = ManagedSession.createNewSessionAndTransaction();

    try {
      session.update(recordReview);
      ManagedSession.commitTransaction(session);
      return recordReview;
    } catch (RuntimeException re) {
      ManagedSession.rollbackTransaction(session);
      log.error("error :" + re.getMessage() + " on update(RecordReview)", re);
      throw re;
    }
  }

  protected List<RecordReview> findByProperty(String property, Object value) {
    Session session = ManagedSession.createNewSessionAndTransaction();
    List<RecordReview> recordReviews = null;
    try {
      /*Criteria criteria = session.createCriteria(RecordReview.class).add(
          Restrictions.eq(property, value));
      recordReviews = criteria.list();*/
      recordReviews = findByProperty(session, property, value);
      ManagedSession.commitTransaction(session);
    } catch (RuntimeException re) {
      ManagedSession.rollbackTransaction(session);
      log.error("error :" + re.getMessage()
          + " on findByProperty(property, value)", re);
      throw re;
    }
    return recordReviews;
  }
  
  protected List<RecordReview> findByProperty(Session session, String property, Object value) {
	    
	    List<RecordReview> recordReviews = null;
	    try {
	      Criteria criteria = session.createCriteria(RecordReview.class).add(
	          Restrictions.eq(property, value));
	      recordReviews = criteria.list();
	      
	    } catch (RuntimeException re) {	      
	      throw re;
	    }
	    return recordReviews;
	  }
  
  
  public List<RecordReview> findByProperty() {
	    Session session = ManagedSession.createNewSessionAndTransaction();
	    List<RecordReview> recordReviews = null;
	    try {
	      Criteria criteria = session.createCriteria(RecordReview.class);
	      recordReviews = criteria.list();
	      ManagedSession.commitTransaction(session);
	    } catch (RuntimeException re) {
	      ManagedSession.rollbackTransaction(session);
	      log.error("error :" + re.getMessage()
	          + " on findByProperty(property, value)", re);
	      throw re;
	    }	    
	    return recordReviews;
	  }
  public List<RecordReview> findByProperty(Object value,List<RecordReview> RecordReviews) {
	  	List<RecordReview> recordReviews =new ArrayList<RecordReview>();
	    for(RecordReview rv : RecordReviews){
	    	if(rv.getOccurrenceId()==value)recordReviews.add(rv);
	    }
	    return recordReviews;
	  }

}
