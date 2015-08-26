/**
 * 
 */
package org.rebioma.server;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.rebioma.client.Email;
import org.rebioma.client.bean.api.APIChangePasswordResponse;
import org.rebioma.client.services.UserService;
import org.rebioma.server.services.UserServiceImpl;

/**
 * @author Mikajy
 *
 */
public class APIChangePasswordServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7587282418629736944L;

	private UserService userService = new UserServiceImpl();
	
	protected APIChangePasswordResponse doChangePassword(HttpServletRequest req){
		String oldPwd = req.getParameter("old_pwd");
		String newPwd = req.getParameter("new_pwd");
		String sessionId = req.getParameter("jeton");
		Email email = createEmail();
		int returnStatus = userService.changeUserPassword(oldPwd, newPwd, sessionId, email);
		APIChangePasswordResponse changePasswordResponse = new APIChangePasswordResponse();
		changePasswordResponse.setChangePassStatus(returnStatus);
		if(returnStatus == 1){
			//success
			changePasswordResponse.setSuccess(true);
			changePasswordResponse.setMessage("Info successfully changed");
		}else if(returnStatus == 0){
			//password doesn't match
			changePasswordResponse.setSuccess(false);
			changePasswordResponse.setMessage("You session may be expired or your old password doesn't match any user");
		}else{
			//error
			changePasswordResponse.setSuccess(false);
			changePasswordResponse.setMessage("Error occured while trying to change the password");
		}
		return changePasswordResponse;
	}
	
	private Email createEmail() {
	    Email email = new Email();
	    email.setSubject("Password changed notification");
	    email.setContent("Your REBIOMA Portal password has been successfully changed. Your new password is not printed in this email to protect your privacy, and your username remains as your email address." + "\n\n"
	            + "If you did not request this change, your account security has been compromised. You should request a password reset through the following link:" + " " + "http://data.rebioma.net");
	    email.setYourNewPassword("Your new password");
	    email.setYourUserName("Your new username");
	    email.setSincerelyMsg("Sincerely,");
	    email.setDear("Dear,");
	    return email;
	  }
}
