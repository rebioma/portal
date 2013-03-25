package org.rebioma.server.services;

// Generated Feb 18, 2009 10:17:00 AM by Hibernate Tools 3.2.4.CR1

import java.util.List;
import java.util.Set;

import com.google.inject.ImplementedBy;

@ImplementedBy(CollaboratorsDbImpl.class)
public interface CollaboratorsDb {

  public void attachClean(Collaborators instance);

  public void attachDirty(Collaborators instance);

  public void attachDirty(Set<Collaborators> addedFriends);

  public void delete(Collaborators persistentInstance);

  public void delete(Integer userId, Set<Integer> removedFriends);

  public List<Collaborators> findByExample(Collaborators instance);

  public Collaborators findById(int id);

  public Set<Integer> getAllCollaboratorIds(Integer userId) throws Exception;

  public Collaborators merge(Collaborators detachedInstance);

  public void persist(Collaborators transientInstance);
}
