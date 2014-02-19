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

import org.rebioma.client.OccurrenceCommentQuery;
import org.rebioma.client.OccurrenceQuery;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.OccurrenceComments;
import org.rebioma.client.bean.OccurrenceReview;
import org.rebioma.client.services.OccurrenceService.OccurrenceServiceException;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Asynchronous interface for {@link OccurrenceService}.
 */
public interface OccurrenceServiceAsync extends IsSerializable {

  /**
   * Deletes all {@link Occurrence} found by this query and belong to the given
   * user.
   * 
   * @param sessionId current user session id
   * @param query the query use to delete these {@link Occurrence}
   * @param cb AsynCallback use to process the result.
   * @return
   */
  public Request delete(String sessionId, OccurrenceQuery query,
      AsyncCallback<Integer> cb);

  /**
   * Deletes a set of {@link Occurrence} objects.
   * 
   * @param sessionId the session id
   * @param occurrences the occurrences tod delete
   */
  public Request delete(String sessionId, Set<Occurrence> occurrences,
      AsyncCallback<String> cb);

  /**
   * Deletes a set of {@link OccurrenceComments} objects.
   * 
   * @param sessionId the session id
   * @param comments the occurrence comments to be deleted
   */
  public Request deleteComments(String sessionId,
      Set<OccurrenceComments> comments, AsyncCallback<Integer> cb);

  /**
   * Asynchronously fetches a set of {@link OccurrenceComments} objects from the
   * server.
   * 
   * @param query the occurrence comment query
   * @param cb the async callback
   * @return the pending request
   */
  public Request fetch(OccurrenceCommentQuery query,
      AsyncCallback<OccurrenceCommentQuery> cb);

  /**
   * Asynchronously fetches a set of {@link Occurrence} objects from the server.
   * 
   * @param sessionId the session id
   * @param query the occurrence query
   * @param cb the async callback
   * @return the pending request
   */
  public Request fetch(String sessionId, OccurrenceQuery query,
      AsyncCallback<OccurrenceQuery> cb);

  /**
   * Asychronously returns the last time an occurrence was created, updated, or
   * deleted from the database.
   * 
   * @param cb
   * @return
   * @throws OccurrenceServiceException
   */
  public Request lastUpdateInMilliseconds(AsyncCallback<Long> cb);

  public Request update(String sessionId, OccurrenceQuery query,
      AsyncCallback<Integer> cb);

  /**
   * Asynchronously updates a set of {@link Occurrence} objects.
   * 
   * @param sessionId the session id
   * @param occurrences set of occurrences to update
   * @return error message or null if no error
   */
  public Request update(String sessionId, Set<Occurrence> occurrences,
	      AsyncCallback<String> cb);
  
  public Request update(String sessionId, Set<Occurrence> occurrences, boolean resetReview,
	      AsyncCallback<String> cb);

	  
  /**
   * Asynchronously updates a set of {@link OccurrenceComments} objects.
   * 
   * @param sessionId the session id
   * @param owner 
   * @param comments set of occurrence comment to be updated
   * @return number of record get updated
   */
  public Request updateComments(String sessionId,
      Integer owner, Set<OccurrenceComments> comments, boolean emailing, AsyncCallback<Integer> cb);

  void getMyReviewedOnRecords(String sid, Map<Integer, Integer> rowOccIdsMap,
      AsyncCallback<Map<Integer, Boolean>> callback);

  void getOccurrenceReviewsOf(Integer occId, AsyncCallback<List<OccurrenceReview>> callback);

  void reviewRecords(String sid, Boolean reviewed, OccurrenceQuery query,
      String comment, boolean notified, AsyncCallback<Integer> callback);
  
  void commentRecords(String sid, Set<Integer> occurrenceIds,
	      String comment, boolean notified, AsyncCallback<Integer> callback);
	  
  void reviewRecords(String sid, Boolean reviewed, Set<Integer> occurrenceIds,
      String comment, boolean notified, AsyncCallback<Integer> callback);

  void editUpdate(List<Occurrence> occurrences, String sessionId,
		  AsyncCallback<Boolean> callback);

}
