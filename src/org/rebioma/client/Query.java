/*
 * Copyright 2008 University of California at Berkeley
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.rebioma.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rebioma.client.bean.Occurrence;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Query<Q> implements IsSerializable {
  public static int UNLIMITED = -2;
  /**
   * Number of records count of this query without limit.
   * 
   * Negative if the query does not count.
   */
  protected int count = -1;

  protected boolean countTotalResults = true;

  /**
   * The set of query updates of the form "Concept = Value".
   */
  protected Set<String> updates;

  /**
   * The set of query search filters i.e AcceptedSpecies = blah, BasicOfRecord =
   * foo, ...
   * 
   * This filters is difference from {@link #baseFilters} for memory tokens
   * purpose.
   */
  protected Set<String> searchFilters = null;
  
  /**
   * Pareil que searchFilters sauf que les filtres dans disjunctionSearchFilters sont liés par OR dans le clause Where.
   */
  protected Set<String> disjunctionSearchFilters = null;

  /**
   * The maximum number of records to return for this query.
   */
  protected int limit = 100;

  /**
   * The query results.
   */
  protected List<Q> results;

  /**
   * The record number to start the query at.
   */
  protected int start = 1;

  protected List<OrderKey> orderingMap = new ArrayList<OrderKey>();

  public Query() {
    this(-1, -1);
  }

  public Query(int start, int limit) {
    this.start = start;
    this.limit = limit;
  }

  public void addOrdering(String property, Boolean isAscending) {
    orderingMap.add(new OrderKey(property, isAscending));
  }

  /**
   * Adds a filter to this query. A valid filter takes the following form:
   * 
   * "concept <operator> value"
   * 
   * where concept can match any of the concept names found in the Darwin Core,
   * the Curatorial Extension, and the Geospatial Extension, and operator is
   * either '=' or 'like'. For example, "Genus like Coeliades" or
   * "DecimalLatitude = 38.4332345" are valid filters.
   * 
   * Filters can be chained like this:
   * 
   * OccurrenceQuery q = new OccurrenceQuery();
   * q.addFilter("Genus = Coeliades").addFilter("DecimalLatitude = 38.4332345");
   * 
   * @param filter the query filter to add
   * @return the query object
   */
  public void addSearchFilter(String searchFilter) {
    if (searchFilters == null) {
      searchFilters = new HashSet<String>();
    }
    searchFilters.add(searchFilter);
  }
  
  /**
   * Adds a filter to this query. A valid filter takes the following form:
   * 
   * "concept <operator> value"
   * 
   * where concept can match any of the concept names found in the Darwin Core,
   * the Curatorial Extension, and the Geospatial Extension, and operator is
   * either '=' or 'like'. For example, "Genus like Coeliades" or
   * "DecimalLatitude = 38.4332345" are valid filters.
   * 
   * Filters can be chained like this:
   * 
   * OccurrenceQuery q = new OccurrenceQuery();
   * q.addFilter("Genus = Coeliades").addFilter("DecimalLatitude = 38.4332345");
   * 
   * @param filter the query filter to add
   * @param disjunction - true if we use OR in where clause false otherwise.
   * @return the query object
   */
  public void addSearchFilter(String searchFilter, boolean disjunction) {
	  if(disjunction){
		  getDisjunctionSearchFilters().add(searchFilter);
	  }else{
		  addSearchFilter(searchFilter);
	  }
  }
  
  public Query<Q> addUpdate(String update) {
    if (updates == null) {
      updates = new HashSet<String>();
    }
    updates.add(update);
    return this;
  }

  public void clearSearchFilter() {
    if (searchFilters != null) {
      searchFilters.clear();
    }
    if(disjunctionSearchFilters != null){
    	disjunctionSearchFilters.clear();
    }
  }

  /**
   * Gets Number of records count of this query without limit.
   * 
   * Negative if the query does not count.
   * 
   * @return count as long
   */
  public int getCount() {
    return count;
  }

  /**
   * Gets the limit.
   * 
   * @return the limit
   */
  public int getLimit() {
    return limit;
  }

  public List<OrderKey> getOrderingMap() {
    return orderingMap;
  }

  /**
   * Gets the results
   * 
   * @return the set of {@link Occurrence} objects
   */
  public List<Q> getResults() {
    return results;
  }

  public Set<String> getSearchFilters() {
    if (searchFilters == null) {
      searchFilters = new HashSet<String>();
    }
    return searchFilters;
  }

  /**
   * Gets the start.
   * 
   * @return the start
   */
  public int getStart() {
    return start;
  }

  /**
   * @return the updates
   */
  public Set<String> getUpdates() {
    return updates;
  }

  /**
   * 
   * @return
   */
  public boolean isCountTotalResults() {
    return countTotalResults;
  }

  /**
   * Sets number records of this search without limit.
   * 
   * @param count long
   */
  public void setCount(int count) {
    this.count = count;
  }

  public void setCountTotalResults(boolean counted) {
    this.countTotalResults = counted;
  }

  /**
   * @param limit the limit to set
   */
  public void setLimit(int limit) {
    this.limit = limit;
  }

  /**
   * Sets the query results.
   * 
   * @param results a set of {@link Occurrence} objects
   */
  public void setResults(List<Q> results) {
    this.results = results;
  }

  public void setSearchFilters(Set<String> searchFilters) {
    this.searchFilters = searchFilters;
  }

  /**
   * Sets the start.
   * 
   * @param newStart
   */
  public void setStart(int newStart) {
    start = newStart;
  }

  /**
   * @param updates the updates to set
   */
  public void setUpdates(Set<String> updates) {
    this.updates = updates;
  }

  public String toString() {
    StringBuilder out = new StringBuilder();
    out.append("start=" + start + ";limit=" + limit);
    if (searchFilters != null) {
      for (String filter : searchFilters) {
        out.append(";filter=" + filter);
      }
    }
    return out.toString();
  }

	/**
	 * @return the disjunctionSearchFilters
	 */
	public Set<String> getDisjunctionSearchFilters() {
		if(disjunctionSearchFilters == null){
			disjunctionSearchFilters = new HashSet<String>();
		}
		return disjunctionSearchFilters;
	}
	
	/**
	 * @param disjunctionSearchFilters the disjunctionSearchFilters to set
	 */
	public void setDisjunctionSearchFilters(Set<String> disjunctionSearchFilters) {
		this.disjunctionSearchFilters = disjunctionSearchFilters;
	}
}
