/**
 * 
 */
package org.rebioma.client.services;

import java.util.List;

import org.rebioma.client.OccurrenceQuery;
import org.rebioma.client.bean.SearchFieldNameValuePair;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Mikajy
 */
@RemoteServiceRelativePath("occurrenceSearchService")
public interface OccurrenceSearchService extends RemoteService{
	
	/**
	   * Provides a singleton proxy to {@link OccurrenceSearchService}. Clients typically
	   * use the proxy as follows:
	   * 
	   * OccurrenceSearchService.Proxy.get();
	   */
	  public static class Proxy {

	    /**
	     * The singleton proxy instance.
	     */
	    private static OccurrenceSearchServiceAsync service;

	    /**
	     * Returns the singleton proxy instance and creates it if needed.
	     */
	    public static synchronized OccurrenceSearchServiceAsync get() {
	      if (service == null) {
	        service = GWT.create(OccurrenceSearchService.class);
	      }
	      return service;
	    }
	  }

	/**
	   * 
	   * @param sessionId
	   * @param query
	   * @return
	   */
	  List<SearchFieldNameValuePair> getSearchFieldNameValuePair(String query);

}
