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

import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.rebioma.client.UserQuery;
import org.rebioma.client.bean.Role;
import org.rebioma.client.bean.User;

/**
 * Service interface for data object access of {@link User}.
 * 
 */
// @ImplementedBy(UserDbImpl.class)
public interface UserDb {
  /**
   * Gives a user a Role
   * 
   * @param user the user to be assigned with the given role
   * @param role
   */
  public void addRole(User user, Role role);

  /**
   * Attaches a clean instance of {@link User} to the database.
   * 
   * @param instance an User
   */
  public void attachClean(User instance);

  /**
   * Attaches a dirty set of {@link User} objects to the database.
   * 
   * @param instances the set of Users
   */
  public void attachDirty(Set<User> instances);

  /**
   * Attaches a dirty instance of {@link User} to the database.
   * 
   * @param instance the User
   */
  public void attachDirty(User instance);

  /**
   * Deletes a persistent instance of {@link User} from the database.
   * 
   * @param persistentInstances the User
   */
  public void delete(User persistentInstance);

  public List<User> findByEmail(Set<String> userEmails);

  public User findByEmail(String userEmails);

  /**
   * Finds a list of {@link User} by using instances of {@link User} in the
   * database.
   * 
   * @param instances the example Users
   * @return a list of Users
   */
  public List<User> findByExample(Set<User> instances);

  /**
   * Finds an {@link User} by using an instance of {@link User} in the database.
   * 
   * @param instance the example User
   * @return an User
   */
  public List<User> findByExample(User instance);

  /**
   * Finds an {@link User} by id.
   * 
   * @param id the User id
   * @return an User
   */
  public User findById(java.lang.Integer id);

  public User findById(Session session,java.lang.Integer id);
  
  /**
   * Finds all {@link User} by id.
   * 
   * @param ids the User ids
   * @return a list of Users
   */
  public List<User> findById(Set<Integer> ids);

  public UserQuery findByQuery(UserQuery query, Integer loggedInUserId) throws Exception;

  /**
   * Merges changes in a detached instance of {@link User}.
   * 
   * @param detachedInstance the User
   * @return the merged User
   */
  public User merge(User detachedInstance);

  /**
   * Persists a transient instance of {@link User}.
   * 
   * @param transientInstance the User
   */
  public void persist(User transientInstance);

  /**
   * Removes the given role form the user
   * 
   * @param user
   * @param role
   */
  public void removeRole(User user, Role role);

  /**
   * Remove a user along with all its associated roles
   * 
   * @param user
   * @return
   */
  public boolean removeUser(User user);

}
