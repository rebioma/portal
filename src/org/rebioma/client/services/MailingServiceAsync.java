package org.rebioma.client.services;

import java.util.Date;
import java.util.List;

import org.rebioma.client.bean.OccurrenceCommentModel;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;

/**
 * The async counterpart of <code>MailingServiceImpl</code>.
 */
public interface MailingServiceAsync {

	void getMailingStat(AsyncCallback<String[]> callback);
	
	void setMailing(String stat, String frequency, String date, String url,
			AsyncCallback<Boolean> callback);
	
	void getOccurrenceComments(PagingLoadConfig config, String mailTo, Date date1, Date date2, 
			AsyncCallback<PagingLoadResult<OccurrenceCommentModel>> callback);

	void sendSelected(String mailTo, Date date1, Date date2,
			List<OccurrenceCommentModel> list, AsyncCallback<Boolean> callback);

	void sendEmail(String model, String title, String firstN, String lastN,
			String activity, String email, String institution, String dataUE,
			AsyncCallback<Void> callback);
	 
}
