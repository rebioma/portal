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

import org.rebioma.client.ApplicationView;
import org.rebioma.client.DataSwitch;
import org.rebioma.client.Email;
import org.rebioma.client.PopupMessage;
import org.rebioma.client.bean.User;
import org.rebioma.client.i18n.AppConstants;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextBox;

/**
 * The user registration form is used to collected information from user input
 * and submit it to the server.
 * 
 */
public class RegisterForm extends Form {

  /**
   * Application constants.
   * 
   */
  private static AppConstants constants = ApplicationView.getConstants();

  /**
   * The first name {@link TextBox} input. Any non-empty input values are valid.
   * 
   */
  FormInput first = new FormInput(constants.FirstName(), new TextBox(), true) {
    @Override
    public void clear() {
      ((TextBox) widget).setText("");
    }

    @Override
    public String getInputValue() {
      return ((TextBox) widget).getText();
    }

    @Override
    public String validate() {
      return isEmpty(getInputValue()) ? " " : null;
    }
  };

  /**
   * The last name {@link TextBox} input. Any non-empty input values are valid.
   * 
   */
  FormInput last = new FormInput(constants.LastName(), new TextBox(), true) {
    @Override
    public void clear() {
      ((TextBox) widget).setText("");
    }

    @Override
    public String getInputValue() {
      return ((TextBox) widget).getText();
    }

    @Override
    public String validate() {
      return isEmpty(getInputValue()) ? " " : null;
    }
  };

  /**
   * The email {@link TextBox} input. Only valid email address formats are
   * valid.
   * 
   */
  FormInput email = new EmailFormInput(constants.Email(), true);
  FormInput confirmEmail = new EmailFormInput(constants.Confirm() + " "
          + constants.Email(), true) {
    @Override
    public String validate() {
      return email.getInputValue().equals(confirmEmail.getInputValue()) ? null
              : "";

    }
  };

  /**
   * The institution {@link TextBox} input.
   * 
   */
  FormInput institution = new FormInput(constants.Institution(), new TextBox(),
          true) {
    @Override
    public void clear() {
      ((TextBox) widget).setText("");
    }

    @Override
    public String getInputValue() {
      return ((TextBox) widget).getText();
    }

    @Override
    public String validate() {
      return isEmpty(getInputValue()) ? " " : null;
    }
  };

  /**
   * Constructs the form.
   * 
   * @param name the name of the form
   */
  public RegisterForm(String name) {
    this(name, null);
  }

  /**
   * Constructs the form.
   * 
   * @param name the name of the form
   */
  public RegisterForm(String name, FormListener listener) {
    super(name, true, Layout.VERTICAL, listener);
    addInput(first);
    addInput(last);
    addInput(email);
    addInput(confirmEmail);
    addInput(institution);
    
  }

  @Override
  public void addInput(FormInput input) {
    super.addInput(input);
  }

  /**
   * Redirects user to the home page.
   * 
   */
  @Override
  protected void onCancel() {
    History.newItem("");
  }

  @Override
  protected void onSubmit() {
	PopupMessage.getInstance().showMessage("loading...");
    final User user = createUser();
    Email email = createWelcomeEmail(user.getEmail());
    DataSwitch.get().register(user, email, new AsyncCallback<Boolean>() {
      public void onFailure(Throwable caught) {
        fireOnSubmitFailed(caught);
      }

      public void onSuccess(Boolean result) {
        fireOnSubmitSuccess(user.getEmail());
      }
    });
  }

  /**
   * Returns a new {@link User} created from input values in the form.
   * 
   */
  private User createUser() {
    User user = new User();
    user.setFirstName(first.getInputValue());
    user.setLastName(last.getInputValue());
    user.setEmail(email.getInputValue());
    user.setInstitution(institution.getInputValue());
    return user;
  }

  /**
   * Returns a new welcome {@link Email}.
   * 
   * @param userEmail
   * 
   */
  private Email createWelcomeEmail(String userEmail) {
    Email email = new Email();
    email.initCommonFields();
    email.setUserEmail(userEmail);
    email.setSubject(constants.WelcomeEmailSubject());
    email.setUserFirstName(first.getInputValue());
    email.setContent(constants.EmailWelcomeMsg());
    email.setSincerelyMsg(constants.Sincerely());
    return email;
  }

}
