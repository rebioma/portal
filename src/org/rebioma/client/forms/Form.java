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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rebioma.client.ApplicationView;
import org.rebioma.client.SourcesClickHandlers;
import org.rebioma.client.i18n.AppConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base class for forms. It's useful because it aggregates {@link FormInput}
 * widgets into a single widget and encapsulates common form features such as
 * validation.
 * 
 * Form validation works as follows: It listens to changes in {@link FormInput}
 * by implementing the {@link ChangeListener} interface. For each change, it
 * calls it's validation method and stores the result. When all form inputs are
 * valid, the submit button is enabled.
 * 
 */
public abstract class Form extends Composite implements SourcesClickHandlers,
        ChangeListener {

  /**
   * Interface for the form submission success and failure.
   * 
   */
  public interface FormListener {
    public void onSubmitFailed(Throwable t);

    public void onSubmitSuccess(Object result);
  }

  /**
   * Layout options for the form.
   */
  public enum Layout {
    HORIZONTAL, VERTICAL
  }

  /**
   * Helper class that handles layout of the submit and cancel buttons.
   * 
   */
  private class ButtonPanel extends HorizontalPanel {
    /**
     * Constructs a new button panel from a submit and cancel button.
     * 
     * @param submit the form submit button
     * @param cancel the form cancel button
     * 
     */
    public ButtonPanel(Button submit, Button cancel) {
      add(submit);
      setCellWidth(submit, "70px");
      setCellHorizontalAlignment(submit, HasHorizontalAlignment.ALIGN_LEFT);
      add(cancel);
      setCellHorizontalAlignment(cancel, HasHorizontalAlignment.ALIGN_LEFT);
    }
  }

  /**
   * The application constants.
   * 
   */
  protected final static AppConstants constants = ApplicationView
          .getConstants();;

  /**
   * The cancel button returns the user to the home page.
   * 
   */
  protected Button cancelButton = new Button(constants.Cancel());

  /**
   * The click listeners.
   * 
   */
  protected final List<ClickHandler> clickListeners = new ArrayList<ClickHandler>();

  protected List<FormListener> formListeners = new ArrayList<FormListener>();

  /**
   * The panel that contains the {@link FormInput} objects.
   * 
   */
  protected final VerticalPanel inputs = new VerticalPanel();

  /**
   * The main panel initialized via initWidget().
   * 
   */
  protected CellPanel mainPanel;

  /**
   * The submit button that sends the form data to the server.
   * 
   */
  protected Button submitButton = new Button(constants.Submit());

  /**
   * A mapping of {@link FormInput} objects to their current validation state
   * (true or false). If all {@link FormInput} objects are validated, the submit
   * button is enabled.
   * 
   */
  protected final Map<FormInput, Boolean> validatedInputs = new HashMap<FormInput, Boolean>();

  private final boolean validateForm;

  /**
   * Constructs a new form without a title.
   * 
   * @param validate
   * @param layout
   */
  public Form(boolean validate, Layout layout) {
    this(null, validate, layout, null);
  }

  /**
   * Constructs the new form.
   * 
   * @param name the form name
   * 
   */
  public Form(String name, boolean validate, Layout layout,
          FormListener listener) {
    validateForm = validate;
    formListeners.add(listener);
    switch (layout) {
    case VERTICAL:
      mainPanel = new VerticalPanel();
      break;
    case HORIZONTAL:
      mainPanel = new HorizontalPanel();
    }

    if (name != null) {
      Label title = new Label(name);
      mainPanel.add(title);
      mainPanel.setCellHeight(title, "1px");
    }

    mainPanel.setSpacing(5);
    mainPanel.add(inputs);
    mainPanel.setCellHeight(inputs, "70px");
    initWidget(mainPanel);

    // Enables and adds a click listener to the submit button:
    submitButton.setEnabled(!validateForm);
    submitButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        onSubmit();
      }
    });

    // Adds a click listener to the cancel button:
    cancelButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        onCancel();
      }
    });

    ButtonPanel b = new ButtonPanel(submitButton, cancelButton);
    b.addStyleName("rebioma-Form-buttons");
    mainPanel.add(b);
    mainPanel.setCellHorizontalAlignment(b, HasHorizontalAlignment.ALIGN_LEFT);
  }

  /**
   * Adds the {@link ClickHandler}.
   */
  public void addClickHandler(ClickHandler listener) {
    clickListeners.add(listener);
  }

  public void addFormListener(FormListener listener) {
    formListeners.add(listener);
  }

  /**
   * Clears all form input values.
   */
  public void clearInputs() {
    for (FormInput input : getInputs()) {
      input.clear();
    }
  }

  /**
   * Returns this form's {@link FormInput} objects in a set.
   * 
   */
  public Set<FormInput> getInputs() {
    Set<FormInput> set = new HashSet<FormInput>();
    for (int i = 0; i < inputs.getWidgetCount(); i++) {
      set.add((FormInput) inputs.getWidget(i));
    }
    return set;
  }

  /**
   * For each {@link FormInput} change, calls it's validation method and stores
   * the result. When all form inputs are valid, the submit button is enabled.
   * If the input enter key is pressed, onSubmit is programatically called.
   * 
   */
  public void onChange(Widget sender) {
    if (!(sender instanceof FormInput)) {
      return;
    }
    FormInput input = (FormInput) sender;

    if (!validateForm) {
      if (input.isEnterKeyPressed()) {
        onSubmit();
      }
      return;
    }

    boolean isValid = input.validate() == null ? true : false;

    // Updates the validation result for the form input:
    if (validatedInputs.containsKey(input)) {
      validatedInputs.remove(input);
    }
    validatedInputs.put(input, isValid);

    // Validates all form inputs:
    String error = validate();
    if (error != null) {
      submitButton.setEnabled(false);
    } else {
      submitButton.setEnabled(true);
      if (input.isEnterKeyPressed()) {
        onSubmit();
      }
    }
  }

  /**
   * Removes the {@link ClickHandler}.
   * 
   */
  public void removeClickHandler(ClickHandler listener) {
    clickListeners.remove(listener);
  }

  public void removeFormListener(FormListener listener) {
    formListeners.remove(listener);
  }

  /**
   * Adds a form input to this form.
   * 
   * @param input the form input to add
   * 
   */
  protected void addInput(FormInput input) {
    inputs.add(input);
    input.addChangeListener(this);
  }

  protected void fireOnSubmitFailed(Throwable t) {
    for (FormListener l : formListeners) {
      l.onSubmitFailed(t);
    }
  }

  protected void fireOnSubmitSuccess(Object result) {
    for (FormListener l : formListeners) {
      l.onSubmitSuccess(result);
    }
  }

  /**
   * This method is called when the cancel button is clicked.
   * 
   */
  protected abstract void onCancel();

  /**
   * This method is called when the submit button is clicked.
   * 
   */
  protected abstract void onSubmit();

  /**
   * Validates form input. Returns null if the form is valid, otherwise returns
   * the error message.
   * 
   */
  protected String validate() {
    if (validatedInputs.keySet().containsAll(getInputs())) {
      for (FormInput fi : getInputs()) {
        if (fi.validate() != null) {
          return fi.validate();
        }
      }
      return null;
    } else {
      return "";
    }
  }

}
