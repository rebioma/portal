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

import com.google.gwt.user.client.ui.TextBox;

/**
 * An email {@link FormInput}.
 * 
 */
public class EmailFormInput extends FormInput {

  /**
   * Constructs a new email form input,
   * 
   * @param inputTitle the email form title
   * 
   */
  public EmailFormInput(String inputTitle, boolean validate) {
    super(inputTitle, new TextBox(), validate);
  }

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
    if (validateInput) {
      return isValidEmailFormat(getInputValue()) ? null : "";
    }
    return null;
  }

}
