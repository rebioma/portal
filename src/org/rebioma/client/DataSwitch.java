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
package org.rebioma.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rebioma.client.bean.AscData;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.OccurrenceComments;
import org.rebioma.client.bean.OccurrenceReview;
import org.rebioma.client.bean.Role;
import org.rebioma.client.bean.User;
import org.rebioma.client.services.AscDataService;
import org.rebioma.client.services.AscDataServiceAsync;
import org.rebioma.client.services.OccurrenceService;
import org.rebioma.client.services.OccurrenceServiceAsync;
import org.rebioma.client.services.RebiomaModelService;
import org.rebioma.client.services.RebiomaModelServiceAsync;
import org.rebioma.client.services.UserService;
import org.rebioma.client.services.UserServiceAsync;
import org.rebioma.client.services.OccurrenceService.OccurrenceServiceException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This class is used as a data switch between server, cache, and possibly
 * gears. Generally it provides the application with a level of data
 * indirection, which might be useful when re-factoring for Chrome.
 * 
 */
public class DataSwitch implements UserServiceAsync, OccurrenceServiceAsync,
    AscDataServiceAsync, RebiomaModelServiceAsync {

  public static final String COMMENT_KEY = "comment";

  public static final String OCCURRENCE_KEY = "occurrences";

  public static final String USER_KEY = "users";

  private static final String OCCURRENCE_REVIEWS_KEY = "getReviewOf_";

  private static DataSwitch dataSwitch = null;

  public static DataSwitch get() {
    if (dataSwitch == null) {
      dataSwitch = new DataSwitch();
    }
    return dataSwitch;
  }

  /**
   * Creates a cache key from an OccurrenceQuery.
   */
  private static String getOccurrenceQueryKey(OccurrenceQuery query) {
    StringBuilder sb = new StringBuilder();
    String SEPARATOR = ";";
    sb.append("start_" + query.getStart());
    sb.append(SEPARATOR);
    sb.append("limit_" + query.getLimit());
    sb.append(SEPARATOR);
    // sb.append("countTheResults_" + query.isCountTotalResults());
    // sb.append(SEPARATOR);
    // sb.append("count_" + query.getCount());
    // sb.append(SEPARATOR);
    if (query.getBaseFilters() != null) {
      for (String filter : query.getBaseFilters()) {
        sb.append("f_" + filter);
        sb.append(SEPARATOR);
      }
    }
    for (String filter : query.getSearchFilters()) {
      sb.append("f_" + filter);
      sb.append(SEPARATOR);
    }
    for (String filter : query.getDisjunctionSearchFilters()) {
	    sb.append("f_or" + filter);
	    sb.append(SEPARATOR);
	  }
    sb.append("rf_" + query.getResultFilter());
    sb.append(SEPARATOR);
    sb.append("pagesize_" + query.getLimit());
    if(query.getOccurrenceIdsFilter() != null && !query.getOccurrenceIdsFilter().isEmpty()){
    	sb.append(SEPARATOR).append("withdrawingfilters");
    }
    System.out.println(sb.toString());
    return sb.toString();
  }

  private static boolean sessionExpired() {
    String sessionId = Cookies.getCookie(ApplicationView.SESSION_ID_NAME);
    return sessionId == null || sessionId.equals("");
  }

  private List<AscData> ascDataList;

  private final Map<String, Object> cache = new HashMap<String, Object>();
  private final Map<String, Set<String>> cacheKeysMap = new HashMap<String, Set<String>>();

  private final OccurrenceServiceAsync occurrenceService = OccurrenceService.Proxy
      .get();

  private final UserServiceAsync userService = UserService.Proxy.get();
  private final RebiomaModelServiceAsync modelService = GWT
      .create(RebiomaModelService.class);

  private Long lastUpdateTime = null;
  private final List<ActivitiesListener> activitiesListeners = new ArrayList<ActivitiesListener>();

  private DataSwitch() {
    // Pings the server every 5 seconds and clears the cache if an update
    // occurred:

  }

  public void addActiviesListener(ActivitiesListener listener) {
    activitiesListeners.add(listener);
  }

  public void addRoles(String sessionId, User user, List<Role> role,
      AsyncCallback<User> callback) {
    userService.addRoles(sessionId, user, role, callback);

  }

  public Request changeUserPassword(String oldPass, String newPass,
      String sessionId, Email passChangeNotificationEmail,
      AsyncCallback<Integer> cb) {
    fireActivities();
    if (sessionExpired()) {
      cb.onFailure(new Exception("Session expired"));
      return null;
    } else {
      return userService.changeUserPassword(oldPass, newPass, sessionId,
          passChangeNotificationEmail, cb);
    }
  }

  /**
   * Clears the data cache.
   */
  public void clearCache() {
    GWT.log("Cache cleared", null);
    cache.clear();
    cacheKeysMap.clear();
  }

  /**
   * Clears all cache in an object key set.
   * 
   * @param objectKey
   */
  public void clearCache(String objectKey) {
    Set<String> cacheKeys = cacheKeysMap.get(objectKey);
    if (cacheKeys == null) {
      return;
    }
    for (String key : cacheKeysMap.get(objectKey)) {
      cache.remove(key);
    }
  }

  /**
   * Deletes all {@link Occurrence} found by the given query and belong to given
   * user, found by given session id.
   * 
   * @see org.rebioma.client.services.OccurrenceServiceAsync#delete(java.lang.String,
   *      org.rebioma.client.OccurrenceQuery,
   *      com.google.gwt.user.client.rpc.AsyncCallback)
   */
  public Request delete(String sessionId, OccurrenceQuery query,
      final AsyncCallback<Integer> cb) {
    fireActivities();
    return occurrenceService.delete(sessionId, query,
        new AsyncCallback<Integer>() {
          public void onFailure(Throwable caught) {
            cb.onFailure(caught);

          }

          public void onSuccess(Integer result) {
            if (result != null && result.intValue() != 0) {
              clearCache(OCCURRENCE_KEY);
            }
            cb.onSuccess(result);

          }

        });
  }

  /**
   * Dispatched to {@link OccurrenceServiceAsync} and clears the cache on
   * success.
   * 
   * @see org.rebioma.client.services.OccurrenceServiceAsync#delete(java.lang.String,
   *      java.util.Set, com.google.gwt.user.client.rpc.AsyncCallback)
   */
  public Request delete(String sessionId, Set<Occurrence> occurrences,
      final AsyncCallback<String> cb) {
    fireActivities();
    return occurrenceService.delete(sessionId, occurrences,
        new AsyncCallback<String>() {
          public void onFailure(Throwable caught) {
            cb.onFailure(caught);
          }

          public void onSuccess(String result) {
            clearCache(OCCURRENCE_KEY);
            cb.onSuccess(result);
          }
        });
  }

  public Request deleteComments(String sessionId,
      Set<OccurrenceComments> comments, final AsyncCallback<Integer> cb) {
    fireActivities();
    return occurrenceService.deleteComments(sessionId, comments,
        new AsyncCallback<Integer>() {
          public void onFailure(Throwable caught) {
            cb.onFailure(caught);
          }

          public void onSuccess(Integer result) {
            if (result != null && result > 0) {
              clearCache(COMMENT_KEY);
            }
            cb.onSuccess(result);
          }

        });
  }

  public Request fetch(final OccurrenceCommentQuery query,
      final AsyncCallback<OccurrenceCommentQuery> cb) {
    fireActivities();
    return lastUpdateInMilliseconds(new AsyncCallback<Long>() {
      public void onFailure(Throwable caught) {
        GWT.log("Update timer error", caught);
      }

      public void onSuccess(Long result) {
        if ((lastUpdateTime != null) && (lastUpdateTime < result)) {
          clearCache(OCCURRENCE_KEY);
          clearCache(COMMENT_KEY);
          clearCache(OCCURRENCE_REVIEWS_KEY);
        }
        final String commentQueryKey = addKey(COMMENT_KEY, query.toString());
        OccurrenceCommentQuery cachResult = (OccurrenceCommentQuery) cache
            .get(commentQueryKey);
        if (cachResult != null) {
          cb.onSuccess(cachResult);
          return;
        }
        occurrenceService.fetch(query,
            new AsyncCallback<OccurrenceCommentQuery>() {
              public void onFailure(Throwable caught) {
                cb.onFailure(caught);

              }

              public void onSuccess(OccurrenceCommentQuery result) {
                cache.put(commentQueryKey, result);
                cb.onSuccess(result);

              }

            });
        lastUpdateTime = result;
      }
    });

  }

  /**
   * Fetches {@link Occurrence} objects from the server that are specified by
   * the {@link OccurrenceQuery}. Before requesting from the server, checks the
   * cache first. If the cache misses, it is updated on a successful call to the
   * server.
   * 
   * @see org.rebioma.client.services.OccurrenceService#fetch(java.lang.String,
   *      org.rebioma.client.OccurrenceQuery)
   */
  public Request fetch(final String sessionId, final OccurrenceQuery query,
      final AsyncCallback<OccurrenceQuery> cb) {
    fireActivities();
    return lastUpdateInMilliseconds(new AsyncCallback<Long>() {
      public void onFailure(Throwable caught) {
        GWT.log("Update timer error", caught);
      }

      public void onSuccess(Long result) {
        if ((lastUpdateTime != null) && (lastUpdateTime < result)) {
          clearCache(OCCURRENCE_KEY);
          clearCache(COMMENT_KEY);
          clearCache(OCCURRENCE_REVIEWS_KEY);
        }
        lastUpdateTime = result;
        final String cacheKey = addKey(OCCURRENCE_KEY,
            getOccurrenceQueryKey(query));
        GWT.log(cacheKey, null);
        //on ne charge pas la cache quand on a des filtres sur les id des occurrences(depuis le mapDrawingControl)
        if(query.getOccurrenceIdsFilter() == null || query.getOccurrenceIdsFilter().isEmpty()){
        	if (cache.containsKey(cacheKey)) {
                OccurrenceQuery cachedQuery = (OccurrenceQuery) cache.get(cacheKey);
                if (cachedQuery.getCount() != -1) {
                  cb.onSuccess(cachedQuery);
                  return;
                }
              }
        }
        query.setCountTotalResults(true);
        Request request = OccurrenceService.Proxy.get().fetch(sessionId, query,
            new AsyncCallback<OccurrenceQuery>() {

              public void onFailure(Throwable caught) {
                try {
                  throw caught;
                } catch (OccurrenceServiceException e) {
                  cb.onFailure(e);
                } catch (Throwable t) {
                  cb.onFailure(t);
                }
              }

              public void onSuccess(OccurrenceQuery result) {
                cache.put(cacheKey, result);
                cb.onSuccess(result);
              }
            });
      }
    });

  }

  public Request fetchUser(String sessionId, UserQuery query,
      final AsyncCallback<UserQuery> cb) {
    fireActivities();
    final String cacheKey = addKey(USER_KEY, query.toString());
    UserQuery userQuery = (UserQuery) cache.get(cacheKey);
    if (userQuery != null) {
      cb.onSuccess(userQuery);
      return null;
    }
    return UserService.Proxy.get().fetchUser(sessionId, query,
        new AsyncCallback<UserQuery>() {
          public void onFailure(Throwable caught) {
            cb.onFailure(caught);
          }

          public void onSuccess(UserQuery result) {
            if (result == null) {
              onFailure(new Exception("result is null"));
            } else {
              cache.put(cacheKey, result);
              cb.onSuccess(result);
            }

          }

        });
  }

  public void findModelLocation(String acceptedSpecies, int start, int limit,
      AsyncCallback<AscModelResult> callback) {
    modelService.findModelLocation(acceptedSpecies, start, limit, callback);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.rebioma.client.services.AscDataServiceAsync#getAscData(com.google.gwt
   * .user.client.rpc.AsyncCallback)
   */
  public Request getAscData(final AsyncCallback<List<AscData>> asyncCallback) {
    fireActivities();
    if (ascDataList != null) {
      asyncCallback.onSuccess(ascDataList);
      return null;
    }
    return AscDataService.Proxy.get().getAscData(
        new AsyncCallback<List<AscData>>() {
          public void onFailure(Throwable caught) {
            asyncCallback.onFailure(caught);
          }

          public void onSuccess(List<AscData> result) {
            ascDataList = result;
            asyncCallback.onSuccess(result);
          }
        });
  }

  public Request getAscDataMap(AsyncCallback<Map<String, AscData>> cb) {
    fireActivities();
    return AscDataService.Proxy.get().getAscDataMap(cb);
  }

  public void getAvailableRoles(final AsyncCallback<List<Role>> callback) {
    fireActivities();
    final String cacheCkey = "getRoles";
    if (cache.containsKey(cacheCkey)) {
      callback.onSuccess((List<Role>) cache.get(cacheCkey));
    } else {
      UserService.Proxy.get().getAvailableRoles(
          new AsyncCallback<List<Role>>() {

            public void onFailure(Throwable t) {
              callback.onFailure(t);
            }

            public void onSuccess(List<Role> result) {
              cache.put(cacheCkey, result);
              callback.onSuccess(result);
            }

          });
    }
  }

  public void getModelClimateEras(String modelLocation,
      AsyncCallback<List<String>> callback) {
    modelService.getModelClimateEras(modelLocation, callback);
  }

  public void getMyReviewedOnRecords(String sid,
      Map<Integer, Integer> rowOccIdsMap,
      AsyncCallback<Map<Integer, Boolean>> callback) {
    fireActivities();
    occurrenceService.getMyReviewedOnRecords(sid, rowOccIdsMap, callback);
  }

  public void getOccurrenceReviewsOf(Integer occId,
      final AsyncCallback<List<OccurrenceReview>> callback) {
    fireActivities();
    final String cacheKey = addKey(OCCURRENCE_REVIEWS_KEY, occId.toString());
    if (cache.containsKey(cacheKey)) {
      callback.onSuccess((List<OccurrenceReview>) cache.get(cacheKey));
    } else {
      occurrenceService.getOccurrenceReviewsOf(occId,
          new AsyncCallback<List<OccurrenceReview>>() {

            public void onFailure(Throwable caught) {
              callback.onFailure(caught);

            }

            public void onSuccess(List<OccurrenceReview> result) {
              cache.put(cacheKey, result);
              callback.onSuccess(result);

            }

          });
    }
  }

  public Request getValue(int ascDataId, double lat, double lng,
      final AsyncCallback<Double> cb) {
    fireActivities();
    final String cacheKey = "getValue_" + ascDataId + "_" + lat + "_" + lng;
    if (cache.containsKey(cacheKey)) {
      cb.onSuccess((Double) cache.get(cacheKey));
      return null;
    }
    return AscDataService.Proxy.get().getValue(ascDataId, lat, lng,
        new AsyncCallback<Double>() {
          public void onFailure(Throwable caught) {
            cb.onFailure(caught);
          }

          public void onSuccess(Double result) {
            cache.put(cacheKey, result);
            cb.onSuccess(result);
          }
        });
  }

  /**
   * Dispatches to {@link UserServiceAsync}.
   */
  public Request isSessionIdValid(String sessionId, AsyncCallback<User> cb) {
    return userService.isSessionIdValid(sessionId, cb);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.rebioma.client.services.OccurrenceServiceAsync#lastUpdateInMilliseconds
   * (com.google.gwt.user.client.rpc.AsyncCallback)
   */
  public Request lastUpdateInMilliseconds(AsyncCallback<Long> cb) {
    return occurrenceService.lastUpdateInMilliseconds(cb);
  }

  public Request loadAscData(final Occurrence occurrence,
      final AsyncCallback<Occurrence> cb) {
    return AscDataService.Proxy.get().loadAscData(occurrence,
        new AsyncCallback<Occurrence>() {

          public void onFailure(Throwable caught) {
            cb.onFailure(caught);

          }

          public void onSuccess(Occurrence result) {
            occurrence.setEtpTotal1950(result.getEtpTotal1950());
            occurrence.setEtpTotal2000(result.getEtpTotal2000());
            occurrence.setEtpTotalfuture(result.getEtpTotalfuture());

            occurrence.setMaxPerc1950(result.getMaxPerc1950());
            occurrence.setMaxPerc2000(result.getMaxPerc2000());
            occurrence.setMaxPercfuture(result.getMaxPercfuture());

            occurrence.setMaxtemp1950(result.getMaxtemp1950());
            occurrence.setMaxTemp2000(result.getMaxTemp2000());
            occurrence.setMaxTempfuture(result.getMaxTempfuture());

            occurrence.setMinPerc1950(result.getMinPerc1950());
            occurrence.setMinPerc2000(result.getMinPerc2000());
            occurrence.setMinPercfuture(result.getMinPercfuture());

            occurrence.setMinTemp1950(result.getMinTemp1950());
            occurrence.setMinTemp2000(result.getMinTemp2000());
            occurrence.setMinTemp2000(result.getMinTempfuture());

            occurrence.setPfc1950(result.getPfc1950());
            occurrence.setPfc1970(result.getPfc1970());
            occurrence.setPfc1990(result.getPfc1990());
            occurrence.setPfc2000(result.getPfc2000());

            occurrence.setRealMar1950(result.getRealMar1950());
            occurrence.setRealMar2000(result.getRealMar2000());
            occurrence.setRealMarfuture(result.getRealMarfuture());

            occurrence.setRealMat1950(result.getRealMat1950());
            occurrence.setRealMat2000(result.getRealMat2000());
            occurrence.setRealMatfuture(result.getRealMatfuture());

            occurrence.setWbpos1950(result.getWbpos1950());
            occurrence.setWbpos2000(result.getWbpos2000());
            occurrence.setWbposfuture(result.getWbposfuture());

            occurrence.setWbyear1950(result.getWbyear1950());
            occurrence.setWbyear2000(result.getWbyear2000());
            occurrence.setWbyearfuture(result.getWbyearfuture());

            occurrence.setGeolStrech(result.getGeolStrech());
            occurrence.setDemelevation(result.getDemelevation());

            cb.onSuccess(result);
          }

        });
  }

  public Request register(User user, Email welcomeEmail, AsyncCallback cb) {
    return userService.register(user, welcomeEmail, cb);
  }

  public void removeActiviesListener(ActivitiesListener listener) {
    activitiesListeners.remove(listener);
  }

  public void removeRoles(String sessionId, User user, List<Role> role,
      AsyncCallback<User> callback) {
    userService.removeRoles(sessionId, user, role, callback);

  }

  /**
   * 
   * @see org.rebioma.client.services.UserServiceAsync#resetPassword(java.lang.String
   *      , org.rebioma.client.Email,
   *      com.google.gwt.user.client.rpc.AsyncCallback)
   */
  public Request resetUserPassword(String emailAddr, Email email,
      AsyncCallback cb) {
    return userService.resetUserPassword(emailAddr, email, cb);
  }

  public void reviewRecords(String sid, Boolean reviewed,
      OccurrenceQuery query, String comment, boolean notified, AsyncCallback<Integer> callback) {
    fireActivities();
    occurrenceService.reviewRecords(sid, reviewed, query, comment, notified, callback);

  }

  public void commentRecords(String sid, Set<Integer> occurrenceIds, 
		  String comment, boolean notified, AsyncCallback<Integer> callback) {
	  fireActivities();
	  occurrenceService.commentRecords(sid, occurrenceIds, comment, notified, callback);

  }

  public void reviewRecords(String sid, Boolean reviewed,
      Set<Integer> occurrenceIds, String comment, boolean notified,
      final AsyncCallback<Integer> callback) {
    fireActivities();
    occurrenceService.reviewRecords(sid, reviewed, occurrenceIds, comment, notified,
        callback);

  }

  /**
   * Dispatches to {@link UserServiceAsync}
   * 
   * @see org.rebioma.client.services.UserServiceAsync#login(java.lang.String,
   *      java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
   */
  public Request signIn(String email, String password,
      final AsyncCallback<User> cb) {
    fireActivities();
    return userService.signIn(email, password, cb);
  }
  /**
   * 
   */
  public Request signInC(String email, String password,
		  final AsyncCallback<User> cb) {
	  fireActivities();
	  return userService.signInC(email, password, cb);
  }
  /**
   * Dispatches to {@link UserServiceAsync}.
   * 
   * @see org.rebioma.client.services.UserServiceAsync#signOut(com.google.gwt.user
   *      .client.rpc.AsyncCallback)
   */
  public Request signOut(String sessionId, AsyncCallback cb) {
    return userService.signOut(sessionId, cb);
  }

  /**
   * Updates occurrences by given query and session id.
   * 
   * @see org.rebioma.client.services.OccurrenceServiceAsync#update(java.lang.String,
   *      org.rebioma.client.OccurrenceQuery,
   *      com.google.gwt.user.client.rpc.AsyncCallback)
   */
  public Request update(String sessionId, OccurrenceQuery query,
      final AsyncCallback<Integer> cb) {
    fireActivities();
    return occurrenceService.update(sessionId, query,
        new AsyncCallback<Integer>() {
          public void onFailure(Throwable caught) {
            cb.onFailure(caught);
          }

          public void onSuccess(Integer result) {
            if (result != null && result.intValue() != 0) {
              clearCache(OCCURRENCE_KEY);
            }
            cb.onSuccess(result);
          }
        });
  }

  /**
   * Dispatches to {@link OccurrenceServiceAsync}. If successful, clears the
   * data cache.
   * 
   * @see org.rebioma.client.services.OccurrenceServiceAsync#update(java.lang.String,
   *      java.util.Set, com.google.gwt.user.client.rpc.AsyncCallback)
   */
  public Request update(String sessionId, Set<Occurrence> occurrences,
      final AsyncCallback<String> cb) {
    fireActivities();
    return occurrenceService.update(sessionId, occurrences,
        new AsyncCallback<String>() {
          public void onFailure(Throwable caught) {
            cb.onFailure(caught);
          }

          public void onSuccess(String result) {
            clearCache(OCCURRENCE_KEY);
            cb.onSuccess(result);
          }
        });
  }
  
  /**
   * 
   * @param sessionId
   * @param occurrences
   * @param resetReview
   * @param cb
   * @return
   */
  public Request update(String sessionId, Set<Occurrence> occurrences, boolean resetReview,
	      final AsyncCallback<String> cb) {
	    fireActivities();
	    return occurrenceService.update(sessionId, occurrences, resetReview,
	        new AsyncCallback<String>() {
	          public void onFailure(Throwable caught) {
	            cb.onFailure(caught);
	          }

	          public void onSuccess(String result) {
	            clearCache(OCCURRENCE_KEY);
	            cb.onSuccess(result);
	          }
	        });
	  }

  public Request update(String userSessionId, User user, String newPass,
      AsyncCallback<Boolean> cb) {
    return userService.update(userSessionId, user, newPass, cb);
  }

  public Request update(String sessionId, UserQuery query,
      final AsyncCallback<Integer> cb) {
    fireActivities();
    return UserService.Proxy.get().update(sessionId, query,
        new AsyncCallback<Integer>() {

          public void onFailure(Throwable caught) {
            cb.onFailure(caught);
          }

          public void onSuccess(Integer result) {
            if (result != null && result > 0) {
              clearCache(USER_KEY);
            }
            cb.onSuccess(result);

          }

        });
  }

  public Request updateComments(String sessionId,
      Integer owner, Set<OccurrenceComments> comments, boolean emailing, final AsyncCallback<Integer> cb) {
    fireActivities();
    return occurrenceService.updateComments(sessionId, owner, comments, emailing, 
        new AsyncCallback<Integer>() {
          public void onFailure(Throwable caught) {
            cb.onFailure(caught);
          }

          public void onSuccess(Integer result) {
            if (result != null && result > 0) {
              clearCache(COMMENT_KEY);
            }
            cb.onSuccess(result);
          }
        });
  }

  /**
   * (non-Javadoc)
   * 
   * @see org.rebioma.client.services.UserServiceAsync#userEmailExists(java.lang.
   *      String, com.google.gwt.user.client.rpc.AsyncCallback)
   */
  public Request userEmailExists(String email, AsyncCallback<Boolean> cb) {
    // TODO Auto-generated method stub
    return userService.userEmailExists(email, cb);
  }

  private String addKey(String keyCache, String keyCacheValue) {
    if (!cacheKeysMap.containsKey(keyCache)) {
      cacheKeysMap.put(keyCache, new HashSet<String>());
    }
    String cacheKey = keyCache + "_" + keyCacheValue;
    cacheKeysMap.get(keyCache).add(cacheKey);
    return cacheKey;
  }

  private void fireActivities() {
    for (ActivitiesListener listener : activitiesListeners) {
      listener.onDoSomething();
    }
  }

  private void updateCache(Long result) {
    if (lastUpdateTime == null) {
      lastUpdateTime = result;
    }
  }

  public void editUpdate(List<Occurrence> occurrences, String sessionId,
		AsyncCallback<Boolean> callback) {
	  occurrenceService.editUpdate(occurrences, sessionId, callback);
  }
}
