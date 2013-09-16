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
package org.rebioma.client.services;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rebioma.client.DataSwitch;
import org.rebioma.client.OccurrenceCommentQuery;
import org.rebioma.client.OccurrenceQuery;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.OccurrenceComments;
import org.rebioma.client.bean.OccurrenceReview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Service for fetching {@link Occurrence} objects from the server.
 * 
 */
@RemoteServiceRelativePath("occurrenceService")
public interface OccurrenceService extends RemoteService {

  /**
   * Custom exception for occurrence service errors.
   */
  @SuppressWarnings("serial")
  public class OccurrenceServiceException extends Exception implements
      IsSerializable {
    public OccurrenceServiceException() {
      this(null);
    }

    public OccurrenceServiceException(final String msg) {
      super(msg);
    }
  }

  /**
   * Provides a singleton proxy to {@link OccurrenceService}. Clients typically
   * use the proxy as follows:
   * 
   * OccurrenceService.Proxy.get();
   */
  public static class Proxy {

    /**
     * The singleton proxy instance.
     */
    private static OccurrenceServiceAsync service;

    /**
     * Returns the singleton proxy instance and creates it if needed.
     */
    public static synchronized OccurrenceServiceAsync get() {
      if (service == null) {
        service = GWT.create(OccurrenceService.class);
      }
      return service;
    }
  }

  /**
   * Deletes all {@link Occurrence} that match the given query.
   * 
   * @param sessionId the current user session id
   * @param query the query use to delete these {@link Occurrence}
   * @return number of records get deleted
   * @throws OccurrenceServiceException TODO
   */
  public int delete(String sessionId, OccurrenceQuery query)
      throws OccurrenceServiceException;

  /**
   * Deletes a set of {@link Occurrence} objects.
   * 
   * @param sessionId the session id
   * @param occurrences the occurrences tod delete
   */
  public String delete(String sessionId, Set<Occurrence> occurrences)
      throws OccurrenceServiceException;

  /**
   * Deletes all {@link OccurrenceComments} that match the given query.
   * 
   * @param sid the current user session id
   * @param comments the set of {@link OccurrenceComments} to be deleted
   * @return number of records get deleted
   * @throws OccurrenceServiceException
   */
  public Integer deleteComments(String sid, Set<OccurrenceComments> comments)
      throws OccurrenceServiceException;

  /**
   * This method queries the server for {@link OccurrenceComments} objects
   * according to specifications in the {@link OccurrenceCommentQuery} and
   * returns the same {@link OccurrenceCommentQuery} (that contains the original
   * query specification) along with the resulting set of
   * {@link OccurrenceComments} objects.
   * 
   * @param query the occurrence comment query
   * @return the occurrence comment query as results
   */
  public OccurrenceCommentQuery fetch(OccurrenceCommentQuery query)
      throws OccurrenceServiceException;

  /**
   * This method queries the server for {@link Occurrence} objects according to
   * specifications in the {@link OccurrenceQuery} and returns the same
   * {@link OccurrenceQuery} (that contains the original query specification)
   * along with the resulting set of {@link Occurrence} objects. It should fail
   * if the session id is invalid.
   * 
   * @param sessionId the session id
   * @param query the occurrence query
   * @return the occurrence query as results
   */
  public OccurrenceQuery fetch(String sessionId, OccurrenceQuery query)
      throws OccurrenceServiceException;

  /**
   * This method returns the last time an {@link Occurrence} was created,
   * updated, or deleted from the database. This is useful because the
   * {@link DataSwitch} cache should be cleared if updates have happened so that
   * users can have a sychronized view of {@link Occurrence} data.
   * 
   * @return the last occurrence create, update, or delete time
   * @throws OccurrenceServiceException
   */
  public Long lastUpdateInMilliseconds() throws OccurrenceServiceException;

  public int reviewRecords(String sid, Boolean reviewed, OccurrenceQuery query, String comment, boolean notified)
      throws OccurrenceServiceException;

  public int reviewRecords(String sid, Boolean reviewed,
      Set<Integer> occurrenceIds, String comment, boolean noified) throws OccurrenceServiceException;

  /**
   * Updates a set of {@link Occurrence} objects.
   * 
   * @param sessionId the session id
   * @param occurrences set of occurrences to update
   * @return error message or null if no error
   * @throws OccurrenceServiceException
   */
  public Integer update(String sessionId, OccurrenceQuery query)
      throws OccurrenceServiceException;

  /**
   * Updates a set of {@link Occurrence} objects.
   * 
   * @param sessionId the session id
   * @param occurrences set of occurrences to update
   * @return error message or null if no error
   * @throws OccurrenceServiceException
   */
  public String update(String sessionId, Set<Occurrence> occurrences)
      throws OccurrenceServiceException;

  int updateComments(String sessionId, Integer owner,
		Set<OccurrenceComments> comments, boolean emailing) throws OccurrenceServiceException;

  Map<Integer, Boolean> getMyReviewedOnRecords(String sid,
      Map<Integer, Integer> rowOccIdsMap) throws OccurrenceServiceException;

  List<OccurrenceReview> getOccurrenceReviewsOf(Integer occId)
      throws OccurrenceServiceException;
}
