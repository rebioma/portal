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
import org.rebioma.client.i18n.AppConstants;
import org.rebioma.client.services.UserService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The form for changing a password.
 * 
 */
public class ChangePasswordForm extends Form {

  /**
   * The application constants.
   * 
   */
  private final static AppConstants constants = ApplicationView.getConstants();

  /**
   * The password form input.
   * 
   */
  private final FormInput password = new PasswordFormInput(constants
          .OldPassword(), true);

  /**
   * The new password form input.
   * 
   */
  private final FormInput newPassword = new PasswordFormInput(constants
          .NewPass(), true);

  /**
   * The new password confirmed form input.
   * 
   */
  private final FormInput newPasswordConfirmed = new PasswordFormInput(
          constants.ConfirmNewPass(), true);

  public ChangePasswordForm(String name) {
    this(name, null);
  }

  public ChangePasswordForm(String name, FormListener listener) {
    super(name, false, Layout.VERTICAL, listener);
    addInput(password);
    addInput(newPassword);
    addInput(newPasswordConfirmed);
  }

  /**
   * Redirects the user to the application home page.
   */
  @Override
  protected void onCancel() {
    History.back();
  }

  /**
   * Uses {@link DataSwitch} to invoke {@link UserService}.
   * 
   */
  @Override
  protected void onSubmit() {
    String newPassValue = newPassword.getInputValue();
    String confirmPassValue = newPasswordConfirmed.getInputValue();
    if (!newPassValue.equals(confirmPassValue)) {
      Window.alert(constants.UnmatchPassMsg());
      return;
    }
    Email email = createEmail();
    String sessionId = Cookies.getCookie(ApplicationView.SESSION_ID_NAME);
    DataSwitch.get().changeUserPassword(password.getInputValue(),
            newPassword.getInputValue(), sessionId, email,
            new AsyncCallback<Integer>() {
              public void onFailure(Throwable caught) {
                fireOnSubmitFailed(caught);
              }

              public void onSuccess(Integer result) {
                fireOnSubmitSuccess(result);
              }
            });
  }

  /**
   * Makes sure that the new password matches the confirmed to password.
   * 
   */
  @Override
  protected String validate() {
    String error = super.validate();
    if (error != null) {
      return error;
    } else {
      return newPassword.getInputValue().equals(
              newPasswordConfirmed.getInputValue()) ? null : " ";
    }
  }

  /**
   * Creates and returns an email confirmation for password change.
   * 
   */
  private Email createEmail() {
    Email email = new Email();
    email.setSubject(constants.ChangePassEmailSubject());
    email.setContent(constants.PassChangedNotification() + "\n\n"
            + constants.MaybeCompromisedMsg() + " " + GWT.getHostPageBaseURL()
            + Window.Location.getQueryString() + "#tab=ForgetPass");
    email.setYourNewPassword(constants.YourNewPass());
    email.setYourUserName(constants.YourUserName());
    email.setSincerelyMsg(constants.Sincerely());
    email.setDear(constants.Dear());
    return email;
  }
}