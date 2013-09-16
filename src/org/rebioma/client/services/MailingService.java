package org.rebioma.client.services;

import java.util.Date;
import java.util.List;

import org.rebioma.client.bean.OccurrenceCommentModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("mailingService")
public interface MailingService extends RemoteService {
	String[] getMailingStat();

	boolean setMailing(String stat, String frequency, String date, String url);
	
	public static class Proxy {

	    /**
	     * The singleton proxy instance.
	     */	
		private static MailingServiceAsync service;

	    /**
	     * Returns the singleton proxy instance and creates it if needed.
	     */
	    public static synchronized MailingServiceAsync get() {
	    	if (service == null) {
	    	  service = GWT.create(MailingService.class);
	      }
	      return service;
	    }
	}
	
	PagingLoadResult<OccurrenceCommentModel> getOccurrenceComments(PagingLoadConfig config, String mailTo, Date date1, Date date2);
	 
	boolean sendSelected(String mailTo, Date date1, Date date2, List<OccurrenceCommentModel> list);
}
