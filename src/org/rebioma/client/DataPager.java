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
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.LoadListener;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * This abstract base class supports paging functionality across generic data
 * types. Subclasses implement the <code>loadData</code> method which is
 * responsible for retrieving data for the current page and returning it via
 * <code>DataPagerCallback</code>. Objects interested in displaying paged data
 * can register a <code>DataPageListener</code>.
 * 
 * @param <D> the type of data to page
 */
public abstract class DataPager<D> {
	
  private ToolBar toolHp;
  
  private XElement mask;
  
  public void setToolHp(ToolBar toolHp) {
	this.toolHp = toolHp;
  }
  
  public void forceLayout() {
	  if(toolHp!=null)toolHp.forceLayout();
//	  GWT.log(toolHp!=null?"toolHp--tsy null":"toolHp--null");
//	  Window.alert(toolHp!=null?"toolHp--tsy null":"toolHp--null");
  }

  public void setMask(XElement mask) {
	this.mask = mask;
  }
  
  public XElement getMask() {
	return this.mask;
  }

  private void mask(String mask) {
	if(this.mask!=null)this.mask.mask(mask);
  }
  
  private void unmask() {
	if(mask!=null)mask.unmask();
  }
/**
   * Interface for data page listeners.
   * 
   * @param <D> the type of data in the page
   */
  public interface PageListener<D> {
    /**
     * This method is called when a page of data is ready.
     * 
     * @param data the page of data
     * @param pageNumber TODO
     */
    public void onPageLoaded(List<D> data, int pageNumber);
  }

  /**
   * Interface for subclasses that retrieve data for pages.
   * 
   * @param <D> the type of data in the page
   */
  protected interface PageCallback<D> {

    /**
     * This method is called when a page of data have been retrieved.
     * 
     * @param data the retrieved page of data
     */
    public void onPageReady(List<D> data);
  }

  public static final int UNDEFINED = -1;

  /**
   * The current page number.
   */
  protected int currentPageNum = 1;

  /**
   * The list of data page listeners.
   */
  protected List<PageListener<D>> listeners = new ArrayList<PageListener<D>>();

  /**
   * The number of data objects in each page.
   */
  protected int pageSize = UNDEFINED;

  /**
   * The total number of data objects across all pages.
   */
  protected int totalDataCount = UNDEFINED;

  private List<D> data;

  private boolean isLoading = false;

  private final Query<D> query;

  /**
   * Constructs a new data pager with the given page size.
   * 
   * @param pageSize the number of data objects in each page
   * @param query TODO
   */
  public DataPager(int pageSize, Query<D> query) {
    this.pageSize = pageSize;
    this.query = query;
  }

  /**
   * Adds a data page listener.
   * 
   * @param listener the listener to add
   */
  public void addPageListener(PageListener<D> listener) {
    listeners.add(listener);
  }

  /**
   * Retrieves data for the previous page, updates the current page number, and
   * then notifies listeners.
   */
  public void backPage() {
	mask(ApplicationView.getConstants().Loading()+"...");
//	forceLayout();
    loadData(new PageCallback<D>() {
      public void onPageReady(List<D> data) {
        currentPageNum--;
        fireOnPage(data);
      }
    });
  }

  /**
   * @return the current page number
   */
  public int getCurrentPageNumber() {
    return currentPageNum;
  }

  public List<D> getData() {
    return data;
  }

  /**
   * @return the number of data per page.
   */
  public int getPageSize() {
    return pageSize;
  }
  
  public void setPageSize(int ps){
	  if(ps > 0){
		  pageSize = ps; 
	  }
  }

  public Query<D> getQuery() {
    return query;
  }

  /**
   * @return the total count of data objects across all pages
   */
  public long getTotalDataCount() {
    return totalDataCount;
  }

  /**
   * Initializes the data pager by loading the first page and then notifying
   * listeners.
   */
  public void init() {
	mask(ApplicationView.getConstants().Loading()+"...");
//	forceLayout();
    totalDataCount = UNDEFINED;
    loadData(new PageCallback<D>() {
      public void onPageReady(List<D> data) {
        if (currentPageNum == UNDEFINED) {
          currentPageNum = 1;
        }
        fireOnPage(data);
      }
    });
  }

  /**
   * Initializes the data pager by loading the given page number and then
   * notifying listeners.
   * 
   * @param pageNumber the page number to initialize
   */
  public void init(int pageNumber) {
    if (pageNumber < 0) {
      pageNumber = 1;
    }
    currentPageNum = pageNumber;
    init();
  }

  /**
   * Initializes the data pager by loading the given page number and then
   * notifying listeners.
   * 
   * @param pageNumber the page number to initialize
   */
  public void init(int pageSize, int pageNumber) {
    this.pageSize = pageSize;
    init(pageNumber);
  }

  public void init(LoadListener listener) {

  }

  /**
   * Retrieves data for the previous page, updates the current page number, and
   * then notifies listeners.
   */
  public void nextPage() {
	mask(ApplicationView.getConstants().Loading()+"...");
//	forceLayout();
    loadData(new PageCallback<D>() {
      public void onPageReady(List<D> data) {
        currentPageNum++;
        fireOnPage(data);
      }
    });
  }

  /**
   * Removes the data page listener.
   * 
   * @param listener the listener to remove
   */
  public void removeListener(PageListener<D> listener) {
    listeners.remove(listener);
  }

  /**
   * 
   */
  public void reset() {
    currentPageNum = 1;
    totalDataCount = UNDEFINED;
  }

  /**
   * Jumps to the given page number, loads the data associated with it, then
   * notifies listeners.
   * 
   * @param pageNumber the page number to load
   */
  public void toPage(int pageNumber) {
	mask(ApplicationView.getConstants().Loading()+"...");
//	forceLayout();
    currentPageNum = pageNumber;
    loadData(new PageCallback<D>() {
      public void onPageReady(List<D> data) {
        fireOnPage(data);
      }
    });
  }

  /**
   * Fires the onPage event for all listeners.
   * 
   * @param data the data paged
   */
  protected void fireOnPage(List<D> data) {
    isLoading = false;
    if (data == null) {
      return;
    }
    this.data = data;
    for (PageListener<D> l : listeners) {
      l.onPageLoaded(data, currentPageNum);
    }
    unmask();
  }

  /**
   * @return the start index of the current page's data.
   */
  protected int getStart() {
    int start;
    if (currentPageNum == UNDEFINED) {
      start = 0;
    } else {
      // Minus 1 because start starts from 0:
      start = (currentPageNum - 1) * pageSize;
    }
    return start;
  }

  /**
   * Subclasses implement this method which is responsible for loading data for
   * the current page and returning it via <code>DataPagerCallback</code>.
   * 
   * @param cb the callback
   */
  protected void loadData(PageCallback<D> cb) {
    if (isLoading) {
      return;
    }
    isLoading = true;
    query.setStart(getStart());
    query.setLimit(getPageSize());
    if (totalDataCount == UNDEFINED) {
      query.setCountTotalResults(true);
    } else {
      query.setCountTotalResults(false);
    }
    requestData(cb);
  }

  protected abstract void requestData(PageCallback<D> cb);
}
