package org.rebioma.client.services;

import java.util.List;

import org.rebioma.client.AscModelResult;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RebiomaModelServiceAsync {

  void findModelLocation(String acceptedSpecies, int start, int limit, int startM, int limitM,
      AsyncCallback<AscModelResult> callback);

  void getModelClimateEras(String modelLocation,
      AsyncCallback<List<String>> callback);

}