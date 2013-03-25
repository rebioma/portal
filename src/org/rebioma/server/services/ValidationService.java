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

import java.util.Set;

import org.rebioma.client.bean.Occurrence;
import org.rebioma.server.upload.Traitement;

/**
 * A service interface for validating {@link Occurrence} data against rules
 * defined by ReBioMa for quality and completeness.
 * 
 * @see http://code.google.com/p/rebioma/wiki/Validation
 */
// @ImplementedBy(ValidationServiceImpl.class)
public interface ValidationService {

  /**
   * Validates a set of {@link Occurrence} objects by setting the validated
   * property of each object.
   * 
   * @param occurrences the set of occurrence objects to validate
   */
  public void validate(Set<Occurrence> occurrences, Traitement traitement);
  
  public void validate(Set<Occurrence> occurrences);
}
