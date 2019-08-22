package org.rebioma.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("HomestatService")
public interface HomestatService extends RemoteService {
	public int getCountOccurrence();
	public int getCountUser();
	public int getCountSpecies();
}
