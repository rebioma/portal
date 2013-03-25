package org.rebioma.server.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.rebioma.client.bean.Role;
import org.rebioma.client.bean.User;
import org.rebioma.client.bean.UserRole;
import org.rebioma.server.util.CsvUtil;

import BCrypt.BCrypt;
import au.com.bytecode.opencsv.CSVReader;

public class Bootrap {

  private static final String FIRST_NAME = "First name";
  private static final String LAST_NAME = "Last name";
  private static final String EMAIL = "Email";
  private static final String INSTITUTION = "Institution";
  private static final String PASSWORD = "Password";
  private static final String CONFIRM_PASSWORD = "Confirm password";
  private static final String CREATE_ADMIN_USER = "Create Admin User";
  private static final String ASSIGN_ROLE_TO_USERS = "Assigning Role to User";
  private static final String EXIT = "Exit";
  private static final String INITIALIZE_ROLES = "Initialize Roles";
  private static final String RESEARCHER_ALL = "Make all existing users 'Researcher'";
  private static final String ADD_USER_BY_CSV = "Add user using csv file";
  private static final String CREATE_LOST_PASSWORD = "Create Lost Passowrd";

  public static void addUsersByCsvFile() {
    String csvFileLocation = getUserInput("users csv file location: ", true);
    File file = new File(csvFileLocation);
    if (!file.exists()) {
      System.err.println(" can't find file " + csvFileLocation);
    } else {
      try {
        CSVReader csvReader = new CSVReader(new FileReader(file));
        Map<String, Integer> headerColMap = CsvUtil
            .getHeaderColIndexes(csvReader.readNext());
        String line[];
        List<User> users = new ArrayList<User>();
        UserDb userDb = DBFactory.getUserDb();
        RoleDb roleDb = DBFactory.getRoleDb();
        while ((line = csvReader.readNext()) != null) {
          String firstName = line[headerColMap.get("firstname")];
          String lastName = line[headerColMap.get("lastname")];
          String institution = line[headerColMap.get("institution")];
          String email = line[headerColMap.get("email")];
          System.out.println("adding user " + email);
          if (userDb.findByEmail(email) != null) {
            System.out.println("user " + email
                + "is already existed. skip this user");
            continue;
          }
          String password = BCrypt.hashpw(line[headerColMap.get("password")],
              BCrypt.gensalt());
          String role = line[headerColMap.get("role")];
          User user = new User();
          user.setFirstName(firstName);
          user.setLastName(lastName);
          user.setInstitution(institution);
          user.setPasswordHash(password);
          user.setEmail(email);
          userDb.attachDirty(user);
          UserRole userRole = UserRole.toUserRole(role);
          if (userRole == null) {
            userRole = UserRole.RESEARCHER;
          }
          userDb.addRole(user, roleDb.getRole(userRole));
          users.add(user);
          System.out.println("user " + email + " added");
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public static void assignRoleForUsers() {
    List<Role> roles = DBFactory.getRoleDb().getAllRoles();
    Map<String, Role> rolesMap = new TreeMap<String, Role>();
    for (Role role : roles) {
      rolesMap.put(role.getNameEn(), role);
    }
    System.out.println("Select a Role (1 - " + rolesMap.size()
        + ") to be assigned to users: ");
    String selectedRole = selectFromCollection(rolesMap.keySet(), false);
    Role role = rolesMap.get(selectedRole);
    System.out.println("Enter a CSV list of user email to assgin "
        + selectedRole + " role to: ");
    String useremails[] = getUserInput(
        "Enter a CSV list of user email to assgin " + selectedRole
            + " role to: ", true).split(",");
    UserDbImpl userDb = (UserDbImpl) DBFactory.getUserDb();
    userDb.assignRoleToUsers(role, useremails);
    System.out.println("Successfully assign roles.");
  }

  public static void createAdminUser() {
    System.out.println("Enter the following fields to create an adminUser");
    String firstName = getUserInput(FIRST_NAME, true);
    String lastName = getUserInput(LAST_NAME, true);
    String institution = getUserInput(INSTITUTION, true);
    String email = getUserInput(EMAIL, true);
    UserDbImpl userDb = (UserDbImpl) DBFactory.getUserDb();
    while (userDb.findByEmail(email) != null) {
      System.out.println(email + " is already register. Try again.");
      email = getUserInput(EMAIL);
    }
    String password = BCrypt.hashpw(getPassInput(), BCrypt.gensalt());
    User user = new User();
    user.setEmail(email);
    user.setApproved(true);
    user.setPasswordHash(password);
    user.setInstitution(institution);
    user.setLastName(lastName);
    user.setFirstName(firstName);
    userDb.addUserAdmin(user);
    System.out.println("User " + user.getEmail()
        + " just created as admin with user id " + user.getId());
  }

  public static void createLostPassword() {
	    System.out.println("Enter the password");
	    String pass = getUserInput("Password", true);
	    String password = BCrypt.hashpw(pass, BCrypt.gensalt());
	    System.out.println("Password crypted :" + password);
	  }
  
  public static String getPassInput() {
    String password = getUserInput(PASSWORD, true);
    String confirmed = getUserInput(CONFIRM_PASSWORD, true);
    while (!password.equals(confirmed)) {
      System.out.println("password does not match. Try again");
      password = getUserInput(PASSWORD, true);
      confirmed = getUserInput(CONFIRM_PASSWORD, true);
    }
    return password;
  }

  public static String getUserInput(String label) {
    System.out.print(label + ": ");
    BufferedInputStream inputStream = new BufferedInputStream(System.in);
    StringBuilder sb = new StringBuilder();
    char c;
    do {
      try {
        c = (char) inputStream.read();
        sb.append(c);
      } catch (IOException e) {
        e.printStackTrace();
        break;
      }
    } while (c != '\n');
    return sb.toString().trim();
  }

  public static String getUserInput(String label, boolean required) {
    if (required) {
      while (true) {
        String input = getUserInput(label);
        if (!input.equals("")) {
          return input;
        }
        System.out.println(label + " field can't be empty.");
      }
    } else {
      return getUserInput(label);
    }
  }

  public static void main(String args[]) {
    List<String> options = new ArrayList<String>();
    // options.add(INITIALIZE_ROLES);
    options.add(CREATE_ADMIN_USER);
    options.add(ASSIGN_ROLE_TO_USERS);
    options.add(RESEARCHER_ALL);
    options.add(ADD_USER_BY_CSV);
    options.add(CREATE_LOST_PASSWORD);
    options.add(EXIT);
    String currentOption = "";
    while (!currentOption.equals(EXIT)) {
      System.out
          .println("What do you want to do? (1 - " + options.size() + ")");
      currentOption = selectFromCollection(options, false);
      if (currentOption.equals(CREATE_ADMIN_USER)) {
        createAdminUser();
      } else if (currentOption.equals(ASSIGN_ROLE_TO_USERS)) {
        assignRoleForUsers();
      } else if (currentOption.equals(CREATE_LOST_PASSWORD)) {
          createLostPassword();
      } else if (currentOption.equals(INITIALIZE_ROLES)) {
        initializeRoles();
      } else if (currentOption.equals(RESEARCHER_ALL)) {
        Role role = DBFactory.getRoleDb().getRole(UserRole.RESEARCHER);
        List<User> users = ((UserDbImpl) DBFactory.getUserDb()).findAll();
        for (User user : users) {
          DBFactory.getUserDb().addRole(user, role);
        }
        System.out
            .println("Successfully assign 'Researcher' role to all users");
      } else if (currentOption.equals(ADD_USER_BY_CSV)) {
        addUsersByCsvFile();
      }

    }
    // createAdminUser();
  }

  public static String selectFromCollection(Collection<String> selectionList,
      boolean noneOption) {
    int selected;
    int count = 1;
    StringBuilder theList = new StringBuilder();
    List<String> cacheList = new ArrayList<String>();
    for (String selection : selectionList) {
      theList.append("\t" + count + ". " + selection + "\n");
      cacheList.add(selection);
      count++;
    }
    if (noneOption) {
      cacheList.add("NONE");
      theList.append("\t" + count + ". NONE\n");
    } else {
      count--;
    }
    do {
      selected = toInt(getUserInput(theList.toString()));
    } while (selected <= 0 || selected > count);
    return cacheList.get(selected - 1);
  }

  public static int selectFromList(List<String> selectionList,
      boolean noneOption) {
    int selected;
    int count = 1;
    StringBuilder theList = new StringBuilder();
    for (String selection : selectionList) {
      theList.append("\t" + count + ". " + selection + "\n");
      count++;
    }
    if (noneOption) {
      theList.append("\t" + count + ". NONE\n");
    } else {
      count--;
    }
    do {
      selected = toInt(getUserInput(theList.toString()));
    } while (selected <= 0 || selected > count);
    return selected;
  }

  public static int toInt(String s) {
    try {
      return Integer.parseInt(s);
    } catch (NumberFormatException ne) {
      return 0;
    }
  }

  private static void initializeRoles() {
    // TODO Auto-generated method stub

  }

}
