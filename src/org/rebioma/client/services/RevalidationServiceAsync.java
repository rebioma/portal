/**
 * 
 */
package org.rebioma.client.services;

import org.rebioma.client.bean.RevalidationResult;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author mikajy
 *
 */
public interface RevalidationServiceAsync {

	void revalidate(String sessionId, AsyncCallback<RevalidationResult> callback);

	void cancelRevalidation(String sessionId, AsyncCallback<Void> callback);

}
