/**
 * 
 */
package org.rebioma.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.rebioma.client.OccurrenceQuery.ResultFilter;
import org.rebioma.client.bean.ListOccurrenceAPIModel;
import org.rebioma.client.bean.StatisticModel;
import org.rebioma.client.bean.User;
import org.rebioma.client.services.StatisticsService;
import org.rebioma.server.services.DBFactory;
import org.rebioma.server.services.OccurrenceDb;
import org.rebioma.server.services.OccurrenceDbImpl.OccurrenceFilter;
import org.rebioma.server.services.QueryFilter;
import org.rebioma.server.services.QueryFilter.InvalidFilter;

/**
 * @author Mika
 *
 */
public class APIInterfaceServlet extends HttpServlet {
	
	protected static final String RES_OCCURRENCES = "occ";
	protected static final String RES_STATISTICS = "stats";
	
	private static final int DEFAULT_PAGE_SIZE = 50;
	//type possible values
	public static final String ALL_OCC = "all occurrences";
	public static final String ALL_POS_REVIEWED = "all pos reviewed";
	public static final String ALL_INVALID = "all invalid";
	public static final String ALL_AWAIT_REVIEW = "all await review";
	public static final String ALL_NEG_REVIEWED = "all neg reviewed";

	//error_type possible values
	protected static final String ALL_ERROR = "all";
	protected static final String XY_ERROR = "Coordinate";
	protected static final String YEAR_ERROR = "YearCollected";
	protected static final String GENUS_ERROR = "GENUS";
	protected static final String SPECIFIC_EPTHET_ERROR = "SpecificEpithet";
	protected static final String DECIMAL_LAT_ERROR = "DecimalLatitude";
	protected static final String DECIMAL_LNG_ERROR = "DecimalLongitude";
	protected static final String TAXO_ERROR = "Taxonomic classification";
	
	OccurrenceDb occurrenceDb = DBFactory.getOccurrenceDb();
	StatisticsService statisticsService = DBFactory.getStatisticsService();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	
	private Set<OccurrenceFilter> getOccurrenceViewFilters(HttpServletRequest request) throws InvalidFilter{
		String type = request.getParameter("type");
		String quicksearch = request.getParameter("quicksearch");
		if(StringUtils.isBlank(type)){
			type = ALL_OCC;
		}
		Set<String> baseFilters = new HashSet<String>();
		if(StringUtils.isNotBlank(quicksearch)){
			baseFilters.add("quickSearch = " + quicksearch);
		}
		switch (type) {
			case ALL_OCC:
				//filtre vide
				break;
			case ALL_AWAIT_REVIEW:
				baseFilters.add("validated = true");
				baseFilters.add("reviewed empty");
				break;
			case ALL_INVALID:
				baseFilters.add("validated = true");
				baseFilters.add("reviewed empty");
				//error_type
				String error_type = request.getParameter("error_type");
				if(StringUtils.isBlank(error_type)) error_type = ALL_ERROR;
				if(!ALL_ERROR.equalsIgnoreCase(error_type)) baseFilters.add("ValidationError like " + error_type);
				break;
			case ALL_NEG_REVIEWED:
				baseFilters.add("validated = true");
				baseFilters.add("reviewed = false");
				break;
			case ALL_POS_REVIEWED:
				baseFilters.add("validated = true");
				baseFilters.add("reviewed = true");
				break;
			default:
				throw new IllegalArgumentException("Le type [" + type + "] est inconnu");
		}
		Set<OccurrenceFilter> filters = QueryFilter.getFilters(baseFilters, OccurrenceFilter.class);
		return filters;
	}
	
	protected List<StatisticModel> findStatisticByType(HttpServletRequest request) throws Exception{
		String statistic = request.getParameter("statistic");
		int statType;
		switch (statistic) {
		case StatisticsService.TYPE_COLLECTION_CODE:
			statType = 3;
			break;
		case StatisticsService.TYPE_DATA_MANAGER:
			statType = 1;
			break;
		case StatisticsService.TYPE_DATA_PROVIDER_INSTITUTION:
			statType = 2;
			break;
		case StatisticsService.TYPE_YEAR_COLLECTED:
			statType = 4;
			break;
		default:
			throw new IllegalArgumentException("Le type de statistique [" + statistic + "] n'est pas géré par l'application.");
		}
		List<StatisticModel> statisticModels = statisticsService.getStatisticsByType(statType);
		return statisticModels;
	}
	
	protected ListOccurrenceAPIModel findOccurrences(HttpServletRequest request) throws Exception{
		ListOccurrenceAPIModel paginationResponse;
		try{
			Set<OccurrenceFilter> filters = getOccurrenceViewFilters(request);
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
			paginationResponse = occurrenceDb.findByOccurrenceFilters(filters, user, ResultFilter.PUBLIC, from, pageSize);
			paginationResponse.setSuccess(true);
		}catch(IllegalArgumentException e){
			paginationResponse = new ListOccurrenceAPIModel();
			paginationResponse.setSuccess(false);
			paginationResponse.setMessage(e.getMessage());
		}
		
		return paginationResponse;
	}
}
