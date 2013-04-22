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

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.rebioma.client.EmailException;
import org.rebioma.client.OccurrenceCommentQuery;
import org.rebioma.client.OccurrenceQuery;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.OccurrenceComments;
import org.rebioma.client.bean.OccurrenceReview;
import org.rebioma.client.bean.RecordReview;
import org.rebioma.client.bean.User;
import org.rebioma.client.services.OccurrenceService;
import org.rebioma.server.MySqlPing;
import org.rebioma.server.util.EmailUtil;
import org.rebioma.server.util.HibernateUtil;
import org.rebioma.server.util.OccurrenceUtil;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Guice;

/**
 * The default implementation of {@link OccurrenceService}. Uses
 * {@link OccurrenceDb} service to fetch {@link Occurrence} objects from the
 * database.
 * 
 */
@SuppressWarnings("serial")
public class OccurrenceServiceImpl extends RemoteServiceServlet implements
    OccurrenceService {
  static {
    MySqlPing.startPingTimer();
  }

  /**
   * The {@link OccurrenceDb} used for querying {@link Occurrence} objects.
   */
  OccurrenceDb occurrenceDb = DBFactory.getOccurrenceDb();

  /**
   * The {@link SessionIdService} used for validating session ids.
   */
  // @Inject
  SessionIdService sessionService = DBFactory.getSessionIdService();

  /**
   * The {@link ValidationService} used to validate {@link Occurrence} objects
   * before they are saved to the database. Injected by {@link Guice}
   */
  ValidationService validationService = DBFactory.getValidationService();

  /**
   * The service used for updating {@link OccurrenceUpdates} on create, insert,
   * and delete.
   */
  UpdateService updateService = DBFactory.getUpdateService();

  /**
   * The service used for querying {@link OccurrenceCommentsService} objects.
   */
  OccurrenceCommentsService commentService = DBFactory
      .getOccurrentCommentService();

  UserDb userDb = DBFactory.getUserDb();

  AscDataDb ascDataDb = DBFactory.getAscDataDb();

  RecordReviewDb recordReviewDb = DBFactory.getRecordReviewDb();

  TaxonomicReviewerDb taxonomicReviewerDb = DBFactory.getTaxonomicReviewerDb();

  private static Logger log = Logger.getLogger(OccurrenceServiceImpl.class);

  /**
   * Deletes all {@link Occurrence} found by given query and belong to the given
   * user, found by session id.
   * 
   * @see org.rebioma.client.services.OccurrenceService#delete(java.lang.String,
   *      org.rebioma.client.OccurrenceQuery)
   */
  public int delete(String sessionId, OccurrenceQuery query)
      throws OccurrenceServiceException {
    User user = sessionService.getUserBySessionId(sessionId);
    try {
      if (user != null) {
        int deletedRecords = occurrenceDb.detele(query, user);
        if (deletedRecords != 0) {
          updateService.update();
        }
        return deletedRecords;
      } else {
        throw new OccurrenceServiceException(
            "Invalid request. No user associated with session id.");
      }
    } catch (Exception e) {
      throw new OccurrenceServiceException("Delete failed: " + e);
    }
  }

  /**
   * (non-Javadoc)
   * 
   * @see org.rebioma.client.services.OccurrenceService#delete(java.lang.String,
   *      java.util.Set)
   */
  public String delete(String sessionId, Set<Occurrence> occurrences)
      throws OccurrenceServiceException {
    try {
      User user = sessionService.getUserBySessionId(sessionId);
      if ((user != null) && (!occurrences.isEmpty())) {
        occurrenceDb.delete(occurrences);
        updateService.update();
        return "";
      } else {
        throw new OccurrenceServiceException(
            "Invalid request. No user associated with session id.");
      }
    } catch (Exception e) {
      throw new OccurrenceServiceException("Delete failed: " + e);
    }
  }

  public Integer deleteComments(String sid, Set<OccurrenceComments> comments)
      throws OccurrenceServiceException {
    try {
      int deleteCount = 0;
      User user = sessionService.getUserBySessionId(sid);
      if ((user != null) && (!comments.isEmpty())) {
        for (OccurrenceComments comment : comments) {
          if (comment.getUserId() == user.getId()) {
            commentService.delete(comments);
            deleteCount++;
          }
        }
        updateService.update();
      } else {
        throw new OccurrenceServiceException(
            "Invalid request. No user associated with session id.");
      }
      return deleteCount;
    } catch (Exception e) {
      throw new OccurrenceServiceException("Unable to update: " + e.toString());
    }
  }

  public OccurrenceCommentQuery fetch(OccurrenceCommentQuery query)
      throws OccurrenceServiceException {
    List<OccurrenceComments> oComments = commentService.findByQuery(query);
    for (OccurrenceComments comment : oComments) {
      User user = userDb.findById(comment.getUserId());
      if (user != null) {
        comment.setUserEmail(user.getEmail());
      }
    }
    query.setResults(oComments);
    return query;
  }

  /**
   * Fetches a set of {@link Occurrence} objects that match the query. Throws an
   * exception if the session id is invalid or if there's a problem querying the
   * database.
   * 
   */
  public OccurrenceQuery fetch(String sessionId, OccurrenceQuery query)
      throws OccurrenceServiceException {
    User user = sessionService.getUserBySessionId(sessionId);
    Integer userId = null;
    if (user == null) {
      enforcePublicQuery(query);
    } else {
      userId = user.getId();
    }
    try {
      List<Occurrence> results = occurrenceDb
          .findByOccurrenceQuery(query, user);
      query.setResults(results);
      return query;
    } catch (Exception e) {
      e.printStackTrace();
      throw new OccurrenceServiceException("Unable to fetch: " + e);
    }
  }

  public Map<Integer, Boolean> getMyReviewedOnRecords(String sid,
      Map<Integer, Integer> rowOccIdsMap) throws OccurrenceServiceException {
    try {
      User user = sessionService.getUserBySessionId(sid);
      if (user != null) {
        Map<Integer, Boolean> userReviewedMap = new HashMap<Integer, Boolean>();
        for (Integer row : rowOccIdsMap.keySet()) {
          RecordReview recordReview = recordReviewDb.getRecordReview(user
              .getId(), rowOccIdsMap.get(row));
          userReviewedMap.put(row, recordReview.getReviewed());
        }
        return userReviewedMap;
      } else {
        throw new OccurrenceServiceException(
            "Invalid request. No user associated with session id.");
      }

    } catch (Exception e) {
      throw new OccurrenceServiceException("Unable to update: " + e.toString());
    }
  }

  @Override
  public List<OccurrenceReview> getOccurrenceReviewsOf(Integer occId)
      throws OccurrenceServiceException {
    try {
      return occurrenceDb.getOccurrenceReviewsOf(occId);
    } catch (Exception e) {
      throw new OccurrenceServiceException("Unable to update: " + e.toString());
    }
  }

  /**
   * @see org.rebioma.client.services.OccurrenceService#lastUpdateInMilliseconds()
   */
  public Long lastUpdateInMilliseconds() throws OccurrenceServiceException {
    return updateService.getLastUpdate().getTime();
  }

  public int reviewRecords(String sid, Boolean reviewed, OccurrenceQuery query,
      String comment) throws OccurrenceServiceException {
    try {
      if (reviewed == null) {
        throw new OccurrenceServiceException(
            "Invalid request. reviewed should be whether true or false not null");
      }

      User user = sessionService.getUserBySessionId(sid);

      if (user != null) {
        query.setStart(0);
        query.setLimit(OccurrenceQuery.UNLIMITED);
        List<Integer> occurrenceIds = occurrenceDb.findOccurrenceIdsByQuery(
            query, user);
        if (occurrenceIds != null && !occurrenceIds.isEmpty()) {
          return reviewRecords(user, reviewed, occurrenceIds, comment);
        }
      } else {
        throw new OccurrenceServiceException(
            "Invalid request. No user associated with session id.");
      }
    } catch (Exception e) {
      HibernateUtil.rollbackTransaction();
      OccurrenceServiceException oe = new OccurrenceServiceException(
          "unable to reviewed records by query because of error: "
              + e.getMessage());
      oe.initCause(e);
      throw oe;
    }
    return 0;
  }

  public int reviewRecords(String sid, Boolean reviewed,
      Set<Integer> occurrenceIds, String comment)
      throws OccurrenceServiceException {
    int count = 0;
    try {
      if (reviewed == null) {
        throw new OccurrenceServiceException(
            "Invalid request. reviewed should be whether true or false not null");
      }

      User user = sessionService.getUserBySessionId(sid);

      if (user != null) {
        count = reviewRecords(user, reviewed, occurrenceIds, comment);
      } else {
        throw new OccurrenceServiceException(
            "Invalid request. No user associated with session id.");
      }
    } catch (Exception e) {
      HibernateUtil.rollbackTransaction();
      OccurrenceServiceException oe = new OccurrenceServiceException(
          "unable to reviewed records because of error: " + e.getMessage());
      oe.initCause(e);
      throw oe;
    }
    return count;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.rebioma.client.services.OccurrenceService#update(java.lang.String,
   * org.rebioma.client.OccurrenceQuery)
   */
  public Integer update(String sessionId, OccurrenceQuery query)
      throws OccurrenceServiceException {
    try {
      User user = sessionService.getUserBySessionId(sessionId);
      if (user != null) {
        int updatedRecordCount = occurrenceDb.update(query, user);
        if (updatedRecordCount != 0) {
          updateService.update();
        }
        return updatedRecordCount;
      } else {
        throw new OccurrenceServiceException(
            "Invalid request. No user associated with session id.");
      }
    } catch (Exception e) {
      throw new OccurrenceServiceException("Unable to update: " + e.toString());
    }
  }

  /**
   * Updates the set of {@link Occurrence} objects. If the session is invalid,
   * returns null.
   * 
   * @throws OccurrenceServiceException
   * @see org.rebioma.client.services.OccurrenceService#update(java.lang.String,
   *      java.util.Set)
   */
  public String update(String sessionId, Set<Occurrence> occurrences)
      throws OccurrenceServiceException {
  	System.out.println("####### update");
    try {
      User user = sessionService.getUserBySessionId(sessionId);
      if ((user != null) && (!occurrences.isEmpty())) {
        occurrenceDb.removeBadId(occurrences, user);
        validationService.validate(occurrences);
        for (Occurrence o : occurrences) {
          // do not populate layer when occurrence is update due to performance
          // of the provider server
          // AscDataUtil.resetLayerValues(o);
          // AscDataUtil.setLayerValuesToOccurrence(o);
          OccurrenceUtil.populateScientificName(o, false);
        }
        occurrenceDb.attachDirty(occurrences);
        updateService.update();
      } else {
        throw new OccurrenceServiceException(
            "Invalid request. No user associated with session id.");
      }
      StringBuilder result = new StringBuilder();
      result.append("Vetting update complete.");
      return result.toString();
    } catch (Exception e) {
      throw new OccurrenceServiceException("Unable to update: " + e.toString());
    }
  }

  public int updateComments(String sessionId, Set<OccurrenceComments> comments)
      throws OccurrenceServiceException {
  	System.out.println("####### updateComments");
    try {
      int updatedCount = 0;
      User user = sessionService.getUserBySessionId(sessionId);
      if ((user != null) && (!comments.isEmpty())) {
        for (OccurrenceComments comment : comments) {
          comment.setUserId(user.getId());
        }
        commentService.attachDirty(comments);
        updatedCount = comments.size();
        updateService.update();
      } else {
        throw new OccurrenceServiceException(
            "Invalid request. No user associated with session id.");
      }
      return updatedCount;
    } catch (Exception e) {
      throw new OccurrenceServiceException("Unable to update: " + e.toString());
    }
  }

  /**
   * Removes {@link OccurrenceType} PRIVATE if it exists in the query type set.
   * 
   * @param query the query to enforce
   * 
   */
  private void enforcePublicQuery(OccurrenceQuery query) {

  }

  private int reviewRecords(User user, Boolean reviewed,
      Collection<Integer> occurrenceIds, String comment) {
    int count = 0;
    log.info("reviewing records: " + occurrenceIds);
    Date date = new Date();
    Map<Integer, Set<Integer>> ownerChangeToNegRecordReviewedMap = new HashMap<Integer, Set<Integer>>();
    for (Integer id : occurrenceIds) {
      RecordReview recordReview = recordReviewDb.getRecordReview(user.getId(),
          id);
      if (recordReview.getReviewed() == null
          || !recordReview.getReviewed().equals(reviewed)) {
        recordReview = recordReviewDb
            .reviewedRecord(user.getId(), id, reviewed);
        count++;
        if (recordReview != null && !reviewed) {
          int ownerId = occurrenceDb.findById(id).getOwner();
          Set<Integer> recordsReviewChanges = ownerChangeToNegRecordReviewedMap
              .get(ownerId);
          if (recordsReviewChanges == null) {
            recordsReviewChanges = new HashSet<Integer>();
            ownerChangeToNegRecordReviewedMap
                .put(ownerId, recordsReviewChanges);
          }
          recordsReviewChanges.add(id);
        }
        if (comment != null && !comment.isEmpty() && !comment.equals("")) {
        	System.out.println("comment" + comment);
          //comment += "\n\n comment left when reviewed";
          OccurrenceComments occurrenceComment = new OccurrenceComments(id,
              user.getId(), comment);
          occurrenceComment.setDateCommented(date);
          commentService.attachDirty(occurrenceComment);
        }
      }

    }

    if (!ownerChangeToNegRecordReviewedMap.isEmpty()) {
      log.info("sending email notification of reviewed changes to users");
      for (Integer userId : ownerChangeToNegRecordReviewedMap.keySet()) {
        User owner = userDb.findById(userId);
        try {
          EmailUtil.notifyUserReviewedChangeToNeg(owner,
              ownerChangeToNegRecordReviewedMap.get(userId));
        } catch (EmailException e) {
          log.error(
              "unable to send email reviewed changes notification email to user "
                  + user.getEmail(), e);
        }
      }
    }
    updateService.update();
    return count;
  }

  // /**
  // *
  // *
  // * @see org.rebioma.client.services.OccurrenceService#vet(java.lang.String,
  // * java.util.Set, boolean)
  // */
  // private String vet(String sessionId, Set<Integer> ids, boolean vetted) {
  // try {
  // List<Occurrence> occurrences = occurrenceDb.findById(ids);
  // for (Occurrence occurrence : occurrences) {
  // occurrence.setVetted(vetted);
  // occurrenceDb.attachDirty(occurrence);
  // }
  // return null;
  // } catch (Exception e) {
  // return "Unable to vet: " + ids.toString() + " " + e.toString();
  // }
  // }
}