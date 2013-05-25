package org.rebioma.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.rebioma.client.DataPager.PageListener;
import org.rebioma.client.bean.User;
import org.rebioma.client.i18n.AppConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A table contains users information that query from the server.
 * 
 * @author Tri
 * 
 */
public class UsersTable extends Composite implements ResizeHandler,
    PageListener<User> {

  /**
   * OnChecked is fire when a check box in the table is changed value where by
   * clicking on a check box or by clicking on All or None action
   * 
   * @author Tri
   * 
   */
  public interface CheckedClickListener {
    void onChecked(String email, Integer id, boolean checked, int row);
  }

  /**
   * Table hears to User table col mapping.
   */
  public enum Criteria {
    CHECK_BOX(0, null, "", "checkbox", null), FIRST_NAME(1, constants
        .SearchByFirstName(), constants.FirstName(), "first-name", "firstName"), LAST_NAME(
        2, constants.SearchByLastName(), constants.LastName(), "last-name",
        "lastName"), EMAIL(3, constants.SearchByEmail(), constants.Email(),
        "email", "email"), INSTITUTION(4, constants.SearchByInstitution(),
        constants.Institution(), "institution", "institution");

    private int index;
    private String instruction;
    private String header;
    private String style;
    private String param;

    Criteria(int index, String instruction, String header, String style,
        String param) {
      this.index = index;
      this.instruction = instruction;
      this.header = header;
      this.style = style;
      this.param = param;
    }

    public String getHeader() {
      return header;
    }

    public int getIndex() {
      return index;
    }

    public String getInstruction() {
      return instruction;
    }

    public String getParam() {
      return param;
    }

    public String getStyle() {
      return style;
    }
  }

  /**
   * This class wires with
   * {@link DataSwitch#fetchUser(String, UserQuery, AsyncCallback)} call to
   * request users from {@link UserQuery}
   * 
   * @author Tri
   * 
   */
  private class UserDataPager extends DataPager<User> {
    public UserDataPager(int pageSize) {
      super(pageSize, new UserQuery(0, pageSize));
    }

    public UserQuery getQuery() {
      return (UserQuery) super.getQuery();
    }

    protected void requestData(final PageCallback<User> cb) {
      String sessionId = ApplicationView.getSessionId();
      if (sessionId == null || sessionId.equals("")) {
        Window.alert("session have expired");
        return;
      }
      DataSwitch.get().fetchUser(sessionId, getQuery(),
          new AsyncCallback<UserQuery>() {
            public void onFailure(Throwable caught) {
              Window.alert(caught.getMessage());
              GWT.log(caught.getMessage(), caught);
              cb.onPageReady(null);
            }

            public void onSuccess(UserQuery result) {
              if (totalDataCount == UNDEFINED) {
                totalDataCount = result.getCount();
              }
              cb.onPageReady(result.getResults());

            }

          });
    }

  }

  private static final AppConstants constants = ApplicationView.getConstants();
  private final static String[] resultsHeader = {
      Criteria.CHECK_BOX.getHeader(), Criteria.FIRST_NAME.getHeader(),
      Criteria.LAST_NAME.getHeader(), 
      //Criteria.EMAIL.getHeader(),
      Criteria.INSTITUTION.getHeader() };
  private final static String[] columnStyles = { Criteria.CHECK_BOX.getStyle(),
      Criteria.FIRST_NAME.getStyle(), Criteria.LAST_NAME.getStyle(),
     //Criteria.EMAIL.getStyle(), 
      Criteria.INSTITUTION.getStyle() };
  private final UserDataPager dataPager = new UserDataPager(ROW_COUNT);
  private final PagerWidget<User> userPager = new PagerWidget<User>(dataPager) {
  };
  private static int CHECK_INDEX = 0;
  private final VerticalPanel mainVp = new VerticalPanel();
  private final ScrollPanel mainSp = new ScrollPanel();
  private final ActionTool actionTool = new ActionTool(true) {

    protected void setCheckedAll(boolean checked) {
      for (int i = 0; i < userTable.getDataRowCount(); i++) {
        CheckBox cb = getRowCheckBox(i);
        cb.setValue(checked);
        fireOnCheckedClick(currentUsers.get(i).getEmail(), currentUsers.get(i)
            .getId(), checked, i);
      }

    }

  };
  public static final int ROW_COUNT = 10;
  private final TableWidget userTable;
  private final List<CheckedClickListener> checkedListeners = new ArrayList<CheckedClickListener>();
  private List<User> currentUsers = null;
  private Set<String> checkedUsers;
  private boolean multiChecked = true;

  /**
   * Call this(true).
   */
  public UsersTable() {
    this(true);
  }

  /**
   * Initialize UsersTable with the action list box if showedActionBar is true.
   * 
   * @param showedActionBar true to show action list box.
   */
  public UsersTable(boolean showedActionBar) {
    userTable = new TableWidget(resultsHeader, columnStyles, ROW_COUNT);
    HorizontalPanel actionHp = new HorizontalPanel();
    actionHp.setWidth("100%");
    if (showedActionBar) {
      actionHp.add(actionTool);
      actionHp.setCellHorizontalAlignment(actionTool,
          HasHorizontalAlignment.ALIGN_LEFT);
      actionHp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
      actionTool.setDefaultSelection(0);
    }
    actionHp.add(userPager);
    actionHp.setCellHorizontalAlignment(userPager,
        HasHorizontalAlignment.ALIGN_RIGHT);
    mainSp.setWidget(mainVp);
    mainVp.add(actionHp);
    mainVp.add(userTable);
    initWidget(mainSp);
    userPager.addPageListener(this);
    Window.addResizeHandler(this);
    Scheduler.get().scheduleDeferred(new ScheduledCommand(){
      public void execute() {
        resize(Window.getClientWidth(), Window.getClientHeight());

      }

    });
  }

  public void addAction(String action, Command actionCommand) {
    actionTool.addAction(action, actionCommand);
  }

  public void addCheckedListener(CheckedClickListener listener) {
    checkedListeners.add(listener);
  }

  public void addWidget(Widget w) {
    actionTool.addWidget(w);
  }

  public int getPageSize() {
    return dataPager.getPageSize();
  }

  public UserQuery getQuery() {
    return dataPager.getQuery();
  }

  public User getUser(int row) {
    return currentUsers.get(row);
  }

  public void onPageLoaded(List<User> data, int pageNumber) {
    Widget rowsWidget[][] = constructTableContent(data);
    int start = (userPager.getCurrentPageNumber() - 1)
        * userPager.getPageSize();
    userTable.showRecord(userPager.getPageSize(), start, rowsWidget);
    currentUsers = data;
    resize(Window.getClientWidth(), Window.getClientHeight());
  }

  public void onResize(ResizeEvent event) {
    resize(event.getWidth(), event.getHeight());
  }

  public void removeCheckedListener(CheckedClickListener listener) {
    checkedListeners.remove(listener);
  }

  public void resetTable() {
    userPager.init(1);
  }

  public void setCheckedUsers(Set<String> checksUsers) {
    this.checkedUsers = checksUsers;
  }

  public void setMultiCheck(boolean multiChecked) {
    this.multiChecked = multiChecked;
  }

  protected void resize(int width, int height) {
    height = height - (mainSp.getAbsoluteTop());
    if (height > mainVp.getOffsetHeight()) {
      height = mainVp.getOffsetHeight();
    }
    if (height < 0) {
      height = -1;
    }
    mainSp.setPixelSize(userTable.getOffsetWidth() + 20, height);
  }

  private Widget[][] constructTableContent(List<User> userList) {
    Widget widgetContent[][] = new Widget[userList.size()][];
    int i = 0;
    for (User user : userList) {
      Widget rowsWidget[] = new Widget[resultsHeader.length];
      final CheckBox cb = new CheckBox();
      final User currentUser = user;
      final int row = i;
      if (checkedUsers != null) {
        boolean checked = checkedUsers.contains(user.getId());
        cb.setValue(checked);
      }
      cb.addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent event) {
          fireOnCheckedClick(currentUser.getEmail(), currentUser.getId(), cb
              .getValue(), row);
          if (!multiChecked && cb.getValue()) {
            for (int i = 0; i < userTable.getDataRowCount(); i++) {
              CheckBox checkBox = getRowCheckBox(i);
              if (checkBox != cb) {
                checkBox.setValue(false);
              }
            }
          }
        }

      });
      rowsWidget[Criteria.CHECK_BOX.getIndex()] = cb;
      rowsWidget[Criteria.FIRST_NAME.getIndex()] = new Label(user
          .getFirstName());
      rowsWidget[Criteria.LAST_NAME.getIndex()] = new Label(user.getLastName());
    // rowsWidget[Criteria.EMAIL.getIndex()] = new Label(user.getEmail());    
      rowsWidget[Criteria.INSTITUTION.getIndex()-1] = new Label(user
          .getInstitution());
      widgetContent[i] = rowsWidget;
      i++;
    }
    return widgetContent;
  }

  private void fireOnCheckedClick(String email, Integer userId,
      boolean checked, int row) {
    for (CheckedClickListener listener : checkedListeners) {
      listener.onChecked(email, userId, checked, row);
    }
  }

  /**
   * Gets the {@link CheckBox} widget of the given row. the check box is at
   * column {@link #CHECK_INDEX}
   * 
   * @param row
   * @return
   */
  private CheckBox getRowCheckBox(int row) {
    return (CheckBox) userTable.getCellWidget(row, CHECK_INDEX);
  }
}
