/**
 * 
 */
package org.rebioma.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rebioma.client.bean.User;
import org.rebioma.client.bean.api.APIRegisterResponse;
import org.rebioma.client.bean.api.GsonXmlTransientExlusionStrategy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Mikajy
 *
 */
public class JsonAPIRegisterServlet extends APIRegisterServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1608061728899958836L;
	
	private Gson gson = new GsonBuilder()
	//	.serializeNulls()
	//	.excludeFieldsWithoutExposeAnnotation()
		.setExclusionStrategies(new GsonXmlTransientExlusionStrategy())//on exclu les champs marqué par l'annotation @XmlTransient
		.setPrettyPrinting()
		.create();//new Gson();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		User user = getUser(req);
		APIRegisterResponse registerResponse = doRegistration(user);
		String json = gson.toJson(registerResponse);
		resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        out.print(json);
	}
}
