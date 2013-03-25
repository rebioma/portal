/**
 * 
 */
package org.rebioma.client;

import java.util.Map;


/**
 * Interface d'ecoute d'une recherche d'occurence.
 * 
 * @author Mikajy
 *
 */
public interface OccurrenceSearchListener {
	public static final String SEARCH_TYPE_PROPERTY_KEY = "searchType";
	public static final String SEARCH_TYPE_VALUE_PROPERTY_KEY = "searchTypeValue";
	public static final String RESULT_FILTER_PROPERTY_KEY = "resultFilter";
	public static final String RESULT_FILTER_VALUE_KEY = "resultFilter_value";
	public static final String ERROR_QUERY_KEY = "errorQuery";
	public static final String ERROR_QUERY_VALUE_KEY = "errorquery_value";
	public static final String SHARED_KEY = "shared";
	public static final String SHARED_VALUE_KEY = "shared_value";
	/**
	 * quand une recherche d'occurrence est lancée.
	 * @param searchFilter - chaque element de cette collection est un paramètre à {@link OccurrenceQuery#addSearchFilter(String)}}
	 */
	public void searchQuery(OccurrenceQuery query, Map<String, Object> propertyMap);
}
