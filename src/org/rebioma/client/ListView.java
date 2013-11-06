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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.form.client.api.DisplayPopup;
import org.rebioma.client.DataPager.PageListener;
import org.rebioma.client.OccurrenceQuery.DataRequestListener;
import org.rebioma.client.PagerWidget.PageClickListener;
import org.rebioma.client.UsersTable.CheckedClickListener;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.User;
import org.rebioma.client.bean.gxt.OccurrenceSummary;
import org.rebioma.client.services.OccurrenceService.OccurrenceServiceException;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.Store.Record;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.WidgetComponent;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.OverflowEvent;
import com.sencha.gxt.widget.core.client.event.OverflowEvent.OverflowHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.SeparatorMenuItem;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
public class ListView extends ComponentView implements
    PageListener<Occurrence>, PageClickListener, DataRequestListener, OccurrencePageSizeChangeHandler {

  private class CollaboratorsList extends DialogBox implements ResizeHandler,
      ClickHandler, CheckedClickListener {
    final UsersTable usersTable = new UsersTable();
    final ListBox sharedUsers = new ListBox(true);
    final Button sharedButton = new Button(constants.Shared());
    final Button unshareButton = new Button(constants.Unshared());
    final Button cancelButton = new Button(constants.Cancel());
    final ScrollPanel sp = new ScrollPanel();
    final VerticalPanel mainVp = new VerticalPanel();
    Set<String> checkedUsers = new HashSet<String>();

    Set<Occurrence> currentSelectedOccurrences;

    CollaboratorsList() {
      super(true);
      setHTML("<center>" + constants.SharedUsersTitle() + "</center>");
      setWidget(sp);
      sp.setWidget(mainVp);
      usersTable.setCheckedUsers(checkedUsers);
      mainVp.add(usersTable);
      HorizontalPanel hp = new HorizontalPanel();
      hp.add(sharedButton);
      hp.add(unshareButton);
      hp.add(cancelButton);
      hp.setSpacing(5);
      mainVp.add(hp);
      sharedButton.addClickHandler(this);
      cancelButton.addClickHandler(this);
      unshareButton.addClickHandler(this);
      sharedButton.setEnabled(false);
      unshareButton.setEnabled(false);
      Window.addResizeHandler(this);
      usersTable.addCheckedListener(this);
      mainSp.setHeight("200px");
    }

    public void onChecked(String email, Integer id, boolean checked, int row) {
      if (checked) {
        checkedUsers.add(email);
      } else {
        checkedUsers.remove(email);
      }
      boolean enabled = !checkedUsers.isEmpty();
      sharedButton.setEnabled(enabled);
      unshareButton.setEnabled(enabled);
      // a hack, no idea why the table is disappeared sometime in the DialogBox
      // when a button is disable.
      mainVp.insert(usersTable, 0);
    }

    public void onClick(ClickEvent event) {
      Object source = event.getSource();
      if (source == sharedButton) {
        sharedRecords();
      } else if (source == unshareButton) {
        unsharedRecords();
      }
      hide();
    }

    public void onResize(ResizeEvent event) {
      if (isShowing()) {
        center();
      }

    }

    public void showUsers(Set<Occurrence> occurrences) {
      this.currentSelectedOccurrences = occurrences;
      center();
      UserQuery query = usersTable.getQuery();
      query.setUsersCollaboratorsOnly(true);
      checkedUsers.clear();
      usersTable.resetTable();
    }
    //shared records
    private void sharedRecords() {
      if (applyToAllCb.getValue()) {
        StringBuilder userIdCSV = new StringBuilder();
        for (String userEmail : checkedUsers) {
          userIdCSV.append(userEmail + ",");
        }
        updateAll("sharedUsers = " + userIdCSV.toString(), "");
      } else {
        Set<Occurrence> occurrences = getCheckedValidOccurrences(UPDATE_COLLABORATORS);
        for (Occurrence o : occurrences) {
          String currentCollaborators = o.getSharedUsersCSV();
          if (currentCollaborators == null) {
            currentCollaborators = "";
          }
          for (String userEmail : checkedUsers) {
            if (!currentCollaborators.contains(userEmail)) {
              currentCollaborators = currentCollaborators.concat(userEmail
                  + ",");
            }
          }
          o.setSharedUsersCSV(currentCollaborators);

        }
        updateOccurrences(occurrences, constants.UpdateSharedUserMsg());
      }
    }

    private void unsharedRecords() {
      if (applyToAllCb.getValue()) {
        StringBuilder userIdCSV = new StringBuilder();
        for (String userEmail : checkedUsers) {
          userIdCSV.append(userEmail + ",");
        }
        updateAll("unsharedUsers = " + userIdCSV.toString(), "");
      } else {
        Set<Occurrence> occurrences = getCheckedValidOccurrences(UPDATE_COLLABORATORS);
        for (Occurrence o : occurrences) {
          String currentCollaborators = o.getSharedUsersCSV();
          if (currentCollaborators == null) {
            currentCollaborators = "";
          }
          for (String userEmail : checkedUsers) {
            if (currentCollaborators.contains(userEmail)) {
              currentCollaborators = currentCollaborators.replace(userEmail
                  + ",", "");
              currentCollaborators = currentCollaborators
                  .replace(userEmail, "");
            }
          }
          o.setSharedUsersCSV(currentCollaborators);
        }
        updateOccurrences(occurrences, constants.UpdateSharedUserMsg());
      }
    }

  }

  /**
   * Manage history states of list view.
   * 
   * @author tri
   * 
   */
  private class ListState extends HistoryState {

    public Object getHistoryParameters(UrlParam param) {
      switch (param) {
      case VIEW:
      case TYPE:
        return stringValue(param);
      case CHECKEDALL:
        return Boolean.parseBoolean(stringValue(param));
      }
      return "";
    }
  }

  private class ReviewerCommentPopup implements ClickHandler {
    private TextArea commentArea;
    private Label header;
    private Button submitButton;
    private VerticalPanel mainContainer;
    private boolean isAll;
    private boolean reviewed;
    private Set<Integer> occurrenceIds;
    private CheckBox checkBox;

    public ReviewerCommentPopup() {

    }

    public void display(boolean isAll, boolean reviewed,
        Set<Integer> occurrenceIds) {
      if (mainContainer == null) {
        mainContainer = new VerticalPanel();
        commentArea = new TextArea();
        checkBox = new CheckBox();
        submitButton = new Button(constants.Submit());
        header = new Label(constants.ReviewComment());
        mainContainer.add(commentArea);
        mainContainer.setSpacing(5);
        HorizontalPanel hp = new HorizontalPanel();
		Label ckLabel = new Label("Do you want to send an email right now?");
		hp.setSpacing(5);
		checkBox.setValue(false);
		hp.add(checkBox);
		hp.add(ckLabel);
		mainContainer.add(hp);
        mainContainer.add(submitButton);
        submitButton.addClickHandler(this);
      }
      setEnable(true);
      this.isAll = isAll;
      this.reviewed = reviewed;
      this.occurrenceIds = occurrenceIds;
      DisplayPopup displayPopup = DisplayPopup.getDefaultDisplayPopup();
      displayPopup.setTitleWidget(header);
      displayPopup.setContentWidget(mainContainer);
      displayPopup.show();
    }

    public void onClick(ClickEvent event) {
      Object source = event.getSource();
      if (source == submitButton) {
        String comment = commentArea.getText().trim();
        if (!comment.isEmpty() || Window.confirm(constants.NoCommentReview())) {
          setEnable(false);
          comment += constants.commentLeftWhenReviewed();  
          if (isAll) {
            reviewAllRecords(reviewed, comment, checkBox.getValue());
          } else {
        	reviewRecords(occurrenceIds, reviewed, comment, checkBox.getValue());
          }
        }

      }

    }

    public void setEnable(boolean enabled) {
      submitButton.setEnabled(enabled);
      if (enabled) {
        submitButton.setText(constants.Submit());
      } else {
        submitButton.setText(constants.Submitting());
      }
    }

  }

  /**
   * A green check image url for table columns with boolean value true/yes.
   */
  public static final String CHECK_IMG_URL = "images/greenCheck.png";

  /**
   * Default number of row per page.
   */
  public static final int DEFAULT_PAGE_SIZE = 50;

  /**
   * A unauthenticated header CSS styles.
   * 
   * Note: this array and {@link #GUEST_REQUIRED_HEADERS} array is a one to one
   * mapping.
   */
  public static final String GUEST_HEADER_CSS_STYLES[] = new String[] {
      "checkbox", "id", "accepted-species", "validated", "vetted",
      "owner-email", "shared-users", "validation-error" };

  /**
   * A authenticated header CSS styles.
   * 
   * Note: this array and {@link #USER_REQUIRED_HEADERS} array is a one to one
   * mapping.
   */
  public static final String USER_HEADER_CSS_STYLES[] = new String[] {
      "checkbox", "id", "accepted-species", "public", "validated", "vetted",
      "owner-email", "shared-users", "validation-error" };

  /**
   * A authenticated header CSS styles.
   * 
   * Note: this array and {@link #USER_REQUIRED_HEADERS} array is a one to one
   * mapping.
   */
  public static final String REVIEWER_HEADER_CSS_STYLES[] = new String[] {
      "checkbox", "id", "accepted-species", "public", "validated",
      "vetted", "owner-email", "shared-users", "shared-users",
      /*"shared-users", "shared-users", "shared-users",*/ "shared-users",
      "shared-users", "shared-users", "shared-users", "shared-users",
      "attributes", "owner-email", "owner-email", "year-c",
      "validation-error" };

  /**
   * An red "X" image for column with boolean value false/no
   */
  public static final String X_IMG_URL = "images/redX.png";
  public static final String WAITING_IMG_URL = "images/waiting.png";

  public static final String THUMB_UP_URL = "images/thumb_up.png";

  public static final String THUMB_DOWN_URL = "images/question_mark.png";

  public static final String NULL_URL = "images/blank.png";

  /**
   * Default style for this OccurrenceListView Widget
   */
  private static final String DEFAULT_STYLE = "OccurrenceView-ListView";

  protected static final String DELETE_ACTION = constants.Delete();

  protected static final String MAKE_PRIVATE_ACTION = constants.MakePrivate();
  protected static final String MAKE_PUBLIC_ACTION = constants.MakePublic();
  protected static final String NO_ACTION = constants.SelectAnAction();
  protected static final String NEGATIVELY_REVIEWED_ACTION = constants
      .NegativelyReview();
  protected static final String POSTIVELY_REVIEWED_ACTION = constants
      .PositivelyReview();
  protected static final String SHOW_EMAIL_ACTION = constants.ShowMyEmail();
  protected static final String HIDE_EMAIL_ACTION = constants.HideMyEmail();

  public static ViewInfo init(final View parent, final OccurrenceQuery query,
      final PageListener<Occurrence> pageListener,
      final OccurrenceListener occurrentListener) {
    return new ViewInfo() {

      public String getName() {
        return LIST;
      }

      protected View constructView() {
        return new ListView(parent, query, pageListener, occurrentListener);
      }

      protected String getHisTokenName() {
        return LIST;
      }
    };
  }

  private CollaboratorsList collaboratorsList = null;
  private Command delCommand;
  private Command makePrivateCommand;
  private Command makePublicCommand;
  private Command negReviewedCommand;
  private Command posReviewedCommand;
  private Command showEmailCommand;
  private Command hideEmailCommand;
  private Command showSharedUsersCommand;

  private final ActionTool actionTool;

  /**
   * A {@link VerticalPanel} contains all the widgets of OccurreceListView
   */
//  private final AbsolutePanel mainVp;
  private final VerticalLayoutContainer mainVp;
  /**
   * An {@link OccurrenceListener} that listens for a selected occurrence in
   * OccurrenceListView.
   */
  private final OccurrenceListener occurrenceListener;

  /**
   * A {@link DataPager} that uses to create data for this occurrence list view.
   */
  private final OccurrencePagerWidget pagerWidget;

  /**
   * 
   */
  private boolean signedIn = false;

  /**
   * The {@link TableWidget} that is used to display a summary of occurrence
   * return by the {@link DataPager}.
   */
  private Grid<Occurrence> table;

  private final CheckBox applyToAllCb;

  private List<Occurrence> currentPageOccurrences;

  private static final int CHECK_BOX_INDEX = 0;

  private static final String APPLY_ALL_STYLE = "Apply-All";

  protected static final String UPDATE_COLLABORATORS = constants
      .SharedUsersAction();

  private static final int MY_REREVIEWED_COL = 5;

  private boolean checkedAll = false;
  private final HistoryState historyState = new ListState();
  //private final ScrollPanel mainSp = new ScrollPanel();
  private final ContentPanel mainSp = new ContentPanel();
  private boolean isActionInit = false;
  private final Map<Integer, Integer> currentSearchColOccIdsMap = new HashMap<Integer, Integer>();
  private List<ColumnConfig<Occurrence, ?>> currentHeaders = null;
  private final ReviewerCommentPopup reviewerCommentPopup = new ReviewerCommentPopup();
  private ToolBar toolHp = null;
//  private ScrollPanel scrollPanel;

  private ListView(View parent, OccurrenceQuery query,
      PageListener<Occurrence> pageListener, OccurrenceListener oListener) {
    super(parent, false);
    occurrenceListener = oListener;
    boolean isMyOccurenceToReviewSelected =
	    isMyOccurenceToReviewSelected(History
	    	.getToken());
    List<ColumnConfig<Occurrence, ?>> authenticatedHeaders = isMyOccurenceToReviewSelected?
    		OccurrenceSummary.getReviewerColumnModel()://REVIEWER_REQUIRED_HEADERS:
    		OccurrenceSummary.getUserColumnModel();//USER_REQUIRED_HEADERS;
//	List<ColumnConfig<Occurrence, ?>> authenticatedHeadersStyle[] = isMyOccurenceToReviewSelected?
//    		REVIEWER_HEADER_CSS_STYLES:
//    		USER_HEADER_CSS_STYLES;
    currentHeaders = signedIn ? authenticatedHeaders
        : OccurrenceSummary.getGuestColumnModel();//GUEST_REQUIRED_HEADERS;
    
    ColumnModel<Occurrence> cm = new ColumnModel<Occurrence>(currentHeaders);
    
    final ListStore<Occurrence> store = new ListStore<Occurrence>(OccurrenceSummary.properties.key());
    
//    store.addAll();
    cm.getColumn(1).setCell(cellRebiomaId);
    table = new Grid<Occurrence>(store, cm);
    table.getView().setAdjustForHScroll(true);
    table.setSelectionModel(OccurrenceSummary.sm);
    
    OccurrenceSummary.sm.addSelectionHandler(new SelectionHandler<Occurrence>() {
		
		@Override
		public void onSelection(SelectionEvent<Occurrence> event) {
			if (!table.getSelectionModel().isSelected(event.getSelectedItem())) {
				setApplyAllChecked(false);
			}
			updateChecksState();
			addHistoryItem(false);
		}
	});
    
//    table.getView().setForceFit(true);
    table.setLoadMask(true);
//    table.getView().setAutoExpandColumn(currentHeaders.get(0));
    
//    table = new TableWidget(currentHeaders, signedIn ? authenticatedHeadersStyle
//        : GUEST_HEADER_CSS_STYLES, 0);
    int pageSize = query.getLimit();
	if(pageSize < 0){
		pageSize = OccurrencePagerWidget.DEFAULT_PAGE_SIZE;
	}
    this.pagerWidget = new OccurrencePagerWidget(pageSize, query, true);
    applyToAllCb = new CheckBox();
    actionTool = new ActionTool() {

      protected void setCheckedAll(boolean checked) {
        ListView.this.setCheckedAll(checked);
        addHistoryItem(false);
      }
    };
    actionTool.setDefaultSelection(0);
    query.addDataRequestListener(this);
    if (pageListener != null) {
      pagerWidget.addPageListener(pageListener);
    }
    pagerWidget.addPageListener(this);

    pagerWidget.addPageClickListener(this);
    pagerWidget.addPageSizeChangeListener(this);

    actionTool.addWidget(applyToAllCb);
//    HorizontalPanel toolHp = new HorizontalPanel();
    toolHp = new ToolBar();
//    WidgetComponent wc = new WidgetComponent(actionTool);
    
//    actionTool.setWidth("480px");
    toolHp.setEnableOverflow(true);
    toolHp.setHeight("28px");
    
    toolHp.add(actionTool);
    toolHp.add(new FillToolItem());
//    pagerWidget.setWidth("450px");
    HorizontalPanel pWHp = new HorizontalPanel();
    pWHp.add(pagerWidget);
    pWHp.setCellHorizontalAlignment(pagerWidget,
          HasHorizontalAlignment.ALIGN_RIGHT);
    pWHp.setWidth("450px");
    
    pWHp.add(editHp);
    
    reset.addClickHandler(new ClickHandler() {
    	 
		@Override
		public void onClick(ClickEvent event) {
			table.getStore().rejectChanges();
		}
    });
    
    save.addClickHandler(new ClickHandler() {
    	 
		@Override
		public void onClick(ClickEvent event) {
			if(table.getStore().getModifiedRecords().isEmpty()){
				Info.display("", "There is no modification to save");
				return;
			}
			table.getStore().commitChanges();
			String sessionId = Cookies.getCookie(ApplicationView.SESSION_ID_NAME);
			List<Occurrence> occurrences = new ArrayList<Occurrence>();
			occurrences.addAll(table.getStore().getAll());
			DataSwitch.get().editUpdate(occurrences, sessionId, 
					new AsyncCallback<Boolean>() {
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());
					GWT.log(caught.getMessage(), caught);
				}
				
				/**
				 * If result != null, update is a successful. Clears the
				 * {@link DataSwitch} cache.
				 * 
				 * @param result
				 */
				public void onSuccess(Boolean result) {
					if (result) {
						Info.display("", "Modification done successfully");
					} else {
						Window.confirm(constants.UnexpectedError());
					}
				}

			});
		}
    });
    
    initModifLink();
    showModifLink(false);
    pWHp.setCellHorizontalAlignment(editHp,
            HasHorizontalAlignment.ALIGN_RIGHT);
    toolHp.add(pWHp);
//    toolHp.setStylePrimaryName("Tool");
    mainVp = new VerticalLayoutContainer();//new AbsolutePanel();
    mainSp.setHeaderVisible(false);
    mainSp.setHeight(Window.getClientHeight() - 115 + "px");
    mainSp.setWidth("100%");
    mainSp.setBorders(false);
    mainSp.setBodyBorder(false);
    mainSp.addStyleName("margin-0");
    
    //mainVp.setSpacing(0);
//    mainVp.add(toolHp);
//    scrollPanel = new ScrollPanel();
//    scrollPanel.setStyleName("l-table");
//    scrollPanel.add(table);
//    mainVp.add(table);
    //mainVp.add(table);
//    mainVp.setStyleName(DEFAULT_STYLE);
//    toolHp.setCellHorizontalAlignment(pagerWidget,
//        HasHorizontalAlignment.ALIGN_RIGHT);
//    toolHp.setCellVerticalAlignment(pagerWidget,
//        HasVerticalAlignment.ALIGN_MIDDLE);
    // mainVp.setCellWidth(table, "100%");
//    mainVp.setWidth("100%");
    mainVp.setBorders(true);
    mainVp.add(toolHp, new VerticalLayoutData(1, -1));
    mainVp.add(table, new VerticalLayoutData(1, 1));
    
    mainSp.setWidget(mainVp);
    initWidget(mainSp);
    
    applyToAllCb.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        boolean isChecked = applyToAllCb.getValue();
        setApplyAllChecked(isChecked);
        if (isChecked) {
          setCheckedAll(true);
        } else {
          setCheckedAll(false);
        }
        addHistoryItem(false);
      }

    });
    String historyToken = History.getToken();
    if (!historyToken.trim().equals("")) {
      handleOnValueChange(historyToken);
    }
  }

  public OccurrenceListener getOccurrenceListener() {
    return occurrenceListener;
  }

  /**
   * Gets history token of this list view in form of (i.e
   * checkedall=true&checked=1,2&unchecked=0,3&view=list)
   */

  public String historyToken() {
    StringBuilder sb = new StringBuilder();
    sb.append(getUrlToken(UrlParam.CHECKEDALL));
    sb.append(getUrlToken(UrlParam.CHECKED));
    sb.append(getUrlToken(UrlParam.UNCHECKED));
    sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }

  public void onPageClicked() {
    parent.resetToDefaultState();
  }

  /**
   * If it is current in list view add a quick summary into the {@link #table}
   */
  public void onPageLoaded(List<Occurrence> data, int pageNumber) {
    if (!isMyView(History.getToken())) {
      return;
    }
    if (data.isEmpty() && pagerWidget.getCurrentPageNumber() != 1) {
      pagerWidget.goToPage(pagerWidget.getCurrentPageNumber() - 1);
      return;
    }
    currentPageOccurrences = data;

    applyToAllCb.setText(constants.ApplyToAll() + " "
        + pagerWidget.getTotalRow() + " " + constants.Results());

//    List<OccurrenceSummary> summaries = new ArrayList<OccurrenceSummary>();
//    for (Occurrence o : data) {
//      summaries.add(new OccurrenceSummary(o));
//    }
//    Widget rowsWidget[][] = constructWidgetContent(summaries);
//    int start = (pagerWidget.getCurrentPageNumber() - 1)
//        * pagerWidget.getPageSize();
    
    boolean isMyOccurenceToReviewSelected =
		    isMyOccurenceToReviewSelected(History
		    	.getToken());
    List<ColumnConfig<Occurrence, ?>> authenticatedHeaders = isMyOccurenceToReviewSelected?
	    		OccurrenceSummary.getReviewerColumnModel()://REVIEWER_REQUIRED_HEADERS:
	    		OccurrenceSummary.getUserColumnModel();//USER_REQUIRED_HEADERS;
//	    String authenticatedHeadersStyle[] = isMyOccurenceToReviewSelected?
//	    		REVIEWER_HEADER_CSS_STYLES:
//	    		USER_HEADER_CSS_STYLES;
	    List<ColumnConfig<Occurrence, ?>> headers = signedIn ? ApplicationView.getCurrentState()==ViewState.SUPERADMIN?
	    		OccurrenceSummary.getSAdminColumnModel(table):authenticatedHeaders
	            : OccurrenceSummary.getGuestColumnModel();//GUEST_REQUIRED_HEADERS;
//	    String headersStyle[] = signedIn ? authenticatedHeadersStyle
//	            : GUEST_HEADER_CSS_STYLES;
//  table.resetHeader(headers,
//		  headersStyle);
	    
	    showModifLink(signedIn && ApplicationView.getCurrentState()==ViewState.SUPERADMIN);
	    
    ColumnModel<Occurrence> cm = new ColumnModel<Occurrence>(headers);
	    
    final ListStore<Occurrence> store = new ListStore<Occurrence>(OccurrenceSummary.properties.key());
	    
    store.addAll(data);
    cm.getColumn(1).setCell(cellRebiomaId);
    //set forcefit if column number is more than 10
    table.getView().setForceFit(headers.size()<=10);
    table.getView().setAdjustForHScroll(true);
    table.reconfigure(store, cm);// = new Grid<Occurrence>(store, cm);
    table.getView().refresh(true);
    table.setSelectionModel(OccurrenceSummary.sm);
    
    
//    mainVp.getWidget(0).asWidget().get;
    currentHeaders = authenticatedHeaders;
//    table.showRecord(pagerWidget.getPageSize(), start, rowsWidget);
    // resizeTable();
    restoreChecks();
    addHistoryItem(false);

    resize(Window.getClientWidth(), Window.getClientHeight());
    ((ComponentView) parent).resize(Window.getClientWidth(), Window
        .getClientHeight());
    if (!currentSearchColOccIdsMap.isEmpty()) {
      String sid = ApplicationView.getSessionId();
      DataSwitch.get().getMyReviewedOnRecords(sid, currentSearchColOccIdsMap,
          new AsyncCallback<Map<Integer, Boolean>>() {

            public void onFailure(Throwable caught) {
              Window.alert(caught.getMessage());
            }

            public void onSuccess(Map<Integer, Boolean> result) {
              for (Integer row : result.keySet()) {
                Boolean reviewed = result.get(row);
                String imgHtml;
                if (reviewed == null) {
                  imgHtml = "<img src='" + WAITING_IMG_URL + "'/>";
                } else if (reviewed) {
                  imgHtml = "<img src='" + THUMB_UP_URL + "'/>";
                } else {
                  imgHtml = "<img src='" + THUMB_DOWN_URL + "'/>";
                }
                getMyReviewedAtRow(row).setHTML(imgHtml);
              }
            }

          });
    }
  }
  
  private HTML reset = new HTML("Reset");
  private HTML save =new HTML("Save");
  private HorizontalPanel editHp = new HorizontalPanel();
  
  private void initModifLink(){
	  editHp.add(new HTML("&nbsp;&nbsp;"));
	  editHp.add(reset);
	  editHp.add(new HTML("&nbsp;&nbsp;"));
	  editHp.add(save);
	  editHp.add(new HTML("&nbsp;&nbsp;"));
	  
	  save.setStyleName("link");
	  reset.setStyleName("link");
  }
  
  private void showModifLink(boolean visible) {
//	  separator.setVisible(visible);
//	  reset.setVisible(visible);
//	  space.setVisible(visible);
//	  save.setVisible(visible);
	  editHp.setVisible(visible);
  }
  public void onStateChanged(ViewState state) {
    //Window.alert("state change"  + state);	
	  showModifLink(false);
    switch (state) {
    case SUPERADMIN:
    	showModifLink(true);
    case ADMIN:
    case REVIEWER:
    case RESEARCHER:
      initActions();
      signedIn = true;
      actionTool.clear();
      actionTool.addAction(NO_ACTION, null);
      actionTool.addAction(DELETE_ACTION, delCommand);
      actionTool.addAction(MAKE_PRIVATE_ACTION, makePrivateCommand);
      actionTool.addAction(MAKE_PUBLIC_ACTION, makePublicCommand);
      actionTool.addAction(SHOW_EMAIL_ACTION, showEmailCommand);
      actionTool.addAction(HIDE_EMAIL_ACTION, hideEmailCommand);
      actionTool.addAction(UPDATE_COLLABORATORS, showSharedUsersCommand);
      boolean isMyOccurenceToReviewSelected =
    		    isMyOccurenceToReviewSelected(History
    		    	.getToken());
      List<ColumnConfig<Occurrence, ?>> authenticatedHeaders = isMyOccurenceToReviewSelected?
    	    		OccurrenceSummary.getReviewerColumnModel()://REVIEWER_REQUIRED_HEADERS:
    	    		state== ViewState.SUPERADMIN ? OccurrenceSummary.getSAdminColumnModel(table):OccurrenceSummary.getUserColumnModel();//USER_REQUIRED_HEADERS;
//    	    String authenticatedHeadersStyle[] = isMyOccurenceToReviewSelected?
//    	    		REVIEWER_HEADER_CSS_STYLES:
//    	    		USER_HEADER_CSS_STYLES;
    	    		
//      table.resetHeader(authenticatedHeaders,
//    		  authenticatedHeadersStyle);
      
      ColumnModel<Occurrence> cm = new ColumnModel<Occurrence>(authenticatedHeaders);
	  
      cm.getColumn(1).setCell(cellRebiomaId);
      table.getView().setForceFit(authenticatedHeaders.size()<=10);
      table.reconfigure(table.getStore(), cm);
      table.getView().refresh(true);
      
      currentHeaders = authenticatedHeaders;
      if (!isMyView(parent.historyToken())) {
        pagerWidget.init(pagerWidget.getCurrentPageNumber());
      }
      addingReviewToolIfAllow(History.getToken());
      break;
    case UNAUTHENTICATED:
      signedIn = false;
      actionTool.clear();
      actionTool.addAction(NO_ACTION, null);
//      table.resetHeader(OccurrenceSummary.GUEST_REQUIRED_HEADERS,
//          GUEST_HEADER_CSS_STYLES);
      ColumnModel<Occurrence> cmU = new ColumnModel<Occurrence>(OccurrenceSummary.getGuestColumnModel());
	    
      cmU.getColumn(1).setCell(cellRebiomaId);
      table.getView().setForceFit(true);
      table.reconfigure(table.getStore(), cmU);
      table.getView().refresh(true);
      currentHeaders = OccurrenceSummary.getGuestColumnModel();//GUEST_REQUIRED_HEADERS;
      if (!isMyView(parent.historyToken())) {
        pagerWidget.init(pagerWidget.getCurrentPageNumber());
      }
      break;

    }

  }

  /**
   * If current view is list view request data for this page.
   * 
   * @see org.rebioma.client.OccurrenceQuery.DataRequestListener#requestData(int)
   */

  public void requestData(int pageNum) {
    String token = History.getToken();
    if (isMyView(token)) {
      addingReviewToolIfAllow(token);
      pagerWidget.init(pageNum);
    }

  }

  protected void handleOnValueChange(String historyToken) {
    historyState.setHistoryToken(historyToken);
    historyState.parseCheckedUrl();
    checkedAll = (Boolean) historyState
        .getHistoryParameters(UrlParam.CHECKEDALL);
    restoreChecks();
    addingReviewToolIfAllow(historyToken);
  }

  protected boolean isMyView(String value) {
    historyState.setHistoryToken(value);
    return historyState.getHistoryParameters(UrlParam.VIEW).toString()
        .equalsIgnoreCase(LIST);
  }

  protected void resetToDefaultState() {
    checkedAll = false;
    historyState.clearChecksState();

  }

  protected void resize(int width, int height) {
    height = height - mainSp.getAbsoluteTop();
    if (height <= 0) {
      height = 1;
    }
    int w = width -22;
    mainSp.setPixelSize(w, height - 10);
//    table.getParent().setHeight((height)- 42 + "px");
//    scrollPanel.setHeight((height)- 42 + "px");
  }

  private void addingReviewToolIfAllow(String token) {
    historyState.setHistoryToken(token);
    String type = (String) historyState.getHistoryParameters(UrlParam.TYPE);
    if (type.equalsIgnoreCase(OccurrenceView.OCCURRENCES_TO_REVIEW)) {
      actionTool.addAction(POSTIVELY_REVIEWED_ACTION, posReviewedCommand);
      actionTool.addAction(NEGATIVELY_REVIEWED_ACTION, negReviewedCommand);
    } else if (type.equalsIgnoreCase(OccurrenceView.MY_POS_REVIEWED)) {
      actionTool.removeAction(POSTIVELY_REVIEWED_ACTION);
      actionTool.addAction(NEGATIVELY_REVIEWED_ACTION, negReviewedCommand);
    } else if (type.equalsIgnoreCase(OccurrenceView.MY_NEG_REVIEWED)) {
      actionTool.addAction(POSTIVELY_REVIEWED_ACTION, posReviewedCommand);
      actionTool.removeAction(NEGATIVELY_REVIEWED_ACTION);
    } else {
      actionTool.removeAction(POSTIVELY_REVIEWED_ACTION);
      actionTool.removeAction(NEGATIVELY_REVIEWED_ACTION);
    }
  }

  /**
   * Initializes all actions belong to this OccurrenceListView.
   */

  private int checkedRecordCount() {
    int count = 0;
    for (int i = 0; i < table.getStore().size()/*getDataRowCount()*/; i++) {
    	if(table.getSelectionModel().isSelected(table.getStore().get(i))){
//    	if (getRowCheckBox(i).getValue()) {
    		count++;
    	}
    }
    return count;
  }

  /**
   * Constructs an 2 dimension array widget represents display content for the
   * table of the current page.
   * 
   * @param summaries {@link OccurrenceSummary}
   * @return Widget[][] data for the current page.
   */
//  private List<Occurrence> constructWidgetContent(List<OccurrenceSummary> summaries) {
//    int length = summaries.size();
//
//    // This is to make sure only display the table with at most page size from
//    // the pager.
//    if (length > pagerWidget.getPageSize()) {
//      length = pagerWidget.getPageSize();
//    }
//    List<Occurrence> rowsWidget = new ArrayList<Occurrence>();
//    boolean isMyOccurenceToReviewSelected =
//    isMyOccurenceToReviewSelected(History
//    	.getToken());
//
//    List<ColumnConfig<Occurrence, ?>> authenticatedHeaders = isMyOccurenceToReviewSelected?
//    		OccurrenceSummary.getReviewerColumnModel()://REVIEWER_REQUIRED_HEADERS:
//    		OccurrenceSummary.getUserColumnModel();//USER_REQUIRED_HEADERS;
//    // if (currentHeaders != authenticatedHeaders) {
//    // table.resetHeader(authenticatedHeaders,
//    // (isMyOccurenceToReviewSelected ? REVIEWER_HEADER_CSS_STYLES
//    // : USER_HEADER_CSS_STYLES));My Reviewed
//    // }
//    List<ColumnConfig<Occurrence, ?>> headers = signedIn ? authenticatedHeaders
//        : OccurrenceSummary.getGuestColumnModel();//.GUEST_REQUIRED_HEADERS;
//    currentSearchColOccIdsMap.clear();
//    
//    for(ColumnConfig<Occurrence, ?> col : headers){
//        String cellData = rowData[(col - 1)];
//        if (cellData == null) {
//          cellData = "----";
//        } else if (cellData.equalsIgnoreCase("none")) {
//          cellData = "----";
//        }
//        if (cellData.equals(OccurrenceSummary.MY_REVIEWED)) {
//          currentSearchColOccIdsMap.put(row, occurrence.getId());
//          currentRow[col] = new HTML(constants.Loading());
//        } else if (cellData.equalsIgnoreCase("waiting")) {
//          currentRow[col] = new HTML("<img src='" + WAITING_IMG_URL + "'/>");
//          // currentRow[col].setTitle("waiting");
//        } else if (cellData.equalsIgnoreCase("pos")) {
//          currentRow[col] = new HTML("<img src='" + THUMB_UP_URL + "'/>");
//        } else if (cellData.equalsIgnoreCase("neg")) {
//          currentRow[col] = new HTML("<img src='" + THUMB_DOWN_URL + "'/>");
//        } else if (cellData.equalsIgnoreCase("yes")) {
//          currentRow[col] = new HTML("<img src='" + CHECK_IMG_URL + "'/>");
//        } else if (cellData.equalsIgnoreCase("no")) {
//          // currentRow[col] = new HTML("No <img src='" + X_IMG_URL +
//          // "'/>");
//          currentRow[col] = new HTML("<img src='" + X_IMG_URL + "'/>");
//        } else {
//          if (col != 1) {
////            if (headers[col].equals(constants.Owner())
////                && !OccurrenceSummary.isEmailVisisble(occurrence)) {
////              currentRow[col] = new Label(constants.EmailNotShow());
////            } else if (headers[col].equals(constants.Collaborators())) {
////              String modifiedShowUsersCSV = cellData.replaceAll(",", ", ");
////              currentRow[col] = new Label(modifiedShowUsersCSV);
////            } else {
////              currentRow[col] = new Label(cellData);
////            }
//          } else {
//            HTML link = new HTML(cellData);
//            currentRow[col] = link;
//
//            /*
//             * This click listener updates the tables currently selected row.
//             */
//            link.addClickHandler(new ClickHandler() {
//
//              public void onClick(ClickEvent event) {
//                parent.switchView(DETAIL, true);
//                occurrenceListener.onOccurrenceSelected(occurrence);
//              }
//            });
//          }
//        }
//    }
//    for (int row = 0; row < length; row++) {
//      OccurrenceSummary summary = summaries.get(row);
//      final Occurrence occurrence = summary.getOccurrence();
//      
//      String[] userSummary = isMyOccurenceToReviewSelected?
//    		null://summary.getReviewerSummary():
//    		null;//summary.getUserSummary();
//
//      String[] rowData = signedIn ? userSummary : null;//summary.getUnauthenticatedSummary();
//      Widget[] currentRow = new Widget[rowData.length + 1];
//      SimplePanel panel = new SimplePanel();
//      CheckBox cb = new CheckBox();
//      cb.addClickHandler(new ClickHandler() {
//
//        public void onClick(ClickEvent event) {
//          CheckBox cb = (CheckBox) event.getSource();
//          if (!cb.getValue()) {
//            setApplyAllChecked(false);
//          }
//          updateChecksState();
//          addHistoryItem(false);
//        }
//
//      });
//      // cb.setEnabled(occurrence.isValidated());
//      panel.add(cb);
//      currentRow[0] = panel;
//      for (int col = 1; col < currentRow.length; col++) {
//        String cellData = rowData[(col - 1)];
//        if (cellData == null) {
//          cellData = "----";
//        } else if (cellData.equalsIgnoreCase("none")) {
//          cellData = "----";
//        }
//        if (cellData.equals(OccurrenceSummary.MY_REVIEWED)) {
//          currentSearchColOccIdsMap.put(row, occurrence.getId());
//          currentRow[col] = new HTML(constants.Loading());
//        } else if (cellData.equalsIgnoreCase("waiting")) {
//          currentRow[col] = new HTML("<img src='" + WAITING_IMG_URL + "'/>");
//          // currentRow[col].setTitle("waiting");
//        } else if (cellData.equalsIgnoreCase("pos")) {
//          currentRow[col] = new HTML("<img src='" + THUMB_UP_URL + "'/>");
//        } else if (cellData.equalsIgnoreCase("neg")) {
//          currentRow[col] = new HTML("<img src='" + THUMB_DOWN_URL + "'/>");
//        } else if (cellData.equalsIgnoreCase("yes")) {
//          currentRow[col] = new HTML("<img src='" + CHECK_IMG_URL + "'/>");
//        } else if (cellData.equalsIgnoreCase("no")) {
//          // currentRow[col] = new HTML("No <img src='" + X_IMG_URL +
//          // "'/>");
//          currentRow[col] = new HTML("<img src='" + X_IMG_URL + "'/>");
//        } else {
//          if (col != 1) {
////            if (headers[col].equals(constants.Owner())
////                && !OccurrenceSummary.isEmailVisisble(occurrence)) {
////              currentRow[col] = new Label(constants.EmailNotShow());
////            } else if (headers[col].equals(constants.Collaborators())) {
////              String modifiedShowUsersCSV = cellData.replaceAll(",", ", ");
////              currentRow[col] = new Label(modifiedShowUsersCSV);
////            } else {
////              currentRow[col] = new Label(cellData);
////            }
//          } else {
//            HTML link = new HTML(cellData);
//            currentRow[col] = link;
//
//            /*
//             * This click listener updates the tables currently selected row.
//             */
//            link.addClickHandler(new ClickHandler() {
//
//              public void onClick(ClickEvent event) {
//                parent.switchView(DETAIL, true);
//                occurrenceListener.onOccurrenceSelected(occurrence);
//              }
//            });
//          }
//        }
//      }
//      rowsWidget[row] = currentRow;
//    }
//    return rowsWidget;
//  }

  private void deleteAll() {
    if (!Window.confirm(constants.Delete() + " " + pagerWidget.getTotalRow()
        + " " + constants.Records())) {
      return;

    }
    String sessionId = Cookies.getCookie(ApplicationView.SESSION_ID_NAME);
    DataSwitch.get().delete(sessionId, pagerWidget.getQuery(),
        new AsyncCallback<Integer>() {

          public void onFailure(Throwable caught) {
            Window.alert(caught.getMessage());
            GWT.log(caught.getMessage(), caught);
          }

          public void onSuccess(Integer result) {
            if (result == 0) {
              showMsg(constants.DeleteMsg());
            } else {
              pagerWidget.init(pagerWidget.getCurrentPageNumber());
            }

          }

        });

  }

  /**
   * Deletes a set of {@link Occurrence} objects via {@link DataSwitch}.
   * 
   * @param toDelete occurrence set to delete
   */
  private void deleteOccurrences(final Set<Occurrence> occurrences) {
    if (occurrences.isEmpty()) {
      Window.alert(constants.NoActionCantDeleteOthers());
      return;
    } else {
      if (!Window.confirm(constants.DeleteConfirm())) {
        return;
      }
    }
    String sessionId = Cookies.getCookie(ApplicationView.SESSION_ID_NAME);
    DataSwitch.get().delete(sessionId, occurrences,
        new AsyncCallback<String>() {
          public void onFailure(Throwable caught) {
            try {
              throw caught;
            } catch (OccurrenceServiceException e) {
              Window.confirm(e.toString());
            } catch (Throwable t) {
              Window.confirm(t.toString());
            }
          }

          public void onSuccess(String result) {
            pagerWidget.init(pagerWidget.getCurrentPageNumber());
          //tax
            setCheckedAll(false);
          }
        });
  }

  private Set<Integer> getCheckedOccurrenceId() {
    Set<Integer> ids = new HashSet<Integer>();
    User user = ApplicationView.getAuthenticatedUser();
    if (user != null) {
    	for(Occurrence oc: table.getSelectionModel().getSelectedItems()){
    		ids.add(oc.getId());
    	}
//    	for (int i = 0; i < table.getSelectionModel().getSelectedItems().size()/*getDataRowCount()*/; i++) {
//    	  
//	        if (getRowCheckBox(i).getValue()) {
//	          ids.add(currentPageOccurrences.get(i).getId());
//	        }
//    	}
    }
    return ids;
  }

  /**
   * Gets all checked valid occurrences with associated action in the current
   * page.
   * 
   * @return set of checked valid occurrences with associated action.
   */
  private Set<Occurrence> getCheckedValidOccurrences(String action) {
    Set<Occurrence> occurrences = new HashSet<Occurrence>();
    User user = ApplicationView.getAuthenticatedUser();
    if (user == null) {
      return occurrences;
    }
    for (Occurrence o : table.getSelectionModel().getSelectedItems()){
//    for (int i = 0; i < table.getDataRowCount(); i++) {
//      if (getRowCheckBox(i).getValue()) {
//        Occurrence o = currentPageOccurrences.get(i);
        String email = o.getOwnerEmail();
        boolean myRecord = email.equals(user.getEmail());
        if (!myRecord) {
          continue;
        }
        // if (action.equals(POSTIVELY_REVIEWED_ACTION) && o.isValidated()) {
        // o.setVetted(true);
        // occurrences.add(o);
        // } else if (action.equals(NEGATIVELY_REVIEWED_ACTION) && o.isVetted())
        // {
        // o.setVetted(false);
        // occurrences.add(o);
        // } else
        if (action.equals(MAKE_PUBLIC_ACTION) && !o.isPublic_()) {
          o.setPublic_(true);
          occurrences.add(o);
        } else if (action.equals(MAKE_PRIVATE_ACTION) && o.isPublic_()) {
          o.setPublic_(false);
          occurrences.add(o);

        } else if (action.equals(DELETE_ACTION)
            || action.equals(UPDATE_COLLABORATORS)) {
          occurrences.add(o);
        } else if (action.equals(SHOW_EMAIL_ACTION)) {
          o.setEmailVisible(true);
          occurrences.add(o);
        } else if (action.equals(HIDE_EMAIL_ACTION)) {
          o.setEmailVisible(false);
          occurrences.add(o);
        }
//      }
    }
    return occurrences;
  }

  private HTML getMyReviewedAtRow(int row) {
//    return (HTML) table.getCellWidget(row, MY_REREVIEWED_COL);
	  return null;
  }

  /**
   * Gets the {@link CheckBox} widget of the given row. the check box is at
   * column {@link CHECK_BOX_INDEX}.
   * 
   * @param row
   * @return
   */
//  private CheckBox getRowCheckBox(int row) {
//    SimplePanel sp = null;//(SimplePanel) table.getCellWidget(row, CHECK_BOX_INDEX);
//    return (CheckBox) sp.getWidget();
//  }

  private String getUrlToken(UrlParam param) {
    String query = param.lower() + "=";
    switch (param) {
    case CHECKEDALL:
      query += checkedAll;
      break;
    case CHECKED:
      String checkedValues = historyState.getCheckedValues(true);
      if (checkedValues.equals("")) {
        query = "";
      } else {
        query += checkedValues;
      }
      break;
    case UNCHECKED:
      String uncheckedValues = historyState.getCheckedValues(false);
      if (uncheckedValues.equals("")) {
        query = "";
      } else {
        query += uncheckedValues;
      }
      break;
    default:
      query = "";
    }
    return query.length() == 0 ? query : query + "&";

  }

  private void initActions() {
    if (isActionInit) {
      return;
    }
    isActionInit = true;
    posReviewedCommand = new Command() {
      public void execute() {
        boolean isAllChecked = applyToAllCb.getValue();
        if (applyToAllCb.getValue()) {
          reviewerCommentPopup.display(isAllChecked, true, null);
          return;
        } else if (!isUpdated(checkedRecordCount())) {
          return;
        }
        Set<Integer> occurrenceIds = getCheckedOccurrenceId();
        if (occurrenceIds.isEmpty()) {
          if (occurrenceIds.isEmpty()) {
            Window.alert(" no valid record to review");

            return;
          }
        }
        reviewerCommentPopup.display(isAllChecked, true, occurrenceIds);
      }

    };
    negReviewedCommand = new Command() {
      public void execute() {
        boolean isAllChecked = applyToAllCb.getValue();
        if (applyToAllCb.getValue()) {
          reviewerCommentPopup.display(isAllChecked, false, null);
          return;
        } else if (!isUpdated(checkedRecordCount())) {
          return;
        }
        Set<Integer> occurrenceIds = getCheckedOccurrenceId();
        if (occurrenceIds.isEmpty()) {
          if (occurrenceIds.isEmpty()) {
            Window.alert(" no valid record to review");

            return;
          }
        }
        reviewerCommentPopup.display(isAllChecked, false, occurrenceIds);
      }

    };

    delCommand = new Command() {
      public void execute() {
        if (applyToAllCb.getValue()) {
          deleteAll();
          return;
        }
        deleteOccurrences(getCheckedValidOccurrences(DELETE_ACTION));
      }

    };

    makePrivateCommand = new Command() {

      public void execute() {
        if (applyToAllCb.getValue()) {
          updateAll("public = false", constants.UpdatePrivateMsg());
          return;
        } else if (!isUpdated(checkedRecordCount())) {
          return;
        }
        updateOccurrences(getCheckedValidOccurrences(MAKE_PRIVATE_ACTION),
            constants.NoActionPublicPrivate());
      }

    };

    makePublicCommand = new Command() {

      public void execute() {
        if (applyToAllCb.getValue()) {
          updateAll("public = true", constants.UpdatePublicMsg());
          return;
        } else if (!isUpdated(checkedRecordCount())) {
          return;
        }

        updateOccurrences(getCheckedValidOccurrences(MAKE_PUBLIC_ACTION),
            constants.NoActionPublicPrivate());
      }

    };

    showEmailCommand = new Command() {

      public void execute() {
        if (applyToAllCb.getValue()) {
          updateAll("emailVisible = true", constants.ShowEmailMsg());
          return;
        } else if (!isUpdated(checkedRecordCount())) {
          return;
        }

        updateOccurrences(getCheckedValidOccurrences(SHOW_EMAIL_ACTION),
            constants.NoActionEmail());
      }

    };

    hideEmailCommand = new Command() {

      public void execute() {
        if (applyToAllCb.getValue()) {
          updateAll("emailVisible = false", constants.HideEmailMsg());
          return;
        } else if (!isUpdated(checkedRecordCount())) {
          return;
        }

        updateOccurrences(getCheckedValidOccurrences(HIDE_EMAIL_ACTION),
            constants.NoActionEmail());

 
      }

    };

    showSharedUsersCommand = new Command() {
      public void execute() {
        if (collaboratorsList == null) {
          collaboratorsList = new CollaboratorsList();
        }
        Set<Occurrence> occurrences = null;
        if (!applyToAllCb.getValue()) {
          occurrences = getCheckedValidOccurrences(UPDATE_COLLABORATORS);
          if (occurrences.isEmpty()) {
            Window.alert(constants.UpdateSharedUserMsg());
            return;
          }
        }

        collaboratorsList.showUsers(occurrences);
      }

    };

  }

  private boolean isMyOccurenceToReviewSelected(String token) {
    historyState.setHistoryToken(token);
    String type = (String) historyState.getHistoryParameters(UrlParam.TYPE);
    GWT.log("type=" + type);
    return type.equalsIgnoreCase(OccurrenceView.OCCURRENCES_TO_REVIEW) ||
    		type.equalsIgnoreCase(OccurrenceView.MY_POS_REVIEWED) ||
    		type.equalsIgnoreCase(OccurrenceView.MY_NEG_REVIEWED);
  }

  private boolean isUpdated(int records) {
    if (records == 0) {
      return false;
    }
    return Window.confirm(constants.Update() + " " + records + " "
        + constants.Occurrences() + "?");
  }

  private void restoreChecks() {
    setApplyAllChecked(checkedAll);
    if (checkedAll) {
      setCheckedAll(true);
      table.getSelectionModel().selectAll();
      return;
    }
    Map<Integer, Boolean> checksMap = historyState.getChecksMap();
    for (int row = 0; row < table.getStore().size()/*getDataRowCount()*/; row++) {
//      CheckBox cb = getRowCheckBox(row);
      Boolean isChecked = checksMap.get(row);
      isChecked = isChecked == null ? false : isChecked;
      if(!isChecked)table.getSelectionModel().deselect(row);
      else table.getSelectionModel().select(row, isChecked);
//      cb.setValue(isChecked == null ? false : isChecked);
    }
  }

  private void reviewAllRecords(final Boolean reviewed, String comment, boolean notified) {
    String sessionId = Cookies.getCookie(ApplicationView.SESSION_ID_NAME);
    DataSwitch.get().reviewRecords(sessionId, reviewed, pagerWidget.getQuery(),
        comment, notified, new AsyncCallback<Integer>() {
          public void onFailure(Throwable caught) {
            Window.alert(caught.getMessage());
            DisplayPopup.getDefaultDisplayPopup().hide();
          }

          public void onSuccess(Integer result) {
            pagerWidget.init(pagerWidget.getCurrentPageNumber());
            showMsg(result + " records was "
                + (reviewed ? "positively" : "negatively") + " reviewed");
            DisplayPopup.getDefaultDisplayPopup().hide();
          }

        });
  }

  private void reviewRecords(Set<Integer> occurrenceIds,
      final Boolean reviewed, String comment, boolean notified) {
    String sessionId = Cookies.getCookie(ApplicationView.SESSION_ID_NAME);
    DataSwitch.get().reviewRecords(sessionId, reviewed, occurrenceIds, comment, notified,
        new AsyncCallback<Integer>() {

          public void onFailure(Throwable caught) {
            Window.alert(caught.getMessage());
            DisplayPopup.getDefaultDisplayPopup().hide();
          }

          public void onSuccess(Integer result) {
            pagerWidget.init(pagerWidget.getCurrentPageNumber());
            showMsg(result + " records was "
                + (reviewed ? "positively" : "negatively") + " reviewed");
            DisplayPopup.getDefaultDisplayPopup().hide();
            
          //tax
            setCheckedAll(false);
          }

        });
  }

  /**
   * Sets whether checked or unchecked to {@link #applyToAllCb} check box and
   * set propriated styles for this check box.
   * 
   * @param checked
   */
  private void setApplyAllChecked(boolean checked) {
    checkedAll = checked;
    applyToAllCb.setValue(checked);
    if (checked) {
      applyToAllCb.setStyleName("gwt-CheckBox");
    } else {
      applyToAllCb.setStyleName(APPLY_ALL_STYLE);
    }
  }

  /**
   * Sets all {@link CheckBox} of the current page to check or unchecked.
   * 
   * @param checked true if check box are checked.
   */
  private void setCheckedAll(boolean checked) {
    if (!checked) {
      setApplyAllChecked(false);
      checkedAll = false;
    }
    Map<Integer, Boolean> checksMap = historyState.getChecksMap();
    for (int row = 0; row < table.getStore().size()/*getDataRowCount()*/; row++) {
//      CheckBox cb = getRowCheckBox(row);
//      cb.setValue(checked);
    	if(!checked) table.getSelectionModel().deselect(row);
    	else table.getSelectionModel().select(row, checked);
    	checksMap.put(row, checked);
    }
  }

  private void showMsg(String htmlMsg) {
    PopupMessage.getInstance().showMessage(htmlMsg);
  }

  private void updateAll(String updateFilter, final String hint) {
    OccurrenceQuery query = pagerWidget.getQuery();
    query.clearsUpdate();
    query.addUpdate(updateFilter);
    String sessionId = Cookies.getCookie(ApplicationView.SESSION_ID_NAME);

    if (!Window.confirm(constants.Update() + " " + pagerWidget.getTotalRow()
        + " " + constants.Occurrences() + "?\n Note: " + hint)) {
      return;
    }
    DataSwitch.get().update(sessionId, query, new AsyncCallback<Integer>() {

      public void onFailure(Throwable caught) {
        Window.alert(caught.getMessage());
        GWT.log(caught.getMessage(), caught);
      }

      public void onSuccess(Integer result) {
        if (result != null) {
          if (result.intValue() == 0) {
            showMsg("No records was updated. Hint: " + hint);
          } else {
            showMsg(result + " records was updated.");
            pagerWidget.init(pagerWidget.getCurrentPageNumber());
          }

        }

      }

    });
  }

  /**
   * Updates all check states base on history token
   */
  private void updateChecksState() {
    Map<Integer, Boolean> checksMap = historyState.getChecksMap();
    for (int row = 0; row < table.getStore().size()/*getDataRowCount()*/; row++) {
//      CheckBox cb = getRowCheckBox(row);
//      table.getSelectionModel().isSelected(table.getStore().get(row));
      checksMap.put(row, table.getSelectionModel().isSelected(table.getStore().get(row))/*cb.getValue()*/);
    }
  }

  /**
   * Handles {@link Occurrence} updates by submitting a request to
   * {@link DataSwitch}.
   * 
   * @param warningMsg a warning message when is updated occurrences is empty.
   * @param ids list of occurrence ids to vet
   * @param vetted true to vet ids, false to unvet ids
   */
  private void updateOccurrences(Set<Occurrence> updatedOccurrences,
      String warningMsg) {
    if (updatedOccurrences.isEmpty()) {
      Window.alert(warningMsg);

      return;
    }
    String sessionId = Cookies.getCookie(ApplicationView.SESSION_ID_NAME);
    DataSwitch.get().update(sessionId, updatedOccurrences,
        new AsyncCallback<String>() {
          public void onFailure(Throwable caught) {
            try {
              throw caught;
            } catch (OccurrenceServiceException e) {
              Window.confirm(e.toString());
            } catch (Throwable t) {
              Window.confirm(t.toString());
            }
          }

          public void onSuccess(String result) {
            pagerWidget.init(pagerWidget.getCurrentPageNumber());
          //tax
            setCheckedAll(false);
          }
        });

  }

	@Override
	public void onPageSizeChange(int newPageSize) {
	  ApplicationView.getApplication().getOccurrenceView().setPageSize(newPageSize);
      //on recharge les données
      requestData(1);
	}

	@Override
	public DataPager<Occurrence> getDataPagerWidget() {
		return pagerWidget.pager;
	}

	@Override
	public OccurrencePagerWidget getOccurrencePagerWidget() {
		return pagerWidget;
	}
	
	interface RebiomaIdTemplate extends XTemplates {
		@XTemplate("<div class='link2'>{value}</div>")
		SafeHtml render(String value);
	}
	
	private RebiomaIdTemplate template = GWT.create(RebiomaIdTemplate.class);
	
	private SafeHtmlRenderer integerSafeHtmlRenderer = new SafeHtmlRenderer<Integer>() {

		@Override
		public SafeHtml render(Integer object) {
			return (object == null) ? SafeHtmlUtils.EMPTY_SAFE_HTML : template.render(String.valueOf(object));
		}

		@Override
		public void render(Integer object, SafeHtmlBuilder builder) {
			builder.append(template.render(String.valueOf(object)));
		}
	};
	
	private Cell<Object> cellRebiomaId = new SimpleSafeHtmlCell<Object>(integerSafeHtmlRenderer, "click") {
		@Override
		public void onBrowserEvent(Context context, Element parentE, Object value, NativeEvent event,
				ValueUpdater<Object> valueUpdater) {
			super.onBrowserEvent(context, parentE, value, event, valueUpdater);
			if ("click".equals(event.getType())) {
				int row = context.getIndex();
				Occurrence occurrence = table.getStore().get(row);
				parent.switchView(DETAIL, true);
            	occurrenceListener.onOccurrenceSelected(occurrence);
			}else if("hover".equals(event.getType())) {
				
			}
		}
	};
	
}
