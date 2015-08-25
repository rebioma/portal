/**
 * 
 */
package org.rebioma.server;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.rebioma.client.OccurrenceQuery.ResultFilter;
import org.rebioma.client.bean.PaginationOccurrences;
import org.rebioma.client.bean.User;
import org.rebioma.server.services.DBFactory;
import org.rebioma.server.services.OccurrenceDb;
import org.rebioma.server.services.OccurrenceDbImpl.OccurrenceFilter;

/**
 * @author Mika
 *
 */
public class APIInterfaceServlet extends HttpServlet {
	
	private static final int DEFAULT_PAGE_SIZE = 50;
	
	OccurrenceDb occurrenceDb = DBFactory.getOccurrenceDb();
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected PaginationOccurrences findOccurrences(HttpServletRequest request) throws Exception{
		String type = request.getParameter("type");
		String page = request.getParameter("page");
		String nombreEltParPage = request.getParameter("pagesize");
		int numPage = 1, pageSize;
		try{
			numPage = Integer.parseInt(page);
		}catch(NumberFormatException e){
			numPage = 1;
			e.printStackTrace();
		}
		try{
			pageSize = Integer.parseInt(nombreEltParPage);
		}catch(NumberFormatException e){
			pageSize = DEFAULT_PAGE_SIZE;
			e.printStackTrace();
		}
		if(pageSize <= 0) pageSize = DEFAULT_PAGE_SIZE;
		if(numPage <= 0) numPage = 1;
		
		int from = (numPage - 1) * pageSize;
		User user = null;//TODO Mikajy - check in session

		Set<OccurrenceFilter> filters = new HashSet<OccurrenceFilter>();
		PaginationOccurrences paginationResponse = occurrenceDb.findByOccurrenceFilters(filters, user, ResultFilter.PUBLIC, from, pageSize);
		return paginationResponse;
	}
}
