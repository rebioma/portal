package org.rebioma.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.rebioma.client.UserQuery.CollaboratorsUpdate;
import org.rebioma.client.UsersTable.CheckedClickListener;
import org.rebioma.client.UsersTable.Criteria;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * A view/add/remove where logged user can view user's existing collaborators.
 * 
 */
public class UserProfilesView extends ComponentView {

  /**
   * a history state with an additional checksMap
   */
  public class CollaboratorHistoryState extends HistoryState {

    private Map<Integer, Boolean> SearchChecksMap = new HashMap<Integer, Boolean>();

    public void clearSearchChecksState() {
      SearchChecksMap.clear();
    }

    @Override
    public Object getHistoryParameters(UrlParam param) {
      switch (param) {
      case TAB:
        return stringValue(param);
      case SEARCH_CHECKED:
        return getSearchCheckedValues(true);
      case SEARCH_UNCHECKED:
        return getSearchCheckedValues(false);
      case COLLABORATOR_SEARCH_FIELDS:
      case COLLABORATOR_WIDGETS_LOADED:
        return stringValue(param);
      }
      return null;
    }

    public String getSearchCheckedValues(boolean checked) {
      StringBuilder sb = new StringBuilder();
      for (Integer index : SearchChecksMap.keySet()) {
        boolean isChecked = SearchChecksMap.get(index);
        if (isChecked == checked) {
          sb.append(index + ",");
        }
      }
      if (sb.length() != 0) {
        sb.deleteCharAt(sb.length() - 1);
      }
      return sb.toString();
    }

    public Map<Integer, Boolean> getSearchChecksMap() {
      return SearchChecksMap;
    }

    public void parseSearchCheckedUrl() {
      SearchChecksMap.clear();
      for (Integer index : intSetValues(UrlParam.SEARCH_CHECKED)) {
        SearchChecksMap.put(index, true);
      }
      for (Integer index : intSetValues(UrlParam.SEARCH_UNCHECKED)) {
        SearchChecksMap.put(index, false);
      }
    }

    public void setSearchChecksMap(Map<Integer, Boolean> checksMap) {
      this.SearchChecksMap = checksMap;
    }

  }

  public enum TableKind {
    FRIENDS_TABLE, SEARCH_TABLE;
  }

  private static final int ROW_COUNT = 10;

  public static ViewInfo init(final View parent, final String name,
      final String historyName) {
    return new ViewInfo() {

      @Override
      public String getName() {
        return name;
      }

      @Override
      protected View constructView() {
        return new UserProfilesView(parent);
      }

      @Override
      protected String getHisTokenName() {
        return historyName;
      }
    };
  }

  private final Set<Integer> checkedFriends = new HashSet<Integer>();

  private Set<Integer> checkedSearch;
  private TextBox emailTextBox;
  /**
   * search Fields
   */
  private TextBox firstNameTextBox;
  private final CheckedClickListener friendsListener = new CheckedClickListener() {

    public void onChecked(String email, Integer id, boolean checked, int row) {
      if (checked) {
        checkedFriends.add(id);
      } else {
        checkedFriends.remove(id);
      }
    }

  };
  /**
   * your friends Table
   */
  private final UsersTable friendsTable = new UsersTable(true);

  private final CollaboratorHistoryState historyState = new CollaboratorHistoryState();
  private TextBox institutionTextBox;
  private boolean isUpadingFriend = false;
  private TextBox lastNameTextBox;
  /**
   * Widgets to be shown in CollaboratorView
   */
  private final ScrollPanel mainSp = new ScrollPanel();
  private final int SEARCH_FIELDS_LOADED = 0;
  private final int SEARCH_TABLE_LOADED = 1;
  private boolean searchFieldsLoaded;
  private final HorizontalPanel mainHp = new HorizontalPanel();
  private CheckedClickListener searchListener;
  /**
   * Table variables for search results
   */
  private UsersTable searchTable;

  private boolean searchTableLoaded = false;
  private final UserProfile userProfile;

  public UserProfilesView(View parent) {
    super(parent, false);
    userProfile = UserProfile.getUserProfile(ApplicationView
        .getAuthenticatedUser());
    VerticalPanel friendsVp = new VerticalPanel();
    friendsVp.add(userProfile);
    friendsVp.add(new HTML(constants.FriendsDescription()));
    friendsTable.addAction(constants.DeleteCollaborators(), new Command() {

      public void execute() {
        executeCommand(checkedFriends, UserQuery.CollaboratorsUpdate.REMOVE);
      }
    });
    HTML searchCollaboratorsLink = new HTML(constants.SearchCollaborators());
    searchCollaboratorsLink.addStyleName("link");
    searchCollaboratorsLink.addClickHandler(new ClickHandler() {

      public void onClick(ClickEvent event) {
        UserProfilesView.this.loadSearchFields();
        addHistoryItem(false);
      }
    });
    friendsTable.addCheckedListener(friendsListener);
    friendsTable.addWidget(searchCollaboratorsLink);
    UserQuery query = friendsTable.getQuery();
    query.setUsersCollaboratorsOnly(true);
    friendsTable.resetTable();
    friendsVp.add(friendsTable);

    mainHp.setSpacing(5);
    mainHp.add(friendsVp);
    mainSp.setWidget(mainHp);
    initWidget(mainSp);
    String hisToken = History.getToken();
    if (!hisToken.equals("")) {
      handleOnValueChange(hisToken);
    }
    Scheduler.get().scheduleDeferred(new ScheduledCommand(){
      public void execute() {
        resize(Window.getClientWidth(), Window.getClientHeight());
      }

    });
    setStyleName("UserProfilesView");
  }

  public void clearUser() {
    // userProfile.user = null;
  }

  @Override
  public String historyToken() {
    StringBuilder sb = new StringBuilder();
    sb.append(getUrlToken(UrlParam.CHECKED));
    sb.append(getUrlToken(UrlParam.SEARCH_CHECKED));
    sb.append(getUrlToken(UrlParam.COLLABORATOR_SEARCH_FIELDS));
    sb.append(getUrlToken(UrlParam.COLLABORATOR_WIDGETS_LOADED));
    return sb.toString();
  }

  @Override
  public void onStateChanged(ViewState state) {
    switch (state) {
    case ADMIN:
    case REVIEWER:
    case RESEARCHER:
      break;
    case UNAUTHENTICATED:
      parent.switchView(ApplicationView.OCCURRENCES, true);
      break;
    }
  }

  protected void executeCommand(Set<Integer> checkedUsers,
      final CollaboratorsUpdate collaboratorsUpdate) {
    if (isUpadingFriend) {
      PopupMessage.getInstance().showMessage(
          constants.CollaboratorsUpdatingMsg());
      return;
    }
    isUpadingFriend = true;
    UserQuery query = new UserQuery(0, ROW_COUNT);
    query.setCollaboratorsUpdate(collaboratorsUpdate);
    for (Integer user : checkedUsers) {
      query.addUpdatedFriend(user);
    }
    DataSwitch.get().update(ApplicationView.getSessionId(), query,
        new AsyncCallback<Integer>() {

          public void onFailure(Throwable caught) {
            Window.alert(caught.getMessage());
            GWT.log(caught.getMessage(), caught);
            isUpadingFriend = false;
          }

          public void onSuccess(Integer result) {
            switch (collaboratorsUpdate) {
            case ADD:
              PopupMessage.getInstance().showMessage(
                  result + " " + constants.AddCollaboratorsConfirm());
              break;
            case REMOVE:
              PopupMessage.getInstance().showMessage(
                  result + " " + constants.DeleteCollaboratorsConfirm());
              historyState.clearChecksState();
              break;
            }
            isUpadingFriend = false;
            loadFriendsTable();
          }
        });
  }

  protected Set<Integer> getCheckedUsers(TableWidget table,
      Map<Integer, Integer> indexUserMap) {
    Set<Integer> userSet = new HashSet<Integer>();
    for (int row = 0; row < table.getDataRowCount(); row++) {
      if (((CheckBox) table.getCellWidget(row, Criteria.CHECK_BOX.getIndex()))
          .getValue()) {
        userSet.add(indexUserMap.get(row));
      }
    }
    return userSet;
  }

  @Override
  protected void handleOnValueChange(String historyToken) {
    historyState.setHistoryToken(historyToken);
    restoreChecks(TableKind.FRIENDS_TABLE);

    String whatIsLoaded[] = ((String) historyState
        .getHistoryParameters(UrlParam.COLLABORATOR_WIDGETS_LOADED)).split(",");
    if (whatIsLoaded.length == 1 && whatIsLoaded[0].equals("")) {
      return;
    }
    boolean searchFieldsLoad = Boolean
        .parseBoolean(whatIsLoaded[SEARCH_FIELDS_LOADED]);
    boolean searchTableLoad = Boolean
        .parseBoolean(whatIsLoaded[SEARCH_TABLE_LOADED]);

    if (searchFieldsLoad) {
      loadSearchFields();
      String searchFields[] = ((String) historyState
          .getHistoryParameters(UrlParam.COLLABORATOR_SEARCH_FIELDS))
          .split(",");
      if (searchFields.length == 1 && searchFields[0].equals("")) {
        firstNameTextBox.setText("");
        lastNameTextBox.setText("");
        emailTextBox.setText("");
        institutionTextBox.setText("");
      } else {
        String searchFieldParam[] = searchFields[Criteria.FIRST_NAME.getIndex() - 1]
            .split("=");
        if (searchFieldParam.length == 1) {
          firstNameTextBox.setText("");
        } else {
          firstNameTextBox.setText(searchFieldParam[1]);
        }
        searchFieldParam = searchFields[Criteria.LAST_NAME.getIndex() - 1]
            .split("=");
        if (searchFieldParam.length == 1) {
          lastNameTextBox.setText("");
        } else {
          lastNameTextBox.setText(searchFieldParam[1]);
        }
        searchFieldParam = searchFields[Criteria.EMAIL.getIndex() - 1]
            .split("=");
        if (searchFieldParam.length == 1) {
          emailTextBox.setText("");
        } else {
          emailTextBox.setText(searchFieldParam[1]);
        }
        searchFieldParam = searchFields[Criteria.INSTITUTION.getIndex() - 1]
            .split("=");
        if (searchFieldParam.length == 1) {
          institutionTextBox.setText("");
        } else {
          institutionTextBox.setText(searchFieldParam[1]);
        }
      }

      if (searchTableLoad) {
        loadSearchTable();
        loadSearchResult(firstNameTextBox.getText(), lastNameTextBox.getText(),
            emailTextBox.getText(), institutionTextBox.getText());
        restoreChecks(TableKind.SEARCH_TABLE);
      }
    }
  }

  @Override
  protected boolean isMyView(String value) {
    historyState.setHistoryToken(value);
    String tabName = (String) historyState.getHistoryParameters(UrlParam.TAB);
    return tabName != null
        && tabName.equalsIgnoreCase(ApplicationView.USER_PROFILES);
  }

  protected void loadFriendsTable() {
    UserQuery query = friendsTable.getQuery();
    query.setUsersCollaboratorsOnly(true);
    friendsTable.resetTable();
  }

  /**
   * Initializes the user search form if it is not yet loaded.
   */
  protected void loadSearchFields() {
    if (searchFieldsLoaded) {
      return;
    }
    searchFieldsLoaded = true;
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
        loadSearchTable();
        loadSearchResult(firstNameTextBox.getText(), lastNameTextBox.getText(),
            emailTextBox.getText(), institutionTextBox.getText());
        addHistoryItem(false);
      }

    });
    vp.add(searchButton);
    ((HorizontalPanel) mainSp.getWidget()).add(vp);
  }

  /**
   * Initializes the user search table if it is not yet loaded.
   */
  protected void loadSearchTable() {
    if (searchTableLoaded) {
      return;
    }
    searchTableLoaded = true;
    searchTable = new UsersTable(true);
    checkedSearch = new HashSet<Integer>();
    searchListener = new CheckedClickListener() {

      public void onChecked(String email, Integer id, boolean checked, int row) {
        if (checked) {
          checkedSearch.add(id);
        } else {
          checkedSearch.remove(id);
        }
      }

    };
    searchTable.addCheckedListener(searchListener);
    searchTable.addAction(constants.AddCollaborators(), new Command() {

      public void execute() {
        executeCommand(checkedSearch, UserQuery.CollaboratorsUpdate.ADD);
      }
    });
    HorizontalPanel hp = (HorizontalPanel) mainSp.getWidget();
    VerticalPanel vp = (VerticalPanel) hp.getWidget(hp.getWidgetCount() - 1);
    vp.add(searchTable);
  }

  @Override
  protected void resetToDefaultState() {
    HorizontalPanel hp = ((HorizontalPanel) mainSp.getWidget());
    Widget vp = hp.getWidget(0);
    hp.clear();
    hp.add(vp);
  }

  @Override
  protected void resize(int width, int height) {
    int pWidth = width - mainHp.getOffsetWidth();
    int pHeight = height - mainHp.getAbsoluteTop();
    if (pWidth < width) {
      pWidth = width;
    }

    if (pHeight < 0) {
      pHeight = height;
    }
    mainSp.setWidth(pWidth + "px");
    mainSp.setHeight(pHeight + "px");
  }

  /**
   * Updates a check map of given {@link TableKind}. Checks mapping are use for
   * history.
   * 
   * @param tableKind
   */
  protected void updateChecksMap(TableKind tableKind) {
    // TableWidget table = null;
    // Map<Integer, Boolean> checksMap = null;
    // switch (tableKind) {
    // case FRIENDS_TABLE:
    // table = friendsTable;
    // checksMap = historyState.getChecksMap();
    // break;
    // case SEARCH_TABLE:
    // table = searchTable;
    // checksMap = historyState.getSearchChecksMap();
    // break;
    // }
    //
    // for (int row = 0; row < table.getDataRowCount(); row++) {
    // CheckBox cb = (CheckBox) table.getCellWidget(row,
    // Criteria.CHECK_BOX.index);
    // checksMap.put(row, cb.getValue());
    // }
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
    case SEARCH_CHECKED:
      String searchCheckedValues = historyState.getSearchCheckedValues(true);
      if (searchCheckedValues.equals("")) {
        query = "";
      } else {
        query += searchCheckedValues;
      }
      break;
    case COLLABORATOR_SEARCH_FIELDS:
      if (!searchFieldsLoaded) {
        query = "";
        break;
      }
      StringBuilder searchFieldValues = new StringBuilder();
      searchFieldValues.append(Criteria.FIRST_NAME.getParam() + "="
          + firstNameTextBox.getText());
      searchFieldValues.append(",");
      searchFieldValues.append(Criteria.LAST_NAME.getParam() + "="
          + lastNameTextBox.getText());
      searchFieldValues.append(",");
      searchFieldValues.append(Criteria.EMAIL.getParam() + "="
          + emailTextBox.getText());
      searchFieldValues.append(",");
      searchFieldValues.append(Criteria.INSTITUTION.getParam() + "="
          + institutionTextBox.getText());
      query += searchFieldValues.toString();
      break;
    case COLLABORATOR_WIDGETS_LOADED:
      query += searchFieldsLoaded;
      query += ",";
      query += searchTableLoaded;
      break;
    default:
      query = "";
    }
    return query.length() == 0 ? query : query + "&";
  }

  private void loadSearchResult(String firstName, String lastName,
      String email, String institution) {
    UserQuery query = searchTable.getQuery();
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
    searchTable.resetTable();
  }

  private void restoreChecks(TableKind tableKind) {
    // TableWidget table = null;
    // Map<Integer, Boolean> checksMap = null;
    // switch (tableKind) {
    // case FRIENDS_TABLE:
    // historyState.parseCheckedUrl();
    // table = friendsTable;
    // checksMap = historyState.getChecksMap();
    // break;
    // case SEARCH_TABLE:
    // historyState.parseSearchCheckedUrl();
    // table = searchTable;
    // checksMap = historyState.getSearchChecksMap();
    // break;
    // }
    // for (int row = 0; row < table.getDataRowCount(); row++) {
    // CheckBox cb = (CheckBox) table.getCellWidget(row,
    // Criteria.CHECK_BOX.index);
    // Boolean isChecked = checksMap.get(row);
    // cb.setValue(isChecked == null ? false : isChecked);
    // }
  }
}