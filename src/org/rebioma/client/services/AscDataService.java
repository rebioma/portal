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
package org.rebioma.client.services;

import java.util.List;
import java.util.Map;

import org.rebioma.client.bean.AscData;
import org.rebioma.client.bean.Occurrence;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Service interface for {@link AscData}.
 * 
 */
@RemoteServiceRelativePath("ascDataService")
public interface AscDataService extends RemoteService {

  /**
   * Custom exception for AscData service errors.
   */
  @SuppressWarnings("serial")
  public class AscDataServiceException extends Exception implements
          IsSerializable {
    public AscDataServiceException() {
      this(null);
    }

    public AscDataServiceException(String msg) {
      super(msg);
    }
  }

  /**
   * Provides a singleton proxy to {@link AscDataService}. Clients typically use
   * the proxy as follows:
   * 
   * AscDataService.Proxy.get();
   */
  public static class Proxy {

    /**
     * The singleton proxy instance.
     */
    private static AscDataServiceAsync service;

    /**
     * Returns the singleton proxy instance and creates it if needed.
     */
    public static synchronized AscDataServiceAsync get() {
      if (service == null) {
        service = GWT.create(AscDataService.class);
      }
      return service;
    }
  }

  /**
   * Returns all {@link AscData} from the server.
   * 
   * @throws AscDataServiceException
   */
  public List<AscData> getAscData() throws AscDataServiceException;

  /**
   * Gets all {@link AscData} in form of Map from ascData's description + year
   * to its AscData.
   * 
   * @return a Map from ascData's description + " - " + year (i.e Mean
   * precipitation of the wettest month - 2000) to its AscData.
   */
  public Map<String, AscData> getAscDataMap();

  /**
   * Returns the value for the given {@link AscData} id at point.
   * 
   * @param ascDataId id that identifies the asc data
   * @param lat coordinate lat
   * @param lng coordinate lng
   * @throws AscDataServiceException
   */
  public Double getValue(int ascDataId, double lat, double lng)
          throws AscDataServiceException;

  /**
   * Lookups and Loads {@link AscData} layer values to {@link Occurrence}.
   * 
   * @param occurrence {@link Occurrence} to be loaded.
   * @throws AscDataServiceException
   */
  public Occurrence loadAscData(Occurrence occurrence)
          throws AscDataServiceException;
}
