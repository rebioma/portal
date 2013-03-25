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

import java.util.Random;

/**
 * This class is used to create random integer and string in a specific digit
 * range
 * 
 * @author Tri
 * 
 */
public class RandomUtil {
  private static Random rn = new Random();

  /**
   * 
   * @return a randomly generate password with at least 8 characters and at most
   * 20 characters
   */
  public static String generateRandomPassword() {
    return randomString(8, 20);
  }

  /**
   * 
   * @return a randomly generated session id with 20 characters.
   */
  public static String generateSessionId() {
    return randomString(20, 20);
  }

  /**
   * This method is used to randomly generate a integer between minDigits and
   * maxDigits
   * 
   * @param minDigits the positive minimum generate integer
   * @param maxDigits the positive maximum generate integer
   * @return a positive integer between minDigits and maxDigits
   */
  public static int randomRangeBound(int minDigits, int maxDigits) {
    int digitBoundDiff = maxDigits - minDigits + 1;
    int i = rn.nextInt() % digitBoundDiff;
    if (i < 0) {
      i = -i;
    }
    return minDigits + i;
  }

  /**
   * This method is used to randomly generate a string with at least minChars
   * and at most maxChars
   * 
   * @param minChars minimum characters of this string
   * @param maxChars maximum characters of this string
   * @return random String with at least min characters and at most max
   * characters
   */
  public static String randomString(int minChars, int maxChars) {
    int randomCharsRange = randomRangeBound(minChars, maxChars);
    byte randomChars[] = new byte[randomCharsRange];
    randomChars[0] = (byte) randomRangeBound('0', '9');
    for (int i = 1; i < randomCharsRange; i++) {
      randomChars[i] = (byte) randomRangeBound('a', 'z');
    }
    return new String(randomChars);
  }

  private RandomUtil() {
  }
}
