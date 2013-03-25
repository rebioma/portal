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

import java.util.Date;

/**
 * Service for updating and getting the last update time of
 * {@link OccurrenceUpdates}.
 * 
 */
// @ImplementedBy(UpdateServiceImpl.class)
public interface UpdateService {

  /**
   * @return the last date when an occurrence was created, updated, or deleted
   */
  public Date getLastUpdate();

  /**
   * Updates the OccurrenceUpdates table with the current time.
   */
  public void update();
}
