package org.rebioma.client.services;

import java.util.List;

import org.rebioma.client.bean.Iucn;
import org.rebioma.client.bean.Taxonomy;
import org.rebioma.client.bean.ThreatenedSpeciesModel;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;

public interface TaxonomyServiceAsync {
	public void maj(String category,String scientific_name,AsyncCallback<Void> callback);
	public void threatenedSpecies(String status,AsyncCallback<List<Taxonomy>> callback);
	public void getiucn_status(AsyncCallback<List<Taxonomy>> callback);
	public void getKingdom(AsyncCallback<List<Taxonomy>> callback);
	public void getKingdomT(AsyncCallback<List<Taxonomy>> callback);
	public void getKingdomM(AsyncCallback<List<Taxonomy>> callback);
	public void majstatut_iucn_occ(AsyncCallback<Void> callback);
	void threatenedSpecies(PagingLoadConfig config, Iucn iucnStatus,
			AsyncCallback<PagingLoadResult<ThreatenedSpeciesModel>> callback);
	public void getIucnStatus(PagingLoadConfig config, AsyncCallback<PagingLoadResult<Iucn>> callback);
}

