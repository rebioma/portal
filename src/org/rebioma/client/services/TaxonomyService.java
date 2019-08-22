package org.rebioma.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
@RemoteServiceRelativePath("TaxonomyService")
public interface TaxonomyService extends RemoteService {
	public void maj(String category,String scientific_name);
}
