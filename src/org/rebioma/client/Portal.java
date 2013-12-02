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
import java.util.Date;

import org.form.client.api.DisplayPopup;
import org.rebioma.client.bean.User;
import org.rebioma.client.i18n.AppConstants;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.LoadApi;
import com.google.gwt.maps.client.LoadApi.LoadLibrary;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * The Portal class serves at the GWT {@link EntryPoint} to the application. It
 * manages the {@link Application} and also provides history support.
 */
public class Portal implements EntryPoint {

  /**
   * The application constants used for internationalization.
   */
  private static final AppConstants constants = ApplicationView.getConstants();

  /**
   * The application image bundle.
   */
  private static final AppImages images = GWT.create(AppImages.class);

  /**
   * Static code that adds an uncaught exception handler.
   */
  static {
    GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
      public void onUncaughtException(Throwable e) {
        GWT.log(e.getMessage(), e);
        StringBuilder sp = new StringBuilder();
        for (StackTraceElement stackTrace : e.getStackTrace()) {
          sp.append("file:" + stackTrace.getFileName() + " at "
              + stackTrace.getMethodName() + ":" + stackTrace.getLineNumber());
        }
        Window.confirm("Unexpected error: " + e.getMessage() + "\n" + sp);
        e.printStackTrace();
      }
    });
  }

  // private final PrintView printView = new PrintView();

  public static native String browserDetect() /*-{
    var browser=navigator.appName;
    var b_version=navigator.appVersion; 
    var version=parseFloat(b_version);

    return browser;
  }-*/;

  public void onModuleLoad() {
//	  loadMapApi();
    // Uncomment this to test new view stuff:
    DisplayPopup.setCloseImageUrl("images/xclose.gif");
    Window.enableScrolling(false);
    HorizontalPanel hp = new HorizontalPanel();
    hp.add(new Label(constants.LoadingUser()));
    hp.setWidth("100%");
    hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    RootPanel.get().add(hp);
    checkCurrentSession();
    // Window.confirm(browserDetect());

  }
  
  private void loadMapApi() {
	  boolean sensor = true;

	    // load all the libs for use in the maps
	    ArrayList<LoadLibrary> loadLibraries = new ArrayList<LoadApi.LoadLibrary>();
	    loadLibraries.add(LoadLibrary.ADSENSE);
	    loadLibraries.add(LoadLibrary.DRAWING);
	    loadLibraries.add(LoadLibrary.GEOMETRY);
	    loadLibraries.add(LoadLibrary.PANORAMIO);
	    loadLibraries.add(LoadLibrary.PLACES);
	    loadLibraries.add(LoadLibrary.WEATHER);
	    loadLibraries.add(LoadLibrary.VISUALIZATION);

	    Runnable onLoad = new Runnable() {
	      @Override
	      public void run() {
	      }
	    };

	    LoadApi.go(onLoad, loadLibraries, sensor);
  }

  /**
   * Checks the current session via RPC. Updates application state. Updates all
   * tab widgets with new application state.
   */
  private void checkCurrentSession() {
	  String sign = Window.Location.getParameter("signinc");
	  if(sign!=null){
		  String email = Window.Location.getParameter("emailc");
		  final String id = Window.Location.getParameter("id");
		  final String dev = Window.Location.getParameter("gwt.codesvr")==null?
				  "":"Portal.html?gwt.codesvr="+Window.Location.getParameter("gwt.codesvr");
		  Cookies.removeCookie(ApplicationView.SESSION_ID_NAME);
		  DataSwitch.get().signInC(email, sign, new AsyncCallback<User>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(User result) {
				// TODO Auto-generated method stub
				if(result.getSessionId()==null)initApplication(null);
				else{
					Cookies.setCookie(ApplicationView.SESSION_ID_NAME, result.getSessionId(),new Date(System
				          .currentTimeMillis() + 86400000));
					Window.Location.replace(GWT.getHostPageBaseURL()+dev+"#tab=occ&view=Detail&id="+id+"&p=false&page=1&asearch=Id = "+id+"&type=all occurrences");
				}
			}
		});
	}else if (!isSessionIdInBrowser()) {
      // links.showLinks(HOME_UNAUTHENTICATED_LINKS);
      initApplication(null);
      return;
    } else {
      String sid = Cookies.getCookie(ApplicationView.SESSION_ID_NAME);
      DataSwitch.get().isSessionIdValid(sid, new AsyncCallback<User>() {
        public void onFailure(Throwable caught) {
          GWT.log(caught.getMessage(), caught);
          initApplication(null);
        }

        public void onSuccess(User result) {
          if (result != null) {
            ApplicationView.setSessionId(result.getSessionId());
            ApplicationView.refreshSession();
          }
          initApplication(result);
        }
      });
    }
  }

  @SuppressWarnings("deprecation")
private void initApplication(User user) {
    ApplicationView.setAuthenticatedUser(user);
    // Make sure the server is notify when the session is longer valid when
    // load.
    if (user == null) {
      ApplicationView.signOutOnServer();
    }
    // ApplicationView.setCurrentState(user == null ? ViewState.UNAUTHENTICATED
    // : ViewState.RESEARCHER);
    Image logo = images.rebiomaLogoSmall().createImage();
    logo.addStyleName("rebioma-logo");
    ApplicationView appView = ApplicationView.getApplication();
    appView.setTitleWidget(logo);
    History.addValueChangeHandler(appView);
    if (History.getToken().length() > 0) {
        History.fireCurrentHistoryState();
    } else {
        appView.init(true);
    }
    RootPanel.get().clear();
    RootPanel.get().add(appView);
    
  }

  /**
   * Returns true if there is a sessionId in the browser
   * 
   * @return true if the browser contains a sessionId
   */
  private boolean isSessionIdInBrowser() {
    String sessionId = Cookies.getCookie(ApplicationView.SESSION_ID_NAME);
    return sessionId != null && !sessionId.equals("");
  }
}
