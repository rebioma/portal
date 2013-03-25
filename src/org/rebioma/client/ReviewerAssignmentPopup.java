package org.rebioma.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ReviewerAssignmentPopup extends PopupPanel {

  private final Image closeImage;
  private final FormPanel formPanel;
  private final CheckBox isClearOldAssignmentCb;
  private final Hidden hidden;

  public ReviewerAssignmentPopup(String title) {
    super(false, true);
    super.setAnimationEnabled(true);
    super.setGlassEnabled(true);
    closeImage = new Image("images/xclose.gif");
    isClearOldAssignmentCb = new CheckBox();
    hidden = new Hidden();
    hidden.setName("sessionId");
    HorizontalPanel titlePanel = new HorizontalPanel();
    Label titleLb = new Label(title);
    titleLb.addStyleName("title");
    titlePanel.add(titleLb);
    titlePanel.add(closeImage);

    formPanel = new FormPanel();
    VerticalPanel formContent = new VerticalPanel();
    formPanel.setWidget(formContent);
    FileUpload fileUpload = new FileUpload();
    fileUpload.setName("file_upload");
    formPanel.setAction(GWT.getModuleBaseURL() + "reviewAssignment");
    formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
    formPanel.setMethod(FormPanel.METHOD_POST);
    formContent.add(fileUpload);
    formContent.add(hidden);
  }

}
