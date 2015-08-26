/**
 * 
 */
package org.rebioma.server;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.rebioma.client.Email;
import org.rebioma.client.EmailException;
import org.rebioma.client.UserExistedException;
import org.rebioma.client.bean.User;
import org.rebioma.client.bean.api.APIRegisterResponse;
import org.rebioma.client.services.UserService;
import org.rebioma.server.services.UserServiceImpl;

/**
 * @author Mikajy
 *
 */
public class APIRegisterServlet  extends HttpServlet {
	
	private UserService userService = new UserServiceImpl();
	/**
	 * 
	 */
	private static final long serialVersionUID = 6858825889205278974L;
	
	
	protected User getUser(HttpServletRequest req){
		String firstname = req.getParameter("first_name");
		String lastname = req.getParameter("last_name");
		String email = req.getParameter("email");
		String institution = req.getParameter("institution");
		User user = new User();
		user.setFirstName(firstname);
		user.setLastName(lastname);
		user.setEmail(email);
		user.setInstitution(institution);
		return user;
	}
	
	protected APIRegisterResponse doRegistration(User user){
		Email welcomeEmail = createWelcomeEmail(user);
		APIRegisterResponse registrationResponse = new APIRegisterResponse();
		try {
			userService.register(user, welcomeEmail);
			if(user.getId() != null){
				registrationResponse.setSuccess(true);
				registrationResponse.setMessage("Rebioma Webportal - Your Registration is Pending Approval");
			}else{
				//error creating user
				registrationResponse.setSuccess(false);
				registrationResponse.setMessage("Rebioma Webportal - Error creating user " + user.getEmail());
			}
		} catch (EmailException e) {
			//error sending welcomeEmail
			registrationResponse.setSuccess(false);
			registrationResponse.setMessage("Rebioma Webportal - Error sending welcome email to " + user.getEmail());
		} catch (UserExistedException e) {
			//error sending welcomeEmail
			registrationResponse.setSuccess(false);
			registrationResponse.setMessage("Rebioma Webportal - " + e.getClass().getName() +  ":" + e.getMessage());
		} catch(Exception e){
			//error sending welcomeEmail
			registrationResponse.setSuccess(false);
			registrationResponse.setMessage("Rebioma Webportal - " + e.getCause().getClass().getName() +  ":" + e.getCause().getMessage());
		}
		return registrationResponse;
	}
	
	private Email createWelcomeEmail(User user) {
	    Email email = new Email();
	    email.setDear("Dear, ");
	    email.setSincerelyMsg("Sincerely,");
	    email.setYourNewPassword("Your new password : ");
	    email.setYourUserName("Your username ");
	    email.setUserEmail(user.getEmail());
	    email.setSubject("[REBIOMA PORTAL] A welcome message from REBIOMA");
	    email.setUserFirstName(user.getFirstName());
	    email.setContent("Welcome to the REBIOMA Portal and thank you for registering with us. Your login and password are included in this email. Please log in using the following username and password:");
	    email.setSincerelyMsg("Sincerely");
	    return email;
	  }


}
