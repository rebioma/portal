package org.rebioma.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.List;
import org.rebioma.client.bean.Taxonomy;

public interface TaxonomyServiceAsync {
	 public void maj(String category,String scientific_name,AsyncCallback<Void> callback);
	 public void threatenedSpecies(String status,AsyncCallback<List<Taxonomy>> callback);
	 public void getiucn_status(AsyncCallback<List<Taxonomy>> callback);
}

