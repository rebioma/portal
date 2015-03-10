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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.rebioma.client.OccurrenceQuery;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.OccurrenceReview;
import org.rebioma.client.bean.RecordReview;
import org.rebioma.client.bean.User;
import org.rebioma.client.services.OccurrenceService.OccurrenceServiceException;
import org.rebioma.server.upload.Traitement;

/**
 * Service interface for data object access of {@link Occurrence}.
 * 
 */
// @ImplementedBy(OccurrenceDbImpl.class)
public interface OccurrenceDb {
  public static class AttributeValue {
    private String attribute;
    private String value;

    public AttributeValue() {
    }

    public AttributeValue(String attribute, String value) {
      this.attribute = OccurrenceDbImpl.getOccurrencePropertyName(attribute);
      this.value = value;
    }

    public boolean equals(Object obj) {
      if (obj == null || !(obj instanceof AttributeValue)) {
        return false;
      }
      AttributeValue attributeValue = (AttributeValue) obj;
      return attribute.equals(attributeValue.getAttribute())
          && value.equals(attributeValue.getValue());
    }

    /**
     * @return the attribute
     */
    public String getAttribute() {
      return attribute;
    }

    /**
     * @return the value
     */
    public String getValue() {
      return value;
    }

    public int hashCode() {
      return toString().hashCode();
    }

    public boolean isValid() {
      return attribute != null;
    }

    /**
     * @param attribute the attribute to set
     */
    public void setAttribute(String attribute) {
      this.attribute = attribute;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
      this.value = value;
    }

    public Criterion toCriterion() {
      return Restrictions.eq(attribute, value);
    }

    public String toString() {
      return attribute + "=" + value;
    }
  }

  /**
   * Assigns all records that match the taxonomic field and value pair to the
   * given user.
   * 
   * @param userEmail the user email that records gonna be assigned to.
   * @param taxoFieldName the name of the taxonomic field.
   * @param taxoFieldValue the value of the corresponding taxonomic field
   * @return true if it is successfully saved, false if the assignment is
   *         already existed.
   * @throws OccurrenceServiceException
   */
  public int assignReviewer(String userEmail, String taxoFieldName,
      String taxoFieldValue, boolean isMarine, boolean isTerrestrial) throws OccurrenceServiceException;

  /**
   * Attaches a clean instance of {@link Occurrence} to the database.
   * 
   * @param instance an occurrence
   */
  public void attachClean(Session session,Occurrence instance);

  /**
   * Attaches a dirty instance of {@link Occurrence} to the database.
   * 
   * @param instance the occurrence
   */
  public void attachDirty(Occurrence instance);

  /**
   * Attaches a dirty set of {@link Occurrence} objects to the database.
   * 
   * @param instances the set of occurrences
   */
  public void attachDirty(Set<Occurrence> instances, Traitement traitement, List<RecordReview> rcdrv, boolean clearReview, boolean isSA);
  
  public void attachDirty(Set<Occurrence> instances);

  public void attachDirty(Set<Occurrence> instances, boolean resetReview);

  /**
   * Deletes a persistent instance of {@link Occurrence} from the database.
   * 
   * @param persistentInstances the occurrence
   */
  public void delete(Occurrence persistentInstance);

  /**
   * Deletes a set of persistent instances of {@link Occurrence} from the
   * database.
   * 
   * @param persistentInstances the occurrence
   */
  public void delete(Set<Occurrence> persistentInstance);

  /**
   * Deletes all {@link Occurrence} that match the query filter.
   * 
   * @param query the query defines all records to be deleted.
   * @param user current logged in user id.
   * @return number of records get deleted.
   */
  public int detele(OccurrenceQuery query, User user);

  /**
   * Find all {@link Occurrence} that match all given the attribute value pair.
   * 
   * @param attributeValue of {@link AttributeValue} pair use to query.
   * @return {@link List} of {@link Occurrence}
   */
  public List<Occurrence> findByAttributeValue(AttributeValue attributeValue);

  /**
   * Find all {@link Occurrence} that match all given the attribute value pairs.
   * 
   * @param attributeValues {@link Set} of {@link AttributeValue} pair use to
   *          query.
   * @return {@link List} of {@link Occurrence}
   */
  public List<Occurrence> findByAttributeValues(
      Set<AttributeValue> attributeValues);

  /**
   * Finds an {@link Occurrence} by using an instance of {@link Occurrence} in
   * the database.
   * 
   * @param instance the example occurrence
   * @return an occurrence
   */
  public List<Occurrence> findByExample(Occurrence instance);

  /**
   * Finds a list of {@link Occurrence} by using instances of {@link Occurrence}
   * in the database.
   * 
   * @param instances the example occurrences
   * @return a list of occurrences
   */
  public List<Occurrence> findByExample(Set<Occurrence> instances);

  /**
   * Finds an {@link Occurrence} by id.
   * 
   * @param id the occurrence id
   * @return an occurrence
   */
  public Occurrence findById(java.lang.Integer id);

  /**
   * Finds all {@link Occurrence} by id.
   * 
   * @param ids the occurrence ids
   * @return a list of occurrences
   */
  public List<Occurrence> findById(Set<Integer> ids);

  public List<Occurrence> findByOccurrenceQuery(OccurrenceQuery query,
      Integer userId);

  /**
   * Finds {@link Occurrence} objects in the database that match a set of
   * filters. Valid filter takes the following form:
   * 
   * "concept <operator> value"
   * 
   * where concept can match any of the concept names found in the Darwin Core,
   * the Curatorial Extension, and the Geospatial Extension, and operator is
   * either '=' or 'like'. For example, "Genus like Coeliades" or
   * "DecimalLatitude = 38.4332345" are valid filters.
   * 
   * If the user id is not null, and the query type set includes
   * {@link OccurrenceType} PRIVATE, then it's used to select occurrences owned
   * by the user.
   * 
   * @param query the occurrence query
   * @param user the user id
   * @return list of {@link Occurrence} objects
   * @throws Exception
   */
  public List<Occurrence> findByOccurrenceQuery(OccurrenceQuery query, User user)
      throws Exception;

  /**
   * Merges changes in a detached instance of {@link Occurrence}.
   * 
   * @param detachedInstance the occurrence
   * @return the merged occurrence
   */
  public Occurrence merge(Occurrence detachedInstance);

  /**
   * Persists a transient instance of {@link Occurrence}.
   * 
   * @param transientInstance the occurrence
   */
  public void persist(Occurrence transientInstance);

  /**
   * Removes occurrences from the set instances which should not be uploaded for
   * the following reasons: 1) Id is formatted improperly 2) Id does not
   * correspond to any occurrence in the database 3) Multiple occurrences with
   * the same Id
   * 
   * @param instances the set of occurrences to be uploaded
   * @param logginUser TODO
   */
  public String removeBadId(Set<Occurrence> instances, User logginUser);

  public void resetReviews();

  /**
   * Updates all {@link Occurrence} objects that match the query filter and
   * updates.
   * 
   * @param query the query the defines the update
   * @param user current user id.
   * @return number of records get updated;
   */
  public int update(OccurrenceQuery query, User user);

  public String validateOccurrences(Set<Occurrence> instances, User logginUser,
      Map<User, Set<Occurrence>> userOccurrencesMap);

  boolean checkForReviewedChanged(int occurrenceId);

  List<Integer> findOccurrenceIdsByQuery(OccurrenceQuery query, User user)
      throws Exception;

  List<OccurrenceReview> getOccurrenceReviewsOf(int occurrenceId);
  
  
  public void resetRecordReview(Occurrence occurrence, boolean isStable);

  public void resetRecordReview(Occurrence occurrence, boolean isStable,Session sess);
  
  public void updateStability(Occurrence o, Boolean stability,Session sess);
  
  /**
   * refreshing the all the variable (list of trb)
   */
  public void refresh();

}
