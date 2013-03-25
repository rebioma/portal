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
package org.rebioma.server.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Helper class for getting, setting, and resetting {@link HttpServletRequest}
 * and {@link HttpServletResponse} {@link Cookie}s.
 * 
 * @author eighty
 * 
 */
public class CookieUtil {

  /**
   * Returns the cookie value for a specific cookie in the a request.
   * 
   * @param request the request
   * @param cookieName the name of the cookie
   * @return the cookie value
   */
  public static String getRequestCookieValue(HttpServletRequest request,
          String cookieName) {
    if (request == null || request.getCookies() == null) {
      return null;
    }
    for (Cookie c : request.getCookies()) {
      if (c == null) {
        continue;
      }
      if (c.getName().equalsIgnoreCase(cookieName)) {
        return c.getValue();
      }
    }
    return null;
  }

  /**
   * Resets cookie values in the request and response.
   * 
   * @param request the request
   * @param response the response
   * @param cookieName the name of the cookie to reset
   */
  public static void resetCookieValue(HttpServletRequest request,
          HttpServletResponse response, String cookieName) {
    if (request.getCookies() == null) {
      response.addCookie(new Cookie(cookieName, null));
      return;
    }
    for (Cookie c : request.getCookies()) {
      if (c.getName().equalsIgnoreCase(cookieName)) {
        c.setValue(null);
        response.addCookie(c);
      }
    }
  }

  public static void setCookie(HttpServletRequest request,
          HttpServletResponse response, String uniqueIdCookieName, String uuid) {
    if (request.getCookies() == null) {
      response.addCookie(new Cookie(uniqueIdCookieName, uuid));
      return;
    }
    for (Cookie c : request.getCookies()) {
      if (c.getName().equalsIgnoreCase(uniqueIdCookieName)) {
        c.setValue(uuid);
        response.addCookie(c);
        return;
      }
    }
    Cookie c = new Cookie(uniqueIdCookieName, uuid);
    response.addCookie(c);
  }
}