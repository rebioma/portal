package org.rebioma.client.services;

import org.rebioma.client.bean.Activity;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;

@RemoteServiceRelativePath("ActivityLogService")
public interface ActivityService extends RemoteService {
	
	PagingLoadResult<Activity> getActivity(PagingLoadConfig config,
			String sId, String type);
	
	Boolean removeActivity(String sId, Activity activity);
	
}
