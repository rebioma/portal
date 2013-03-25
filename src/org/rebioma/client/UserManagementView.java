package org.rebioma.client;

import org.rebioma.client.UsersTable.CheckedClickListener;
import org.rebioma.client.UsersTable.Criteria;
import org.rebioma.client.bean.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;

public class UserManagementView extends ComponentView implements ClickHandler {
  private static class ReviewerAssignmentPopup extends PopupPanel implements
      SubmitHandler, SubmitCompleteHandler, ClickHandler {

    private final Image closeImage;
    private final FormPanel formPanel;
    private final CheckBox isClearOldAssignmentCb;
    private final ListBox delimitersLb;
    private final Hidden sessionField;
    private final Button submitButton;

    public ReviewerAssignmentPopup(String title) {
      super(false, true);
      super.setAnimationEnabled(true);
      super.setGlassEnabled(true);
      closeImage = new Image("images/xclose.gif");
      isClearOldAssignmentCb = new CheckBox(constants.isClearOldAssignment());
      delimitersLb = new ListBox();
      sessionField = new Hidden();
      sessionField.setName("sessionId");
      VerticalPanel mainVp = new VerticalPanel();
      HorizontalPanel titlePanel = new HorizontalPanel();
      titlePanel.setWidth("100%");
      Label titleLb = new Label(title);
      titleLb.addStyleName("title");
      titlePanel.add(titleLb);
      titlePanel.add(closeImage);

      formPanel = new FormPanel();
      VerticalPanel formContent = new VerticalPanel();
      formPanel.setWidget(formContent);
      FileUpload fileUpload = new FileUpload();
      fileUpload.setName("file_upload");
      isClearOldAssignmentCb.setName("clear");
      delimitersLb.setName("delimiter");
      delimitersLb.addItem(constants.Comma(), ",");
      delimitersLb.addItem(constants.Semicolon(), ";");
      formPanel.setAction(GWT.getModuleBaseURL() + "reviewAssignment");
      formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
      formPanel.setMethod(FormPanel.METHOD_POST);

      HorizontalPanel uploadRows = new HorizontalPanel();
      uploadRows.setSpacing(5);
      uploadRows.add(fileUpload);
      uploadRows.add(delimitersLb);
      formContent.add(uploadRows);
      formContent.add(isClearOldAssignmentCb);
      formContent.add(sessionField);
      submitButton = new Button(constants.Upload());
      formContent.add(submitButton);

      mainVp.add(titlePanel);
      mainVp.add(formPanel);
      mainVp.setSpacing(5);
      setWidget(mainVp);
      formPanel.addSubmitHandler(this);
      formPanel.addSubmitCompleteHandler(this);
      titlePanel.setCellVerticalAlignment(closeImage,
          HasVerticalAlignment.ALIGN_TOP);
      titlePanel.setCellHorizontalAlignment(closeImage,
          HasHorizontalAlignment.ALIGN_CENTER);
      submitButton.addClickHandler(this);
      closeImage.addClickHandler(this);
      addStyleName("assignment_popup");
      titleLb.addStyleName("title");
    }

    public void onClick(ClickEvent event) {
      Object source = event.getSource();
      if (source == closeImage) {
        if (submitButton.isEnabled()) {
          hide();
        }
      } else {
        sessionField.setValue(ApplicationView.getSessionId());
        setEnable(false);
        formPanel.submit();
      }
    }

    public void onSubmit(SubmitEvent event) {
      // TODO Auto-generated method stub
    }

    public void onSubmitComplete(SubmitCompleteEvent event) {
      // TODO Auto-generated method stub
      Window.confirm(event.getResults());
      setEnable(true);
      hide();
    }

    public void show() {
      super.setPopupPosition(Window.getClientWidth() / 5, Window
          .getClientHeight() / 5);
      super.show();
    }

    private void setEnable(boolean enabled) {
      if (enabled) {
        submitButton.setText(constants.Upload());
      } else {
        submitButton.setText(constants.Uploading());
      }
      submitButton.setEnabled(enabled);
    }

  }

  private static class UserView extends Composite {
    HTML userLabel = new HTML("");
    UserProfile userProfile = UserProfile.getUserProfile(null);

    UserView() {
      VerticalPanel mainVp = new VerticalPanel();
      initWidget(mainVp);
      mainVp.add(userLabel);
      mainVp.add(userProfile);
      userLabel.setStyleName("text");
      mainVp.setSpacing(5);
    }

    void displayUser(User user) {
      userLabel.setHTML(constants.UserProfiles() + " : " + user.getId());
      userProfile.loadUser(user);
    }
  }

  private static final int NONE = -1;

  public static ViewInfo init(final View parent, final String name,
      final String historyName) {
    return new ViewInfo() {

      public String getName() {
        return name;
      }

      protected View constructView() {
        return new UserManagementView(parent);
      }

      protected String getHisTokenName() {
        return historyName;
      }
    };
  }

  private final HistoryState historyState = new HistoryState() {

    @Override
    public Object getHistoryParameters(UrlParam param) {
      switch (param) {
      case TAB:
        return stringValue(param);
      }
      return null;
    }
  };

  // private FormWidget searchForm;
  private final UsersTable usersTable;
  private final SimplePanel userViewPanel = new SimplePanel();
  private int currentCheck = NONE;

  private TextBox firstNameTextBox;

  private TextBox lastNameTextBox;

  private TextBox emailTextBox;

  private TextBox institutionTextBox;

  private final CheckedClickListener searchListener;

  private UserView userView = null;

  // private final Label assignmentLink;

  private ReviewerAssignmentPopup reviewerAssignments;

  /*
   * (non-Javadoc)
   * 
   * @see org.rebioma.client.ComponentView#historyToken()
   */

  public UserManagementView(View parent) {
    super(parent, false);
    // TODO Auto-generated constructor stub
    HorizontalPanel mainHp = new HorizontalPanel();
    VerticalPanel mainContainer = new VerticalPanel();
    mainHp.setSpacing(5);
    VerticalPanel searchPanel = new VerticalPanel();
    // assignmentLink = new Label(constants.assignReviewers());
    // assignmentLink.addStyleName("link");
    // assignmentLink.addStyleName("assignmentLink");
    // assignmentLink.addClickHandler(this);
    searchPanel.setSpacing(5);
    initWidget(mainContainer);
    // mainContainer.add(assignmentLink);
    mainContainer.add(mainHp);
    mainHp.add(searchPanel);
    mainHp.add(userViewPanel);
    // mainVp.add(new HTML(constants.CollaboratorViewInstruction()));
    // checkedSearch = new HashSet<Integer>();
    usersTable = new UsersTable(false);
    searchListener = new CheckedClickListener() {

      public void onChecked(String email, Integer id, boolean checked, int row) {
        if (checked) {
          currentCheck = row;
          if (userView == null) {
            userView = new UserView();
            userViewPanel.setWidget(userView);
          } else if (!userView.isAttached()) {
            userViewPanel.setWidget(userView);
          }
          userView.displayUser(usersTable.getUser(row));
          // userViewPanel
          // checkedSearch.add(id);
        } else {
          if (currentCheck == row) {
            currentCheck = NONE;
            userViewPanel.clear();
          }
          // checkedSearch.remove(id);
        }
      }

    };
    usersTable.addCheckedListener(searchListener);
    usersTable.setMultiCheck(false);
    searchPanel.add(loadSearchFields());
    searchPanel.add(usersTable);
    // searchTable.addAction(constants.AddCollaborators(), new Command() {
    //
    // public void execute() {
    // executeCommand(checkedSearch, UserQuery.CollaboratorsUpdate.ADD);
    // }
    // });
    // HorizontalPanel hp = (HorizontalPanel) mainSp.getWidget();
    // VerticalPanel vp = (VerticalPanel) hp.getWidget(hp.getWidgetCount() - 1);
    // vp.add(searchTable);
    // searchForm.addTextVertically(sectionName, formLabel, validationCheck)
  }

  public String historyToken() {
    // TODO Auto-generated method stub
    return super.historyToken();
  }

  public void onClick(ClickEvent event) {
    if (reviewerAssignments == null) {
      reviewerAssignments = new ReviewerAssignmentPopup(constants
          .assignReviewers());
    }
    reviewerAssignments.show();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.rebioma.client.ComponentView#onShow()
   */

  public void onShow() {
    // TODO Auto-generated method stub
    super.onShow();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.rebioma.client.ComponentView#onStateChanged(org.rebioma.client.View
   * .ViewState)
   */

  public void onStateChanged(ViewState state) {
    // TODO Auto-generated method stub
    super.onStateChanged(state);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.rebioma.client.ComponentView#handleOnValueChange(java.lang.String)
   */

  protected void handleOnValueChange(String historyToken) {
    // TODO Auto-generated method stub
    super.handleOnValueChange(historyToken);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.rebioma.client.ComponentView#isMyView(java.lang.String)
   */
  @Override
  protected boolean isMyView(String value) {
    historyState.setHistoryToken(value);
    String tabName = (String) historyState.getHistoryParameters(UrlParam.TAB);
    return tabName != null
        && tabName.equalsIgnoreCase(ApplicationView.USER_MANAGEMENT);
  }

  /**
   * Initializes the user search form if it is not yet loaded.
   */
  protected Widget loadSearchFields() {
    VerticalPanel vp = new VerticalPanel();
    vp.add(new Label(constants.CollaboratorViewInstruction()));
    Grid searchGrid = new Grid(4, 2);
    searchGrid.setStyleName("UserSearchField");
    firstNameTextBox = new TextBox();
    lastNameTextBox = new TextBox();
    emailTextBox = new TextBox();
    institutionTextBox = new TextBox();
    HTML firstNameLb = new HTML(Criteria.FIRST_NAME.getInstruction());
    HTML lastNameLb = new HTML(Criteria.LAST_NAME.getInstruction());
    HTML emailNameLb = new HTML(Criteria.EMAIL.getInstruction());
    HTML instNameLb = new HTML(Criteria.INSTITUTION.getInstruction());
    firstNameLb.setStyleName("text");
    lastNameLb.setStyleName("text");
    emailNameLb.setStyleName("text");
    instNameLb.setStyleName("text");
    searchGrid.setWidget(0, 0, new HTML(Criteria.FIRST_NAME.getInstruction()));
    searchGrid.setWidget(1, 0, new HTML(Criteria.LAST_NAME.getInstruction()));
    searchGrid.setWidget(2, 0, new HTML(Criteria.EMAIL.getInstruction()));
    searchGrid.setWidget(3, 0, new HTML(Criteria.INSTITUTION.getInstruction()));
    searchGrid.setWidget(0, 1, firstNameTextBox);
    searchGrid.setWidget(1, 1, lastNameTextBox);
    searchGrid.setWidget(2, 1, emailTextBox);
    searchGrid.setWidget(3, 1, institutionTextBox);
    vp.add(searchGrid);
    Button searchButton = new Button(constants.SearchUsers());
    searchButton.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent event) {
        // loadSearchTable();
        loadSearchResult(firstNameTextBox.getText(), lastNameTextBox.getText(),
            emailTextBox.getText(), institutionTextBox.getText());
        addHistoryItem(false);
      }

    });
    vp.add(searchButton);
    return vp;
  }

  /**
   * Initializes the user search table if it is not yet loaded.
   */
  protected void loadSearchTable() {
    // searchTable = new UsersTable(false);
    // checkedSearch = new HashSet<Integer>();
    // searchListener = new CheckedClickListener() {
    //
    // public void onChecked(String email, Integer id, boolean checked) {
    // if (checked) {
    // checkedSearch.add(id);
    // } else {
    // checkedSearch.remove(id);
    // }
    // }
    //
    // };
    // searchTable.addCheckedListener(searchListener);
    // searchTable.addAction(constants.AddCollaborators(), new Command() {
    //
    // public void execute() {
    // executeCommand(checkedSearch, UserQuery.CollaboratorsUpdate.ADD);
    // }
    // });
    // HorizontalPanel hp = (HorizontalPanel) mainSp.getWidget();
    // VerticalPanel vp = (VerticalPanel) hp.getWidget(hp.getWidgetCount() - 1);
    // vp.add(searchTable);
  }

  protected void resetToDefaultState() {
    // TODO Auto-generated method stub

  }

  private void loadSearchResult(String firstName, String lastName,
      String email, String institution) {
    UserQuery query = usersTable.getQuery();
    query.getSearchFilters().clear();
    if (firstName != null && !firstName.equals("")) {
      query.addSearchFilter(Criteria.FIRST_NAME.getParam() + " like "
          + firstName);
    }
    if (lastName != null && !lastName.equals("")) {
      query
          .addSearchFilter(Criteria.LAST_NAME.getParam() + " like " + lastName);
    }
    if (email != null && !email.equals("")) {
      query.addSearchFilter(Criteria.EMAIL.getParam() + " like " + email);
    }
    if (institution != null && !institution.equals("")) {
      query.addSearchFilter(Criteria.INSTITUTION.getParam() + " like "
          + institution);
    }
    usersTable.resetTable();
  }

}
