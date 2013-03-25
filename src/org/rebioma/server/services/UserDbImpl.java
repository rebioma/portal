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

import static org.hibernate.criterion.Example.create;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.rebioma.client.OrderKey;
import org.rebioma.client.UserQuery;
import org.rebioma.client.bean.Role;
import org.rebioma.client.bean.User;
import org.rebioma.client.bean.UserRole;
import org.rebioma.server.services.QueryFilter.InvalidFilter;
import org.rebioma.server.util.HibernateUtil;

/**
 * Default implementation of {@link UserDb}. Uses Hibernate for database
 * operations.
 * 
 */
public class UserDbImpl implements UserDb {

  private static class UserFilter extends QueryFilter {
    public UserFilter(String filter) throws InvalidFilter {
      super(filter, User.class);
    }

    public String getPropertyName(String property) {
      if (property.equalsIgnoreCase("id")) {
        property = "id";
      } else if (property.equals("firstName")) {
        property = "firstName";
      } else if (property.equals("lastName")) {
        property = "lastName";
      } else if (property.equals("email")) {
        property = "email";
      } else if (property.equals("institution")) {
        property = "institution";
      } else {
        property = null;
      }
      return property;
    }

  }

  /**
   * The {@link Logger} for this class.
   */
  private static final Logger log = Logger.getLogger(UserDbImpl.class);

  private static QueryFilter dummyFilter = null;

  public static void main(String args[]) {
    UserDbImpl userDb = new UserDbImpl();
    Session session = HibernateUtil.getCurrentSession();
    HibernateUtil.beginTransaction(session);
    try {
      // Criteria criteria = session.createCriteria(UserRole.class);
      Query query = session
          .createQuery("from UserRoles ur where (select count(id) from User u where u.id = ur.userId) = 0");
      List<UserRoles> userRoles = query.list();
      for (UserRoles userRole : userRoles) {
        session.delete(userRole);
        System.out.println("userId: " + userRole.getUserId() + " delete");
      }
      HibernateUtil.commitCurrentTransaction();
    } catch (Exception e) {

    }
  }

  private static String getUserPropertyName(String property) {
    if (dummyFilter == null) {
      try {
        dummyFilter = new UserFilter("id = 0");
      } catch (InvalidFilter e) {
        e.printStackTrace();
        return null;
      }
    }
    return dummyFilter.getPropertyName(property);

  }

  public UserDbImpl() {
  }

  public void addRole(User user, Role role) {
    if (role != null) {
      log.debug("adding role " + role.getNameEn() + " to user " + user.getId());
      Session session = HibernateUtil.getCurrentSession();
      boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      try {
        Criteria criteria = session.createCriteria(UserRoles.class);
        criteria.add(Restrictions.eq("userId", user.getId()));
        criteria.add(Restrictions.eq("roleId", role.getId()));
        UserRoles userRoles = (UserRoles) criteria.uniqueResult();
        if (userRoles == null) {
          userRoles = new UserRoles(user.getId(), role.getId());
          session.save(userRoles);
        }
        // session.merge(user);
        if (isFirstTransaction) {
          HibernateUtil.commitCurrentTransaction();
        }
      } catch (RuntimeException re) {
        log.error("add role " + role.getNameEn() + " to user " + user.getId() + " failed", re);
        HibernateUtil.rollbackTransaction();
        throw re;
      }
    }
  }

  public void attachClean(User instance) {
    log.debug("attaching clean User instance");
    try {
      HibernateUtil.getCurrentSession().lock(instance, LockMode.NONE);
      log.debug("attach successful");
    } catch (RuntimeException re) {
      log.error("attach failed", re);
      throw re;
    }
  }

  public void attachDirty(Set<User> instances) {
    log.debug("attaching dirty User instances");
    User ref = null;
    try {
      Session session = HibernateUtil.getCurrentSession();
      boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      for (User instance : instances) {
        ref = instance;
        session.saveOrUpdate(instance);
      }
      if (isFirstTransaction) {
        HibernateUtil.commitCurrentTransaction();
      }
      log.debug("attach successful");
    } catch (RuntimeException re) {
      log.info("attach failed (" + ref + ") ", re);
    }
  }

  public void attachDirty(User instance) {
    log.debug("attaching dirty User instance");
    try {
      Session session = HibernateUtil.getCurrentSession();
      boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      session.saveOrUpdate(instance);
      log.debug("attach successful");
      if (isFirstTransaction) {
        HibernateUtil.commitCurrentTransaction();
      }
    } catch (RuntimeException re) {
      log.error("attach failed", re);
      throw re;
    }
  }

  public void delete(User persistentInstance) {
    log.debug("deleting User instance");
    try {
      HibernateUtil.getCurrentSession().delete(persistentInstance);
      log.debug("delete successful");
    } catch (RuntimeException re) {
      log.error("delete failed", re);
      throw re;
    }
  }

  public List<User> findAll() {
    Session session = HibernateUtil.getCurrentSession();
    boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
    List<User> users = new ArrayList<User>();
    try {
      users = session.createCriteria(User.class).list();
      if (isFirstTransaction) {
        HibernateUtil.commitCurrentTransaction();
      }
      return users;
    } catch (Exception e) {
      HibernateUtil.rollbackTransaction();
      e.printStackTrace();
    } finally {
      if (isFirstTransaction) {
        HibernateUtil.commitCurrentTransaction();
      }
      // session.close();
    }
    for (User user : users) {
      user.setSessionId(null);
    }
    return users;
  }

  public List<User> findByEmail(Set<String> userEmails) {
    List<User> results = new ArrayList<User>();
    log.debug("getting User instance with user emails: " + userEmails);
    Session session = HibernateUtil.getCurrentSession();
    boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
    try {
      Criteria criteria = session.createCriteria(User.class);
      Criterion criterion = null;
      for (String userEmail : userEmails) {
        if (criterion == null) {
          criterion = Restrictions.eq("email", userEmail);
        } else {
          criterion = Restrictions.or(criterion, Restrictions.eq("email", userEmail));
        }
      }
      criteria.add(criterion);
      results = criteria.list();
      if (isFirstTransaction) {
        HibernateUtil.commitCurrentTransaction();
      }
    } catch (RuntimeException re) {
      HibernateUtil.rollbackTransaction();
      log.error("get failed", re);
      throw re;
    }
    return results;
  }

  public User findByEmail(String userEmail) {
    User result = null;
    log.debug("getting User instance with user email: " + userEmail);
    Session session = HibernateUtil.getCurrentSession();
    boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
    try {
      Criteria criteria = session.createCriteria(User.class);
      criteria.add(Restrictions.eq("email", userEmail));
      result = (User) criteria.uniqueResult();
      if (result != null) {
        result.setSessionId(null);
      }
      if (isFirstTransaction) {
        HibernateUtil.commitCurrentTransaction();
      }
    } catch (RuntimeException re) {
      HibernateUtil.rollbackTransaction();
      log.error("get failed", re);
      throw re;
    }
    return result;
  }

  public List<User> findByExample(Set<User> instances) {
    List<User> result = new ArrayList<User>();
    for (User instance : instances) {
      result.addAll(findByExample(instance));
    }
    return result;
  }

  public List<User> findByExample(User instance) {
    log.debug("finding User instance by example");
    try {
      Session session = HibernateUtil.getCurrentSession();
      boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      List<User> results = session.createCriteria("org.rebioma.client.bean.User")
          .add(create(instance)).list();
      log.debug("find by example successful, result size: " + results.size());
      if (isFirstTransaction) {
        HibernateUtil.commitCurrentTransaction();
      }
      return results;
    } catch (RuntimeException re) {
      log.error("find by example failed", re);
      throw re;
    }
  }

  public User findById(java.lang.Integer id) {
    log.debug("getting User instance with id: " + id);
    try {
      Session session = HibernateUtil.getCurrentSession();
      boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      User instance = findById(session, id); 
      if (isFirstTransaction) {
        HibernateUtil.commitCurrentTransaction();
      }
      return instance;
    } catch (RuntimeException re) {
      log.error("get failed", re);
      throw re;
    }
  }
  
  public User findById(Session session, java.lang.Integer id) {
	    log.debug("getting User instance with id: " + id);
	    try {
	      
	      User instance = (User) session.get("org.rebioma.client.bean.User", id);
	      if (instance == null) {
	        log.debug("get successful, no instance found");
	      } else {
	        log.debug("get successful, instance found");
	      }
	      
	      return instance;
	    } catch (RuntimeException re) {
	      log.error("get failed", re);
	      throw re;
	    }
	  }

  public List<User> findById(Set<Integer> ids) {
    List<User> results = new ArrayList<User>();
    for (Integer id : ids) {
      results.add(findById(id));
    }
    return results;
  }

  public UserQuery findByQuery(UserQuery query, Integer loggedInUserId) throws Exception {
    log.debug("finding User instances by query.");
    Session session = HibernateUtil.getCurrentSession();
    boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
    try {
      Criteria criteria = session.createCriteria(User.class);
      List<OrderKey> orderingMap = query.getOrderingMap();
      if (orderingMap == null) {
        criteria.addOrder(Order.asc(getUserPropertyName("firstName")));
      } else {
        for (OrderKey orderKey : orderingMap) {
          String property = orderKey.getAttributeName();
          if (orderKey.isAsc()) {
            criteria.addOrder(Order.asc(getUserPropertyName(property)));
          } else {
            criteria.addOrder(Order.desc(getUserPropertyName(property)));
          }
        }
      }
      criteria.setFirstResult(query.getStart());
      criteria.setMaxResults(query.getLimit());
      if (!query.isUsersCollaboratorsOnly()) {
        criteria.add(Restrictions.ne("id", loggedInUserId));
      }
      Set<UserFilter> filters = QueryFilter.getFilters(query.getSearchFilters(), UserFilter.class);
      for (UserFilter filter : filters) {
        switch (filter.getOperator()) {
        case EQUAL:
          criteria.add(Restrictions.eq(filter.column, filter.value));
          break;
        case CONTAIN:
          criteria.add(Restrictions.ilike(filter.column, filter.value.toString(),
              MatchMode.ANYWHERE));
          break;
        case IN:
          criteria.add(Restrictions.in(filter.column, filter.getIntCollectionValues()));
          break;
        }
      }
      List<User> users = criteria.list();
      query.setResults(users);
      if (query.isCountTotalResults()) {
        criteria.setFirstResult(0);
        criteria.setProjection(Projections.count("id"));
        Integer count = (Integer) criteria.uniqueResult();
        if (count != null) {
          query.setCount(count);
        }
      } else {
        query.setCount(-1);
      }
      if (isFirstTransaction) {
        HibernateUtil.commitCurrentTransaction();
      }
    } catch (Exception e) {
      HibernateUtil.rollbackTransaction();
      e.printStackTrace();
      throw e;
    }
    return query;

  }

  public User merge(User detachedInstance) {
    log.debug("merging User instance");
    try {
      Session session = HibernateUtil.getCurrentSession();
      boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      User result = (User) session.merge(detachedInstance);
      log.debug("merge successful");
      if (isFirstTransaction) {
        HibernateUtil.commitCurrentTransaction();
      }
      return result;
    } catch (RuntimeException re) {
      log.error("merge failed", re);
      throw re;
    }
  }

  public void persist(User transientInstance) {
    log.debug("persisting User instance");
    try {
      HibernateUtil.getCurrentSession().persist(transientInstance);
      log.debug("persist successful");
    } catch (RuntimeException re) {
      log.error("persist failed", re);
      throw re;
    }
  }

  public void removeRole(User user, Role role) {
    log.debug("removing role " + role.getNameEn() + " to user " + user.getId());
    Session session = HibernateUtil.getCurrentSession();
    boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
    try {

      Criteria criteria = session.createCriteria(UserRoles.class);
      criteria.add(Restrictions.eq("userId", user.getId()));
      criteria.add(Restrictions.eq("roleId", role.getId()));
      UserRoles userRoles = (UserRoles) criteria.uniqueResult();
      if (userRoles != null) {
        session.delete(userRoles);
      }
      // session.merge(user);

      if (isFirstTransaction) {
        HibernateUtil.commitCurrentTransaction();
      }
    } catch (RuntimeException re) {
      log.error("remove role " + role.getNameEn() + " to user " + user.getId() + " failed", re);
      HibernateUtil.rollbackTransaction();
      throw re;
    }
  }

  public boolean removeUser(User user) {
    if (user != null) {
      log.debug("removing user " + user.getId());
      Session session = HibernateUtil.getCurrentSession();
      boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      try {
        Criteria criteria = session.createCriteria(UserRoles.class);
        criteria.add(Restrictions.eq("userId", user.getId()));
        List<UserRole> userRoles = criteria.list();
        for (UserRole userRole : userRoles) {
          session.delete(userRole);
        }
        session.delete(user);
        // session.merge(user);
        if (isFirstTransaction) {
          HibernateUtil.commitCurrentTransaction();
        }
        return true;
      } catch (RuntimeException re) {
        log.error("remove user " + user.getId() + " failed", re);
        HibernateUtil.rollbackTransaction();
        throw re;
      }
    }
    return false;
  }

  boolean addUserAdmin(User user) {
    if (user.getId() == null) {
      attachDirty(user);
    }
    Role adminRole = (new RoleDbImpl()).getRole(UserRole.ADMIN);
    addRole(user, adminRole);
    // roles.add(adminRole);

    return false;
  }

  void assignRoleToUsers(Role role, String... useremails) {
    try {
      Session session = HibernateUtil.getCurrentSession();
      boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      for (String email : useremails) {
        User user = findByEmail(email.trim());
        addRole(user, role);
      }
      if (isFirstTransaction) {
        HibernateUtil.commitCurrentTransaction();
      }
    } catch (RuntimeException re) {
      log.error("get failed", re);
      throw re;
    }
  }
}
