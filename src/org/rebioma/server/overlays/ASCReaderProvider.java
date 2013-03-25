/*
 * Copyright 2008 University of California at Berkeley.
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
package org.rebioma.server.overlays;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class ASCReaderProvider {
  private static class CacheEntry {
    public long lastUsedTime;
    public ASCFileReader reader;
  }

  private static Map<String, CacheEntry> cache = new HashMap<String, CacheEntry>();

  private static Logger log = Logger.getLogger(ASCReaderProvider.class);

  private static int MAX_FILES = 5;

  public synchronized static ASCFileReader getReader(String completePath)
      throws IOException {
    CacheEntry ce = cache.get(completePath);
    if (ce != null) {
      ce.lastUsedTime = System.currentTimeMillis();
    } else {
      long beginTime = System.currentTimeMillis();
      if (cache.size() >= MAX_FILES) {
        removeOldest();
      }

      ce = new CacheEntry();
      ce.reader = new ASCFileReader(completePath);
      ce.lastUsedTime = System.currentTimeMillis();
      cache.put(completePath, ce);

      long endTime = System.currentTimeMillis();
      log.info("Cache miss, " + (endTime - beginTime) + "ms to add to cache");
    }
    return ce.reader;
  }

  private synchronized static void removeOldest() {
    log.info("Removing ASCReader from cache");

    String oldestPath = null;
    long oldestTime = Long.MAX_VALUE;

    for (String path : cache.keySet()) {
      CacheEntry ce = cache.get(path);
      if (ce.lastUsedTime < oldestTime) {
        oldestPath = path;
        oldestTime = ce.lastUsedTime;
      }
    }

    assert (oldestPath != null);

    cache.remove(oldestPath);

  }
}
