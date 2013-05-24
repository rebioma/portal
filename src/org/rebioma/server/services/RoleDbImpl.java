package org.rebioma.server.services;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.rebioma.client.bean.Role;
import org.rebioma.client.bean.UserRole;
import org.rebioma.server.util.CsvUtil;
import org.rebioma.server.util.ManagedSession;

public class RoleDbImpl implements RoleDb {
  /**
   * The {@link Logger} for this class.
   */
  private static final Logger log = Logger.getLogger(RoleDbImpl.class);

  public static void main(String args[]) throws UnsupportedEncodingException {
    if (args.length < 1) {
      System.err.println("USAGE: RoleDbImpl csvfileLocation");
      System.exit(1);
    }
    List<Role> roles = CsvUtil.loadEntities(args[0], Role.class);
    RoleDbImpl roleDb = new RoleDbImpl();
    // for (Role role : roleDb.getAllRoles()) {
    // System.out.println(role.getDescriptionFr());
    // }
    for (Role role : roles) {
      String descriptionFr = new String(role.getDescriptionFr().getBytes(),
          "UTF-8");
      String nameFr = new String(role.getDescriptionFr().getBytes(), "UTF-8");
      role.setNameFr(nameFr);
      role.setDescriptionFr(descriptionFr);
      // role.setDescriptionFr("un r�le pour les administrateurs syst�me");
      System.out.println(role.getDescriptionFr());
      roleDb.save(role);
    }
  }

  public boolean delete(Role role) {
    Session session = ManagedSession.createNewSessionAndTransaction();

    try {
      session.delete(role);
      ManagedSession.commitTransaction(session);
      return true;
    } catch (RuntimeException re) {
      ManagedSession.rollbackTransaction(session);
      log.error("error :" + re.getMessage() + " on delete(Role)", re);
      return false;
    }
  }

  public Role edit(Role role) {
    Session session = ManagedSession.createNewSessionAndTransaction();
    try {
      session.update(role);
      ManagedSession.commitTransaction(session);
      return role;
    } catch (RuntimeException re) {
      ManagedSession.rollbackTransaction(session);
      log.error("error :" + re.getMessage() + " on delete(Role)", re);
      throw re;
    }
  }

  public Role findById(int id) {
    Session session = ManagedSession.createNewSessionAndTransaction();
    try {
      Role role = (Role) session.get(Role.class, id);
      ManagedSession.commitTransaction(session);
      return role;
    } catch (RuntimeException re) {
      ManagedSession.rollbackTransaction(session);
      log.error("error :" + re.getMessage() + " on delete(Role)", re);
      throw re;
    }
  }

  public List<Role> getAllRoles() {
    Session session = ManagedSession.createNewSessionAndTransaction();
    try {
      List<Role> roles = session.createCriteria(Role.class).list();
      ManagedSession.commitTransaction(session);
      return roles;
    } catch (RuntimeException re) {
      ManagedSession.rollbackTransaction(session);
      log.error("error :" + re.getMessage() + " on delete(Role)", re);
      throw re;
    }
  }

  public Role getRole(UserRole userRole) {
    Session session = ManagedSession.createNewSessionAndTransaction();
    try {
      Criteria criteria = session.createCriteria(Role.class);
      criteria.add(Restrictions.eq("nameEn", userRole.toString()));
      Role role = (Role) criteria.uniqueResult();
      ManagedSession.commitTransaction(session);
      return role;
    } catch (RuntimeException re) {
      ManagedSession.rollbackTransaction(session);
      log.error("error :" + re.getMessage() + " on delete(Role)", re);
      throw re;
    }
  }

  public Set<Role> getRoles(int userId) {
    Session session = ManagedSession.createNewSessionAndTransaction();
    try {
      Criteria criteria = session.createCriteria(UserRoles.class);
      criteria.add(Restrictions.eq("userId", userId));
      List<UserRoles> userRoles = criteria.list();
      Set<Role> roles = new HashSet<Role>();
      for (UserRoles userRole : userRoles) {
        roles.add(findById(userRole.getRoleId()));
      }
      ManagedSession.commitTransaction(session);
      return roles;
    } catch (RuntimeException re) {
      ManagedSession.rollbackTransaction(session);
      log.error("error :" + re.getMessage() + " on delete(Role)", re);
      throw re;
    }
  }

  public Role save(Role role) {
    Session session = ManagedSession.createNewSessionAndTransaction();
    try {
      Criteria crit = session.createCriteria(Role.class);
      crit.add(Restrictions.eq("nameEn", role.getNameEn()));
      Role existenceRole = (Role) crit.uniqueResult();
      if (existenceRole != null) {
        role = existenceRole;
      } else {
        session.save(role);
      }
      ManagedSession.commitTransaction(session);
      // System.out.println(role.getId());
      return role;
    } catch (RuntimeException re) {
      ManagedSession.rollbackTransaction(session);
      log.error("error :" + re.getMessage() + " on delete(Role)", re);
      throw re;
    }
  }
}
