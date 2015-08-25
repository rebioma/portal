/**
 * 
 */
package org.rebioma.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rebioma.client.bean.PaginationOccurrences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Mika
 *
 */
public class JsonAPIInterfaceServlet extends APIInterfaceServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8933728866567397819L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PaginationOccurrences paginationResponse;
		try {
			paginationResponse = this.findOccurrences(request);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
//		Gson gson = new Gson();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(paginationResponse);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(json);
	}
}
