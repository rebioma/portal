package org.rebioma.client.bean;

public enum UserRole {
  ADMIN("admin"),

  REVIEWER("reviewer"),

  RESEARCHER("researcher")

  ;
  public static UserRole toUserRole(String role) {
    UserRole userRole = null;

    if (role.equalsIgnoreCase("admin")) {
      userRole = ADMIN;
    } else if (role.equalsIgnoreCase("reviewer")) {
      userRole = REVIEWER;
    } else if (role.equalsIgnoreCase("researcher")) {
      userRole = RESEARCHER;
    }

    return userRole;
  }

  String name;

  UserRole(String name) {
    this.name = name;
  }

  public String toString() {
    return name;
  }
}
