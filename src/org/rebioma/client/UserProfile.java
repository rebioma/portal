package org.rebioma.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.rebioma.client.bean.Role;
import org.rebioma.client.bean.User;
import org.rebioma.client.i18n.AppConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;

/**
 * A widget to view and change user profiles such as email, first name, last
 * name, institution and password.
 * 
 * @author Tri
 * 
 */
public class UserProfile extends Composite implements ClickHandler,
    MouseOutHandler, MouseOverHandler, BlurHandler, KeyUpHandler {

  private static class RoleAssignment extends Composite implements
      ClickHandler, MouseOutHandler, MouseOverHandler {
    ListBox userRoles;
    ListBox assignableRoles;
    Button addButton;
    Button removeButton;
    Button applyButton;
    User user;
    HTML displayRoles;
    SimplePanel mainSp = new SimplePanel();
    VerticalPanel assignmentPanel = null;

    public RoleAssignment() {
      displayRoles = new HTML("");
      displayRoles.addStyleName("out");
      mainSp.setWidget(displayRoles);
      initWidget(mainSp);
      displayRoles.setStyleName("out");
      displayRoles.addClickHandler(this);
      displayRoles.addMouseOutHandler(this);
      displayRoles.addMouseOverHandler(this);
    }

    public void done() {
      if (applyButton != null) {
        applyButton.click();
      }

    }

    public void onClick(ClickEvent event) {
      Object source = event.getSource();
      if (source == addButton) {
        transfer(assignableRoles, userRoles);
      } else if (source == removeButton) {
        transfer(userRoles, assignableRoles);
      } else if (source == applyButton) {
        mainSp.setWidget(displayRoles);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < userRoles.getItemCount(); i++) {
          sb.append(", " + userRoles.getItemText(i));
        }
        sb.delete(0, 2);
        displayRoles.setHTML(sb.toString());
        displayRoles.addClickHandler(this);
        displayRoles.addMouseOutHandler(this);
        displayRoles.addMouseOverHandler(this);
      } else if (source == displayRoles && ApplicationView.isAdmin()) {
        initAssignmentPanel();
        mainSp.setWidget(assignmentPanel);
      }
    }

    public void onMouseOut(MouseOutEvent event) {
      Object source = event.getSource();
      if (source == displayRoles && ApplicationView.isAdmin()) {
        displayRoles.removeStyleName("hover");
        displayRoles.addStyleName("out");
      }

    }

    public void onMouseOver(MouseOverEvent event) {
      Object source = event.getSource();
      if (source == displayRoles && ApplicationView.isAdmin()) {
        displayRoles.removeStyleName("out");
        displayRoles.addStyleName("hover");
      }

    }

    void loadUser() {
      if (user != null) {
        Set<String> roleNames = new TreeSet<String>(availableRoles.keySet());
        for (Role role : user.getRoles()) {
          String roleName = role.getName(ApplicationView.getCurrentLocale());
          userRoles.addItem(roleName, role.getNameEn());
          roleNames.remove(roleName);
        }
        for (String roleName : roleNames) {
          if(!availableRoles.get(roleName).getNameEn().contains("super"))
        	  assignableRoles.addItem(roleName, availableRoles.get(roleName).getNameEn());
        }

      }
    }

    void setUser(User user) {
      this.user = user;
      displayRoles.setHTML("");
      if (user != null) {
        for (Role role : user.getRoles()) {
          String roleName = role.getName(ApplicationView.getCurrentLocale());
          String rs = displayRoles.getHTML().trim();
          if (rs.equals("")) {
            rs = roleName;
          } else {
            rs += ", " + roleName;
          }
          displayRoles.setHTML(rs);
        }
        if (displayRoles.getHTML().trim().equals("")) {
          displayRoles.setHTML(constants.None());
        }
      } else {
        displayRoles.setHTML("");
      }
    }

    private List<Integer> getCurrentSelects(ListBox listBox) {
      List<Integer> selectedIndexes = new ArrayList<Integer>();
      for (int i = 0; i < listBox.getItemCount(); i++) {
        if (listBox.isItemSelected(i)) {
          selectedIndexes.add(i);
        }
      }

      return selectedIndexes;
    }

    private void initAssignmentPanel() {
      if (assignmentPanel == null) {
        assignmentPanel = new VerticalPanel();
        HorizontalPanel rolesPanel = new HorizontalPanel();
        displayRoles = new HTML("");
        userRoles = new ListBox(true);
        assignableRoles = new ListBox(true);
        userRoles.setVisibleItemCount(availableRoles.size());
        assignableRoles.setVisibleItemCount(availableRoles.size());
        addButton = new Button("&laquo;");
        removeButton = new Button("&raquo;");
        rolesPanel.add(userRoles);
        VerticalPanel buttons = new VerticalPanel();
        buttons.add(addButton);
        buttons.add(removeButton);
        rolesPanel.add(buttons);
        rolesPanel.add(assignableRoles);
        rolesPanel.setSpacing(5);
        assignmentPanel.add(rolesPanel);
        applyButton = new Button(constants.Done());
        assignmentPanel.add(applyButton);
        assignmentPanel.setCellHorizontalAlignment(applyButton,
            VerticalPanel.ALIGN_CENTER);
        addButton.addClickHandler(this);
        removeButton.addClickHandler(this);
        applyButton.addClickHandler(this);
        loadUser();
      }
    }

    private void transfer(ListBox from, ListBox to) {
      List<Integer> selectedIndexes = new ArrayList<Integer>();
      for (int i = 0; i < from.getItemCount(); i++) {
        if (from.isItemSelected(i)) {
          selectedIndexes.add(i);
          to.addItem(from.getItemText(i), from.getValue(i));
        }
      }
      for (Integer index : selectedIndexes) {
        from.removeItem(index);
      }
    }
  }

  private static UserProfile userProfile = null;

  private static Map<String, Role> availableRoles = null;

  private static final AppConstants constants = ApplicationView.getConstants();

  static {
    initRoles();
  }

  public static UserProfile getUserProfile(User user) {
    if (userProfile == null) {
      userProfile = new UserProfile();
    }
    userProfile.loadUser(user);
    return userProfile;
  }

  private static void initRoles() {
    if (availableRoles == null) {
      availableRoles = new TreeMap<String, Role>();
      DataSwitch.get().getAvailableRoles(new AsyncCallback<List<Role>>() {

        public void onFailure(Throwable t) {
          GWT.log(t.getMessage(), t);
          availableRoles = null;
        }

        public void onSuccess(List<Role> result) {
          for (Role role : result) {
            availableRoles.put(
                role.getName(ApplicationView.getCurrentLocale()), role);
          }
        }

      });
    }
  }

  private User user;
  private final TextBox editUserProfiles = new TextBox();
  private final HTML email;
  private final HTML firstName;
  private final HTML lastName;
  private final HTML institution;
  private final Grid userProfileTable;
  private final VerticalPanel mainPanel = new VerticalPanel();
  private final HTML changePassLink = new HTML(constants.ChangePassword());
  private final Button saveButton = new Button(constants.SaveChanges());
  private final Button cancelButton = new Button(constants.Cancel());
  private PasswordTextBox oldPass = null;
  private PasswordTextBox newPass = null;
  private PasswordTextBox confirmPass = null;
  private HTML currentEditing = null;
  private int currentEditingRow = -1;
  private final RoleAssignment roleAssignment;

  /**
   * Initialize UserProfile with user email, first name, last name and
   * institution.
   * 
   * @param user
   */
  public UserProfile() {
    email = new HTML("");
    firstName = new HTML("");
    lastName = new HTML("");
    institution = new HTML("");
    roleAssignment = new RoleAssignment();
    email.setStyleName("out");
    firstName.setStyleName("out");
    lastName.setStyleName("out");
    institution.setStyleName("out");
    userProfileTable = new Grid(5, 3);
    userProfileTable.setBorderWidth(0);
    userProfileTable.setCellSpacing(0);
    userProfileTable.setCellPadding(3);

    userProfileTable.setText(0, 0, constants.Email() + ":");
    userProfileTable.setText(1, 0, constants.FirstName() + ":");
    userProfileTable.setText(2, 0, constants.LastName() + ":");
    userProfileTable.setText(3, 0, constants.Institution() + ":");
    userProfileTable.setText(4, 0, constants.Roles() + ":");

    userProfileTable.setWidget(0, 1, email);
    userProfileTable.setWidget(1, 1, firstName);
    userProfileTable.setWidget(2, 1, lastName);
    userProfileTable.setWidget(3, 1, institution);
    userProfileTable.setWidget(4, 1, roleAssignment);
    RowFormatter rowFormatter = userProfileTable.getRowFormatter();
    CellFormatter cellFormatter = userProfileTable.getCellFormatter();
    for (int row = 0; row < userProfileTable.getRowCount(); row++) {
      rowFormatter.setVerticalAlign(row, HasVerticalAlignment.ALIGN_MIDDLE);
      cellFormatter.setStyleName(row, 2, "error");
    }
    HorizontalPanel buttonPanel = new HorizontalPanel();
    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);
    buttonPanel.setSpacing(5);

    changePassLink.setStyleName("link");
    mainPanel.add(userProfileTable);
    mainPanel.add(changePassLink);
    mainPanel.add(buttonPanel);
    initWidget(mainPanel);
    changePassLink.addClickHandler(this);
    email.addClickHandler(this);
    email.addMouseOutHandler(this);
    email.addMouseOverHandler(this);
    firstName.addClickHandler(this);
    firstName.addMouseOutHandler(this);
    firstName.addMouseOverHandler(this);
    lastName.addClickHandler(this);
    lastName.addMouseOutHandler(this);
    lastName.addMouseOverHandler(this);
    institution.addClickHandler(this);
    institution.addMouseOutHandler(this);
    institution.addMouseOverHandler(this);
    editUserProfiles.addBlurHandler(this);
    editUserProfiles.addKeyUpHandler(this);
    saveButton.addClickHandler(this);
    cancelButton.addClickHandler(this);
    saveButton.setEnabled(true);
    cancelButton.setEnabled(true);
    setStyleName("UserProfile");
  }

  /**
   * @return the user
   */
  public User getUser() {
    return user;
  }

  /**
   * @param user the user to set
   */
  public void loadUser(User user) {
    this.user = user;
    roleAssignment.setUser(user);
    if (user != null) {
      email.setHTML(user.getEmail());
      firstName.setHTML(user.getFirstName());
      lastName.setHTML(user.getLastName());
      institution.setHTML(user.getInstitution());
    } else {
      email.setHTML("");
      firstName.setHTML("");
      lastName.setHTML("");
      institution.setHTML("");
    }
  }

  /**
   * When {@link #editUserProfiles} lost focus, checks to see whether the input
   * value was valid.
   */
  public void onBlur(BlurEvent event) {
    if (user == null) {
      return;
    }
    Object source = event.getSource();
    if (source == editUserProfiles) {
      checkInputValue();
    } else if (source == oldPass) {
      if (oldPass.getText().trim().equals("")) {
        userProfileTable.setText(4, 2, constants.OldPass() + " "
            + constants.CanNotBeEmpty());
      } else {

        userProfileTable.setText(4, 2, "");
      }
      checkNewPass();
      // saveButton.setEnabled(isChanged());
      // cancelButton.setEnabled(isInputError() || isChanged());
    } else if (source == newPass || source == confirmPass) {
      checkNewPass();
      // saveButton.setEnabled(isChanged());
      // cancelButton.setEnabled(isInputError() || isChanged());
    }
  }

  /**
   * When a field is clicked change it to editable. When {@link #changePassLink}
   * is clicked add change password fields and change the changePassLink link
   * text. When {@link #changePassLink} is clicked again cancel all password
   * changes and collapse all the text.
   */
  public void onClick(ClickEvent event) {
    Object source = event.getSource();
    if (source == email) {
      userProfileTable.setWidget(0, 1, editUserProfiles);
      setEditProfileDefaultText(email, 0);
    } else if (source == firstName) {
      userProfileTable.setWidget(1, 1, editUserProfiles);
      setEditProfileDefaultText(firstName, 1);
    } else if (source == lastName) {
      userProfileTable.setWidget(2, 1, editUserProfiles);
      setEditProfileDefaultText(lastName, 2);
    } else if (source == institution) {
      userProfileTable.setWidget(3, 1, editUserProfiles); // Edit Institution
      // Not Email
      setEditProfileDefaultText(institution, 3);
    } else if (source == changePassLink) {
      if (changePassLink.getText().equals(constants.ChangePassword())) {
        if (oldPass == null) {
          oldPass = new PasswordTextBox();
          newPass = new PasswordTextBox();
          confirmPass = new PasswordTextBox();
          oldPass.addBlurHandler(this);
          // newPass.addBlurHandler(this);
          // confirmPass.addBlurHandler(this);
          newPass.addKeyUpHandler(this);
          confirmPass.addKeyUpHandler(this);
        }
        userProfileTable.resizeRows(8);
        userProfileTable.setText(5, 0, constants.OldPass() + ":");
        userProfileTable.setText(6, 0, constants.NewPass() + ":");
        userProfileTable.setText(7, 0, constants.ConfirmPassword() + ":");

        userProfileTable.setWidget(5, 1, oldPass);
        userProfileTable.setWidget(6, 1, newPass);
        userProfileTable.setWidget(7, 1, confirmPass);
        CellFormatter cellFormatter = userProfileTable.getCellFormatter();
        cellFormatter.setStyleName(5, 2, "error");
        cellFormatter.setStyleName(6, 2, "error");
        cellFormatter.setStyleName(7, 2, "error");

        changePassLink.setText(constants.CancelChangePass());
        oldPass.setFocus(true);
      } else {
        disableChangePass();
        // saveButton.setEnabled(isChanged());
        // cancelButton.setEnabled(isInputError() || isChanged());
      }
    } else if (source == saveButton && user != null) {
      // saveButton.setEnabled(false);
      // cancelButton.setEnabled(false);
      getUser().setEmail(email.getText());
      getUser().setFirstName(firstName.getText());
      getUser().setLastName(lastName.getText());
      getUser().setInstitution(institution.getText());
      Set<Role> roles = new HashSet<Role>();
      roleAssignment.done();
      String rs[] = roleAssignment.displayRoles.getHTML().split(",");
      for (String role : rs) {
        roles.add(availableRoles.get(role.trim()));
      }
      user.setRoles(roles);
      String newPassword = null;
      String oldPassword = "";
      if (oldPass != null && oldPass.isAttached()
          && !oldPass.getText().equals("")) {
        boolean isPasswordChanged = userProfileTable.getText(5, 2).trim()
            .equals("");
        if (isPasswordChanged) {
          newPassword = newPass.getText();
          oldPassword = oldPass.getText();
          // What the?!
          getUser().setPasswordHash(oldPassword);
        }
      }
      DataSwitch.get().update(
          ApplicationView.getAuthenticatedUser().getSessionId(), getUser(),
          newPassword, new AsyncCallback<Boolean>() {

            public void onFailure(Throwable caught) {
              Window.alert(caught.getMessage());
              GWT.log(caught.getMessage(), caught);
              saveButton.setEnabled(true);
              cancelButton.setEnabled(true);
            }

            public void onSuccess(Boolean result) {
              PopupMessage.getInstance().showMessage(
                  constants.ChangedProfilesSuccessMsg());
              saveButton.setEnabled(true);
              cancelButton.setEnabled(true);
              cancelButton.click();
              //
              // saveButton.setEnabled(false);
              // cancelButton.click();
              // cancelButton.setEnabled(false);
            }
          });
    } else if (source == cancelButton && user != null) {
      disableChangePass();
      email.setText(getUser().getEmail());
      firstName.setText(getUser().getFirstName());
      lastName.setText(getUser().getLastName());
      institution.setText(getUser().getInstitution());
      roleAssignment.setUser(user);
      // saveButton.setEnabled(isChanged());
      // cancelButton.setEnabled(isInputError() || isChanged());
      checkedEditedField();
    }
  }

  public void onKeyUp(KeyUpEvent event) {
    if (user == null) {
      return;
    }
    Object source = event.getSource();
    if (source == editUserProfiles) {
      if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
        checkInputValue();
        // saveButton.setEnabled(isChanged());
        // cancelButton.setEnabled(isInputError() || isChanged());
      }
    } else if (source == newPass || source == confirmPass) {
      checkNewPass();
      // saveButton.setEnabled(isChanged());
      // cancelButton.setEnabled(isInputError() || isChanged());
    }
  }

  public void onMouseOut(MouseOutEvent event) {
    if (user == null) {
      return;
    }
    Widget w = (Widget) event.getSource();
    w.addStyleName("out");
    w.removeStyleName("hover");
  }

  public void onMouseOver(MouseOverEvent event) {
    if (user == null) {
      return;
    }
    Widget w = (Widget) event.getSource();
    w.addStyleName("hover");
    w.removeStyleName("out");
  }

  public void resetError() {
    for (int row = 0; row < userProfileTable.getRowCount(); row++) {
      userProfileTable.setText(row, 2, "");
    }
  }

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

  private void checkedEditedField() {
    if (!getUser().getEmail().equalsIgnoreCase(email.getText())) {
      email.addStyleName("edited");
    } else {
      email.removeStyleName("edited");
    }
    if (!getUser().getFirstName().equals(firstName.getText())) {
      firstName.addStyleName("edited");
    } else {
      firstName.removeStyleName("edited");
    }
    if (!getUser().getLastName().equals(lastName.getText())) {
      lastName.addStyleName("edited");
    } else {
      lastName.removeStyleName("edited");
    }
    if (!getUser().getInstitution().equals(institution.getText())) {
      institution.addStyleName("edited");
    } else {
      institution.removeStyleName("edited");
    }
  }

  private void checkInputValue() {
    String newValue = editUserProfiles.getText().trim();
    if (newValue.equals("")) {
      Window.alert(userProfileTable.getText(currentEditingRow, 0)
          + constants.FieldEmptyMsg());
      newValue = currentEditing.getText();
    } else if (currentEditing == email) {
      if (!isValidEmailFormat(editUserProfiles.getText())) {
        userProfileTable.setText(currentEditingRow, 2, constants
            .NotValidEmailFormat());
      } else {
        final String newEmail = newValue;
        // saveButton.setEnabled(false);
        if (!newEmail.equalsIgnoreCase(getUser().getEmail())) {
          DataSwitch.get().userEmailExists(newEmail,
              new AsyncCallback<Boolean>() {
                public void onFailure(Throwable caught) {
                  Window.alert(caught.getMessage());
                  GWT.log(caught.getMessage(), caught);
                }

                public void onSuccess(Boolean result) {
                  if (result) {
                    userProfileTable.setText(currentEditingRow, 2, newEmail
                        + " " + constants.EmailExisted());
                    // saveButton.setEnabled(isChanged());
                    // cancelButton.setEnabled(isInputError() || isChanged());
                  } else {

                  }
                }
              });
        }
        userProfileTable.setText(currentEditingRow, 2, "");
      }
    }
    userProfileTable.setWidget(currentEditingRow, 1, currentEditing);
    currentEditing.setText(newValue);
    currentEditing.addStyleName("out");
    currentEditing.removeStyleName("hover");
    checkedEditedField();
  }

  private boolean checkNewPass() {
    String newPassword = newPass.getText();
    String confirmPassword = confirmPass.getText();
    if (newPassword.equals("") && confirmPassword.equals("")) {
      userProfileTable.setText(5, 2, constants.NewPass() + " "
          + constants.CanNotBeEmpty());
      userProfileTable.setText(6, 2, constants.ConfirmPassword() + " "
          + constants.CanNotBeEmpty());
      return false;
    } else {
      boolean isTheSame = newPassword.equals(confirmPassword);
      if (!isTheSame) {
        userProfileTable.setText(5, 2, constants.PasswordDoesNotMactch());
        userProfileTable.setText(6, 2, constants.PasswordDoesNotMactch());
      } else {
        userProfileTable.setText(5, 2, "");
        userProfileTable.setText(6, 2, "");
      }
      return isTheSame;
    }
  }

  private void disableChangePass() {
    userProfileTable.resizeRows(5);
    changePassLink.setText(constants.ChangePassword());
    if (oldPass != null) {
      oldPass.setText("");
      newPass.setText("");
      confirmPass.setText("");
    }
  }

  private boolean isChanged() {
    if (!isInputError()) {
      if (!getUser().getEmail().equalsIgnoreCase(email.getText())) {
        return true;
      }
      if (!getUser().getFirstName().equalsIgnoreCase(firstName.getText())) {
        return true;
      }
      if (!getUser().getLastName().equalsIgnoreCase(lastName.getText())) {
        return true;
      }
      if (!getUser().getInstitution().equalsIgnoreCase(institution.getText())) {
        return true;
      }
      // if(roleAssignment.isChanged()) {
      // return true;
      // }
      if (newPass != null && newPass.isAttached()) {
        return newPass.getText().equals(confirmPass.getText());
      }
    }
    return false;
  }

  private boolean isInputError() {
    for (int row = 0; row < userProfileTable.getRowCount(); row++) {
      String errorText = userProfileTable.getText(row, 2).trim();
      if (errorText != null && errorText.trim().length() > 1) {
        return true;
      }
    }
    return false;
  }

  private void setEditProfileDefaultText(HTML editField, int row) {
    currentEditing = editField;
    currentEditingRow = row;
    editUserProfiles.setText(currentEditing.getText());
    editUserProfiles.selectAll();
    editUserProfiles.setFocus(true);
  }
}
