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

import org.rebioma.client.View.ViewState;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.User;
import org.rebioma.client.i18n.AppConstants;

/**
 * The OccurrenceQuery class is used to encapsulate query parameters used to
 * fetch specific types of {@link Occurrence} objects from a database. It also
 * contains the results of the query.
 * 
 */
public class OccurrenceQuery extends Query<Occurrence> {
  /**
   * A listener to listen on data requests form this OccurrenceQuery.
   * 
   * @author tri
   * 
   */
  public interface DataRequestListener {
    /**
     * Get called when {@link OccurrenceQuery#requestData(int)} called.
     * 
     * @param pageNum
     */
    void requestData(int pageNum);
  }

  /**
   * A filter to determine whether the result should contains public, private,
   * or both records.
   * 
   * @author Tri
   * 
   */
  public enum ResultFilter {
    PRIVATE, PUBLIC, BOTH;
  }

  // public static final String DEFAULT_FILTER = "vetted = true";

  /**
   * The set of query filters of the form "Concept = Value". This filter on
   * contain concept and value pairs that match specific occurrence search type
   * (i.e All Occurrence: this filter is empty).
   */
  protected Set<String> baseFilters;

  private final List<DataRequestListener> dataRequestListeners = new ArrayList<DataRequestListener>();
  private ResultFilter resultFilter;

  private Set<Integer> occurrenceIdsFilter = new HashSet<Integer>();
  
  /**
   * The default constructor.
   */
  public OccurrenceQuery() {
    this(-1, -1);
  }

  /**
   * Constructs a new query against all occurrence types.
   * 
   * @param start the record number to start the query at
   * @param limit the maximum records to return for this query
   */
  public OccurrenceQuery(int start, int limit) {
    super(start, limit);
    // super.addOrdering("AcceptedSpecies", true);
    super.addOrdering("reviewed", false);
    super.addOrdering("validated", false);
    super.addOrdering("AcceptedSpecies", true);
  }

  public OccurrenceQuery(OccurrenceQuery other) {
    this(other.start, other.limit);
    baseFilters = new HashSet<String>();
    baseFilters.addAll(other.baseFilters);
    results = new ArrayList<Occurrence>();
    results.addAll(other.results);
    count = new Integer(other.count).intValue();
    countTotalResults = new Boolean(other.countTotalResults).booleanValue();
    orderingMap = other.orderingMap == null ? null : new ArrayList<OrderKey>(other.orderingMap);
  }
  
  public void reinitFilters(){
	//on initialise toutes les filtres
		clearSearchFilter();
		setBaseFilters(new HashSet<String>());
		setResults(new ArrayList<Occurrence>());
	    addOrdering("reviewed", false);
	    addOrdering("validated", false);
	    addOrdering("AcceptedSpecies", true);
	    setCount(-1);
	    countTotalResults = true;
	    orderingMap = new ArrayList<OrderKey>();
	    start = 1;
	    limit = 100;
  }

  /**
   * Adds a filter to this query. A valid filter takes the following form:
   * 
   * "concept <operator> value"
   * 
   * for matching occurrence search type and should only be modify by
   * {@link #getFiltersFromProperty(String, User)} or
   * {@link #getFiltersFromProperty(String, User, ResultFilter)}
   * 
   * @param filter the query filter to add
   * @return the query object
   * @see org.rebioma.client.OccurrenceQuery#baseFilters This filter only use
   */
  public OccurrenceQuery addBaseFilter(String filter) {
    if (baseFilters == null) {
      baseFilters = new HashSet<String>();
    }
    baseFilters.add(filter);
    return this;
  }

  public void addDataRequestListener(DataRequestListener listener) {
    dataRequestListeners.add(listener);
  }

  public void clearsUpdate() {
    if (updates != null) {
      updates.clear();
    }

  }

  /**
   * Gets the filters.
   * 
   * @return the filters
   */
  public Set<String> getBaseFilters() {
    return baseFilters;
  }

  public Set<String> getFiltersFromProperty(String property, User loggedinUser) {
    return getFiltersFromProperty(property, loggedinUser, ResultFilter.PUBLIC);
  }

  public Set<String> getFiltersFromProperty(String property, User loggedinUser,
      ResultFilter resultFilter) {
    AppConstants constants = ApplicationView.getConstants();
    Set<String> filters = new HashSet<String>();
    boolean isLoggedIn = loggedinUser != null;
    if (property != null) {
      if (property.equalsIgnoreCase(constants.AllOccurrences())) {
        this.resultFilter = isLoggedIn ? ResultFilter.BOTH : ResultFilter.PUBLIC;
        // filters.add("public = true");
      } else if (property.equalsIgnoreCase(constants.AllValidated())) {
        this.resultFilter = isLoggedIn ? ResultFilter.BOTH : ResultFilter.PUBLIC;
        // filters.add("public = true");
        filters.add("validated = true");
      } else if (property.equalsIgnoreCase(constants.AllInvalid())) {
        // filters.add("public = true");
        this.resultFilter = isLoggedIn ? ResultFilter.BOTH : ResultFilter.PUBLIC;
        filters.add("validated = false");
      } else if (property.equalsIgnoreCase(constants.AllPositivelyReviewed())) {
        // filters.add("public = true");
        this.resultFilter = isLoggedIn ? ResultFilter.BOTH : ResultFilter.PUBLIC;
        filters.add("validated = true");
        filters.add("reviewed = true");
      } else if (property.equalsIgnoreCase(constants.AllNegativelyReviewed())) {
        // filters.add("public = true");
        this.resultFilter = isLoggedIn ? ResultFilter.BOTH : ResultFilter.PUBLIC;
        filters.add("validated = true");
        filters.add("reviewed = false");
      } else if (property.equalsIgnoreCase(constants.AllAwaitingReview())) {
        // filters.add("public = true");
        this.resultFilter = isLoggedIn ? ResultFilter.BOTH : ResultFilter.PUBLIC;
        filters.add("validated = true");
        filters.add("reviewed empty");
      } else if (loggedinUser == null) {
        this.resultFilter = ResultFilter.PUBLIC;
        // filters.add(DEFAULT_FILTER);
      } else if (property.equalsIgnoreCase(constants.MyOccurrences())) {
        this.resultFilter = resultFilter;
        // filters.add("public = " + !privateOnly);
        filters.add("ownerEmail = " + loggedinUser.getEmail());
      } else if (property.equalsIgnoreCase(constants.MyValidated())) {
        // filters.add("public = " + !privateOnly);
        this.resultFilter = resultFilter;
        filters.add("ownerEmail = " + loggedinUser.getEmail());
        filters.add("validated = true");
      } else if (property.equalsIgnoreCase(constants.MyInvalid())) {
        // filters.add("public = " + !privateOnly);
        this.resultFilter = resultFilter;
        filters.add("ownerEmail = " + loggedinUser.getEmail());
        filters.add("validated = false");
      } else if (property.equalsIgnoreCase(constants.MyAwaitingReview())) {
        // filters.add("public = " + !privateOnly);
        this.resultFilter = resultFilter;
        /* fixing issue1 "my awaiting review" */
        filters.add("validated = true");
        filters.add("ownerEmail = " + loggedinUser.getEmail());
        /* end fixing issue 1 */
        //filters.add("reviewed empty");
      } else if (property.equalsIgnoreCase(constants.MyOverallNegativelyReviewed())) {
        this.resultFilter = resultFilter;
        // filters.add("public = " + !privateOnly);
        filters.add("ownerEmail = " + loggedinUser.getEmail());
        filters.add("validated = true");
        filters.add("reviewed = false");
      } else if (property.equalsIgnoreCase(constants.MyOverallPositivelyReviewed())) {
        this.resultFilter = resultFilter;
        // filters.add("public = " + !privateOnly);
        filters.add("ownerEmail = " + loggedinUser.getEmail());
        filters.add("validated = true");
        filters.add("reviewed = true");
      } else if (property.equalsIgnoreCase(constants.MyPositivelyReviewed())) {
        this.resultFilter = resultFilter == ResultFilter.BOTH ? null : resultFilter;
        // filters.add("public = " + !privateOnly);
        // filters.add("ownerEmail = " + loggedinUser.getEmail());
        filters.add("validated = true");
        filters.add("userReviewed = true");
      } else if (property.equalsIgnoreCase(constants.MyNegativelyReviewed())) {
        this.resultFilter = resultFilter == ResultFilter.BOTH ? null : resultFilter;
        // filters.add("public = " + !privateOnly);
        // filters.add("ownerEmail = " + loggedinUser.getEmail());
        filters.add("validated = true");
        filters.add("userReviewed = false");
      } else if (property.equalsIgnoreCase(constants.OccurrencesToReview())) {
        this.resultFilter = resultFilter == ResultFilter.BOTH ? null : resultFilter;
        // this.resultFilter = null;
        // switch (resultFilter) {
        // case BOTH:
        // this.resultFilter = null;
        // break;
        // case PUBLIC:
        //
        // case PRIVATE:
        // }
        // filters.add("public = " + !privateOnly);
        // filters.add("ownerEmail = " + loggedinUser.getEmail());
        filters.add("validated = true");
        filters.add("userReviewed empty");
      } else {
        this.resultFilter = ResultFilter.PUBLIC;
        // filters.add(DEFAULT_FILTER);
      }
    } else {
      this.resultFilter = ResultFilter.PUBLIC;
      // filters.add(DEFAULT_FILTER);
    }
    if(ApplicationView.getCurrentState() == ViewState.SUPERADMIN)
    	this.resultFilter = property==null?ResultFilter.PUBLIC:resultFilter;
    return filters;
  }

  public String getQuery() {
    StringBuilder query = new StringBuilder();
    if (baseFilters != null && !baseFilters.isEmpty()) {
      for (String filter : baseFilters) {
        query.append(filter + ", ");
      }
      // delete the last and
      query.delete(query.length() - 2, query.length());
    }
    return query.toString();
  }

  /**
   * Gets {@link ResultFilter} for this query.
   * 
   * @return {@link ResultFilter} for this query.
   */
  public ResultFilter getResultFilter() {
    return resultFilter;
  }

  /**
   * Notified all {@link DataRequestListener} that register in this query to get
   * data from the {@link DataSwitch}.
   * 
   * @param pageNum the request data page number.
   */
  public void requestData(int pageNum) {
    for (DataRequestListener listener : dataRequestListeners) {
      listener.requestData(pageNum);
    }
  }

  /**
   * 
   * @param filters the filters to set
   */
  public void setBaseFilters(Set<String> filters) {
    this.baseFilters = filters;
  }

  public void setResultFilter(ResultFilter resultFilter) {
    this.resultFilter = resultFilter;
  }

  public String toString() {
    StringBuilder out = new StringBuilder(super.toString());
    if (baseFilters != null) {
      for (String filter : baseFilters) {
        out.append(";filter=" + filter);
      }
    }
    out.append(resultFilter);
    return out.toString();
  }
  
  public Set<Integer> getOccurrenceIdsFilter() {
		return occurrenceIdsFilter;
	}

	public void setOccurrenceIdsFilter(Set<Integer> occurrenceIdsFilter) {
		this.occurrenceIdsFilter = occurrenceIdsFilter;
	}

}
