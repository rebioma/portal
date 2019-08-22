package org.rebioma.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface HomestatServiceAsync{
	public void getCountOccurrence(AsyncCallback<Integer> asyncCallback);
	public void getCountUser(AsyncCallback<Integer> asyncCallback);
	public void getCountSpecies(AsyncCallback<Integer> asyncCallback);
}
