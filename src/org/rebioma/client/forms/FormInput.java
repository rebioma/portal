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
package org.rebioma.client.forms;

import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ChangeListenerCollection;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SourcesChangeEvents;
import com.google.gwt.user.client.ui.SourcesKeyboardEvents;

/**
 * The base class for form input widgets.
 * 
 */
public abstract class FormInput extends Composite implements
        SourcesChangeEvents {

  /**
   * Returns true if the input is not null with a length > 0.
   * 
   * @param input text to check
   * 
   */
  protected final static boolean isEmpty(String input) {
    try {
      return input.length() > 0 ? false : true;
    } catch (Exception e) {
      return true;
    }
  }

  /**
   * Change listeners.
   * 
   */
  protected final ChangeListenerCollection changeListeners = new ChangeListenerCollection();

  /**
   * The main panel.
   * 
   */
  private final HorizontalPanel mainPanel = new HorizontalPanel();

  /**
   * The form input name label.
   * 
   */
  protected Label title;

  /**
   * The form element widget.
   * 
   */
  protected final FocusWidget widget;

  /**
   * The keyboard code of the most recently pressed key.
   */
  private int keyCode;

  /**
   * True if this input needs to be validated.
   */
  protected boolean validateInput;

  public FormInput(FocusWidget inputWidget, boolean validate) {
    this(null, inputWidget, validate);
  }

  /**
   * Constructs a new form input with the given title and widget. The widget
   * must implement {@link SourcesKeyboardEvents}.
   * 
   * @param inputTitle the title of this input
   * @param inputWidget form input that implements {@link SourcesKeyboardEvents}
   * 
   */
  public FormInput(String inputTitle, FocusWidget inputWidget, boolean validate) {
	    validateInput = validate;
    if (inputTitle != null) {
      title = new Label();
      title.setText(inputTitle + ":");
      title.setStyleName("rebioma-FormInput-title");
      mainPanel.add(title);
    }

    widget = inputWidget;
    widget.setStyleName("rebioma-FormInput-widget");
    mainPanel.setSpacing(10);
    mainPanel.add(inputWidget);
    initWidget(mainPanel);
    mainPanel.add(inputWidget);

    // Notifies change listeners when a character has been generated:
    inputWidget.addKeyUpHandler(new KeyUpHandler() {
      public void onKeyUp(KeyUpEvent event) {
        getThis().keyCode = event.getNativeKeyCode();
        changeListeners.fireChange(getThis());

      }
    });
  }

  /**
   * Adds the {@link ChangeListener}.
   * 
   */
  public void addChangeListener(ChangeListener listener) {
    changeListeners.add(listener);
  }

  /**
   * Clears input data.
   */
  public abstract void clear();

  /**
   * Returns the form element value.
   * 
   * @return the form element value
   */
  public abstract String getInputValue();

  /**
   * Returns true if the last key pressed was the enter key, false otherwise.
   * 
   */
  public boolean isEnterKeyPressed() {
    return keyCode == KeyCodes.KEY_ENTER;
  }

  /**
   * Removes the {@link ChangeListener}.
   * 
   */
  public void removeChangeListener(ChangeListener listener) {
    changeListeners.remove(listener);
  }

  /**
   * Sets the title width so that all of them line up in the form.
   * 
   * @param width width for the title
   * 
   */
  public void setTitleWidth(String width) {
    if (title != null) {
      mainPanel.setCellWidth(title, width);
    }
  }

  /**
   * Returns "form title: input value".
   * 
   */
  @Override
  public String toString() {
    return title.getText() + "'" + getInputValue() + "' ";
  }

  /**
   * Validates the form element. Returns null if valid, or an error message.
   * 
   * @return null if valid, or an error message
   * 
   */
  public abstract String validate();

  /**
   * This method is a quick check for a legal email format. It does not check
   * 100% of all legal emails, but it is good enough for this purpose.
   * 
   * @param email an email address.
   * @return true if the email is valid, false otherwise.
   */
  protected boolean isValidEmailFormat(String email) {
    return email
            .matches("[A-Za-z0-9_%+-][A-Za-z0-9._%+-]*@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
  }

  /**
   * Helper method that returns a reference to this {@link FormInput}.
   * 
   */
  private FormInput getThis() {
    return this;
  }

}
