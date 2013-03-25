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

import org.rebioma.client.DataSwitch;
import org.rebioma.client.bean.User;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The user sign-in form has input for an email and password.
 * 
 */
public class SignInForm extends Form {

  /**
   * The email form input.
   * 
   */
  private final FormInput email = new EmailFormInput(constants.Email(), false);

  /**
   * The password form input that doesn't validate the password.
   * 
   */
  private final FormInput password = new PasswordFormInput(
          constants.Password(), false);

  /**
   * Constructs the form with a link to recover a forgotten password.
   * 
   * @param name the form name
   * 
   */
  public SignInForm(String name) {
    this(name, null);
  }

  public SignInForm(String name, FormListener listener) {
    super(name, false, Layout.VERTICAL, listener);
    addInput(email);
    addInput(password);
  }

  /**
   * Redirects to the application home page.
   */
  @Override
  protected void onCancel() {
    // History.newItem("");
  }

  /**
   * Submits a sign in request to {@link DataSwitch}. Failures result in an
   * alert box with the message. Success sets the {@link Application}
   * authenticated user, sets the session id cookie, and redirects immediately
   * to the home page.
   */
  @Override
  protected void onSubmit() {
    DataSwitch.get().signIn(email.getInputValue(), password.getInputValue(),
            new AsyncCallback<User>() {
              public void onFailure(Throwable caught) {
                fireOnSubmitFailed(caught);
              }

              public void onSuccess(User result) {
                fireOnSubmitSuccess(result);
              }
            });
  }
}
