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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rebioma.client.View.ViewInfo;
import org.rebioma.client.bean.Role;
import org.rebioma.client.bean.User;
import org.rebioma.client.forms.ChangePasswordForm;
import org.rebioma.client.forms.ForgotPasswordForm;
import org.rebioma.client.forms.Form;
import org.rebioma.client.forms.RegisterForm;
import org.rebioma.client.forms.SignInForm;
import org.rebioma.client.forms.Form.FormListener;
import org.rebioma.client.i18n.AppConstants;
import org.rebioma.client.i18n.LocaleListBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

public class ApplicationView extends View implements ClickHandler {

  public enum LinksGroup {
    HOME(constants.Home()), SIGN_IN(constants.SignIn()), CHANGE_PASS(constants
        .ChangePassword()), REGISTER(constants.Register()), EDIT_COLLABORATORS(
        constants.EditCollaborators());
    String value;

    LinksGroup(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  private class FormView extends ComponentView {
    private final Form form;

    public FormView(View parent, Form viewForm, String name) {
      super(parent, false);
      form = viewForm;
      initWidget(form);
    }

    @Override
    protected void resetToDefaultState() {
      form.clearInputs();

    }

  }

  private class FormViewInfo extends ViewInfo {
    private final String name;
    private final View parent;
    private final Form formView;
    private final String historyName;

    public FormViewInfo(String name, String historyName, View parent,
        Form formView) {
      this.parent = parent;
      this.name = name;
      this.formView = formView;
      this.historyName = historyName;
    }

    @Override
    protected View constructView() {
      return new FormView(parent, formView, name);
    }

    @Override
    protected String getHisTokenName() {
      return historyName;
    }

    @Override
    protected String getName() {
      return name;
    }

  }

  /**
   * The Links inner class is a widget that displays application links. Groups
   * of links can be added and shown by keyword.
   */
  private class Links extends Composite {
    /**
     * A mapping of keywords to an array of links widgets.
     */
    private final Map<String, Widget[]> linkGroups = new HashMap<String, Widget[]>();

    /**
     * The widget wrapped by this composite that contains the link widgets.
     */
    private final FlowPanel linksPanel;
    private final HTML emailLabel = new HTML("");

    /**
     * Creates a Links class.
     */
    Links() {
      linksPanel = new FlowPanel();
      // linksPanel.setHeight("10px");
      initWidget(linksPanel);
      linksPanel.add(emailLabel);
      linksPanel.setWidth("100%");
      // linksPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
      // linksPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    }

    /**
     * {@link FlowPanel#clear()} also clears HTML content element so use this
     * method to clear {@link #linksPanel} instance of {@link FlowPanel#clear()}
     * .
     */
    void clear() {
      while (linksPanel.getWidgetCount() > 0) {
        linksPanel.remove(0);
      }
    }

    /**
     * Clears the links.
     */
    void clearLinks() {
      linksPanel.clear();
    }

    /**
     * Creates a new keyword mapping to an array of link widgets.
     * 
     * @param keyword the keyword that identifies the array of link widgets
     * @param widgets the array of link widgets
     */
    void putLinks(String keyword, Widget[] widgets) {
      for (Widget w : widgets) {
        if (w instanceof Label) {
          ((Label) w).setWordWrap(false);
        }
      }
      linkGroups.put(keyword, widgets);
    }

    void removeEmail() {
      emailLabel.setHTML("&nbsp;|&nbsp;");
    }

    void showEmail(String email) {
      emailLabel.setHTML("&nbsp;|&nbsp;" + email + "&nbsp;|&nbsp;");
    }

    /**
     * Shows the links identified by the keyword.
     * 
     * @param keyword the keyword that identifies the links to show
     */
    void showLinks(String keyword) {
      if (linkGroups.containsKey(keyword)) {
        Widget space = new HTML("&nbsp;", true);
        clear();
        for (Widget link : linkGroups.get(keyword)) {
          addLink(link);
        }
        linksPanel.insert(emailLabel, 1);
        addLink(space, false);
      }
    }

    /**
     * A helper method that adds the link widget along with a separator after
     * it.
     * 
     * @param link the link to add
     */
    private void addLink(Widget link) {
      addLink(link, true);
    }

    /**
     * A helper method that adds the link widget and a separator after it
     * requested.
     * 
     * @param link the link to add
     * @param addSeparator if true add a separator after the link
     */
    private void addLink(Widget link, boolean addSeparator) {
      if (linksPanel.getWidgetCount() > 1 && addSeparator) {
        addSeparator();
      }
      linksPanel.add(link);
    }

    /**
     * A helper method that adds a "|" separator.
     */
    private void addSeparator() {
      HTML sep = new HTML("&nbsp;|&nbsp;");
      linksPanel.add(sep);
      // linksPanel.setCellWidth(sep, "15px");
      // linksPanel.setCellHorizontalAlignment(sep,
      // HorizontalPanel.ALIGN_CENTER);
    }
  }

  protected static final String OCCURRENCES = "Occ";
  protected static final String SIGN_IN = "Signin";
  protected static final String REGISTER = "Register";
  protected static final String FORGET_PASS = "ForgetPass";
  protected static final String CHANGE_PASS = "ChangePass";
  protected static final String USER_PROFILES = "Profiler";
  protected static final String USER_MANAGEMENT = "Management";
  protected static final String SPECIES_EXPLORER = "spec_expl";
  
  
  protected static final String STATS_TAB = "stats_tab";
 // protected static final String MAIL_TAB = "mail_tab";
  public static ApplicationView getApplication() {
    if (instance == null) {
      instance = new ApplicationView();
    }
    return instance;
  }

  public static String getCurrentLocale() {
    return locale.getCurrentLocale();
  }

  public static ViewState getCurrentState() {
    return currentState;
  }

  public static Map<String, List<String>> getHistoryTokenParams() {
    String urlParsing[] = Window.Location.getHref().split("#", 2);
    if (urlParsing.length != 2) {
      return new HashMap<String, List<String>>();
    } else {
      return getHistoryTokenParams(urlParsing[1]);
    }
  }

  public static Map<String, List<String>> getHistoryTokenParams(
      String historyToken) {
    Map<String, List<String>> out = new HashMap<String, List<String>>();
    if (historyToken != null && historyToken.length() > 0) {
      for (String kvPair : historyToken.split("&")) {
        String[] kv = kvPair.split("=", 2);
        if (kv[0].length() == 0) {
          continue;
        }

        List<String> values = out.get(kv[0]);
        if (values == null) {
          values = new ArrayList<String>();
          out.put(kv[0], values);
        }
        values.add(kv.length > 1 ? URL.decode(kv[1]) : "");
      }
    }

    for (Map.Entry<String, List<String>> entry : out.entrySet()) {
      entry.setValue(Collections.unmodifiableList(entry.getValue()));
    }

    out = Collections.unmodifiableMap(out);

    return out;

  }

  /**
   * Returns a list of values created from a parameter in a history token, or an
   * empty list.
   * 
   * @param historyToken
   * @param paramName
   * @return
   */
  public static List<String> getHistoryTokenParamValues(String historyToken,
      String paramName) {
    Map<String, List<String>> params = getHistoryTokenParams(historyToken);
    return params == null ? new ArrayList<String>() : params.get(paramName);
  }

  public static boolean isAdmin() {
    return currentState == ViewState.ADMIN;
  }

  /**
   * This method is a quick check for a legal email format. It does not check
   * 100% of all legal emails, but it is good enough for this purpose.
   * 
   * @param email an email address.
   * @return true if the email is valid, false otherwise.
   */
  public static boolean isValidEmailFormat(String email) {
    return email
        .matches("[A-Za-z0-9_%+-][A-Za-z0-9._%+-]*@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
  }

  public static void setAuthenticatedUser(User user) {
    authenticatedUser = user;
    if (user != null) {
      Set<Role> roles = user.getRoles();
      if (isAllowToView(roles, ViewState.ADMIN)) {
        currentState = ViewState.ADMIN;
      } else if (isAllowToView(roles, ViewState.REVIEWER)) {
        currentState = ViewState.REVIEWER;
      } else if (isAllowToView(roles, ViewState.RESEARCHER)) {
        currentState = ViewState.RESEARCHER;
      } else {
        currentState = ViewState.UNAUTHENTICATED;
      }
      // Checks whether session is still valid in each 15 minutes (900000ms)
      checkSessionTimer.scheduleRepeating(900000);
    } else {
      checkSessionTimer.cancel();
      currentState = ViewState.UNAUTHENTICATED;
    }
  }

  private final Map<String, ViewInfo> viewInfos = new HashMap<String, ViewInfo>();
  private final DecoratedTabPanel tabPanel = new DecoratedTabPanel();

  private HTML signinLink;

  private HTML regLink;
  private HTML signOutLink;

  // private final HorizontalPanel mainContentPanel;

  // private HTML changePassLink;

  private HTML issuesLink;
  private HTML homeLink;
  private HTML helpLink;
  private HTML webPortalLink;
  private HTML acknowledgmentLink;
  // private HTML editCollaboratorsLink;
  private final Links links = new Links();

  private static User authenticatedUser = null;

  public static User getAuthenticatedUser() {
    return authenticatedUser;
  }

  private final HistoryState historyState = new HistoryState() {
    @Override
    public Object getHistoryParameters(UrlParam param) {
      switch (param) {
      case TAB:
        return stringValue(param);
      }
      return "";
    }
  };
  private HorizontalPanel linksPanel;

  private FlexTable topPanel;

  public static final String SESSION_ID_NAME = "sid";

  /**
   * The ReBioMa issues URL on the Google code site.
   */
  private static final String CODE_SITE_URL = "<a target='_blank' href='http://code.google.com/p/rebioma/issues/list'>"
      + constants.IssueTracker() + "</a>";

  private static final String HELP_PAGE_URL = "<a target='_blank' href='https://sites.google.com/site/rebiomahelp/home/'>"
      + constants.Help() + "</a>";
  
  private static final String ACKNOWLEDGMENT = "<a target='_blank' href='http://www.rebioma.net/index.php?option=com_content&view=article&id=41'>"
      + constants.Acknowledgment() + "</a>";
  
  
  private static final String WEB_PORTAL_URL = "<a target='_blank' href='http://www.rebioma.net'>"
      + "REBIOMA" + "</a>";

  private static final String DEFAULT_STYLE_NAME = "Application";

  private static ViewState currentState = ViewState.UNAUTHENTICATED;

  private static ApplicationView instance = null;

  private static String sessionId;

  public static AppConstants getConstants() {
    return constants;
  }

  public static String getSessionId() {
    return sessionId;
  }

  public static void refreshSession() {
    if (sessionId != null) {
      Cookies.setCookie(SESSION_ID_NAME, getSessionId(), new Date(System
          .currentTimeMillis() + 86400000));
    }
  }

  public static void setSessionId(String sid) {
    sessionId = sid;
  }

  /**
   * Signs out of the server via RPC.
   */
  public static void signOutOnServer() {
    if (sessionId == null || sessionId.equals("")) {
      return;
    }
    DataSwitch.get().signOut(sessionId, new AsyncCallback<String>() {
      public void onFailure(Throwable caught) {
        Window.alert("Failed to sign out: " + caught.toString());
      }

      public void onSuccess(String result) {
        GWT.log("User logged out", null);
      }
    });
  }

  /**
   * Checks whether the current session id is still valid. If the user is logged
   * again in other system, the session id of the older systems will be invalid.
   */
  protected static void checkCurrentSession() {
    if (sessionId == null) {
      sessionId = Cookies.getCookie(SESSION_ID_NAME);
    }
    DataSwitch.get().isSessionIdValid(sessionId, new AsyncCallback<User>() {
      public void onFailure(Throwable caught) {
        GWT.log(caught.getMessage(), caught);
        Window.alert(constants.SessionExpired());
        ApplicationView.getApplication().signOutUser(false);
      }

      public void onSuccess(User result) {
        if (result == null || result.getEmail() == null
            || result.getEmail().equals("")) {
          onFailure(new IllegalStateException("Invalid user"));
        }
      }
    });
  }

  private static boolean isAllowToView(Set<Role> roles, ViewState viewState) {
    for (Role role : roles) {
      ViewState roleView = ViewState.toViewState(role.getNameEn());
      if (roleView == viewState) {
        return true;
      }
    }
    return false;
  }

  private ViewInfo activeViewInfo = null;
  private boolean isInitialized;

  private ViewInfo editCollaboratorsViewInfo;
  private ViewInfo userManagementViewInfo;

  /**
   * A timer to check periodically for if whether the the user sessionId is
   * still valid. If not make a RPC call to alert the server to log out the
   * user. If the browser still have a session id make an RPC to the server to
   * check whether the session id is still valid. This checks is too ensure the
   * user only logged on one system at a time because the current server
   * implementation only support one session id at a time.
   */
  private static final Timer checkSessionTimer = new Timer() {
    @Override
    public void run() {
      if (instance == null) {
        return;
      }
      if (!instance.isSessionIdInBrowser()) {
        Window.alert(constants.SessionExpired() + " browser checked");
        instance.signOutUser(false);
      } else {
        checkCurrentSession();
      }

    }

  };

  private static final LocaleListBox locale = new LocaleListBox();

  /**
   * Initializes the application view.
   */
  private ApplicationView() {
    FlowPanel layout = new FlowPanel();
    // layout.setWidth("100%");
    initWidget(layout);
    createTopPanel();
    layout.add(topPanel);
    tabPanel.setWidth("100%");
    tabPanel.add(new VerticalPanel(), " ");
    tabPanel.addStyleName("Application-bottom");
    layout.add(tabPanel);
    tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
      public void onSelection(SelectionEvent<Integer> event) {
        int selectedIndex = tabPanel.getTabBar().getSelectedTab();
        Widget selectedWidget = tabPanel.getWidget(selectedIndex);
        String tabName = tabPanel.getTabBar().getTabHTML(selectedIndex)
            .toLowerCase();
        /**
         * if the selectedWidget is a label which mean is it is from initial
         * dummy label {@link ApplicationView#addTab(ViewInfo)}
         * 
         */
        if (selectedWidget instanceof Label) {
          String viewName = ((Label) selectedWidget).getText().toLowerCase();
          View view = viewInfos.get(viewName).getView();
          selectedWidget = view;
          tabPanel.remove(selectedIndex);
          tabPanel.insert(selectedWidget, tabName, selectedIndex);
          tabPanel.selectTab(selectedIndex);
          return;
        }
        if (activeViewInfo != null
            && selectedWidget != activeViewInfo.getView()) {
          activeViewInfo = ((View) selectedWidget).getViewInfo();
          if (selectedWidget instanceof FormView) {
            activeViewInfo.getView().resetToDefaultState();
          }
          addHistoryItem(false);
        }
        // Widget tabWidget = null;
        // if (listener != null) {
        // tabWidget = tabPanel.getWidget(event.getSelectedItem());
        // listener.onTabSelected((View) tabWidget);
        // }

      }
    });
    linksPanel.add(links);
    DataSwitch.get().addActiviesListener(new ActivitiesListener() {
      public void onDoSomething() {
        refreshSession();
      }

    });
  }

  /**
   * Put the current state history token into the History stack if
   * activeViewInfo is not null and the Application is not changing state by
   * history button click such as back, forward, and refresh.
   */
  @Override
  public void addHistoryItem(boolean issueEvent) {
    if (historyButtonClicked || activeViewInfo == null) {
      historyButtonClicked = false;
      return;
    }
    String historyToken = historyToken();
    GWT.log("add token: " + historyToken, null);
    History.newItem(historyToken, issueEvent);

  }

  /**
   * Construct this view history token (i.e tab=occurrences) and concat it with
   * active child view history token.
   */
  @Override
  public String historyToken() {
    if (activeViewInfo == null) {
      return History.getToken();
    }
    String historyToken = getUrlToken(UrlParam.TAB);
    String activeViewToken = activeViewInfo.getView().historyToken();
    historyToken += activeViewToken.equals("") ? "" : ("&" + activeViewToken);
    return historyToken;
  }

  /**
   * Initialize the ApplicationView with all the {@link Links} and children
   * {@link ViewInfo}.
   * 
   * @param loadDefaultView true if default child view should be loaded. Default
   *          child view is OccurrenceView
   */
  public void init(boolean loadDefaultView) {
    if (!isInitialized) {
      initLinks();
      initViews();
      isInitialized = true;
      if (loadDefaultView) {
        switchView(OCCURRENCES);
      }
    }
  }

  public void onClick(ClickEvent event) {
    Object sender = event.getSource();
    if (sender == signinLink) {
      switchView(SIGN_IN);
    } else if (sender == regLink) {
      switchView(REGISTER);
    } else if (sender == signOutLink) {
      signOutUser(true);
    } else if (sender == homeLink) {
      switchView(OCCURRENCES);
    } /*
       * else if (sender == changePassLink) { switchView(CHANGE_PASS); } else if
       * (sender == editCollaboratorsLink) { switchView(EDIT_COLLABORATORS); }
       */

  }

  public void onResize(ResizeEvent event) {
    // do nothing when window is resize

  }

  /**
   * Set the {@link Widget} to use as the title bar.
   * 
   * @param title the title widget
   */
  public void setTitleWidget(Widget title) {
    topPanel.setWidget(1, 0, title);

  }

  @Override
  protected void handleOnValueChange(String historyToken) {
    historyState.setHistoryToken(historyToken);
    String tabName = historyState.getHistoryParameters(UrlParam.TAB) + "";
    init(false);
    switchView(tabName);
  }

  /**
   * Always return true because ApplicationView is root view.
   */
  @Override
  protected boolean isMyView(String value) {
    // historyState.setHistoryToken(value);
    // String tabName = historyState.getHistoryParameters(UrlParam.TAB) + "";

    return true;
  }

  @Override
  protected void resetToDefaultState() {

  }
  
  protected OccurrenceView getOccurrenceView(){
	  ViewInfo viewInfo = viewInfos.get(OCCURRENCES.toLowerCase());
	  OccurrenceView occView = (OccurrenceView)viewInfo.getView();
	  return occView;
  }

  /**
   * Switches child view to the given view if the child view is not the same as
   * active view nor active view is null. When a view is switch change the links
   * to propriated links.
   * 
   * @see org.rebioma.client.View#switchView(java.lang.String, boolean)
   */
  @Override
  protected void switchView(String view, boolean addHistory) {
    view = view.toLowerCase();
    ViewInfo switchViewInfo = viewInfos.get(view);
    if (switchViewInfo == null) {
      switchViewInfo = viewInfos.get(OCCURRENCES.toLowerCase());
    } else {
      view = switchViewInfo.getHisTokenName();
    }
    if (activeViewInfo == null
        || !activeViewInfo.getHisTokenName().equalsIgnoreCase(view)) {
      tabPanel.clear();
      if (activeViewInfo != null
          && activeViewInfo.getHisTokenName().equals(OCCURRENCES)) {
        OccurrenceView oView = (OccurrenceView) activeViewInfo.getView();
        oView.setVisible(false);
      }
      //pour cacher les CustomPopupPanel (map et list view ) de OccurrenceView 
      ViewInfo occViewInfo = viewInfos.get(OCCURRENCES.toLowerCase());
      OccurrenceView occView = (OccurrenceView)occViewInfo.getView();
      occView.setVisible(false);
      //on en profite pour ajouter l'occurenceView comme OccurrenceSearchListener de speciesExplorerView
      ViewInfo spExpViewInfo = viewInfos.get(SPECIES_EXPLORER.toLowerCase());
      SpeciesExplorerView spExpView = (SpeciesExplorerView)spExpViewInfo.getView();
      spExpView.addOccurrenceSearchListener((OccurrenceSearchListener)occView);
      ViewInfo statViewInfo = viewInfos.get(STATS_TAB.toLowerCase());
      StatisticsTabView statsView = (StatisticsTabView) statViewInfo.getView();
      
     /* ViewInfo mailViewInfo = viewInfos.get(MAIL_TAB.toLowerCase());
      MailTabView mailView = (MailTabView) mailViewInfo.getView();*/
      
      List<ViewInfo> currentTabs = new ArrayList<ViewInfo>();
      int selectedTabIndex = 0;
      boolean isSignedIn = currentState != ViewState.UNAUTHENTICATED;
      String linksGroup = LinksGroup.HOME.toString() + isSignedIn;
      if (view.equalsIgnoreCase(OCCURRENCES)) {
        currentTabs.add(viewInfos.get(OCCURRENCES.toLowerCase()));
        currentTabs.add(viewInfos.get(SPECIES_EXPLORER.toLowerCase()));
        currentTabs.add(viewInfos.get(STATS_TAB.toLowerCase()));
        if (isSignedIn) {
          currentTabs.add(viewInfos.get(USER_PROFILES.toLowerCase()));
          // currentTabs.add(viewInfos.get(arg0))
        }
        if (isAdmin()) {
          currentTabs.add(viewInfos.get(USER_MANAGEMENT.toLowerCase()));
         // currentTabs.add(viewInfos.get(MAIL_TAB.toLowerCase()));
        }
      }else if(view.equalsIgnoreCase(SPECIES_EXPLORER)){
    	  currentTabs.add(viewInfos.get(OCCURRENCES.toLowerCase()));
          currentTabs.add(viewInfos.get(SPECIES_EXPLORER.toLowerCase()));
          currentTabs.add(viewInfos.get(STATS_TAB.toLowerCase()));
          if (isSignedIn) {
	          currentTabs.add(viewInfos.get(USER_PROFILES.toLowerCase()));
	          // currentTabs.add(viewInfos.get(arg0))
	        }
	        if (isAdmin()) {
	          currentTabs.add(viewInfos.get(USER_MANAGEMENT.toLowerCase()));
	        }
          
      } else if (view.equalsIgnoreCase(SIGN_IN)) {
        currentTabs.add(viewInfos.get(SIGN_IN.toLowerCase()));
        currentTabs.add(viewInfos.get(FORGET_PASS.toLowerCase()));
        linksGroup = LinksGroup.SIGN_IN + "";
      } else if (view.equalsIgnoreCase(CHANGE_PASS)) {
        currentTabs.add(viewInfos.get(CHANGE_PASS.toLowerCase()));
        linksGroup = LinksGroup.CHANGE_PASS + "";
      } else if (view.equalsIgnoreCase(REGISTER)) {
        currentTabs.add(viewInfos.get(REGISTER.toLowerCase()));
        linksGroup = LinksGroup.REGISTER + "";
      } else if (view.equalsIgnoreCase(FORGET_PASS)) {
        currentTabs.add(viewInfos.get(SIGN_IN.toLowerCase()));
        currentTabs.add(viewInfos.get(FORGET_PASS.toLowerCase()));
        linksGroup = LinksGroup.SIGN_IN + "";
        selectedTabIndex = 1;
      } else if (view.equalsIgnoreCase(USER_PROFILES)) {
        currentTabs.add(viewInfos.get(OCCURRENCES.toLowerCase()));
        //currentTabs.add(viewInfos.get(SPECIES_EXPLORER.toLowerCase()));
        currentTabs.add(viewInfos.get(USER_PROFILES.toLowerCase()));
        if (isAdmin()) {
          currentTabs.add(viewInfos.get(USER_MANAGEMENT.toLowerCase()));
        }
        selectedTabIndex = 1;
      } else if (view.equalsIgnoreCase(USER_MANAGEMENT)) {
        if (isAdmin()) {
          currentTabs.add(viewInfos.get(OCCURRENCES.toLowerCase()));
          //currentTabs.add(viewInfos.get(SPECIES_EXPLORER.toLowerCase()));
          currentTabs.add(viewInfos.get(USER_PROFILES.toLowerCase()));
          currentTabs.add(viewInfos.get(USER_MANAGEMENT.toLowerCase()));
          selectedTabIndex = 2;
        }
      }  else {
        currentTabs.add(viewInfos.get(OCCURRENCES.toLowerCase()));
        currentTabs.add(viewInfos.get(SPECIES_EXPLORER.toLowerCase()));
        //currentTabs.add(viewInfos.get(MAIL_TAB.toLowerCase()));
        selectedTabIndex = 1;

      }
      for (ViewInfo viewInfo : currentTabs) {
        addTab(viewInfo);
      }
      //exception IndexOutOfBoundsException
      tabPanel.selectTab(selectedTabIndex);
      activeViewInfo = switchViewInfo;
      links.showLinks(linksGroup);
      activeViewInfo.getView().onShow();
    }
    if (addHistory) {
      addHistoryItem(false);
    }
  }

  private void addTab(ViewInfo viewInfo) {
    // the dummy label is to ensure the application does not load unnecessary
    // view
    tabPanel.add(new Label(viewInfo.getHisTokenName()), viewInfo.getName());
  }

  private void addViewInfo(ViewInfo viewInfo) {
    viewInfos.put(viewInfo.getHisTokenName().toLowerCase(), viewInfo);
  }

  private void createTopPanel() {
    boolean isRTL = LocaleInfo.getCurrentLocale().isRTL();
    topPanel = new FlexTable();
    topPanel.setCellPadding(0);
    topPanel.setCellSpacing(0);
    topPanel.setStyleName(DEFAULT_STYLE_NAME + "-top");
    FlexCellFormatter formatter = topPanel.getFlexCellFormatter();

    // Setup the links cell
    linksPanel = new HorizontalPanel();
    topPanel.setWidget(0, 0, linksPanel);
    formatter.setStyleName(0, 0, DEFAULT_STYLE_NAME + "-links");
    formatter.setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
    if (isRTL) {
      formatter.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
    } else {
      formatter
          .setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_RIGHT);
    }
    formatter.setColSpan(0, 0, 2);

    // Setup the title cell
    setTitleWidget(null);
    formatter.setStyleName(1, 0, DEFAULT_STYLE_NAME + "-title");

    // Setup the options cell
    // setOptionsWidget(null);
    // formatter.setStyleName(1, 1, DEFAULT_STYLE_NAME + "-options");
    // if (isRTL) {
    // formatter.setHorizontalAlignment(1, 1,
    // HasHorizontalAlignment.ALIGN_LEFT);
    // } else {
    // formatter
    // .setHorizontalAlignment(1, 1, HasHorizontalAlignment.ALIGN_RIGHT);
    // }

    // Align the content to the top
    topPanel.getRowFormatter().setVerticalAlign(0,
        HasVerticalAlignment.ALIGN_TOP);
    topPanel.getRowFormatter().setVerticalAlign(1,
        HasVerticalAlignment.ALIGN_TOP);
  }

  private String getUrlToken(UrlParam urlParam) {
    String token = urlParam.lower() + "=";
    switch (urlParam) {
    case TAB:
      String tabView = OCCURRENCES.toLowerCase();
      if (activeViewInfo != null) {
        tabView = activeViewInfo.getHisTokenName().toLowerCase();
      }
      token += tabView;
      break;
    default:
      token = "";
      break;
    }
    return token;
  }

  /**
   * Updates the application when the user is authenticated. Sets a cookie,
   * updates the links, and updates TabContent state.
   * 
   * @param user the authenticated user
   */
  private void handleAuthenticatedUser(User user) {
    links.showLinks(LinksGroup.HOME + "true");
    links.showEmail(user.getEmail());
    sessionId = user.getSessionId();
    setAuthenticatedUser(user);
    refreshSession();
  }

  private void initLinks() {
    signinLink = new HTML(constants.SignIn());
    regLink = new HTML(constants.Register());
    signOutLink = new HTML(constants.SignOut());
    issuesLink = new HTML(CODE_SITE_URL);
    webPortalLink = new HTML(WEB_PORTAL_URL);
    acknowledgmentLink = new HTML(ACKNOWLEDGMENT);
    // changePassLink = new HTML(constants.ChangePassword());
    // editCollaboratorsLink = new HTML(constants.EditCollaborators());

    homeLink = new HTML(constants.Home());
    helpLink = new HTML(HELP_PAGE_URL);
    HTML version = new HTML(constants.Version() + " "
        + constants.VersionNumber());
    signinLink.setStyleName("link");
    regLink.setStyleName("link");
    signOutLink.setStyleName("link");
    // issuesLink.setStyleName("link");
    // changePassLink.setStyleName("link");
    homeLink.setStyleName("link");
    // editCollaboratorsLink.setStyleName("link");
    signinLink.addClickHandler(this);
    regLink.addClickHandler(this);
    signOutLink.addClickHandler(this);
    // changePassLink.addClickHandler(this);
    homeLink.addClickHandler(this);
    // editCollaboratorsLink.addClickHandler(this);

    // true is the key for authenticated user
    links.putLinks(LinksGroup.HOME.toString() + true, new Widget[] { version,
        signOutLink, webPortalLink, issuesLink, helpLink, acknowledgmentLink, locale });
    // false is the key for unauthenticated user
    links.putLinks(LinksGroup.HOME.toString() + false, new Widget[] { version,
        signinLink, regLink, webPortalLink, issuesLink, helpLink, acknowledgmentLink, locale });
    links.putLinks(LinksGroup.SIGN_IN + "", new Widget[] { version, homeLink,
        regLink, webPortalLink, issuesLink, helpLink, acknowledgmentLink, locale });
    links.putLinks(LinksGroup.CHANGE_PASS + "", new Widget[] { version,
        homeLink, signOutLink, webPortalLink, issuesLink, helpLink, acknowledgmentLink, locale });
    links.putLinks(LinksGroup.REGISTER + "", new Widget[] { version, homeLink,
        signinLink, webPortalLink, issuesLink, helpLink, acknowledgmentLink, locale });
    links.putLinks(LinksGroup.EDIT_COLLABORATORS + "", new Widget[] { version,
        homeLink, signOutLink, webPortalLink, issuesLink, helpLink, acknowledgmentLink, locale });
    switch (currentState) {
    case ADMIN:
    case REVIEWER:
    case RESEARCHER:
      links.showEmail(authenticatedUser.getEmail());
      break;
    case UNAUTHENTICATED:
      links.removeEmail();
      break;
    }
  }

  private void initViews() {
    ViewInfo occurrenveViewInfo = OccurrenceView.init(this, constants
        .Occurrences(), OCCURRENCES);
    
    ViewInfo signinViewInfo = new FormViewInfo(constants.SignIn(), SIGN_IN,
        this, new SignInForm(constants.SignInMsg(), new FormListener() {
          public void onSubmitFailed(Throwable t) {
            Window.alert(constants.SignInFailure());
          }

          public void onSubmitSuccess(Object result) {
            if (result == null || ((User) result).getEmail() == null) {
              Window.alert(constants.WrongEmailOrPass());
            } else {
              User user = (User) result;
              handleAuthenticatedUser(user);
              switchView(OCCURRENCES);
              fireOnStateChange(currentState);
              // pager.init();
              // View view = occurrenceViewInfo.getInstance();
              // historyListener.onHistoryChanged(view.getHistoryToken());
            }
          }
        }));

    ViewInfo registerViewInfo = new FormViewInfo(constants.Register(),
        REGISTER, this, new RegisterForm(constants.Register(),
            new FormListener() {
              public void onSubmitFailed(Throwable t) {
                if (t instanceof UserExistedException) {
                  Window.alert(t.getMessage() + " " + constants.EmailExisted());
                } else {
                  Window.alert(constants.UnableToRegister());
                }
                GWT.log(t.getMessage(), t);
              }

              /**
               * Note that History.newItem() is called here.
               */
              public void onSubmitSuccess(Object result) {
                String userMail = (String) result;
                if (result == null || userMail.length() < 0) {
                  Window.confirm(constants.UnableToRegister());
                } else {
                  if (Window
                      .confirm(constants.RegSuccessMsg() + " " + userMail)) {
                    History.newItem("");
                  }
                }
              }
            }));

    ViewInfo changePassViewInfo = new FormViewInfo(constants.ChangePassword(),
        CHANGE_PASS, this, new ChangePasswordForm(constants.ChangePassword(),
            new FormListener() {
              public void onSubmitFailed(Throwable t) {
                Window.alert(t.toString());

              }

              public void onSubmitSuccess(Object result) {
                Integer returnVal = (Integer) result;
                if (returnVal.equals(1)) {
                  Window.alert(constants.SuccessPassChangedMsg());
                  History.newItem("");
                } else if (returnVal.equals(0)) {
                  Window.alert(constants.WrongOldPassMsg());
                } else {
                  Window.alert(constants.UnexpectedError());
                }
              }
            }));

    ViewInfo forgetPassViewInfo = new FormViewInfo(constants.ForgotPassword(),
        FORGET_PASS, this, new ForgotPasswordForm(constants
            .RecoverPasswordMsg(), new FormListener() {
          public void onSubmitFailed(Throwable t) {
            Window.alert("Failed to reset password");
          }

          public void onSubmitSuccess(Object result) {
            Window.alert(constants.ResetPasswordSuccessfull());
          }
        }));

    editCollaboratorsViewInfo = UserProfilesView.init(this, constants
        .UserProfiles(), USER_PROFILES);
    userManagementViewInfo = UserManagementView.init(this, constants
        .UserManagement(), USER_MANAGEMENT);
    ViewInfo speciesExplorerViewInfo = SpeciesExplorerView.init(this, constants
    	.SpeciesExplorer(), SPECIES_EXPLORER);
    ViewInfo statsViewInfo = StatisticsTabView.init(this, constants
        	.Statistics(), STATS_TAB);
    addViewInfo(occurrenveViewInfo);
    addViewInfo(speciesExplorerViewInfo);
    addViewInfo(statsViewInfo);
    addViewInfo(forgetPassViewInfo);
    addViewInfo(signinViewInfo);
    addViewInfo(changePassViewInfo);
    addViewInfo(registerViewInfo);
    addViewInfo(editCollaboratorsViewInfo);
    addViewInfo(userManagementViewInfo);
  }

  /**
   * Returns true if there is a sessionId in the browser
   * 
   * @return true if the browser contains a sessionId
   */
  private boolean isSessionIdInBrowser() {
    String sessionId = Cookies.getCookie(SESSION_ID_NAME);
    return sessionId != null && !sessionId.equals("");
  }

  /**
   * Signs out the user on the server, invalidates the session, updates content
   * state, and shows the unauthenticated navigation links.
   * 
   * @param serverSignOut true to sign out the user on server as well.
   */
  private void signOutUser(boolean serverSignOut) {
    if (serverSignOut) {
      signOutOnServer();
    }
    Cookies.removeCookie(SESSION_ID_NAME);
    setAuthenticatedUser(null);
    // /currentState = ViewState.UNAUTHENTICATED;
    links.removeEmail();
    links.showLinks(LinksGroup.HOME.toString() + false);
    activeViewInfo = null;
    switchView(OCCURRENCES);
    fireOnStateChange(currentState);

    // HACK: UserProfilesView maintains a final User reference that is set when
    // initialized. Need to clobber that user when user logs out we
    // reinitializing the view.
    viewInfos.remove(editCollaboratorsViewInfo.getHisTokenName().toLowerCase());
    editCollaboratorsViewInfo = UserProfilesView.init(this, constants
        .UserProfiles(), USER_PROFILES);
    addViewInfo(editCollaboratorsViewInfo);
    // ((UserProfilesView) editCollaboratorsViewInfo.getView()).clearUser();
  }

  private void switchView(String view) {
    switchView(view, true);

  }

}
