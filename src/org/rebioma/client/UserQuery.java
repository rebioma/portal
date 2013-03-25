package org.rebioma.client;

import java.util.HashSet;
import java.util.Set;

import org.rebioma.client.bean.User;

public class UserQuery extends Query<User> {

  /**
   * An enum for updating user's collaborators request.
   * 
   * @author Tri
   * 
   */
  public enum CollaboratorsUpdate {
    /**
     * A request to add a list of User in {@link Query#results} to current
     * logged in User.
     */
    ADD,
    /**
     * A request to remove a list of User in {@link Query#results} to current
     * logged in User.
     */
    REMOVE,
    /**
     * No collaborators update request.
     */
    NONE;
  }

  private CollaboratorsUpdate collaboratorsUpdate = CollaboratorsUpdate.NONE;

  private Set<Integer> updatedFriends = null;

  private boolean usersCollaboratorsOnly = false;

  public UserQuery() {
    super();
  }

  public UserQuery(int start, int limit) {
    super(start, limit);
  }

  public void addUpdatedFriend(Integer updatedFriendId) {
    if (updatedFriends == null) {
      updatedFriends = new HashSet<Integer>();
    }
    updatedFriends.add(updatedFriendId);
  }

  public void clearUpdatedFriends() {
    updatedFriends.clear();
  }

  public CollaboratorsUpdate getCollaboratorsUpdate() {
    return collaboratorsUpdate;
  }

  public Set<Integer> getUpdatedFriends() {
    if (updatedFriends == null) {
      updatedFriends = new HashSet<Integer>();
    }
    return updatedFriends;
  }

  public boolean isUsersCollaboratorsOnly() {
    return usersCollaboratorsOnly;
  }

  public void removeUpdatedFriend(Integer updatedFriendId) {
    if (updatedFriends == null) {
      updatedFriends = new HashSet<Integer>();
    }
    updatedFriends.remove(updatedFriendId);
  }

  public void setCollaboratorsUpdate(CollaboratorsUpdate collaboratorsUpdate) {
    this.collaboratorsUpdate = collaboratorsUpdate;
  }

  public void setUsersCollaboratorsOnly(boolean usersCollaboratorsOnly) {
    this.usersCollaboratorsOnly = usersCollaboratorsOnly;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder(super.toString());
    sb.append("CollaboratorsOnly: " + usersCollaboratorsOnly);
    return sb.toString();
  }

}
