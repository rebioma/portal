/**
 * 
 */
package org.rebioma.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rebioma.client.bean.ListOccurrenceAPIModel;
import org.rebioma.client.bean.ListStatisticAPIModel;
import org.rebioma.client.bean.StatisticModel;
import org.rebioma.client.bean.api.APITaxonomyResponse;
import org.rebioma.client.bean.api.GsonXmlTransientExlusionStrategy;
import org.rebioma.server.services.QueryFilter.InvalidFilter;

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
	
	
	private Gson gson = new GsonBuilder()
//		.serializeNulls()
//		.excludeFieldsWithoutExposeAnnotation()
		.setExclusionStrategies(new GsonXmlTransientExlusionStrategy())//on exclu les champs marqué par l'annotation @XmlTransient
		.setPrettyPrinting()
		.create();//new Gson();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String json;
		final String res = request.getParameter("res");
		switch (res) {
		case RES_OCCURRENCES:
			ListOccurrenceAPIModel paginationResponse = new ListOccurrenceAPIModel();
			try {
				paginationResponse = this.findOccurrences(request);
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
			json = gson.toJson(paginationResponse);
			break;
		case RES_STATISTICS:
			ListStatisticAPIModel listStatisticAPIModel = new ListStatisticAPIModel();
			long startTime = System.currentTimeMillis();
			try {
				List<StatisticModel> statisticModel = this.findStatisticByType(request);
				listStatisticAPIModel.setStatistics(statisticModel);
			} catch (IllegalArgumentException e) {
				listStatisticAPIModel.setSuccess(false);
				listStatisticAPIModel.setMessage(e.getClass().getName() + ":" + e.getMessage());
			} catch (Exception e) {
				listStatisticAPIModel.setSuccess(false);
				listStatisticAPIModel.setMessage(e.getClass().getName() + ":" + e.getMessage());
			}
			long endTime = System.currentTimeMillis();
			listStatisticAPIModel.setTookInMillis(endTime - startTime);
			json = gson.toJson(listStatisticAPIModel);
			break;
		case RES_TAXONOMY:
				APITaxonomyResponse txonomyResponse = getTaxonomies(request);
				json = gson.toJson(txonomyResponse);
			break;
		default:
			Map<String, String> map = new HashMap<String, String>();
			map.put("message", "Les valeurs possible pour le paramètre [res] sont {" + RES_OCCURRENCES + ", " + RES_STATISTICS + "}");
			json = gson.toJson(map);
			break;
		}
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(json);
	}
}
