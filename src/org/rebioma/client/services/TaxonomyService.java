package org.rebioma.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import java.util.List;
import org.rebioma.client.bean.Taxonomy;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
@RemoteServiceRelativePath("TaxonomyService")
public interface TaxonomyService extends RemoteService {
	public void maj(String category,String scientific_name);
	List<Taxonomy> threatenedSpecies(String status);
	List<Taxonomy> getiucn_status();
	List<Taxonomy> getKingdom();
	List<Taxonomy> getKingdomT();
	List<Taxonomy> getKingdomM();
}
