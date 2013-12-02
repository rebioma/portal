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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.rebioma.client.bean.Occurrence;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;

public class OccurrencePagerWidget extends PagerWidget<Occurrence>{
  private static final CsvDownloadWidget csvDownload = new CsvDownloadWidget();
  
  public final static int DEFAULT_PAGE_SIZE = 10;
  
  public static CsvDownloadWidget getCsvDownloader() {
    return csvDownload;
  }

  private final HTML downloadLink;
  
  private final PageSizeListBox searchPageSizeLb;
  private final Map<String, Integer> pageSizeIndexMap;

  public OccurrencePagerWidget(int pageSize, OccurrenceQuery query,
          boolean enableDownload) {
    super(new OccurrencePager(pageSize, query));
    searchPageSizeLb = new PageSizeListBox();
    pageSizeIndexMap = new HashMap<String, Integer>();
	searchPageSizeLb.addItem("10");
	pageSizeIndexMap.put("10", searchPageSizeLb.getItemCount() - 1);
	searchPageSizeLb.addItem("20");
	pageSizeIndexMap.put("20", searchPageSizeLb.getItemCount() - 1);
	searchPageSizeLb.addItem("50");
	pageSizeIndexMap.put("50", searchPageSizeLb.getItemCount() - 1);
	searchPageSizeLb.addItem("100");
	pageSizeIndexMap.put("100", searchPageSizeLb.getItemCount() - 1);
	searchPageSizeLb.addItem("250");
	pageSizeIndexMap.put("250", searchPageSizeLb.getItemCount() - 1);
	addWidgetToHorizontalPanel(mainPanel, searchPageSizeLb, true);
	if(pageSize < 0){
		pageSize = DEFAULT_PAGE_SIZE;
	}
	String v = Integer.toString(pageSize);
	if(!pageSizeIndexMap.containsKey(v)){
		//on ajoute la valeur par default dans la liste
		searchPageSizeLb.addItem(v);
		pageSizeIndexMap.put(v, searchPageSizeLb.getItemCount() - 1);
	}
	setPageSizeSelectedItem(v);
	//HTML pageSizeLabel = new HTML("Nombre d'articles par page: ");
	//mainPanel.insert(pageSizeLabel, 0);
	//mainPanel.insert(searchPageSizeLb, 1);
    if (enableDownload) {
      downloadLink = new HTML("CSV");
      downloadLink.setStyleName("downloadlink");
      downloadLink.addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent event) {
          Set<String> filters = getQuery().getBaseFilters();
          OccurrenceQuery query = getQuery();
          filters.addAll(query.getSearchFilters());
          csvDownload.show(filters, query.getResultFilter().toString()
                  .toLowerCase());
        }
      });
    } else {
      downloadLink = null;
    }
  }
  /**
   * cf doc {@link ListBox#setItemSelected(int, boolean)}
   * Note that setting the selection programmatically does not cause the 
   * ChangeHandler.onChange(ChangeEvent) event to be fired. 
   * => donc pas de risque de recursivit�.
   * @param selectedValue
   */
  public void setPageSizeSelectedItem(String selectedValue){
	  if(pageSizeIndexMap.containsKey(selectedValue)){
			int defaultSelectedIndex = pageSizeIndexMap.get(selectedValue);
			searchPageSizeLb.setItemSelected(defaultSelectedIndex, true);
	  }
  }
  
  public void addPageSizeChangeListener(PageSizeChangeHandler pageSizeChangeHandler){
	  searchPageSizeLb.addPageChangeListener(pageSizeChangeHandler);
  }

  public OccurrenceQuery getQuery() {
    return ((OccurrencePager) super.pager).getQuery();
  }

  protected void onPagingDisabled() {
    if (downloadLink != null) {
      mainPanel.remove(downloadLink);
    }
  }

  protected void onPagingEnabled() {
    if (downloadLink != null) {
      addWidgetToHorizontalPanel(mainPanel, downloadLink, false);
    }
  }

}
