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

import org.rebioma.client.Email;
import org.rebioma.client.UserQuery;
import org.rebioma.client.bean.Role;
import org.rebioma.client.bean.User;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Asynchronous interface for {@link UserService}.
 * 
 */
public interface UserServiceAsync extends IsSerializable {

  void addRoles(String sessionId, User user, List<Role> role,
      AsyncCallback<User> callback);

  Request changeUserPassword(String oldPass, String newPass, String sessionId,
      Email passChangeNotificationEmail, AsyncCallback<Integer> cb);

  Request fetchUser(String sessionId, UserQuery query,
      AsyncCallback<UserQuery> cb);

  void getAvailableRoles(AsyncCallback<List<Role>> callback);

  Request isSessionIdValid(String sessionId, AsyncCallback<User> cb);

  Request register(User user, Email welcomeEmail, AsyncCallback cb);

  void removeRoles(String sessionId, User user, List<Role> role,
      AsyncCallback<User> callback);

  Request resetUserPassword(String emailAddr, Email email, AsyncCallback cb);

  Request signIn(String email, String password, AsyncCallback<User> cb);

  Request signInC(String email, String password, AsyncCallback<User> cb);

  Request signOut(String sessionId, AsyncCallback cb);

  /**
   * Update giving user information if newPas is null no password is changed.
   * 
   * @param user the {@link User} will be updated.
   * @param newPass new password of the given user. Set to null if no wish to
   *          change password.
   * @param cb
   * @return
   */
  Request update(String userSessionId, User user, String newPass,
      AsyncCallback<Boolean> cb);

  Request update(String sessionId, UserQuery query, AsyncCallback<Integer> cb);

  Request userEmailExists(String email, AsyncCallback<Boolean> cb);

}
