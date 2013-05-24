package org.rebioma.server.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;

public interface TaxonomicReviewerDb {

  void clearExistenceAssignments();

  boolean delete(TaxonomicReviewer TaxonomicReviewer);

  List<TaxonomicReviewer> findAll();

  List<TaxonomicReviewer> getTaxonomicReviewers(int userId);

  boolean isAssignmentExisted(int userId, String fieldName, String fieldValue);

  TaxonomicReviewer save(TaxonomicReviewer TaxonomicReviewer);

  TaxonomicReviewer update(TaxonomicReviewer TaxonomicReviewer);
  
  List<TaxonomicReviewer> findByProperty();
  
  List<TaxonomicReviewer> findByProperty(Object value,List<TaxonomicReviewer> TaxonomicReviewers);
}
