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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.rebioma.client.bean.AscData;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.services.AscDataService;
import org.rebioma.server.overlays.ASCFileReader;
import org.rebioma.server.overlays.ASCReaderProvider;
import org.rebioma.server.overlays.StoragePathManager;
import org.rebioma.server.util.AscDataUtil;
import org.rebioma.server.util.ManagedSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implementation of {@link AscDataService}.
 * 
 */
@SuppressWarnings("serial")
public class AscDataServiceImpl extends RemoteServiceServlet implements
    AscDataService {

  private final AscDataDb ascDataDb = DBFactory.getAscDataDb();

  // private final OccurrenceDb occurrenceDb = DBFactory.getOccurrenceDb();
  // private final UpdateService updateService = DBFactory.getUpdateService();

  public List<AscData> getAscData() throws AscDataServiceException {
    try {
      return ascDataDb.findAll();
    } catch (Exception e) {
      throw new AscDataServiceException(e.toString());
    }
  }

  public Map<String, AscData> getAscDataMap() {
    return AscDataUtil.convertDescriptionsMap(ascDataDb.findAll());
  }

  public Double getValue(int ascDataId, double lat, double lng)
      throws AscDataServiceException {
    try {
      //Session session = HibernateUtil.getCurrentSession();
      //boolean isFirstTransaction = HibernateUtil.beginTransaction(session);
      Session session = ManagedSession.createNewSessionAndTransaction();
      AscData evd = (AscData) session
          .get(AscData.class, new Integer(ascDataId));

      // optimization: if outside bounds, return null
      boolean outsideBounds = evd.getSouthBoundary() > lat
          || evd.getNorthBoundary() < lat || evd.getWestBoundary() > lng
          || evd.getEastBoundary() < lng;
      if (outsideBounds) {
        //if (isFirstTransaction) {
        //  HibernateUtil.commitCurrentTransaction();
        //}
    	ManagedSession.commitTransaction(session);
        return null;
      }

      String ascPath = StoragePathManager.getStoragePath(evd.getFileName(),
          super.getServletContext().getRealPath("/"));
      ASCFileReader asc = ASCReaderProvider.getReader(ascPath);
      //if (isFirstTransaction) {
      //  HibernateUtil.commitCurrentTransaction();
      //}
      ManagedSession.commitTransaction(session);
      // session.close();
      double val = asc.getValue(lat, lng);
      if (val == asc.noDataValue()) {
        return null;
      } else {
        return new Double(val);
      }
    } catch (HibernateException e) {
      //HibernateUtil.rollbackTransaction();
      throw new AscDataServiceException("DB error, see server log");
    } catch (IOException e) {
      //HibernateUtil.rollbackTransaction();
      throw new AscDataServiceException("File error, see server log");
    }
  }

  public Occurrence loadAscData(Occurrence occurrence)
      throws AscDataServiceException {
    AscDataUtil.setLayerValuesToOccurrence(occurrence);
    // occurrenceDb.attachDirty(occurrence);
    // updateService.update();
    return occurrence;
  }

}
