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
package org.rebioma.client;

import org.rebioma.client.bean.Occurrence;

/**
 * Interface for listening to when {@link Occurrence} objects are selected.
 * 
 */
public interface OccurrenceListener {

  /**
   * Fired when the {@link Occurrence} has been selected.
   * 
   * @param occurrence the selected occurrence
   */
  public void onOccurrenceSelected(Occurrence occurrence);
}
