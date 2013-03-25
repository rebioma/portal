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

import com.google.gwt.user.client.ui.PasswordTextBox;

/**
 * A password {@link FormInput} that if required, validates input using the
 * following password rules:
 * 
 * <pre>
 * Must be 6-16 characters. 
 * Alphanumeric, hyphen(-), and underscore(_) allowed.
 * Must contain both numbers and letters.
 * </pre>
 * 
 */
public class PasswordFormInput extends FormInput {
  /**
   * The regular expression that passwords must match.
   * 
   */
  private final static String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9_-]{6,24}$";

  /**
   * Constructs a new password form input.
   * 
   * @param inputTitle form title
   * @param validate if true, validate the input
   */
  public PasswordFormInput(String inputTitle, boolean validate) {
    super(inputTitle, new PasswordTextBox(), validate);
    validateInput = validate;
  }

  @Override
  public void clear() {
    ((PasswordTextBox) widget).setText("");
  }

  @Override
  public String getInputValue() {
    return ((PasswordTextBox) widget).getText();
  }

  @Override
  public String validate() {
    if (validateInput) {
      return getInputValue().matches(PASSWORD_REGEX) ? null : " ";
    }
    return null;
  }
}
