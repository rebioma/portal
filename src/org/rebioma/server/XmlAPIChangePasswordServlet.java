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

import org.rebioma.client.bean.api.APIChangePasswordResponse;

/**
 * @author Mikajy
 *
 */
public class XmlAPIChangePasswordServlet extends APIChangePasswordServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8473979692716797032L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		APIChangePasswordResponse changePasswordResponse = doChangePassword(req);
		try{
			JAXBContext jaxbContext = JAXBContext.newInstance(APIChangePasswordResponse.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			resp.setContentType("application/xml");
			jaxbMarshaller.marshal(changePasswordResponse, resp.getWriter());
		}catch(JAXBException e){
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

}
