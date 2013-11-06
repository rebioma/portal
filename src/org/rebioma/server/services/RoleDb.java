package org.rebioma.server.services;

import java.util.List;
import java.util.Set;

import org.rebioma.client.bean.Role;
import org.rebioma.client.bean.UserRole;

//@ImplementedBy(RoleDbImpl.class)
public interface RoleDb {

  boolean delete(Role role);

  Role edit(Role role);

  Role findById(int id);

  List<Role> getAllRoles();

  Role getRole(UserRole userRole);

  Set<Role> getRoles(int userId);

  Role save(Role role);
  
  boolean isSAdmin(int userId);

}
