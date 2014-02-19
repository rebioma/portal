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

import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.IUploader.OnFinishUploaderHandler;
import gwtupload.client.IUploader.Utils;

import java.util.HashSet;
import java.util.Set;

import org.rebioma.client.UsersTable.CheckedClickListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.info.Info;

public class UploadView extends ComponentView implements CheckedClickListener {
  public interface UploadListener {
    void onUploadComplete();

    void onUploadStart();
  }

  private class StatusMessage extends PopupPanel implements
      OpenHandler<TreeItem>, CloseHandler<TreeItem>, ResizeHandler {
    private final Tree badIdTree = new Tree();
    private final Label failedLabel = new Label();
    private final TreeItem idNotInDbRoot = new TreeItem();
    private final TreeItem improperIdRoot = new TreeItem();

    private final ScrollPanel infoSp = new ScrollPanel();
    private final TreeItem multipleIdRoot = new TreeItem();
    private final TreeItem outerRoot = new TreeItem();
    private final Label updatedLabel = new Label();
    private final Label uploadedLabel = new Label();

    private final VerticalPanel vp = new VerticalPanel();
    private final TreeItem notYourRecordRoot = new TreeItem();

    public StatusMessage() {
      super(true);
      hide();

      setWidget(infoSp);
      vp.add(new Label(constants.UploadSuccess1()));
      vp.add(uploadedLabel);
      vp.add(updatedLabel);
      vp.add(failedLabel);
      failedLabel.setStylePrimaryName("cw-RedText");

      badIdTree.addItem(outerRoot);
      outerRoot.setHTML(constants.UploadFailedBadIds1());
      outerRoot.addItem(improperIdRoot);
      outerRoot.addItem(idNotInDbRoot);
      outerRoot.addItem(multipleIdRoot);
      outerRoot.addItem(notYourRecordRoot);

      vp.add(badIdTree);
      infoSp.setWidget(vp);

      badIdTree.addOpenHandler(this);
      badIdTree.addCloseHandler(this);
      Window.addResizeHandler(this);
    }

    public void checkAndResize() {
      int width = Window.getClientWidth() - (getPopupLeft() + 20);
      if (vp.getOffsetWidth() > width) {
        setWidth(width + "px");
      } else {
        setWidth(vp.getOffsetWidth() + 20 + "px");
      }
      int height = Window.getClientHeight() - getPopupTop();
      if (vp.getOffsetHeight() > height) {
        setHeight(height + "px");
      } else {
        setHeight(vp.getOffsetHeight() + "px");
      }
    }

    public void onClose(CloseEvent<TreeItem> event) {
      checkAndResize();
    }

    public void onOpen(OpenEvent<TreeItem> event) {
      checkAndResize();
    }

    public void onResize(ResizeEvent event) {
      checkAndResize();
    }

    public void showMessage(JSONValue messageValue) {
      /*setPopupPosition(fileUpload.getAbsoluteLeft()
          + fileUpload.getOffsetWidth(), fileUpload.getAbsoluteTop());
      setHeight((uploadButton.getAbsoluteTop() + uploadButton.getOffsetHeight() - fileUpload
          .getAbsoluteTop())
          + "px");*/
      center();
      JSONObject messageObj = messageValue.isObject();

      uploadedLabel.setText(messageObj.get("Uploaded") + " "

      + constants.UploadSuccess2());
      updatedLabel.setText(messageObj.get("Updated") + " "
          + constants.UploadSuccess3());
      failedLabel.setText(messageObj.get("Failed") + " "
          + constants.UploadSuccess4());

      JSONValue badIdValue = messageObj.get("badIdMessage");
      if (badIdValue != null) {
        JSONObject badIdObj = badIdValue.isObject();

        improperIdRoot.removeItems();
        JSONArray improperIdArray = badIdObj.get("improperIds").isArray();
        for (int i = 0; i < improperIdArray.size(); i++) {
          improperIdRoot.addItem(improperIdArray.get(i).toString());
        }
        improperIdRoot.setHTML(constants.UploadFailedBadIds2() + " ("
            + improperIdArray.size() + "):");
        improperIdRoot.setVisible(true);

        idNotInDbRoot.removeItems();
        JSONArray idNotInDbArray = badIdObj.get("idNotInDb").isArray();
        for (int i = 0; i < idNotInDbArray.size(); i++) {
          idNotInDbRoot.addItem(idNotInDbArray.get(i).toString());
        }
        idNotInDbRoot.setHTML(constants.UploadFailedBadIds3() + " ("
            + idNotInDbArray.size() + "):");
        idNotInDbRoot.setVisible(true);

        multipleIdRoot.removeItems();
        JSONArray multipleIdArray = badIdObj.get("multipleId").isArray();
        for (int i = 0; i < multipleIdArray.size(); i++) {
          multipleIdRoot.addItem(multipleIdArray.get(i).toString());
        }
        multipleIdRoot.setHTML(constants.UploadFailedBadIds4() + " ("
            + multipleIdArray.size() + "):");
        multipleIdRoot.setVisible(true);

        JSONArray notYourRecordsArry = badIdObj.get("notYourRecords").isArray();
        for (int i = 0; i < notYourRecordsArry.size(); i++) {
          notYourRecordRoot.addItem(notYourRecordsArry.get(i).toString());
        }
        notYourRecordRoot.setHTML(constants.UploadFailedBadIds5() + " ("
            + notYourRecordsArry.size() + "):");
        notYourRecordRoot.setVisible(true);
      }

      boolean showTree = false;
      for (int i = 0; i < outerRoot.getChildCount(); i++) {
        if (outerRoot.getChild(i).getChildCount() == 0) {
          TreeItem rootElem = outerRoot.getChild(i);
          rootElem.setVisible(false);
        } else {
          showTree = true;
        }
      }

      badIdTree.setVisible(showTree);
      show();
    }
  }

  public static ViewInfo init(final View parent,
      final UploadListener uploadListener) {
    return new ViewInfo() {

      @Override
      public String getName() {
        return UPLOAD;
      }

      @Override
      protected View constructView() {
        return new UploadView(parent, uploadListener);
      }

      @Override
      protected String getHisTokenName() {
        // TODO Auto-generated method stub
        return UPLOAD;
      }
    };
  }

  private final Button cancelButton = new Button(constants.Cancel());

  private final Set<String> checkedFriends = new HashSet<String>();
  private final Hidden collaboratorsSharing = new Hidden("collaborators");
  private final ListBox delimiterBox = new ListBox();
  private final FileUpload fileUpload = new FileUpload();
  /**
   * your friends Table
   */
  private final UsersTable friendsTable = new UsersTable(true);
  private final Hidden hiddenSessionId = new Hidden("sessionId");
  private final HistoryState historyState = new HistoryState() {

    @Override
    public Object getHistoryParameters(UrlParam param) {
      switch (param) {
      case VIEW:
        return stringValue(param);
      case CHECKED:
        return getCheckedValues(true);
      case SHOW_EMAIL:
      case PRIVATE:
      case PUBLIC:
      case MODELING:
        return Boolean.parseBoolean(stringValue(param));
      }
      return "";
    }

  };
  private final ScrollPanel mainSp = new ScrollPanel();
  private final CheckBox modelingBox = new CheckBox(constants.Modeling());
  private final StatusMessage popupStatusMessage = new StatusMessage();
  private final RadioButton privateRadioButton = new RadioButton(
      "private_vetter", constants.Private());
  private final RadioButton publicRadioButton = new RadioButton(
	      "public_vetter", constants.Public());
  private final CheckBox clearReviewCheckBox = new CheckBox("Clear review");
  private final CheckBox showEmailBox = new CheckBox(constants.ShowEmail());
  private final Button uploadButton = new Button(constants.AcceptDsaUpload());
  //private final FormPanel uploadForm = new FormPanel();
  private final UploadListener uploadListener;
  private final HTML dataSharingAgreementLink = new HTML(
      "<a href='https://sites.google.com/site/rebiomahelp/home/english#datasharing' target='_blank'>Data Sharing Agreement (DSA)</a>");

  private UploadView(View parent, UploadListener uListener) {
	  super(parent, false);
	    this.uploadListener = uListener;
	    modelingBox.setName("modeling");
	    final VerticalPanel uploadPanel = new VerticalPanel();
	    uploadPanel.setStyleName("pupload");
	    HorizontalPanel privateModelField = new HorizontalPanel();
	    privateModelField.add(privateRadioButton);
	    privateModelField.add(new HTML("&nbsp;("));
	    privateModelField.add(modelingBox);
	    privateModelField.add(new HTML(")"));
	    HorizontalPanel delimiterPanel = new HorizontalPanel();
	    	delimiterBox.addItem(constants.Comma(), ",");
	    	delimiterBox.addItem(constants.Semicolon(), ";");
	    	delimiterBox.setName("delimiter");
	    	delimiterPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
    	delimiterPanel.add(delimiterBox);
	    delimiterPanel.add(new HTML("&nbsp;&nbsp;" + constants.CSVDelimiter()));
		    
	    fileUpload.setName("file_upload");
	    //uploadForm.setAction(GWT.getModuleBaseURL() + "upload");
	    //uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
	    //uploadForm.setMethod(FormPanel.METHOD_POST);
	    //uploadPanel.add(fileUpload);
	    uploadPanel.add(delimiterPanel);
	    showEmailBox.setName("show_email");
	    clearReviewCheckBox.setName("clear_review");
	    clearReviewCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(event.getValue()) {
					MessageBox boxWarning = new MessageBox("Reset review state?", "");
	            	boxWarning.setPredefinedButtons(PredefinedButton.YES, PredefinedButton.CANCEL);
	            	boxWarning.setIcon(MessageBox.ICONS.warning());
	            	boxWarning.setWidth("415px");
	            	boxWarning.setMessage("You are resetting all the TRB's review on all occurrences. Would you like to continue?");
	            	boxWarning.addHideHandler(new HideHandler() {

						@Override
						public void onHide(HideEvent eventW) {
							Dialog btnW = (Dialog) eventW.getSource();
							if(!btnW.getHideButton().getText().equalsIgnoreCase("yes")) {
								clearReviewCheckBox.setChecked(false);
							} 
						}});
	            	
	            	boxWarning.show();
				} 
			}
		});
	    uploadPanel.add(clearReviewCheckBox);
	    uploadPanel.add(showEmailBox);
	    uploadPanel.add(privateModelField);
	    uploadPanel.add(publicRadioButton);
	    setClearReview(ApplicationView.getCurrentState() == ViewState.SUPERADMIN);
	    UserQuery query = friendsTable.getQuery();
	    query.setUsersCollaboratorsOnly(true);
	    friendsTable.addCheckedListener(this);
	    // TODO: fix resize
	    // friendsTable.setSize("500px", "300px");
	    friendsTable.resetTable();
	    HorizontalPanel hp = new HorizontalPanel();
	    hp.setSpacing(10);
	    uploadPanel.add(hp);
	    //HorizontalPanel buttonPanel = new HorizontalPanel();
	   // buttonPanel.add(uploadButton);
	   // buttonPanel.add(cancelButton);
	   // buttonPanel.setSpacing(2);
	    //uploadPanel.add(dataSharingAgreementLink);
	   // uploadPanel.add(buttonPanel);
	    hiddenSessionId.setValue(Cookies
	            .getCookie(ApplicationView.SESSION_ID_NAME));
	    uploadPanel.add(hiddenSessionId);
	    uploadPanel.add(collaboratorsSharing);
	    final VerticalPanel friendsPanel = new VerticalPanel();
	    Label collaborator = new Label(constants.CollaboratorsOnUpload());
	    collaborator.setStyleName("title1");
	    friendsPanel.add(collaborator);
	    friendsPanel.add(friendsTable);
	    //HorizontalPanel friendUplodaHp = new HorizontalPanel();
	    //friendUplodaHp.add(uploadPanel);
	    //friendUplodaHp.add(friendsPanel);
	    //friendUplodaHp.setSpacing(5);

	    //uploadForm.setWidget(friendUplodaHp);
	    /*mainSp.setWidget(uploadForm);
	    initWidget(mainSp);*/

	    final FlexTable grid = new FlexTable();
	    grid.setStyleName("GWTUpld");
	    final FormPanel form = new FormPanel(){
	      public void add(Widget w) {
	    	  grid.setWidget(grid.getRowCount(), 1, uploadPanel);
	    	  grid.getFlexCellFormatter().setRowSpan(0, 2, 20);
	    	  grid.setWidget(0, 2, new Label("   "));
	    	  grid.getFlexCellFormatter().setStyleName(0, 2, "sep");
	    	  grid.getFlexCellFormatter().setRowSpan(0, 3, 20);
	    	  grid.setWidget(0, 3, friendsPanel);
	    	  grid.setWidget(grid.getRowCount(), 1, dataSharingAgreementLink);
	    	  grid.setWidget(grid.getRowCount(), 1, w);
	    	  //grid.getFlexCellFormatter().setColSpan(grid.getRowCount()+1, 1, 2);
	      }
	      {super.add(grid);}
	    };
	    class MyFancyLookingSubmitButton extends Composite implements HasClickHandlers {
	        DecoratorPanel widget = new DecoratorPanel();

	        public MyFancyLookingSubmitButton() {
	            Button widget = new Button(constants.AcceptDsaUpload());
	            initWidget(widget);
	        }

	        public HandlerRegistration addClickHandler(ClickHandler handler) {
	            return addDomHandler(handler, ClickEvent.getType());
	        }
	        
	    }
	    
	    SingleUploader uploader = new SingleUploader(FileInputType.LABEL, new ModalUploadStatus(), new MyFancyLookingSubmitButton(), form);
	    uploader.setServletPath("send.file");
	    //mainSp.add(uploader);
	    //grid.setText(5, 0, "Attachment:");
	    mainSp.setWidget(uploader);
	    uploader.getFileInput().setText(constants.chooseFileLabel());
	    initWidget(mainSp);
	    
	    uploader.addOnFinishUploadHandler(new OnFinishUploaderHandler(){
	      public void onFinish(IUploader uploader) {
	        if (uploader.getStatus() == Status.SUCCESS) {
	        	Document doc = null;
	        	String displayMsg = null;
	        	try{
	        		doc = XMLParser.parse(uploader.getServerResponse());
	        		displayMsg = Utils.getXmlNodeValue(doc, "message");
	        	}catch(Exception e){}
	            
	          //Window.alert("Server response: \n" + displayMsg);
	          
	          setUploadEnable(true);
	          if(displayMsg!=null){
	          if (displayMsg.matches("<.+>.+<.+>")) {
	            displayMsg = displayMsg.substring(displayMsg.indexOf('>') + 1,
	                displayMsg.lastIndexOf('<'));
	          }
	          JSONObject serverMsg = JSONParser.parse(displayMsg).isObject();
	          if (serverMsg.get("onSuccess") != null) {
	            JSONValue successValue = serverMsg.get("onSuccess");
	            popupStatusMessage.showMessage(successValue);
	            popupStatusMessage.setWidth("300px");
	            //uploadForm.reset();
	          } else {
	            JSONObject error = serverMsg.get("onFailure").isObject();
	            String errorKey = error.keySet().iterator().next();
	            JSONValue jValue = error.get(errorKey);
	            if (errorKey.equals("No File")) {
	              Window.alert(constants.UploadFailedNoFile());
	            } else if (errorKey.equals("Invalid File")) {
	              Window.alert(constants.UploadFailedInvalidFile());
	            } else if (errorKey.equals("Missing Required Headers")) {
	              Window.alert(constants.MissingRequiredHeader() + jValue.isArray());
	            } else {
	              Window.alert(constants.UploadFailedException() + jValue.isString());
	            }
	          }
	          }
	          DataSwitch.get().clearCache(DataSwitch.OCCURRENCE_KEY);
	          uploadListener.onUploadComplete();
	          popupStatusMessage.checkAndResize();
	          uploader.getFileInput().setText(constants.chooseFileLabel());
	          uploader.getFileInput().setSize("200px", "auto");
	          uploader.reset();
	          form.reset();
	        }
	      }
	    });
	    
	    initListeners();
	    cancelButton.setEnabled(false);
	    publicRadioButton.setValue(true);
	    showEmailBox.setValue(true);

	    // Adds upload form handlers
	    //uploadForm.addSubmitHandler(new SubmitHandler() {

	      /**
	       * If the uploading file is empty display an error message and cancels the
	       * submit event.
	       * 
	       * @see com.google.gwt.user.client.ui.FormPanel.SubmitHandler#onSubmit(com.google.gwt.user.client.ui.FormPanel.SubmitEvent)
	       */
	      /*public void onSubmit(SubmitEvent event) {
	        if (fileUpload.getFilename().equals("")) {
	          Window.alert(constants.FileCantBeEmpty());
	          event.cancel();
	        } else {
	          uploadListener.onUploadStart();
	          setUploadEnable(false);
	        }
	      }

	    });*/

	    //uploadForm.addSubmitCompleteHandler(new SubmitCompleteHandler() {
	      /**
	       * Notifies the user whether the upload was successful. Also clears the
	       * DataSwitch cache since new records were uploaded.
	       * 
	       * @see com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler#onSubmitComplete(com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent)
	       */
	    /*public void onSubmitComplete(SubmitCompleteEvent event) {
	        setUploadEnable(true);
	        String displayMsg = event.getResults();
	        // displayMsg =
	        // "<something>{\"onFailure\": \"No file uploaded\"}</something>";
	        if (displayMsg.matches("<.+>.+<.+>")) {
	          displayMsg = displayMsg.substring(displayMsg.indexOf('>') + 1,
	              displayMsg.lastIndexOf('<'));
	        }
	        JSONObject serverMsg = JSONParser.parse(displayMsg).isObject();
	        if (serverMsg.get("onSuccess") != null) {
	          JSONValue successValue = serverMsg.get("onSuccess");
	          popupStatusMessage.showMessage(successValue);
	          uploadForm.reset();
	        } else {
	          JSONObject error = serverMsg.get("onFailure").isObject();
	          String errorKey = error.keySet().iterator().next();
	          JSONValue jValue = error.get(errorKey);
	          if (errorKey.equals("No File")) {
	            Window.alert(constants.UploadFailedNoFile());
	          } else if (errorKey.equals("Invalid File")) {
	            Window.alert(constants.UploadFailedInvalidFile());
	          } else if (errorKey.equals("Missing Required Headers")) {
	            Window.alert(constants.MissingRequiredHeader() + jValue.isArray());
	          } else {
	            Window.alert(constants.UploadFailedException() + jValue.isString());
	          }
	        }
	        DataSwitch.get().clearCache(DataSwitch.OCCURRENCE_KEY);
	        uploadListener.onUploadComplete();
	        popupStatusMessage.checkAndResize();
	      }
	    });*/

	  }

  private void setClearReview(boolean visible) {
	  clearReviewCheckBox.setChecked(false);
	  clearReviewCheckBox.setVisible(visible);
  }
  @Override
  public String historyToken() {
    StringBuilder sb = new StringBuilder();
    sb.append(getUrlToken(UrlParam.PRIVATE));
    sb.append(getUrlToken(UrlParam.PUBLIC));
    sb.append(getUrlToken(UrlParam.MODELING));
    sb.append(getUrlToken(UrlParam.SHOW_EMAIL));
    sb.append(getUrlToken(UrlParam.CHECKED));
    sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }

  public void onChecked(String email, Integer id, boolean checked, int row) {
    if (checked) {
      checkedFriends.add(email);
      resetCollaboratorsSharing();
    } else {
      checkedFriends.remove(email);
      resetCollaboratorsSharing();
    }
  }

  @Override
  protected void handleOnValueChange(String historyToken) {
    historyState.setHistoryToken(historyToken);
    restoreChecks();
    boolean showEmailBoxChecked = (Boolean) historyState
        .getHistoryParameters(UrlParam.SHOW_EMAIL);
    boolean privateChecked = (Boolean) historyState
        .getHistoryParameters(UrlParam.PRIVATE);
    boolean publicChecked = (Boolean) historyState
        .getHistoryParameters(UrlParam.PUBLIC);
    boolean modelingChecked = (Boolean) historyState
        .getHistoryParameters(UrlParam.MODELING);
    showEmailBox.setValue(showEmailBoxChecked);
    publicRadioButton.setValue(publicChecked);
    privateRadioButton.setValue(privateChecked && !publicChecked);
    modelingBox.setValue(modelingChecked && !publicChecked && privateChecked);
    addHistoryItem(false);
  }

  @Override
  protected boolean isMyView(String value) {
    historyState.setHistoryToken(value);
    return historyState.getHistoryParameters(UrlParam.VIEW).toString()
        .equalsIgnoreCase(UPLOAD);
  }

  @Override
  protected void resetToDefaultState() {
    showEmailBox.setValue(true);
    privateRadioButton.setValue(false);
    publicRadioButton.setValue(true);
    modelingBox.setValue(false);
    setClearReview(ApplicationView.getCurrentState() == ViewState.SUPERADMIN);
  }

  @Override
  protected void resize(int width, int height) {
    height = height - mainSp.getAbsoluteTop();
    if (height <= 0) {
      height = 1;
    }
    mainSp.setPixelSize(width - 22, height - 10);
    setClearReview(ApplicationView.getCurrentState() == ViewState.SUPERADMIN);
  }

  protected void updateChecksMap() {
    // TODO:
  }

  private String getUrlToken(UrlParam param) {
    String query = param.lower() + "=";
    switch (param) {
    case CHECKED:
      String checkedValues = historyState.getCheckedValues(true);
      if (checkedValues.equals("")) {
        query = "";
      } else {
        query += checkedValues;
      }
      break;
    case SHOW_EMAIL:
      query += showEmailBox.getValue();
      break;
    case PRIVATE:
      query += privateRadioButton.getValue();
      break;
    case PUBLIC:
      query += publicRadioButton.getValue();
      break;
    case MODELING:
      query += modelingBox.getValue();
      break;
    }
    return query.length() == 0 ? query : query + "&";
  }

  /**
   * Initialize ClickHandler for private radio, public radio, and upload buttons
   */
  private void initListeners() {

    cancelButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        stop();
        setUploadEnable(true);
        uploadListener.onUploadComplete();
      }

    });

    showEmailBox.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        addHistoryItem(false);
      }
    });

    publicRadioButton.addClickHandler(new ClickHandler() {
      /**
       * Unchecks the private radio button and the modeling check box when the
       * public radio button is clicked.
       * 
       * @see com.google.gwt.user.client.ui.ClickHandler#onClick(com.google.gwt.
       *      user.client.ui.Widget)
       */
      public void onClick(ClickEvent event) {
        if (publicRadioButton.getValue()) {
          privateRadioButton.setValue(false);
          modelingBox.setValue(false);
          addHistoryItem(false);
        }
      }

    });

    privateRadioButton.addClickHandler(new ClickHandler() {
      /**
       * Unchecks the public radio button when the private radio button is
       * clicked.
       * 
       * @see com.google.gwt.user.client.ui.ClickHandler#onClick(com.google.gwt.
       *      user.client.ui.Widget)
       */
      public void onClick(ClickEvent event) {
        if (privateRadioButton.getValue()) {
          publicRadioButton.setValue(false);
          addHistoryItem(false);
        }

      }

    });

    uploadButton.addClickHandler(new ClickHandler() {
      /**
       * When the upload button is clicked, submits the upload form.
       * 
       * @see com.google.gwt.user.client.ui.ClickHandler#onClick(com.google.gwt.
       *      user.client.ui.Widget)
       */
      public void onClick(ClickEvent event) {
        hiddenSessionId.setValue(Cookies
            .getCookie(ApplicationView.SESSION_ID_NAME));
        //uploadForm.submit();
      }

    });

    modelingBox.addClickHandler(new ClickHandler() {
      /**
       * Sets the modeling box to false if the public radio button is currently
       * checked.
       */
      public void onClick(ClickEvent event) {
        boolean addHistory = true;
        if (publicRadioButton.getValue() && modelingBox.getValue()) {
          modelingBox.setValue(false);
          addHistory = false;
        }
        if (addHistory) {
          addHistoryItem(false);
        }
      }
    });

  }

  /**
   * sharedUserIdsCSV format: space userid space, space userid space, ...,
   * .There is and comma in the end of the id list to rule out end of the list
   * corner case when a user id is removed from the list. A space before and
   * after userId to ensure searching user id in sharedUserIdsCSV return the
   * correct result (i.e 18 and 8 are there same in like query without extra
   * spaces).
   */
  private void resetCollaboratorsSharing() {
    String sharedUserIdsCSV = "";
    for (String email : checkedFriends) {
      sharedUserIdsCSV += email + ",";
    }
    collaboratorsSharing.setValue(sharedUserIdsCSV);
  }

  private void restoreChecks() {
    historyState.parseCheckedUrl();
    // TODO:
  }

  private void setUploadEnable(boolean enabled) {
    if (enabled) {
      uploadButton.setText(constants.Upload());
    } else {
      uploadButton.setText(constants.Uploading());
    }
    uploadButton.setEnabled(enabled);
    cancelButton.setEnabled(!enabled);
  }

  private native void stop() /*-{
    var browser=navigator.appName;
    if(browser == "Microsoft Internet Explorer") {
    $doc.execCommand('Stop');
    } else if (browser == "Netscape" || browser == "Mozilla" || browser == "Firefox") {
    $wnd.stop();
    } else {
    $wnd.location.reload();
    }
  }-*/;
}
