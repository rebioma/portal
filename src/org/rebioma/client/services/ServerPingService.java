package org.rebioma.client.services;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
/**
 * 
 * @author mikajy
 *
 */
@RemoteServiceRelativePath("serverPingService")
public interface ServerPingService extends RemoteService {
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
	    private static ServerPingServiceAsync service;

	    /**
	     * Returns the singleton proxy instance and creates it if needed.
	     */
	    public static synchronized ServerPingServiceAsync get() {
	      if (service == null) {
	        service = GWT.create(ServerPingService.class);
	      }
	      return service;
	    }
	  }
	  
	  public void ping();

}
