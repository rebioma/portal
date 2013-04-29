package org.rebioma.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("mailingService")
public interface MailingService extends RemoteService {
	String[] getMailingStat();

	boolean setMailing(String stat, String frequency, String date, String url);
}
