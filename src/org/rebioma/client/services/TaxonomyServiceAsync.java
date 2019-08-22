package org.rebioma.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TaxonomyServiceAsync {
	 public void maj(String category,String scientific_name,AsyncCallback<Void> callback);
}

