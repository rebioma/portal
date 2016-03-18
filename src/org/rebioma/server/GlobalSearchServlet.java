/**
 * 
 */
package org.rebioma.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.rebioma.client.bean.GlobalSearchResult;
import org.rebioma.client.bean.GlobalSearchResultModel;
import org.rebioma.client.bean.api.GsonXmlTransientExlusionStrategy;
import org.rebioma.server.elasticsearch.search.OccurrenceSearch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Mikajy
 *
 */
public class GlobalSearchServlet  extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4496345522291734159L;
	
	private Gson gson = new GsonBuilder()
	//	.serializeNulls()
	//	.excludeFieldsWithoutExposeAnnotation()
//		.generateNonExecutableJson()
		.setExclusionStrategies(new GsonXmlTransientExlusionStrategy())//on exclu les champs marqué par l'annotation @XmlTransient
//		.setPrettyPrinting()
		.create();
	
	private OccurrenceSearch occurrenceSearch = OccurrenceSearch.getInstance();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String callBackJavaScripMethodName = req.getParameter("callback");
		String query = req.getParameter("query");
		Map<String, Set<String>> fieldValues = occurrenceSearch.getFieldValues(query);
		ArrayList<GlobalSearchResultModel> models= new ArrayList<GlobalSearchResultModel>();
		int i = 0;
		for(String key: fieldValues.keySet()){
			for(String value: fieldValues.get(key)){
				i++;
				GlobalSearchResultModel res = new GlobalSearchResultModel();
				res.setEs_value(value);
				res.setOcc_id("" + i);
				res.setEs_field(key);
				models.add(res);
			}
		}
		GlobalSearchResult result = new GlobalSearchResult(""+i, models);
		String json = gson.toJson(result);
		resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        if(StringUtils.isEmpty(callBackJavaScripMethodName)){
            out.print(json);
        }else{
        	String jsonP = callBackJavaScripMethodName + "("+ json + ");";
        	out.print(jsonP);
        }
        
	}
	
	

}
