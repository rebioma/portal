/**
 * 
 */
package org.rebioma.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.rebioma.client.bean.GlobalSearchResult;
import org.rebioma.client.bean.GlobalSearchResultModel;
import org.rebioma.client.bean.api.GsonXmlTransientExlusionStrategy;

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
		GlobalSearchResult result = new GlobalSearchResult("100", new ArrayList<GlobalSearchResultModel>());
		for(int i=0; i< 5; i++){
			GlobalSearchResultModel res = new GlobalSearchResultModel();
			res.setEs_value(" blablabla <em>" + query +"</em> "+ i);
			res.setOcc_id("" + i);
			res.setEs_field("field " + i);
			result.getTopics().add(res);
		}
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
