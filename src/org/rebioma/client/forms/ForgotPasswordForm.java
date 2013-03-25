package org.rebioma.client.forms;

import org.rebioma.client.ApplicationView;
import org.rebioma.client.DataSwitch;
import org.rebioma.client.Email;
import org.rebioma.client.i18n.AppConstants;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ForgotPasswordForm extends Form {

  /**
   * The application constants.
   * 
   */
  private final static AppConstants constants = ApplicationView.getConstants();

  /**
   * The email form input.
   * 
   */
  private final FormInput email = new EmailFormInput(constants.Email(), false);

  public ForgotPasswordForm(String name) {
    this(name, null);
  }

  public ForgotPasswordForm(String name, FormListener listener) {
    super(name, true, Layout.VERTICAL, listener);
    addInput(email);
  }

  @Override
  protected void onCancel() {
    History.newItem("");
  }

  @Override
  protected void onSubmit() {
    Email mail = createEmail();
    DataSwitch.get().resetUserPassword(email.getInputValue(), mail,
            new AsyncCallback() {
              public void onFailure(Throwable caught) {
                fireOnSubmitFailed(caught);
              }

              public void onSuccess(Object result) {
                fireOnSubmitSuccess(result);
              }
            });
  }

  private Email createEmail() {
    Email email = new Email();
    email.setSubject(constants.PasswordRecoverySubject());
    email.setContent(constants.RecoveryNotificationMsg());
    email.initCommonFields();
    return email;
  }

}
