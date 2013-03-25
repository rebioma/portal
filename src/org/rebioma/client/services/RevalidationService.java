package org.rebioma.client.services;

import org.rebioma.client.bean.RevalidationResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
/**
 * 
 * @author mikajy
 *
 */
@RemoteServiceRelativePath("revalidationService")
public interface RevalidationService extends RemoteService {
	/**
	   * Provides a singleton proxy to {@link RevalidationService}. Clients typically
	   * use the proxy as follows:
	   * 
	   * RevalidationService.Proxy.get();
	   */
	  public static class Proxy {

	    /**
	     * The singleton proxy instance.
	     */
	    private static RevalidationServiceAsync service;

	    /**
	     * Returns the singleton proxy instance and creates it if needed.
	     */
	    public static synchronized RevalidationServiceAsync get() {
	      if (service == null) {
	        service = GWT.create(RevalidationService.class);
	      }
	      return service;
	    }
	  }
	  /**
	   * Revalidate all unstable occurrences.
	   * @throws Exception
	   */
	  public RevalidationResult revalidate(String sessionId) throws Exception ;
	  
	  /**
	   * Annuler une revalidation en cours
	   * @throws Exception
	   */
	  public void cancelRevalidation(String sessionId) throws Exception;
}
