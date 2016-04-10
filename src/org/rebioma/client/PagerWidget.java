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

import org.rebioma.client.DataPager.PageListener;
import org.rebioma.client.i18n.AppConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * The DataPagerWidget abstract class provides a simple paging interface backed
 * by an underlying {@link DataPager}. The interface supports next, last, back,
 * and first links.
 * 
 * @param <D> the type of data that is pageable
 */
public abstract class PagerWidget<D> extends Composite implements ClickHandler {

  /**
   * Fires when a paging link is clicked.
   * 
   * @author tri
   * 
   */
  public interface PageClickListener {
    /**
     * Get Called when a paging link is clicked.
     */
    void onPageClicked();
  }

  /**
   * The default style.
   */
  public static final String DEFAULT_STYLE = "Pager";

  /**
   * Application constants.
   */
  protected final AppConstants constants = ApplicationView.getConstants();

  /**
   * The underlying pager.
   */
  protected final DataPager<D> pager;

  /**
   * Clicking goes to the first page.
   */
  protected final HTML firstLink = createLink("&laquo;&nbsp;"
          + constants.First());

  /**
   * Clicking goes to the last page.
   */
  protected final HTML lastLink = createLink(constants.Last() + "&nbsp;&raquo;");

  /**
   * Clicking goes to the next page.
   */
  protected final HTML nextLink = createLink(constants.Next()
          + "&nbsp;&#x203A;");

  /**
   * Clicking goes back a page.
   */
  protected final HTML backLink = createLink("&#x2039;&nbsp;"
          + constants.Previous());

  /**
   * Displays a status message.
   */
  protected final HTML status = new HTML();

  /**
   * The main panel wrapped by this composite.
   */
  protected final HorizontalPanel mainPanel;

  private final List<PageClickListener> pageClickListeners = new ArrayList<PageClickListener>();

  public PagerWidget(DataPager<D> dataPager) {
    pager = dataPager;
    pager.addPageListener(new PageListener<D>() {
      public void onPageLoaded(List<D> data, int pageNumber) {
        if (data == null) {
          return;
        }
        int currentPage = pageNumber;
        if (!data.isEmpty()) {
          // Handles the case where data contains more than pageSize objects:
          int dataCount = Math.min(data.size(), pager.pageSize);
          // Updates the display with data page count with total:
          int dataOrderStart = getDataOrderStart();
          int dataOrderEnd = dataOrderStart + dataCount;
          updateDisplay((dataOrderStart + 1), dataOrderEnd);
          // Sets link visibility:
          boolean isLastPage = currentPage == getTotalPageCount();
          nextLink.setVisible(!isLastPage);
          lastLink.setVisible(!isLastPage);
          backLink.setVisible(dataOrderEnd > pager.pageSize);
          firstLink.setVisible(dataOrderEnd > pager.pageSize);
          onPagingEnabled();
        } else {
          updateDisplay("No Search Results");
          nextLink.setVisible(false);
          backLink.setVisible(false);
          lastLink.setVisible(false);
          firstLink.setVisible(false);
          onPagingDisabled();
        }
      }

    });

    mainPanel = new HorizontalPanel();
    initWidget(mainPanel);

    mainPanel.setStyleName(DEFAULT_STYLE);
    addWidgetToHorizontalPanel(mainPanel, firstLink, true);
    // mainPanel.setCellWidth(firstLink, "40px");
    addWidgetToHorizontalPanel(mainPanel, backLink, true);
    // mainPanel.setCellWidth(backLink, "40px");
    addWidgetToHorizontalPanel(mainPanel, status, true);
    addWidgetToHorizontalPanel(mainPanel, nextLink, true);
    // mainPanel.setCellWidth(nextLink, "40px");
    addWidgetToHorizontalPanel(mainPanel, lastLink, true);
    // mainPanel.setCellWidth(lastLink, "40px");
    backLink.addClickHandler(this);
    nextLink.addClickHandler(this);
    lastLink.addClickHandler(this);
    firstLink.addClickHandler(this);
    nextLink.setVisible(false);
    backLink.setVisible(false);
    lastLink.setVisible(false);
    firstLink.setVisible(false);
  }
  /**
   * Pour actualiser l'affichge pendant le chargment
   * @param toolHp
   */
  public void setToolBar(ToolBar toolHp) {
	  pager.setToolHp(toolHp);
  }
  /**
   * Setters pour masquer un element 
   * @param mask
   */
  public void setXElement(XElement mask) {
	pager.setMask(mask);
  }

  public void addPageClickListener(PageClickListener listener) {
    pageClickListeners.add(listener);
  }

  public void addPageListener(PageListener<D> listener) {
    pager.addPageListener(listener);
  }

  public List<D> getCurrentPageData() {
    return pager.getData();
  }

  public int getCurrentPageNumber() {
    return pager.getCurrentPageNumber();
  }

  public int getPageSize() {
    return pager.getPageSize();
  }

  /**
   * Returns the total number of pages.
   */
  public long getTotalPageCount() {
    long dataCount = pager.getTotalDataCount();
    if (dataCount == DataPager.UNDEFINED) {
      return 1;
    } else {
      int pageSize = pager.getPageSize();
      int totalPage = (int) dataCount / pageSize;
      if (dataCount % pageSize != 0) {
        totalPage++;
      }
      return totalPage;
    }
  }

  public long getTotalRow() {
    return pager.getTotalDataCount();
  }

  public void goToPage(int page) {
    updateDisplay(constants.Loading() + "&nbsp;&nbsp;");
    disablePaging();
    pager.toPage(page);
  }

  public void init() {
    updateDisplay(constants.Loading() + "&nbsp;&nbsp;");
    disablePaging();
    pager.init();
  }

  public void init(int pageNum) {
    updateDisplay(constants.Loading() + "&nbsp;&nbsp;");
    disablePaging();
    pager.init(pageNum);
  }

  public boolean isLoading() {
    return false;
  }

  public void onClick(ClickEvent event) {
    int currentPage = pager.getCurrentPageNumber();
    long totalPageCount = getTotalPageCount();
    HTML sender = (HTML) event.getSource();
    fireOnPageClicked();
    if (sender == firstLink) {
      goToPage(1);
    } else if (sender == backLink) {
      if (currentPage <= 1) {
        Window.alert("out of range");
      } else {
        goToPage(currentPage - 1);
      }
    } else if (sender == lastLink) {
      if (totalPageCount <= 0) {
        Window.alert("out of range");
      } else {
        goToPage((int) totalPageCount);
      }
    } else if (sender == nextLink) {
      if (currentPage >= totalPageCount) {
        Window.alert("out of range");
      } else {
        goToPage(currentPage + 1);
      }
    }
  }

  /**
   * Resets the underlying {@link DataPager}.
   */
  public void reset() {
    pager.reset();
  }

  protected void addWidgetToHorizontalPanel(HorizontalPanel hp, Widget w,
          boolean spaced) {
    hp.add(w);
    if (spaced) {
      hp.add(new HTML("&nbsp;"));
    }
  }

  protected HTML createLink(String linkName) {
    HTML link = new HTML(linkName);
    link.setStyleName("link");
    return link;
  }

  protected void disablePaging() {
    nextLink.setVisible(false);
    backLink.setVisible(false);
    lastLink.setVisible(false);
    firstLink.setVisible(false);
    onPagingDisabled();
	pager.forceLayout();
  }

  protected int getDataOrderStart() {
    return (pager.getCurrentPageNumber() - 1) * pager.getPageSize();
  }

  /**
   * Hook for subclasses.
   */
  protected void onPagingDisabled() {
  }

  /**
   * Hook for subclasses.
   */
  protected void onPagingEnabled() {
  }

  protected void updateDisplay(int dataOrderStart, int dataOrderEnd) {
    String display = "<b>" + dataOrderStart + "&nbsp;-&nbsp;" + dataOrderEnd
            + "</b>&nbsp;of&nbsp;<b>" + pager.getTotalDataCount()
            + "</b>&nbsp;&nbsp;";
    status.setHTML("<font color='" + "black" + "'>" + display + "</font>");
  }

  protected void updateDisplay(String message) {
 	// Updated so that color is set in Portal.css (under .Pager .gwt-HTML { color:red; })
	// In original, color was hard-wired: 
	// status.setHTML("<font color='" + "black" + "'>" + message + "</font>");
	  status.setHTML(message);  
  }

  private void fireOnPageClicked() {
    for (PageClickListener listener : pageClickListeners) {
      listener.onPageClicked();
    }
  }

}
