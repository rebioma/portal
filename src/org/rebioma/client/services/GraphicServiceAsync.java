package org.rebioma.client.services;

import java.util.List;

import org.rebioma.client.bean.GraphicModel;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.Taxonomy;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GraphicServiceAsync {
	void getCountSpeciesGpByKingdom (AsyncCallback<List<Taxonomy>> asyncCallback);
	void getCountSpeciesGpByIUCN_Status(AsyncCallback<List<Taxonomy>> asyncCallback);
	void getCountSpeciesGpByKingdomGpByIUCN_Status(String kingdom,AsyncCallback<List<Taxonomy>> asyncCallback);
	void getCountSpeciesTerrestreGpByIUCN_Status(AsyncCallback<List<Taxonomy>> asyncCallback);
	void getCountTSpeciesGpByKingdom(AsyncCallback<List<Taxonomy>> asyncCallback);
	void getCountSpeciesTerrestreGpByKingdomGpByIUCN_Status(String kingdom,AsyncCallback<List<Taxonomy>> asyncCallback);
	void getCountSpeciesMarinGpByIUCN_Status(AsyncCallback<List<Taxonomy>> asyncCallback);
	void getCountMSpeciesGpByKingdom(AsyncCallback<List<Taxonomy>> asyncCallback);
	void getCountMSpeciesGpByKingdomAndIUCN_cat(String kingdom,AsyncCallback<List<Taxonomy>> asyncCallback);
	void getOccurrenceByRegion(AsyncCallback<List<GraphicModel>> asyncCallback);
	void getOccPerYear(AsyncCallback<List<Occurrence>> asyncCallback);
}
