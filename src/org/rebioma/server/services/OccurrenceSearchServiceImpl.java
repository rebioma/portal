/**
 * 
 */
package org.rebioma.server.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rebioma.client.OccurrenceQuery;
import org.rebioma.client.bean.SearchFieldNameValuePair;
import org.rebioma.client.bean.User;
import org.rebioma.client.services.OccurrenceSearchService;
import org.rebioma.server.services.OccurrenceDbImpl.OccurrenceFilter;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Guice;

/**
 * @author Mikajy
 */
public class OccurrenceSearchServiceImpl extends RemoteServiceServlet implements
		OccurrenceSearchService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -23288874237529960L;

	private OccurrenceSearchDbES occurrenceSearchDbES = new OccurrenceSearchDbES();

	/**
	 * The {@link SessionIdService} used for getting the {@link User} associated
	 * with the sessionId in the upload request. Injected by {@link Guice}.
	 */

	private SessionIdService sessionService = DBFactory.getSessionIdService();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rebioma.client.services.OccurrenceSearchService#
	 * getSearchFieldNameValuePair(java.lang.String,
	 * org.rebioma.client.OccurrenceQuery)
	 */
	@Override
	public List<SearchFieldNameValuePair> getSearchFieldNameValuePair(
			String sessionId, OccurrenceQuery query) throws Exception {
		Set<OccurrenceFilter> filters = QueryFilter.getFilters(
				query.getBaseFilters(), OccurrenceFilter.class);
		filters.addAll(QueryFilter.getFilters(query.getSearchFilters(),
				OccurrenceFilter.class));
		Set<OccurrenceFilter> disjunctionFilters = new HashSet<OccurrenceDbImpl.OccurrenceFilter>();
		disjunctionFilters.addAll(QueryFilter.getFilters(
				query.getDisjunctionSearchFilters(), OccurrenceFilter.class));
		for (OccurrenceFilter filtre : disjunctionFilters) {
			filtre.setDisjunction(true);
		}
		filters.addAll(disjunctionFilters);
		User user = null;
		if (sessionId != null && !sessionId.trim().equals("")
				&& !sessionId.equalsIgnoreCase("null")) {
			user = sessionService.getUserBySessionId(sessionId);
		}
		List<SearchFieldNameValuePair> nvpList = occurrenceSearchDbES
				.findFieldNameValuePair(query, filters, user);
		return nvpList;
	}

}
