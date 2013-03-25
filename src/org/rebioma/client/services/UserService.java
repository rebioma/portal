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
import org.rebioma.client.EmailException;
import org.rebioma.client.UserExistedException;
import org.rebioma.client.UserQuery;
import org.rebioma.client.bean.Role;
import org.rebioma.client.bean.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * A service interface for user operations, such as OpenID login, registration,
 * settings, profile, getting the currently signed in user, etc.
 * 
 */
@RemoteServiceRelativePath("userService")
public interface UserService extends RemoteService {

  /**
   * Provides a proxy to a UserService. If running in hosted mode or web mode,
   * returns a stubbed version of UserService. This allows client development
   * without needing concrete server side implementations.
   * 
   */
  public static class Proxy {

    private static UserServiceAsync service;

    public static synchronized UserServiceAsync get() {
      if (service == null) {
        service = GWT.create(UserService.class);
      }
      return service;
    }
  }

  /**
   * Simple exception if an error occurs while waiting for RPC to return from
   * the server.
   * 
   * @author Tri
   * 
   */
  public static class UserServiceException extends Exception implements
      IsSerializable {

    public UserServiceException() {
      this(null);
    }

    public UserServiceException(String message) {
      super(message);
    }

  }

  public User addRoles(String sessionId, User user, List<Role> role)
      throws UserServiceException;

  /**
   * This method is used to change an existing password of the current user to a
   * new password
   * 
   * @param oldPass current user old password
   * @param newPass current user new password
   * @param sessionId current user sessionId
   * @param passChangeNotificationEmail TODO
   * @return 1 if password changed successfully, 0 if old password does not
   *         match, and -1 if there is an error occurs
   */
  public int changeUserPassword(String oldPass, String newPass,
      String sessionId, Email passChangeNotificationEmail);

  /**
   * Looks up users using session id and {@link UserQuery}
   * 
   * @param sessionId the session id of current logged in user.
   * @param query the {@link UserQuery} for this look up
   * @return {@link UserQuery} contains the result of the lookup.
   */
  public UserQuery fetchUser(String sessionId, UserQuery query)
      throws UserServiceException;

  /**
   * Get current available roles of the system
   * 
   * @return avaiable roles;
   */
  public List<Role> getAvailableRoles();

  /**
   * This method checks whether the given sessionId is valid
   * 
   * @param sessionId a session id in user database
   * @return true if there is such sessionId in database
   * @throws UserServiceException
   */
  public User isSessionIdValid(String sessionId) throws UserServiceException;

  /**
   * Register a new account for the user.
   * 
   * @param user a new User object uses for registration
   * @param welcomeEmail a multi-language welcome email
   * @throws EmailException TODO
   * @throws UserExistedException TODO
   */
  public void register(User user, Email welcomeEmail) throws EmailException,
      UserExistedException;

  public User removeRoles(String sessionId, User user, List<Role> role)
      throws UserServiceException;

  /**
   * Send a random password to the given email address
   * 
   * @param emailAddr an email address for new randomly generated password to be
   *          sent to
   * 
   * @param email a multi-language notification password recovery email
   */
  public void resetUserPassword(String emailAddr, Email email);

  /**
   * Signs in the user.
   * 
   * @param email
   * @param password
   * @return User object if email and password are correct, null otherwise
   */
  public User signIn(String email, String password);

  /**
   * 
   */
  public User signInC(String email, String password);
  /**
   * Signs out the user.
   * 
   * @param sessionId the user session id
   * 
   */
  public void signOut(String sessionId);

  /**
   * update logged in user information using session id and {@link UserQuery}
   * 
   * @param sessionId the session id of current logged in user.
   * @param query the {@link UserQuery} for this update.
   * @return the number of user's updated data.
   */
  public int update(String sessionId, UserQuery query)
      throws UserServiceException;

  /**
   * Update giving user information if newPas is null no password is changed.
   * @param userSessionId TODO
   * @param user the {@link User} will be updated.
   * @param newPass new password of the given user. Set to null if no wish to
   *          change password.
   * 
   * @return true if updated.
   */
  public boolean update(String userSessionId, User user, String newPass) throws UserServiceException;

  /**
   * This method checks if the given email is existed in the Rebioma database
   * 
   * @param email
   * @return true if user is already existed in the database, false otherwise
   */
  public boolean userEmailExists(String email);

}
