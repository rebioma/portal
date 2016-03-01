/**
 * 
 */
package org.rebioma.server.services;

import java.util.List;
import java.util.Set;

import org.rebioma.client.OccurrenceQuery;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.User;
import org.rebioma.server.services.OccurrenceDbImpl.OccurrenceFilter;

/**
 * @author Mikajy
 *
 */
public interface IOccurrenceSearchDb {
	
	public List<Occurrence> find(OccurrenceQuery query, Set<OccurrenceFilter> filters, User user,
		      int tryCount) throws Exception;
}
