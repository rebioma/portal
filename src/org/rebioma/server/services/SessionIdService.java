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

import org.rebioma.client.bean.User;

/**
 * This is a service interface for sessions. It is used to check for valid
 * session ids and to get user ids for valid sessions.
 * 
 */
// @ImplementedBy(SessionIdServiceImpl.class)
public interface SessionIdService {
  /**
   * Returns the {@link User} associated with a session id. If the session id is
   * not valid, returns null.
   * 
   * @param sid the session id
   * @return user session id is valid, otherwise null
   */
  public User getUserBySessionId(String sid);

  /**
   * Returns true if the session id is valid by checking the user database.
   * 
   * @param sid the session id to check
   * @return true if session id is valid, otherwise false
   */
  public boolean isSessionIdValid(String sid);
}
