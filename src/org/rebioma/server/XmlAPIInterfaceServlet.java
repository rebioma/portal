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

import org.rebioma.client.bean.PaginationOccurrences;

/**
 * @author Mika
 *
 */
public class XmlAPIInterfaceServlet extends APIInterfaceServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2069921163136265295L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PaginationOccurrences paginationResponse;
		try {
			paginationResponse = findOccurrences(req);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(PaginationOccurrences.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			resp.setContentType("application/xml");
			jaxbMarshaller.marshal(paginationResponse, resp.getWriter());
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}

		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(req, resp);
	}

}
