/**
 * 
 */
package org.rebioma.server.services;

import java.util.ArrayList;
import java.util.List;

import org.rebioma.client.bean.SearchFieldNameValuePair;
import org.rebioma.client.services.OccurrenceSearchService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author Mikajy
 */
public class OccurrenceSearchServiceImpl extends RemoteServiceServlet implements OccurrenceSearchService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -23288874237529960L;

	/* (non-Javadoc)
	 * @see org.rebioma.client.services.OccurrenceSearchService#getSearchFieldNameValuePair(java.lang.String, org.rebioma.client.OccurrenceQuery)
	 */
	@Override
	public List<SearchFieldNameValuePair> getSearchFieldNameValuePair(
			String query) {
		
		List<SearchFieldNameValuePair> nvpList = new ArrayList<SearchFieldNameValuePair>();
		for(int i=0;i< 10;i++){
			SearchFieldNameValuePair nvp = new SearchFieldNameValuePair();
			nvp.setFieldName("fieldName " + i);
			nvp.setFieldValue("fieldValue "+ i);
			nvpList.add(nvp);
		}
		return nvpList;
	}

}
