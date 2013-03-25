package org.rebioma.server.services;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.rebioma.client.User;
import org.rebioma.client.UserQuery;
import org.rebioma.client.UserQuery.CollaboratorsUpdate;
import org.rebioma.client.services.UserService.UserServiceException;
import org.rebioma.server.util.HibernateUtil;

import com.google.inject.testing.guiceberry.GuiceBerryEnv;
import com.google.inject.testing.guiceberry.NoOpTestScopeListener;
import com.google.inject.testing.guiceberry.TestScopeListener;
import com.google.inject.testing.guiceberry.junit3.GuiceBerryJunit3Env;
import com.google.inject.testing.guiceberry.junit3.GuiceBerryJunit3TestCase;

@GuiceBerryEnv("org.rebioma.server.services.OccurrenceDbTest$OccurrenceDbEnv")
public class UserServiceTest extends GuiceBerryJunit3TestCase {
  public static final class UserServiceEnv extends GuiceBerryJunit3Env {
    @Override
    protected Class<? extends TestScopeListener> getTestScopeListener() {
      return NoOpTestScopeListener.class;
    }
  }

  private static UserServiceImpl userServiceImpl = new UserServiceImpl();
  private static List<User> allUsers;
  static {
    userServiceImpl.collaboratorsDb = new CollaboratorsDbImpl();
    userServiceImpl.sessionService = new SessionIdServiceImpl();
    userServiceImpl.userDb = new UserDbImpl();
    Session session = HibernateUtil.getSessionFactory().openSession();
    Transaction tx = session.beginTransaction();
    try {
      allUsers = session.createQuery("from User").list();
    } catch (Exception e) {
      e.printStackTrace();
      fail("fail to get all users from the database");
    }
  }

  public void testAddFriend() {
    User firstUser = allUsers.get(0);
    UserQuery query = new UserQuery();
    query.setCollaboratorsUpdate(CollaboratorsUpdate.REMOVE);
    for (int i = 1; i < allUsers.size(); i++) {
      query.addUpdatedFriend(allUsers.get(i).getId());
    }

    try {
      userServiceImpl.update(firstUser.getSessionId(), query);
      query.setCollaboratorsUpdate(CollaboratorsUpdate.ADD);
      userServiceImpl.update(firstUser.getSessionId(), query);
      query.setUsersCollaboratorsOnly(true);
      query.setCountTotalResults(true);
      query = userServiceImpl.fetchUser(firstUser.getSessionId(), query);
      assertEquals(allUsers.size() - 1, query.getCount());
      for (User user : query.getResults()) {
        assertTrue(allUsers.contains(user));
        assertFalse(firstUser.equals(user));
      }
    } catch (UserServiceException e) {
      e.printStackTrace();
      fail("fail to add friends");
    }
  }

  public void testfetchUser() {
    User firstUser = allUsers.get(0);
    UserQuery query = new UserQuery(0, 10);
    List<User> results = new ArrayList<User>();
    query.setCountTotalResults(true);
    try {
      userServiceImpl.fetchUser(firstUser.getSessionId(), query);
      query.setCountTotalResults(false);
      int count = query.getCount();
      List<User> queryResult;
      while ((queryResult = query.getResults()).size() > 0) {
        results.addAll(queryResult);
        query.setStart(query.getLimit() + query.getStart());
        userServiceImpl.fetchUser(firstUser.getSessionId(), query);
      }
      assertEquals(count, results.size());
      for (User user : results) {
        assertTrue(allUsers.contains(user));
        assertFalse(firstUser.equals(user));
      }
    } catch (UserServiceException e) {
      e.printStackTrace();
      fail("fail to fetchUser all");
    }
  }
}
