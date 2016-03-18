/**
 * 
 */
package org.rebioma.client.services;

import java.util.List;

import org.rebioma.client.OccurrenceQuery;
import org.rebioma.client.bean.SearchFieldNameValuePair;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Asynchronous interface for {@link OccurrenceSearchService}.
 * 
 * @author Mikajy
 *
 */
public interface OccurrenceSearchServiceAsync extends IsSerializable {

	void getSearchFieldNameValuePair(String query,
			AsyncCallback<List<SearchFieldNameValuePair>> callback);

}
