/**
 * 
 */
package org.rebioma.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.rebioma.client.bean.User;
import org.rebioma.client.bean.api.APIRegisterResponse;

/**
 * @author Mikajy
 *
 */
public class XmlAPIRegisterServlet extends APIRegisterServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1865964562699998687L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		User user = getUser(req);
		APIRegisterResponse registerResponse = doRegistration(user);
		try{
			JAXBContext jaxbContext = JAXBContext.newInstance(APIRegisterResponse.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			resp.setContentType("application/xml");
			jaxbMarshaller.marshal(registerResponse, resp.getWriter());
		}catch(JAXBException e){
			throw new RuntimeException(e);
		}
	}

}
