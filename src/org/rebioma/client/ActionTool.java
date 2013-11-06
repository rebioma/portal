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

import java.util.HashMap;
import java.util.Map;

import org.rebioma.client.i18n.AppConstants;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A widget that manage all actions.
 * 
 * @author tri
 * 
 */
public abstract class ActionTool extends Composite {

  private final AppConstants constants = ApplicationView.getConstants();
  private final HTML allLink;
  private final HTML noneLink;
  private final ListBox actionListBox;
  private final Map<String, Command> actionMap = new HashMap<String, Command>();
  private static final String DEFAULT_STYLE = "Action-Tool";
  private int defaultSelection = -1;
  private final HorizontalPanel mainHp = new HorizontalPanel();

  public ActionTool() {
    this(true);
  }

  public ActionTool(boolean showedActionList) {
    SimplePanel sp = new SimplePanel();
    sp.setStyleName(DEFAULT_STYLE);

    sp.setWidget(mainHp);
    initWidget(sp);
//    sp.setWidth("100%");
    allLink = new HTML(constants.All());
    noneLink = new HTML(constants.None());
    allLink.setStyleName("link");
    noneLink.setStyleName("link");
    actionListBox = new ListBox();
    Label selectLabel = new Label(constants.Select() + ":");
    addWidget(selectLabel);
    addWidget(allLink);
    addWidget(noneLink);
    if (showedActionList) {
      addWidget(actionListBox);
    }
    addAction(constants.SelectAnAction(), null);
    allLink.addClickHandler(new ClickHandler() {
      /**
       * Calls {@link ActionTool#setCheckedAll(boolean)} when check all is
       * clicked
       */
      public void onClick(ClickEvent event) {
        setCheckedAll(true);

      }

    });

    noneLink.addClickHandler(new ClickHandler() {
      /**
       * Calls {@link ActionTool#setCheckedAll(boolean)} when check none is
       * clicked
       */
      public void onClick(ClickEvent sender) {
        setCheckedAll(false);
      }

    });

    actionListBox.addChangeHandler(new ChangeHandler() {
      /**
       * Execute the command of associated with the selected item and select the
       * action list box to its default selected index
       */
      public void onChange(ChangeEvent sender) {
        String currentSelectText = actionListBox.getItemText(actionListBox.getSelectedIndex())
            .trim();
        Command command = actionMap.get(currentSelectText);
        if (command != null) {
          command.execute();
        }
        if (defaultSelection >= 0) {
          actionListBox.setSelectedIndex(defaultSelection);
        }

      }

    });
  }

  /**
   * Add an action list box. Command
   * 
   * @param action String name of the action
   * @param command that will be executed when the action is clicked.
   * @return true if action is added.
   */
  public boolean addAction(String action, Command command) {
    action = action.trim();
    if (!actionMap.containsKey(action)) {
      actionMap.put(action, command);
      actionListBox.addItem(action);
      return true;
    }
    return false;
  }

  /**
   * Adds a widget to the action tool.
   * 
   * @param w widget to be added
   */
  public void addWidget(Widget w) {
    mainHp.add(new HTML("&nbsp;"));
    mainHp.add(w);
    mainHp.setCellVerticalAlignment(w, HasVerticalAlignment.ALIGN_MIDDLE);
  }

  /**
   * Clear all item in actions in action list box;
   */
  public void clear() {
    actionListBox.clear();
    actionMap.clear();
  }

  /**
   * Gets default selection of the action box.
   * 
   * @return the default selection of the action box.
   */
  public int getDefaultSelection() {
    return defaultSelection;
  }

  /**
   * remove the given action from action list box and the command map associate
   * with it.
   * 
   * @param action name of action in action list box.
   * @return true if the action is removed, false if the action can't be removed
   *         of it is not existed.
   */
  public boolean removeAction(String action) {
    int actionIndex = getActionIndex(action);
    if (actionIndex < 0) {
      return false;
    }
    actionListBox.removeItem(actionIndex);
    return actionMap.remove(action) != null;
  }

  /**
   * Sets default selection of the action box.
   * 
   * Note: set a positive integer for returning to default select item of the
   * {@link #actionListBox}
   * 
   * @param defaultSelection integer.
   */
  public void setDefaultSelection(int defaultSelection) {
    this.defaultSelection = defaultSelection;
  }

  /**
   * Gets called when All link of None link is clicked.
   * 
   * @param checked true if All is clicked, false if None is clicked.
   */
  protected abstract void setCheckedAll(boolean checked);

  /**
   * Gets the index of the given action in action list box
   * 
   * @param action name of the action
   * @return index of the given action, -1 if action can't be founded.
   */
  private int getActionIndex(String action) {
    for (int index = 0; index < actionListBox.getItemCount(); index++) {
      if (action.equals(actionListBox.getItemText(index))) {
        return index;
      }
    }
    return -1;
  }

}
