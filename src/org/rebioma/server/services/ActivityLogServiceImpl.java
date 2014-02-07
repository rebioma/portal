package org.rebioma.server.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.rebioma.client.bean.Activity;
import org.rebioma.client.bean.User;
import org.rebioma.client.services.ActivityService;
import org.rebioma.server.hibernate.ActivityLogDA;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;

public class ActivityLogServiceImpl extends RemoteServiceServlet implements
		ActivityService {

	private ActivityLogDA activityDA = new ActivityLogDA();
	
	@Override
	public PagingLoadResult<Activity> getActivity(PagingLoadConfig config, String sId, String type) {
		List<Activity> activities;
		User user = DBFactory.getSessionIdService().getUserBySessionId(sId);
		if(user == null) return null;
		int userId = user.getId();
		if(type.equalsIgnoreCase("comment")) {
			activities = activityDA.getCommentActivity(userId); 
		} else if(type.equalsIgnoreCase("review")) {
			activities = activityDA.getReviewActivity(userId);
		} else {
			activities = activityDA.getReviewActivity(userId);
			activities.addAll(activityDA.getCommentActivity(userId));
		}
		if (config.getSortInfo().size() > 0) {
			SortInfo sort = config.getSortInfo().get(0);
			if (sort.getSortField() != null) {
				final String sortField = sort.getSortField();
				if (sortField != null) {
					Collections.sort(activities, sort.getSortDir().comparator(new Comparator<Activity>() {
						public int compare(Activity p1, Activity p2) {
							if (sortField.equalsIgnoreCase("date")) {
								return p1.getDate().compareTo(p2.getDate());
							} else if (sortField.toLowerCase().startsWith("comment")) {
								return p1.getComment().compareTo(p2.getComment());
							} else if (sortField.toLowerCase().startsWith("occurrence")) {
								return p1.getOccurrenceCount().compareTo(p2.getOccurrenceCount());
							} else if (sortField.toLowerCase().startsWith("action")) {
								return p1.getAction().compareTo(p2.getAction());
							}
							return 0;
						}
					}));
				}
			} else Collections.sort(activities,new Comparator<Activity>() {
				public int compare(Activity p1, Activity p2) {
					return p1.getDate().compareTo(p2.getDate());
				}
			});
		}
		ArrayList<Activity> sublist = new ArrayList<Activity>();
		int start = config.getOffset();
		int limit = activities.size();
		if (config.getLimit() > 0) {
			limit = Math.min(start + config.getLimit(), limit);
		}
		for (int i = config.getOffset(); i < limit; i++) {
			sublist.add(activities.get(i));
		}
		return new PagingLoadResultBean<Activity>(sublist, activities.size(), config.getOffset());
	}

	@Override
	public Boolean removeActivity(String sId, Activity activity) {
		User user = DBFactory.getSessionIdService().getUserBySessionId(sId);
		if(user == null) return null;
		int userId = user.getId();
		if(activity.getAction()==null) {
			return activityDA.removeCommentActivity(userId, activity); 
		} else {
			return activityDA.removeReviewActivity(userId, activity);
		}
	}

	public static void main(String[] args) {
		System.out.println(new ActivityLogDA().getCommentActivity(108).size());
	}
}
