package org.rebioma.client.services;

import org.rebioma.client.bean.Activity;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;

public interface ActivityServiceAsync {

	void getActivity(PagingLoadConfig config, String sId, String type,
			AsyncCallback<PagingLoadResult<Activity>> callback);

	void removeActivity(String sId, Activity activity,
			AsyncCallback<Boolean> callback);

}
