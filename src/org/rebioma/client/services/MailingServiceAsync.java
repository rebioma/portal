package org.rebioma.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>MailingServiceImpl</code>.
 */
public interface MailingServiceAsync {

	void getMailingStat(AsyncCallback<String[]> callback);

	void setMailing(String stat, String frequency, String date, String url,
			AsyncCallback<Boolean> callback);
}
