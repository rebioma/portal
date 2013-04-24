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

import org.rebioma.client.View.ViewStateChangeListener;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;

/**
 * A type of view that is intended to be wrapped by a parent view.
 * 
 * When changes to a component view require a new item on the {@link History}
 * stack, it's not sufficient for it to simply add the history item since there
 * are other views wrapped by the parent whose state needs to also be
 * represented in the history token.
 * 
 * To solve this problem, the component view simply asks it's parent to add a
 * history item instead. The parent then constructs a single history token
 * representing all wrapped views, and then that token is added to the history
 * stack.
 */
public abstract class ComponentView extends View implements
        ViewStateChangeListener {
  View parent;
  protected static final String MAP = "Map";
  protected static final String LIST = "List";
  protected static final String DETAIL = "Detail";
  protected static final String UPLOAD = "Upload";
  protected static final String ADVANCE = "Advance";
  protected final boolean isDefaultView;
  /**
   * The common style name for all views.
   */
  protected static final String STYLE_NAME = "View-Content content";

  public ComponentView(View parent, boolean isDefaultView) {
    this.parent = parent;
    this.isDefaultView = isDefaultView;
  }

  /**
   * Calls parent addHistoryItem() if historyButtonClicked is true which mean
   * refresh, back, or forward browser button is clicked.
   * 
   * @see org.rebioma.client.View#addHistoryItem(boolean)
   */

  public void addHistoryItem(boolean issueEvent) {
    if (!parent.historyButtonClicked && !historyButtonClicked) {
      parent.addHistoryItem(issueEvent);
    }
    parent.historyButtonClicked = false;
    historyButtonClicked = false;
  }

  /**
   * Override this method is this view have to construct its own history token
   */

  public String historyToken() {
    return "";
  }

  /**
   * Gets called when browser size changed.
   */
  public void onResize(ResizeEvent event) {
    if (isMyView(History.getToken())) {
      resize(event.getWidth(), event.getHeight());
    }
  }

  /**
   * Called before the view is displayed.
   */
  public void onShow() {
    DeferredCommand.addCommand(new Command() {
      public void execute() {
        if (isMyView(History.getToken())) {
          resize(Window.getClientWidth(), Window.getClientHeight());
        }
      }

    });
    if (isMyView(History.getToken())) {
      resize(Window.getClientWidth(), Window.getClientHeight());
    }
  }

  /**
   * Override this method is this view need to handle onStateChange
   * {@link ViewStateChangeListener}.
   */

  public void onStateChanged(ViewState state) {

  }

  boolean isDefaultView() {
    return isDefaultView;
  }

  protected void addStateChangeListener(ViewStateChangeListener listener) {
    parent.addStateChangeListener(listener);
    // if (parent instanceof ComponentView) {
    // ((ComponentView) parent).addStateChangeListener(listener);
    // } else {
    // super.addStateChangeListener(listener);
    // }
  }

  /**
   * Override this method is this view need to handle history changed.
   */
  protected void handleOnValueChange(String historyToken) {

  }

  /**
   * Override this method is this view need to handle history changed.
   */
  protected boolean isMyView(String value) {
    return false;
  }

  /**
   * Override this method to handle on browser window resize instead of
   * {@link #onResize(ResizeEvent)}
   * 
   * @param width
   * @param height
   */
  protected void resize(int width, int height) {

  }
}
