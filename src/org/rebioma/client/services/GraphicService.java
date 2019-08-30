package org.rebioma.client.services;

import java.util.List;

import org.rebioma.client.bean.GraphicModel;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.Taxonomy;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("GraphicService")
public interface GraphicService extends RemoteService {
	List<Taxonomy> getCountSpeciesGpByKingdom();
	List<Taxonomy> getCountSpeciesGpByIUCN_Status();
	List<Taxonomy> getCountSpeciesGpByKingdomGpByIUCN_Status(String kingdom);
	List<Taxonomy> getCountSpeciesTerrestreGpByIUCN_Status();
	List<Taxonomy> getCountTSpeciesGpByKingdom();
	List<Taxonomy> getCountSpeciesTerrestreGpByKingdomGpByIUCN_Status(String kingdom);
	List<Taxonomy> getCountMSpeciesGpByKingdom();
	List<Taxonomy> getCountSpeciesMarinGpByIUCN_Status();
	List<Taxonomy> getCountMSpeciesGpByKingdomAndIUCN_cat(String kingdom);
	List<Occurrence> getOccPerYear();
	List<GraphicModel> getOccurrenceByRegion();
}
