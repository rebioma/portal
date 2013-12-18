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

import org.rebioma.client.View.ViewState;
import org.rebioma.client.bean.User;
import org.rebioma.client.i18n.AppConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.FormPanel.Encoding;
import com.sencha.gxt.widget.core.client.form.FormPanel.Method;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

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
  private final Hidden userEmail;
  private final Button downloadButton;
  private final Button cancelButton;
  private Set<String> queryFilters;
  private String extraInfomation;
  private Label info;
  boolean myDownoad;
  private final HTML termsOfUseLabel = new HTML(
      "<a href='https://sites.google.com/site/rebiomahelp/home/english#datause' target='_blank'>Data Use Agreement (DUA)</a>");

  private FieldLabel fieldTitle;
  private FieldLabel fieldFirstN;
  private FieldLabel fieldLastN;
  private FieldLabel fieldActivity;
  private FieldLabel fieldEmail;
  private FieldLabel fieldInstitution;
  private FieldLabel fieldDataUE;
  
  private class RTextField extends TextField {
	  public RTextField(boolean allowBlank){
		  super();
		  setAllowBlank(allowBlank);
		  setWidth(270);
	  }
	  
  }
  public CsvDownloadWidget() {
    super(true);
    AppConstants constants = ApplicationView.getConstants();
    myDownoad = false;
    form = new FormPanel();
    downloadButton = new Button(constants.AcceptAndDownload());
    form.setAction(GWT.getModuleBaseURL() + "download");

    form.setEncoding(Encoding.MULTIPART);
    form.setMethod(Method.POST);
//    form.setEncoding(FormPanel.ENCODING_MULTIPART);
//    form.setMethod(FormPanel.METHOD_POST);
    hiddenSessionId = new Hidden("sessionId");
    hiddenQuery = new Hidden("query");
    extraInfo = new Hidden("extra");
    userEmail = new Hidden("useremail");
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
    mainVp.setWidth("380px");
    mainVp.add(hiddenSessionId);
    mainVp.add(hiddenQuery);
    mainVp.add(extraInfo);
    mainVp.add(userEmail);
    RTextField title = new RTextField(false);
    title.setName("title");
    title.setEmptyText("Mr, Mme, Pr, Dr, ...");
    RTextField firstN = new RTextField(false);
    firstN.setName("firstN");
    RTextField lastN = new RTextField(true);
    lastN.setName("lastN");
    RTextField activity = new RTextField(false);
    activity.setName("activity");
    RTextField email = new RTextField(false);
    email.setName("email");
    RTextField institution = new RTextField(true);
    institution.setName("institution");
    TextArea dataUE = new TextArea();
    dataUE.setWidth(270);
    dataUE.setAllowBlank(false);
    dataUE.setName("dataue");
    info = new Label("* requiered field");
    fieldTitle =new FieldLabel(title, "Title*");
    fieldFirstN =new FieldLabel(firstN, "First name*");
    fieldLastN =new FieldLabel(lastN, "Last name");
    fieldActivity =new FieldLabel(activity, "Activities*/ Profession");
    fieldEmail =new FieldLabel(email, "Email*");
    fieldInstitution =new FieldLabel(institution, "Institution");
    fieldDataUE =new FieldLabel(dataUE, "Data use explanation*");
    
    mainVp.add(fieldTitle);
    mainVp.add(fieldFirstN);
    mainVp.add(fieldLastN);
    mainVp.add(fieldActivity);
    mainVp.add(fieldEmail);
    mainVp.add(fieldInstitution);
    mainVp.add(fieldDataUE);
    mainVp.add(info);
    mainVp.add(new FieldLabel(delimiterBox, constants.CSVDelimiter()));
//    mainVp.add(delimiterPanel);
    mainVp.add(termsOfUseLabel);
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
	form.reset();
    setVisible(false);
  }

  public void onClick(ClickEvent event) {
    Object source = event.getSource();
    if (source == downloadButton) {
    	if(!form.isValid() && !myDownoad)return;
      if (queryFilters != null) {
        String sid = Cookies.getCookie(ApplicationView.SESSION_ID_NAME);
        submit(sid);
        hide();
      }
    } else if (source == cancelButton) {
      hide();
    }
  }
  
  public void showForm(boolean show) {
	  info.setVisible(show);
	  fieldTitle.setVisible(show);
	  fieldFirstN.setVisible(show);
	  fieldLastN.setVisible(show);
	  fieldActivity.setVisible(show);
	  fieldEmail.setVisible(show);
	  fieldInstitution.setVisible(show);
	  fieldDataUE.setVisible(show);
  }

  public void show(Set<String> queryFilters, String extraInfomation) {
    this.queryFilters = queryFilters;
    boolean show = true;
    User currentUser = ApplicationView.getAuthenticatedUser();
    if(currentUser != null) {
    	if (ApplicationView.getCurrentState() == ViewState.SUPERADMIN) {
    		show = false;
		} else for(String s:queryFilters)
    		show=!(s.trim().contains("ownerEmail")&&s.trim().contains(currentUser.getEmail()));
    	userEmail.setValue(currentUser.getEmail());
    }
    myDownoad = !show;
    showForm(show);
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
      filters += ";" + filter;
    }
    filters = filters.replaceFirst(";", "");
    hiddenQuery.setValue(filters);
    form.submit();
  }

  private void submit(String sid) {
    submit(sid, queryFilters);
  }

}
