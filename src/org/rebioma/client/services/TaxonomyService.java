package org.rebioma.client.services;

import java.util.List;

import org.rebioma.client.bean.Iucn;
import org.rebioma.client.bean.Taxonomy;
import org.rebioma.client.bean.ThreatenedSpeciesModel;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
@RemoteServiceRelativePath("TaxonomyService")
public interface TaxonomyService extends RemoteService {
	public void maj(String category,String scientific_name);
	List<Taxonomy> threatenedSpecies(String status);
	List<Taxonomy> getiucn_status();
	List<Taxonomy> getKingdom();
	List<Taxonomy> getKingdomT();
	List<Taxonomy> getKingdomM();
	public void majstatut_iucn_occ();
	PagingLoadResult<ThreatenedSpeciesModel> threatenedSpecies(PagingLoadConfig config, Iucn iucnStatus);
	PagingLoadResult<Iucn> getIucnStatus(PagingLoadConfig config);
}
