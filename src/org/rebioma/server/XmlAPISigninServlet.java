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

import org.rebioma.client.bean.api.APISigninResponse;

/**
 * @author Mikajy
 *
 */
public class XmlAPISigninServlet extends APISigninServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8912465797359173878L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		APISigninResponse signinResponse = doSignIn(req);
		try{
			JAXBContext jaxbContext = JAXBContext.newInstance(APISigninResponse.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			resp.setContentType("application/xml");
			jaxbMarshaller.marshal(signinResponse, resp.getWriter());
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
