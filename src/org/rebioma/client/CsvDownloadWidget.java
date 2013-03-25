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

import java.util.HashSet;
import java.util.Set;

import org.rebioma.client.i18n.AppConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A {@link PopupPanel} to contains download form to send a {@link Set} of
 * search filter to the server to download the records in CSV format.
 * 
 * @author Tri
 * 
 */
public class CsvDownloadWidget extends PopupPanel implements ClickHandler {
  private final VerticalPanel mainVp = new VerticalPanel();
  private final FormPanel form;
  private final Hidden hiddenSessionId;
  private final Hidden hiddenQuery;
  private final Hidden extraInfo;
  private final Button downloadButton;
  private final Button cancelButton;
  private Set<String> queryFilters;
  private String extraInfomation;

  private final HTML termsOfUseLabel = new HTML(
      "<a href='https://sites.google.com/site/rebiomahelp/home/english#datause' target='_blank'>Data Use Agreement (DUA)</a>");

  public CsvDownloadWidget() {
    super(true);
    AppConstants constants = ApplicationView.getConstants();
    form = new FormPanel();
    downloadButton = new Button(constants.AcceptAndDownload());
    form.setAction(GWT.getModuleBaseURL() + "download");
    form.setEncoding(FormPanel.ENCODING_MULTIPART);
    form.setMethod(FormPanel.METHOD_POST);
    hiddenSessionId = new Hidden("sessionId");
    hiddenQuery = new Hidden("query");
    extraInfo = new Hidden("extra");
    HorizontalPanel delimiterPanel = new HorizontalPanel();
    ListBox delimiterBox = new ListBox();
    delimiterBox.setName("delimiter");
    delimiterBox.addItem(constants.Comma(), ",");
    delimiterBox.addItem(constants.Semicolon(), ";");
    delimiterPanel.setSpacing(2);
    delimiterPanel.add(delimiterBox);
    delimiterPanel.add(new HTML(constants.CSVDelimiter()));

    HorizontalPanel downloadPanel = new HorizontalPanel();
    downloadPanel.setSpacing(2);
    cancelButton = new Button(constants.Close());
    downloadPanel.add(downloadButton);
    downloadPanel.add(cancelButton);
    mainVp.setWidth("300px");
    mainVp.add(hiddenSessionId);
    mainVp.add(hiddenQuery);
    mainVp.add(extraInfo);

    mainVp.add(termsOfUseLabel);
    mainVp.add(delimiterPanel);
    mainVp.add(downloadPanel);
    form.setWidget(mainVp);
    setWidget(form);
    mainVp.setSpacing(5);
    downloadButton.addClickHandler(this);
    cancelButton.addClickHandler(this);
    // FormPanel only works if attached to something.
    // RootPanel.get().add(this);

  }

  @Override
  public void hide(boolean autoClose) {
    setVisible(false);
  }

  public void onClick(ClickEvent event) {
    Object source = event.getSource();
    if (source == downloadButton) {
      if (queryFilters != null) {
        String sid = Cookies.getCookie(ApplicationView.SESSION_ID_NAME);
        submit(sid);
        hide();
      }
    } else if (source == cancelButton) {
      hide();
    }
  }

  public void show(Set<String> queryFilters, String extraInfomation) {
    this.queryFilters = queryFilters;
    extraInfo.setValue(extraInfomation);
    setVisible(true);
    center();
  }

  public void show(String filter, String extraInfo) {
    if (filter != null && !filter.equals("")) {
      Set<String> filters = new HashSet<String>();
      filters.add(filter);
      show(filters, extraInfo);
    }

  }

  public void submit(String sid, Set<String> queryFilters) {
    hiddenSessionId.setValue(sid);
    String filters = "";
    for (String filter : queryFilters) {
      filters += "," + filter;
    }
    filters = filters.replaceFirst(",", "");
    hiddenQuery.setValue(filters);
    form.submit();
  }

  private void submit(String sid) {
    submit(sid, queryFilters);
  }

}
