package org.rebioma.server.services;

import org.rebioma.client.AscModelResult;
import org.rebioma.client.bean.AscModel;

public interface AscModelDb {

  /**
   * Get a list of {@link AscModel} which contains location of the model for the
   * given Accepted species
   * 
   * @param acceptedSpecies
   * @param start
   * @param limit
   * @return AscModelResult
   */
  AscModelResult findAscModel(String acceptedSpecies, int start, int limit, int startM, int limitM);
}