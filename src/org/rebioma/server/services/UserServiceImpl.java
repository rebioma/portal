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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.htmlunit.corejs.javascript.tools.debugger.Main;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.rebioma.client.Email;
import org.rebioma.client.EmailException;
import org.rebioma.client.OccurrenceQuery;
import org.rebioma.client.UserExistedException;
import org.rebioma.client.UserQuery;
import org.rebioma.client.UserQuery.CollaboratorsUpdate;
import org.rebioma.client.bean.Role;
import org.rebioma.client.bean.User;
import org.rebioma.client.bean.UserRole;
import org.rebioma.client.services.UserService;
import org.rebioma.server.util.EmailUtil;
import org.rebioma.server.util.ManagedSession;
import org.rebioma.server.util.RandomUtil;

import BCrypt.BCrypt;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * This class implements UserServer for all user related Async calls
 * 
 */
public class UserServiceImpl extends RemoteServiceServlet implements
    UserService {

  public static final String USER_ID_COOKIE_NAME = "userId";
  public static final String USER_OPEN_ID_COOKIE_NAME = "userOpenId";

  private static final long serialVersionUID = -2164756501352824143L;

  public static void main(String args[]) {
    Set<Integer> integer = new HashSet<Integer>();
    integer.add(1);
    integer.add(2);
    Set<Integer> integer2 = new HashSet<Integer>();
    integer2.add(2);
    integer2.add(4);
    integer.retainAll(integer2);
    System.out.println(integer);
    System.out.println(integer2);
    // new UserServiceImpl().addFriends("5fqzplbwotptuxobylvf", integer);
    // new UserServiceImpl().changeUserPassword("mszfmrasyrtkrhwhisqs", "test",
    // "32EE8DEA4538A398BD71F36C2FECA4A2", null);
  }

  SessionIdService sessionService = DBFactory.getSessionIdService();

  UserDb userDb = DBFactory.getUserDb();

  CollaboratorsDb collaboratorsDb = DBFactory.getCollaboratorsDb();

  OccurrenceDb occurrenceDb = DBFactory.getOccurrenceDb();

  RoleDb roleDb = DBFactory.getRoleDb();

  private final Logger log = Logger.getLogger(UserServiceImpl.class);

  public UserServiceImpl() {
    super();
  }

  public User addRoles(String sessionId, User user, List<Role> roles)
      throws UserServiceException {
    try {
      Session session = ManagedSession.createNewSessionAndTransaction();User sessionUser = sessionService.getUserBySessionId(sessionId);
      Set<Role> sessionUserRole = roleDb.getRoles(sessionUser.getId());
      if (sessionUser != null) {
        Role adminRole = roleDb.getRole(UserRole.ADMIN);
        if (sessionUserRole.contains(adminRole)) {
          for (Role role : roles) {
            userDb.addRole(user, role);
          }
        } else {
          ManagedSession.commitTransaction(session);
          throw new UserServiceException("user " + sessionUser.getId()
              + " is not an admin. Only admin can addRoles to an user");
        }
        // if(sessionUserRoles.contains(arg0))
        ManagedSession.commitTransaction(session);
      } else {
        ManagedSession.commitTransaction(session);
        throw new UserServiceException("Bad or corrupted session id");
      }
      user.setRoles(roleDb.getRoles(user.getId())); // this to convert
      // PersistentSet to
      // HashSet because GWT
      // can't serialize
      // PersistentSet
      return user;
    } catch (HibernateException de) {
      log.error("Error while adding role to " + user.getId() + ": "
          + de.getMessage(), de);
    } catch (RuntimeException re) {
      log.error("Error while adding role to " + user.getId() + ": "
          + re.getMessage(), re);
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * This method looks up the user infomation by sessionId. If the sessionId is
   * existed, it checks the old password with the stored password. If match, it
   * updates the user's password with the new password.
   * 
   * @see
   * org.rebioma.client.services.UserService#changeUserPassword(java.lang.String
   * , java.lang.String)
   */
  public int changeUserPassword(String oldPass, String newPass,
      String sessionId, Email email) {
    Session session = ManagedSession.createNewSessionAndTransaction();
    int passwordReturnStatus = -1;
    try {
      User user = sessionService.getUserBySessionId(sessionId);
      if (BCrypt.checkpw(oldPass, user.getPasswordHash())) {
        user.setPasswordHash(BCrypt.hashpw(newPass, BCrypt.gensalt()));
        session.update(user);
        email.setUserFirstName(user.getFirstName());
        email.buildBody();
        EmailUtil.adminSendEmailTo(user.getEmail(), email.getSubject(), email
            .toString());
        ManagedSession.commitTransaction(session);

        passwordReturnStatus = 1;
        log.info(user.getFirstName() + " with email address " + user.getEmail()
            + " has successfully changed her/his password.");
      } else {
        passwordReturnStatus = 0;
      }
    } catch (HibernateException e) {
      ManagedSession.rollbackTransaction(session);
      log.error("Error: There is a problem during changing password", e);
      e.printStackTrace();
    } catch (EmailException e) {
      log
          .error(
              "Error: There is a problem during sending change password notification email",
              e);
      e.printStackTrace();
      ManagedSession.rollbackTransaction(session);
    } finally {
      // session.close();
    }
    return passwordReturnStatus;
  }

  public UserQuery fetchUser(String sessionId, UserQuery query)
      throws UserServiceException {
    try {
      Session session = ManagedSession.createNewSessionAndTransaction();System.out.println("session calling fetchUser is " + session.isOpen());
      User user = sessionService.getUserBySessionId(sessionId);
      if (query.isUsersCollaboratorsOnly()) {
        query = getFriends(sessionId, query);
        // query.setResults(new ArrayList<User>(getFriends(sessionId, query)));
        // if (query.isCountTotalResults()) {
        // query.setCount(query.getResults().size());
        // } else {
        // query.setCount(-1);
        // }
      } else {
        query = userDb.findByQuery(query, user.getId());
        for (User u : query.getResults()) {
          u.setRoles(roleDb.getRoles(u.getId()));
        }
      }
      ManagedSession.commitTransaction(session);
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage(), e);
      throw new UserServiceException(e.getMessage());
    }
    return query;
  }

  public List<Role> getAvailableRoles() {
    return roleDb.getAllRoles();
  }

  /**
   * (non-Javadoc)
   * 
   * This method looks up the given sessionId. If there is no such sessionId,
   * then this sessionId is not valid (i.e return false).
   * 
   * @throws UserServiceException
   * 
   * @see org.rebioma.client.services.UserService#isSessionIdValid(java.lang.String)
   */
  public User isSessionIdValid(String sessionId) throws UserServiceException {
    Session session = ManagedSession.createNewSessionAndTransaction();
    try {
      User user = sessionService.getUserBySessionId(sessionId);
      if (user != null) {
        user.setRoles(roleDb.getRoles(user.getId()));
      }
      ManagedSession.commitTransaction(session);

      return user;
    } catch (Exception e) {
      e.printStackTrace();
      log.error(e.getMessage(), e);
      ManagedSession.rollbackTransaction(session);
      throw new UserServiceException(e.getMessage());
    }
  }

  /**
   * This method creates a random password and store given user with the random
   * password hash to the database. It then sends a welcome email to the user's
   * email address.
   * 
   * @see org.rebioma.client.services.UserService#register(org.rebioma.client.bean.User,
   *      Email)
   */
  public void register(User user, Email welcomeEmail) throws EmailException,
      UserExistedException {
    Session session = ManagedSession.createNewSessionAndTransaction();
    try {
      boolean isUserExists = userEmailExists(user.getEmail());
      if (!isUserExists) {
        Role researcherRole = roleDb.getRole(UserRole.RESEARCHER);
        String pass = RandomUtil.generateRandomPassword();
        String passwordHash = BCrypt.hashpw(pass, BCrypt.gensalt());
        user.setPasswordHash(passwordHash);
        session.save(user);
        welcomeEmail.setUserPassword(pass);
        welcomeEmail.buildBody();
        System.out.println(pass);
        EmailUtil.adminSendEmailTo(user.getEmail(), welcomeEmail.getSubject(),
            welcomeEmail.toString());
        ManagedSession.commitTransaction(session);
        log.info("user " + user.getFirstName() + " with email "
            + user.getEmail() + " is created, and an Welcome email is sent to "
            + user.getEmail());
        userDb.addRole(user, researcherRole);

      } else {
        ManagedSession.commitTransaction(session);
        throw new UserExistedException(user.getEmail());
      }
    } catch (HibernateException e) {
      ManagedSession.rollbackTransaction(session);
      log.error("Error: can't save user info", e);
      e.printStackTrace();
    } finally {
      // session.close();
    }
  }

  public User removeRoles(String sessionId, User user, List<Role> roles)
      throws UserServiceException {
    try {
      Session session = ManagedSession.createNewSessionAndTransaction();User sessionUser = sessionService.getUserBySessionId(sessionId);
      Set<Role> sessionUserRoles = roleDb.getRoles(sessionUser.getId());
      if (sessionUser != null) {
        Role adminRole = roleDb.getRole(UserRole.ADMIN);
        if (sessionUserRoles.contains(adminRole)) {
          for (Role role : roles) {
            if (user.getRoles().size() > 1) {
              userDb.removeRole(user, role);
            }
          }
        } else {
          ManagedSession.commitTransaction(session);
          throw new UserServiceException("user " + sessionUser.getId()
              + " is not an admin. Only admin can addRoles to an user");
        }
        // if(sessionUserRoles.contains(arg0))
        ManagedSession.commitTransaction(session);
      } else {
        ManagedSession.commitTransaction(session);
        throw new UserServiceException("Bad or corrupted session id");
      }
      user.setRoles(roleDb.getRoles(user.getId())); // this to convert
      // PersistentSet to
      // HashSet because GWT
      // can't serialize
      // PersistentSet
      return user;
    } catch (HibernateException de) {
      log.error("Error while adding role to " + user.getId() + ": "
          + de.getMessage(), de);
    } catch (RuntimeException re) {
      log.error("Error while adding role to " + user.getId() + ": "
          + re.getMessage(), re);
    }
    return null;
  }

  /**
   * This method looks up the user information by the given email address. If
   * there is such user, it randomly generates a password and send it to user's.
   * email.
   * 
   * @see org.rebioma.client.services.UserService#resetPassword(java.lang.String,
   *      org.rebioma.client.Email)
   */
  public void resetUserPassword(String emailAddr, Email email) {
    Session session = ManagedSession.createNewSessionAndTransaction();
    try {
      // query.setString("pass", hashPass);
      User user = userDb.findByEmail(emailAddr);
      if (user != null) {
        String pass = RandomUtil.generateRandomPassword();
        String passwordHash = BCrypt.hashpw(pass, BCrypt.gensalt());
        user.setPasswordHash(passwordHash);
        session.update(user);
        email.setUserPassword(pass);
        email.setUserFirstName(user.getFirstName());
        email.buildBody();
        ManagedSession.commitTransaction(session);
        System.out.println("password: " + pass);
        EmailUtil.adminSendEmailTo(emailAddr, email.getSubject(), email
            .toString());
        log.info(user.getFirstName() + " with email address " + emailAddr
            + " has reset password.");
      }

    } catch (HibernateException e) {
      ManagedSession.rollbackTransaction(session);
      log.error("Error: can't reset password", e);
      e.printStackTrace();
    } catch (EmailException e) {
      ManagedSession.rollbackTransaction(session);
      log.error("admin is unabled to send welcome email to " + emailAddr, e);
      e.printStackTrace();
    } finally {
      // session.close();
    }

  }

  /**
   * This method looks up the user information by email address. If there is
   * such user, it checks if the given password and the stored password. If they
   * match, return the User object. If they do not match, return null.
   * 
   * @see org.rebioma.client.services.UserService#signIn(java.lang.String,
   *      java.lang.String)
   */
  public User signIn(String email, String password) {
    Session session = ManagedSession.createNewSessionAndTransaction();
    User user = null;
    try {
      user = (User) session.createCriteria(User.class).add(
          Restrictions.eq("email", email)).uniqueResult();
      if (user != null) {
        if (BCrypt.checkpw(password, user.getPasswordHash())) {
          String sessionId = RandomUtil.generateSessionId();

          // TODO: Check for duplicate session id.

          user.setSessionId(sessionId);
          session.update(user);
          ManagedSession.commitTransaction(session);
          user.setRoles(roleDb.getRoles(user.getId())); // this to convert
        } else {
          user = new User();
        }

      } else {
        user = new User();
      }
    } catch (HibernateException e) {
      ManagedSession.rollbackTransaction(session);
      log.error("Error: There is a problem during sign in", e);
      e.printStackTrace();
    } catch (Exception e) {
      ManagedSession.rollbackTransaction(session);
      log.error("Error: There is a problem during sign in", e);
      e.printStackTrace();
    } finally {
      // session.close();
    }

    return user;
  }

  /**
   * 
   */
  public User signInC(String email, String password) {
	    Session session = ManagedSession.createNewSessionAndTransaction();
	    User user = null;
	    try {

	      // String userLookupQuery = "from User as u where u.email = :email";
	      // Query query = session.createQuery(userLookupQuery);
	      // query.setString("email", email);
	      // query.setString("pass", hashPass);
	      user = (User) session.createCriteria(User.class).add(
	          Restrictions.eq("email", email)).uniqueResult();
	      if (user != null) {
	    	  //if (BCrypt.checkpw(user.getPasswordHash()+"redirection",password)) {
	    	  if (user.getPasswordHash().equals(password)) {
	    		  String sessionId = RandomUtil.generateSessionId();

	          // TODO: Check for duplicate session id.

	          user.setSessionId(sessionId);
	          session.update(user);
	          ManagedSession.commitTransaction(session);
	          
	          // user.setPasswordHash(null); // this is to ensure password never
	          // return
	          user.setRoles(roleDb.getRoles(user.getId())); // this to convert
	          // PersistentSet to
	          // HashSet because
	          // GWT can't
	          // serialize
	          // PersistentSet
	          // to the client
	        } else {
	          // for some reason onSuccess never get call if null object is return
	          user = new User();
	        }

	      } else {
	        // for some reason onSuccess never get call if null object is return
	        user = new User();
	      }
	    } catch (HibernateException e) {
	      ManagedSession.rollbackTransaction(session);
	      log.error("Error: There is a problem during sign in", e);
	      e.printStackTrace();
	    } catch (Exception e) {
	      ManagedSession.rollbackTransaction(session);
	      log.error("Error: There is a problem during sign in", e);
	      e.printStackTrace();
	    } finally {
	      // session.close();
	    }

	    return user;
	  }
  
  public void signOut(String sessionId) {
    // TODO: http://code.google.com/p/rebioma/issues/detail?id=70
    Session session = ManagedSession.createNewSessionAndTransaction();
    try {
      User user = sessionService.getUserBySessionId(sessionId);
      if (user == null) {
        return;
      }
      user.setSessionId("");
      userDb.attachDirty(user);
      ManagedSession.commitTransaction(session);
    } catch (RuntimeException re) {
      log.error("error while signing out for user with sessionId " + sessionId,
          re);
      ManagedSession.rollbackTransaction(session);
    }
  }

  public boolean update(String userSessionId, User user, String newPass)
      throws UserServiceException {

    try {
      Session session = ManagedSession.createNewSessionAndTransaction();User loginUser = sessionService.getUserBySessionId(userSessionId);
      User dbUser = userDb.findById(user.getId());
      Role adminRole = roleDb.getRole(UserRole.ADMIN);
      Set<Role> loginUserRoles = roleDb.getRoles(loginUser.getId());
      if (loginUser == null) {
        ManagedSession.commitTransaction(session);
        throw new UserServiceException(
            "Bad session. Session is expired or corrupted");
      }
      if (!loginUser.getId().equals(user.getId())
          && !loginUserRoles.contains(adminRole)) {
        ManagedSession.commitTransaction(session);
        throw new UserServiceException("You do not allow to change "
            + user.getFirstName() + " user information");
      }
      if (!user.getEmail().equalsIgnoreCase(dbUser.getEmail())
          || !user.getFirstName().equalsIgnoreCase(dbUser.getFirstName())
          || !user.getLastName().equalsIgnoreCase(dbUser.getLastName())) {
        OccurrenceQuery query = new OccurrenceQuery();
        query.addSearchFilter("ownerEmail = " + dbUser.getEmail());
        query.addUpdate("ownerEmail = " + user.getEmail());
        query.addUpdate("ownerName = " + user.getFirstName() + " "
            + user.getLastName());
        occurrenceDb.update(query, dbUser);
      }
      boolean passwordChanged = true;
      String password = user.getPasswordHash();
      if (newPass != null) {
        if (BCrypt.checkpw(password, dbUser.getPasswordHash())) {
          user.setPasswordHash(BCrypt.hashpw(newPass, BCrypt.gensalt()));
        } else {
          passwordChanged = false;
          user.setPasswordHash(dbUser.getPasswordHash());
        }
      } else {
        user.setPasswordHash(dbUser.getPasswordHash());
      }
      Set<Role> dbRoles = roleDb.getRoles(user.getId());
      Set<Role> updatedRoles = user.getRoles();
      // dbRoles.
      dbUser.setEmail(user.getEmail());
      dbUser.setPasswordHash(user.getPasswordHash());
      dbUser.setFirstName(user.getFirstName());
      dbUser.setLastName(user.getLastName());
      dbUser.setInstitution(user.getInstitution());
      userDb.attachDirty(dbUser);
      Set<Role> commonRoles = new HashSet<Role>(dbRoles);
      commonRoles.retainAll(updatedRoles);
      for (Role role : dbRoles) {
        if (!commonRoles.contains(role)) {
          userDb.removeRole(dbUser, role);
        }
      }
      for (Role role : updatedRoles) {
        if (!commonRoles.contains(role)) {
          userDb.addRole(dbUser, role);
        }
      }
      ManagedSession.commitTransaction(session);
      if (!passwordChanged) {
        throw new UserServiceException(
            "Bad old password. Password remain the same and other changes are saved");
      }
      return true;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new UserServiceException(e.getMessage());
    }
  }

  /**
   * Editing user profiler is not yet implemented.
   * 
   * @param sessionId
   * @param query
   * @return
   * @throws UserServiceException
   */
  public int update(String sessionId, UserQuery query)
      throws UserServiceException {
    try {
      Session session = ManagedSession.createNewSessionAndTransaction();CollaboratorsUpdate collaboratorsUpdate = query.getCollaboratorsUpdate();
      int count = 0;
      switch (collaboratorsUpdate) {
      case ADD:
        count = addFriends(sessionId, query.getUpdatedFriends());
        break;
      case REMOVE:
        count = removeFriends(sessionId, query.getUpdatedFriends());
        break;
      case NONE:
        // TODO: user profiler updating.
        break;
      }
      ManagedSession.commitTransaction(session);
      return count;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new UserServiceException(e.getMessage());
    }
  }

  /**
   * This method looks up for user information by the given email. If there is
   * such user, return true.
   * 
   * @see org.rebioma.client.services.UserService#userEmailExists(org.rebioma.client.bean.User,
   *      boolean)
   */
  public boolean userEmailExists(String email) {
    // Session session = HibernateUtil.getCurrentSession();
    // boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
    // boolean isUserExisted = false;
    // try {
    // // String userquery = "from User as u where u.email = :email";
    User user = userDb.findByEmail(email);
    return user != null;
    // isUserExisted = user != null;
    //
    // } catch (HibernateException e) {
    // log.error("Error: " + e.getMessage(), e);
    // ManagedSession.rollbackTransaction(session);
    // } finally {
    // if (isFirstTransaction) {
    // HibernateUtil.commitCurrentTransaction();
    // }
    // // session.close();
    // }
    //
    // return isUserExisted;
  }

  boolean addUserAdmin(User user) {
    if (user.getId() == null) {
      userDb.attachDirty(user);
    }
    Role adminRole = roleDb.getRole(UserRole.ADMIN);
    userDb.addRole(user, adminRole);
    // roles.add(adminRole);

    return false;
  }

  private int addFriends(String sessionId, Set<Integer> userFriends)
      throws Exception {
    User user = sessionService.getUserBySessionId(sessionId);
    int count = 0;
    if (user != null) {
      Integer userId = user.getId();
      Set<Integer> friendIds = collaboratorsDb.getAllCollaboratorIds(userId);
      Set<Collaborators> addedFriends = new HashSet<Collaborators>();
      for (Integer friendId : userFriends) {
        if (friendIds.add(friendId)) {
          addedFriends.add(new Collaborators(userId, friendId));
          count++;
        }
      }
      collaboratorsDb.attachDirty(addedFriends);
    }
    return count;
  }

  private UserQuery getFriends(String sessionId, UserQuery userQuery)
      throws Exception {
    Integer userId = sessionService.getUserBySessionId(sessionId).getId();
    if (userId == null) {
      return userQuery;
    }
    Set<Integer> friendsId = collaboratorsDb.getAllCollaboratorIds(userId);
    if (friendsId != null && !friendsId.isEmpty()) {
      StringBuilder idsBuilder = new StringBuilder();
      for (Integer id : friendsId) {
        idsBuilder.append(id + ",");
      }
      idsBuilder.deleteCharAt(idsBuilder.length() - 1);
      userQuery.clearSearchFilter();
      userQuery.addSearchFilter("id in " + idsBuilder.toString());
      return userDb.findByQuery(userQuery, userId);
    } else {
      userQuery.setResults(new ArrayList<User>());
      userQuery.setCount(0);
      return userQuery;
    }

  }

  private int removeFriends(String sessionId, Set<Integer> userFriends)
      throws Exception {
    User user = sessionService.getUserBySessionId(sessionId);
    int count = 0;
    if (user != null) {
      Integer userId = user.getId();
      Set<Integer> currentFriends = collaboratorsDb
          .getAllCollaboratorIds(userId);
      Set<Integer> removedFriends = new HashSet<Integer>();
      for (Integer removedFriend : userFriends) {
        if (currentFriends.remove(removedFriend)) {
          removedFriends.add(removedFriend);
          count++;
        }
      }
      collaboratorsDb.delete(userId, removedFriends);
    }
    return count;
  }

  private Set<User> removeFriends(User user, Set<User> userFriends) {

    Collaborators collaborators = collaboratorsDb.findById(user.getId());
    // Set<User> friends = collaborators.getFriends();
    Set<User> removedFriends = new HashSet<User>();
    // for (User removedUser : userFriends) {
    // if (friends.remove(removedUser)) {
    // removedFriends.add(removedUser);
    // }
    // }
    collaboratorsDb.attachDirty(collaborators);
    return removedFriends;
  }
}
