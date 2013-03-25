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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Keeps a global variable <code>ascPath</code> representing the location of
 * various .asc files.
 * 
 * The location is specified in "rebioma.properties" using key
 * "asc_storage_path"
 * 
 * @author daen
 * 
 */
public class StoragePathManager {
  private static Logger log = Logger.getLogger(StoragePathManager.class);

  private static String storageDir;

  private static final String DEFAULT_STORAGE_PATH = "/rebioma/storage";

  private static final String PROPERTIES_FILE = "/rebioma.properties";

  private static final String PROPERTIES_STORAGE_KEY = "storage_path";

  public static String getStoragePath() {
    if (storageDir != null)
      return storageDir;

    String path = DEFAULT_STORAGE_PATH;
    try {
      Properties prop = new Properties();
      InputStream propStream = StoragePathManager.class
          .getResourceAsStream(PROPERTIES_FILE);
      prop.load(propStream);
      path = prop.getProperty(PROPERTIES_STORAGE_KEY);
    } catch (IOException e) {
      log.warn("Using default value for storage directory");
    }

    log.info("Storage directory: " + path);

    storageDir = path;
    return storageDir;
  }

  public static String getStoragePath(String fileName, String webRoot)
      throws IOException {
    File file = new File(getStoragePath(), fileName);
    if (file.exists()) {
      return file.getCanonicalPath();
    }
    return webRoot + fileName;
  }
}
