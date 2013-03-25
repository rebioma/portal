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

import java.io.File;
import java.io.IOException;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.rebioma.client.Occurrence;

/**
 * The CsvUtilTest class provides unit testing for {@link CsvUtil}.
 * 
 */
public class CsvUtilTest extends TestCase {

  private static final Logger log = Logger.getLogger(CsvUtilTest.class);

  /**
   * Loads {@link Occurrence} objects from a CSV file.
   */
  public void testLoad() {
    File csvFile = new File(CsvUtilTest.class.getResource(
            "MaNISMadagascarDwC14.txt").getFile());
    log.info("Test CSV data size: " + csvFile.length());
    long start = System.currentTimeMillis();
    try {
      Set<Occurrence> set = CsvUtil.loadOccurrences(csvFile);
      log.info(((System.currentTimeMillis() - start) / 1000.0)
              + " seconds to load " + set.size() + " occurrences.");
      assertNotNull(set);
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }
}
