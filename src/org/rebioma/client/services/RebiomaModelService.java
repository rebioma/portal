package org.rebioma.client.services;

import java.util.List;

import org.rebioma.client.AscModelResult;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("model")
public interface RebiomaModelService extends RemoteService {

  AscModelResult findModelLocation(String acceptedSpecies, int start, int limit,
		int startM, int limitM);

  /**
   * Gets folder name of the model climate era from the given model folder
   * location.
   * 
   * @param modelLocation
   * @return
   */
  List<String> getModelClimateEras(String modelLocation);

}
