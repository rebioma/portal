package org.rebioma.server.services;

import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.rebioma.client.OccurrenceCommentQuery;
import org.rebioma.client.bean.OccurrenceComments;

//@ImplementedBy(OccurrenceCommentsServiceImpl.class)
public interface OccurrenceCommentsService {
  public void attachClean(OccurrenceComments instance);

  public void attachDirty(OccurrenceComments instance);

  public void attachDirty(Set<OccurrenceComments> instance);
  
  public void attachDirty(Session session, Set<OccurrenceComments> instances) ;

  public void delete(OccurrenceComments persistentInstance);

  public void delete(Set<OccurrenceComments> persistentInstance);

  public List<OccurrenceComments> findByExample(OccurrenceComments instance);

  public OccurrenceComments findById(int id);

  public List<OccurrenceComments> findByQuery(OccurrenceCommentQuery query);

  public OccurrenceComments merge(OccurrenceComments detachedInstance);

  public void persist(OccurrenceComments transientInstance);

  public void save(OccurrenceComments comment);
}
