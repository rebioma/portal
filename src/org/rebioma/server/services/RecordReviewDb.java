package org.rebioma.server.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.rebioma.client.bean.RecordReview;

public interface RecordReviewDb {

  void clear();

  boolean delete(RecordReview recordReview);
  
  boolean delete(Session session,RecordReview recordReview);

  RecordReview getRecordReview(int userId, int occurrenceId);

  List<Integer> getRecordReviewOccIds(int userId, Boolean reviewed);

  List<RecordReview> getRecordReviewsByOcc(int occurrenceId);
  
  List<RecordReview> getRecordReviewsByOcc(Session session,int occurrenceId);

  List<RecordReview> getRecordReviewsByUser(int userId);

  RecordReview reviewedRecord(int userId, int occurrenceId, boolean reviewed);

  RecordReview reviewedRecord(int userId, int occurrenceId, boolean reviewed, Date date);

  RecordReview save(RecordReview recordReview);

  // RecordReview update(RecordReview recordReview);
  
  	List<RecordReview> findByProperty();
  	List<RecordReview> findByProperty(Object value,List<RecordReview> RecordReviews);
}
