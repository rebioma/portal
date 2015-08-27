/**
 * 
 */
package org.rebioma.server;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.rebioma.client.bean.ListOccurrenceAPIModel;
import org.rebioma.client.bean.ListStatisticAPIModel;
import org.rebioma.client.bean.StatisticModel;
import org.rebioma.client.bean.api.APITaxonomyResponse;
import org.rebioma.server.services.QueryFilter.InvalidFilter;

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
		
		final String res = req.getParameter("res");
		switch (res) {
			case RES_OCCURRENCES:
				ListOccurrenceAPIModel paginationResponse = new ListOccurrenceAPIModel();
				try {
					paginationResponse = findOccurrences(req);
				} catch (IllegalArgumentException e) {
					paginationResponse.setSuccess(false);
					paginationResponse.setMessage(e.getClass().getName() + ":" + e.getMessage());
				} catch (InvalidFilter e) {
					paginationResponse.setSuccess(false);
					paginationResponse.setMessage(e.getClass().getName() + ":" + e.getMessage());
				} catch (Exception e) {
					paginationResponse.setSuccess(false);
					paginationResponse.setMessage(e.getClass().getName() + ":" + e.getMessage());
				}
				try {
					JAXBContext jaxbContext = JAXBContext.newInstance(ListOccurrenceAPIModel.class);
					Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
					// output pretty printed
					jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
					resp.setContentType("application/xml");
					jaxbMarshaller.marshal(paginationResponse, resp.getWriter());
				} catch (JAXBException e) {
					throw new RuntimeException(e);
				}
				break;
			case RES_STATISTICS:
				ListStatisticAPIModel listStatisticAPIModel = findStatisticByType(req);
				try{
					JAXBContext jaxbContext = JAXBContext.newInstance(ListStatisticAPIModel.class);
					Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
					jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
					resp.setContentType("application/xml");
					jaxbMarshaller.marshal(listStatisticAPIModel, resp.getWriter());
				} catch (JAXBException e) {
					throw new RuntimeException(e);
				}
				break;
			case RES_TAXONOMY:
				APITaxonomyResponse txonomyResponse = getTaxonomies(req);
				try {
					JAXBContext jaxbContext = JAXBContext.newInstance(APITaxonomyResponse.class);
					Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
					// output pretty printed
					jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
					resp.setContentType("application/xml");
					jaxbMarshaller.marshal(txonomyResponse, resp.getWriter());
				} catch (JAXBException e) {
					throw new RuntimeException(e);
				}
				break;
			default:
				break;
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(req, resp);
	}

}
