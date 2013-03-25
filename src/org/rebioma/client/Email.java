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

import org.rebioma.client.i18n.AppConstants;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * This class is used to create a multi-language email message
 * 
 * @author tri
 * 
 */
public class Email implements IsSerializable {
  // Variables that is generally used by every kind of email message
  private String subject;
  private String body;
  private String dear;
  private String userFirstName;
  private String sincerelyMsg;
  // Translation of the English phase: "Your user name: "
  private String yourUserName;

  public static final String REBIOMA_TEAM = "Rebioma Portal Team \n";

  // Variables for welcome email message only
  private String userEmail;

  private String userPassword;

  private String content;
  // Translation of the English phase: "your password: "
  private String yourNewPassword;

  /**
   * This method needs to be called before calling toString() to build this
   * email message's content.
   * 
   * Note: if the email content username or/and password, yourUserName,
   * userEmail, userPassword and yourNewPassword should be set before calling
   * this method
   */
  public void buildBody() {
    StringBuilder sb = new StringBuilder();
    sb.append(dear + " " + userFirstName + ", \n\n");
    sb.append(content + "\n\n");
    if (userEmail != null) {
      sb.append(yourUserName + " " + userEmail + "\n");
    }
    if (userPassword != null) {
      sb.append(yourNewPassword + " " + userPassword + "\n");
    }
    sb.append("\n" + sincerelyMsg + "\n" + REBIOMA_TEAM);
    body = sb.toString();
  }

  public String getBody() {
    return body;
  }

  public String getContent() {
    return content;
  }

  public String getDear() {
    return dear;
  }

  public String getSincerelyMsg() {
    return sincerelyMsg;
  }

  public String getSubject() {
    return subject;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public String getUserFirstName() {
    return userFirstName;
  }

  public String getUserPassword() {
    return userPassword;
  }

  public String getYourNewPassword() {
    return yourNewPassword;
  }

  public String getYourUserName() {
    return yourUserName;
  }

  /**
   * This method initializes all common fields for an email message: dear,
   * sincerelyMsg, yourNewPasssword, and yourUserName
   */
  public void initCommonFields() {
    AppConstants constants = ApplicationView.getConstants();
    dear = constants.Dear();
    sincerelyMsg = constants.Sincerely();
    yourNewPassword = constants.YourNewPass() + ":";
    yourUserName = constants.YourUserName();
  }

  public void setBody(String body) {
    this.body = body;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setDear(String dear) {
    this.dear = dear;
  }

  public void setSincerelyMsg(String sincerelyMsg) {
    this.sincerelyMsg = sincerelyMsg;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  public void setUserFirstName(String userFirstName) {
    this.userFirstName = userFirstName;
  }

  public void setUserPassword(String userPassword) {
    this.userPassword = userPassword;
  }

  public void setYourNewPassword(String yourNewPassword) {
    this.yourNewPassword = yourNewPassword;
  }

  public void setYourUserName(String yourUserName) {
    this.yourUserName = yourUserName;
  }

  @Override
  public String toString() {
    if (body == null) {
      throw new IllegalStateException(
              "call buildBody() before calling toString");
    }
    return body;
  }
}
