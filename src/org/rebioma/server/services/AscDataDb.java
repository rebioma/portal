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
import java.util.Set;

import org.rebioma.client.bean.AscData;

/**
 * Service interface for data object access of {@link AscData}.
 * 
 */
// @ImplementedBy(AscDataDbImpl.class)
public interface AscDataDb {
  /**
   * Attaches a clean instance of {@link AscData} to the database.
   * 
   * @param instance an AscData
   */
  public void attachClean(AscData instance);

  /**
   * Attaches a dirty instance of {@link AscData} to the database.
   * 
   * @param instance the AscData
   */
  public void attachDirty(AscData instance);

  /**
   * Attaches a dirty set of {@link AscData} objects to the database.
   * 
   * @param instances the set of AscDatas
   */
  public void attachDirty(Set<AscData> instances);

  /**
   * Deletes a persistent instance of {@link AscData} from the database.
   * 
   * @param persistentInstances the AscData
   */
  public void delete(AscData persistentInstance);

  /**
   * Deletes a set of persistent instances of {@link AscData} from the database.
   * 
   * @param persistentInstances the AscData
   */
  public void delete(Set<AscData> persistentInstance);

  /**
   * Returns a list of all {@link AscData} in the database.
   */
  public List<AscData> findAll();

  /**
   * Finds an {@link AscData} by using an instance of {@link AscData} in the
   * database.
   * 
   * @param instance the example AscData
   * @return an AscData
   */
  public List<AscData> findByExample(AscData instance);

  /**
   * Finds a list of {@link AscData} by using instances of {@link AscData} in
   * the database.
   * 
   * @param instances the example AscDatas
   * @return a list of AscDatas
   */
  public List<AscData> findByExample(Set<AscData> instances);

  /**
   * Finds an {@link AscData} by id.
   * 
   * @param id the AscData id
   * @return an AscData
   */
  public AscData findById(java.lang.Integer id);

  /**
   * Finds all {@link AscData} by id.
   * 
   * @param ids the AscData ids
   * @return a list of AscDatas
   */
  public List<AscData> findById(Set<Integer> ids);

  /**
   * Merges changes in a detached instance of {@link AscData}.
   * 
   * @param detachedInstance the AscData
   * @return the merged AscData
   */
  public AscData merge(AscData detachedInstance);

  /**
   * Persists a transient instance of {@link AscData}.
   * 
   * @param transientInstance the AscData
   */
  public void persist(AscData transientInstance);
}
