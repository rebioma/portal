/**
 * 
 */
package org.rebioma.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rebioma.client.bean.api.APISigninResponse;
import org.rebioma.client.bean.api.GsonXmlTransientExlusionStrategy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Mikajy
 *
 */
public class JsonAPISigninServlet extends APISigninServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -864321819551433956L;
	
	private Gson gson = new GsonBuilder()
	//	.serializeNulls()
	//	.excludeFieldsWithoutExposeAnnotation()
		.setExclusionStrategies(new GsonXmlTransientExlusionStrategy())//on exclu les champs marqué par l'annotation @XmlTransient
		.setPrettyPrinting()
		.create();//new Gson();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		APISigninResponse signinResponse = doSignIn(req);
		String json = gson.toJson(signinResponse);
		resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        out.print(json);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

}
