package org.rebioma.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ServerPingServiceAsync {

	void ping(AsyncCallback<Void> callback);

}
