/**
 * 
 */
package org.rebioma.server;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.rebioma.client.bean.User;
import org.rebioma.client.bean.api.APISigninResponse;
import org.rebioma.client.services.UserService;
import org.rebioma.server.services.UserServiceImpl;

/**
 * @author Mikajy
 *
 */
public class APISigninServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4405053411721712732L;
	
	private UserService userService = new UserServiceImpl();

	protected APISigninResponse doSignIn(HttpServletRequest req) {
		String email = req.getParameter("email");
		String password = req.getParameter("password");
		User user = userService.signIn(email, password);
		APISigninResponse signinResponse = new APISigninResponse();
		if(user != null){
			signinResponse.setSuccess(true);
			signinResponse.setSessionId(user.getSessionId());
		}else{
			signinResponse.setSuccess(false);
			signinResponse.setMessage("Error signing in with email " + email);
		}
		return signinResponse;
	}
}
