package org.rebioma.client;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rebioma.client.bean.OccurrenceComments;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A query used to fetch, update and delete OccurrenceComments.
 */
public class OccurrenceCommentQuery implements IsSerializable {

  private int start;
  private int limit;

  private Set<String> filters;

  private List<OccurrenceComments> result;

  public OccurrenceCommentQuery() {
    this(0, 0);
  }

  public OccurrenceCommentQuery(int start, int limit) {
    this.start = start;
    this.limit = limit;
  }

  public void addFilter(String filter) {
    if (filters == null) {
      filters = new HashSet<String>();
    }
    String values[] = filter.split(" ");
    filter = "";
    for (String value : values) {
      if (value.equals("")) {
        continue;
      } else {
        filter += value + " ";
      }
    }
    filters.add(filter);
  }

  public void clearFilters() {
    if (filters != null) {
      filters.clear();
    }
  }

  public Set<String> getFilters() {
    return filters;
  }

  public int getLimit() {
    return limit;
  }

  public List<OccurrenceComments> getResults() {
    return result;
  }

  public int getStart() {
    return start;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public void setResults(List<OccurrenceComments> result) {
    this.result = result;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder("start=" + start + "&limit=" + limit);
    if (filters != null) {
      for (String filter : filters) {
        sb.append("&" + filter);
      }
    }
    return sb.toString();
  }
}
