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
import org.rebioma.client.services.AscDataService.AscDataServiceException;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * AsyncService interface for {@link AscData}.
 * 
 */
public interface AscDataServiceAsync extends IsSerializable {

  public Request getAscData(AsyncCallback<List<AscData>> cb);

  public Request getAscDataMap(AsyncCallback<Map<String, AscData>> cb);

  public Request getValue(int ascDataId, double lat, double lng,
          AsyncCallback<Double> cb);

  /**
   * Lookups and Loads {@link AscData} layer values to {@link Occurrence}.
   * 
   * @param occurrence {@link Occurrence} to be loaded.
   * @throws AscDataServiceException
   */
  public Request loadAscData(Occurrence occurrence, AsyncCallback<Occurrence> cb);
}
