/**
 * 
 */
package org.rebioma.server;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.rebioma.client.OccurrenceQuery.ResultFilter;
import org.rebioma.client.bean.ListOccurrenceAPIModel;
import org.rebioma.client.bean.ListStatisticAPIModel;
import org.rebioma.client.bean.SpeciesTreeModel;
import org.rebioma.client.bean.User;
import org.rebioma.client.bean.api.APITaxonomyResponse;
import org.rebioma.client.services.SpeciesExplorerService;
import org.rebioma.client.services.StatisticsService;
import org.rebioma.server.services.DBFactory;
import org.rebioma.server.services.OccurrenceDb;
import org.rebioma.server.services.OccurrenceDbImpl.OccurrenceFilter;
import org.rebioma.server.services.QueryFilter;
import org.rebioma.server.services.QueryFilter.InvalidFilter;
import org.rebioma.server.services.SpeciesExplorerServiceImpl;

/**
 * @author Mika
 *
 */
public class APIInterfaceServlet extends HttpServlet {
	
	protected static final String RES_OCCURRENCES = "occ";
	protected static final String RES_STATISTICS = "stats";
	protected static final String RES_TAXONOMY = "ta";
	
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
	private SpeciesExplorerService speciesExplorerService = new SpeciesExplorerServiceImpl();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	
	private Set<OccurrenceFilter> getOccurrenceViewFilters(HttpServletRequest request) throws InvalidFilter{
		String type = request.getParameter("type");
		String quicksearch = request.getParameter("quicksearch");
		String text = request.getParameter("text");
		Set<String> baseFilters = new HashSet<String>();
		if(StringUtils.isNotBlank(text)){
			baseFilters.add("globalsearchtext = " + text);
		}else{
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
		}

		if(StringUtils.isBlank(type)){
			type = ALL_OCC;
		}
		Set<OccurrenceFilter> filters = QueryFilter.getFilters(baseFilters, OccurrenceFilter.class);
		return filters;
	}
	
	protected ListStatisticAPIModel findStatisticByType(HttpServletRequest request) {

		ListStatisticAPIModel listStatisticApiModels;
		try{
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
			listStatisticApiModels = occurrenceDb.getStatisticsByType(statistic);
//			statisticsService.getStatisticsByType(statType);
		}catch(Exception e){
			listStatisticApiModels = new ListStatisticAPIModel();
			listStatisticApiModels.setSuccess(false);
			listStatisticApiModels.setMessage(e.getClass().getName() +": " + e.getMessage());
		}
		return listStatisticApiModels;
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
			User user = null;//TODO Mikajy - check in session
			paginationResponse = occurrenceDb.findByOccurrenceFilters(filters, user, ResultFilter.PUBLIC, numPage, pageSize);
			paginationResponse.setSuccess(true);
		}catch(IllegalArgumentException e){
			paginationResponse = new ListOccurrenceAPIModel();
			paginationResponse.setSuccess(false);
			paginationResponse.setMessage(e.getMessage());
		}
		
		return paginationResponse;
	}
	
	public APITaxonomyResponse getTaxonomies(HttpServletRequest request){
		APITaxonomyResponse response = new APITaxonomyResponse();
//		List<Taxonomy> taxonomies = new ArrayList<Taxonomy>();
		String kingdom = getParameter(request, "kingdom");
		String phylum = getParameter(request, "phylum");
		String classe = getParameter(request, "class");
		String order = getParameter(request, "order");
		String family = getParameter(request, "family");
		String genus = getParameter(request, "genus");
		String species = getParameter(request, "species");
		SpeciesTreeModel treeModel = new SpeciesTreeModel();
		treeModel.setKingdom(kingdom);
		treeModel.setPhylum(phylum);
		treeModel.setClass_(classe);
		treeModel.setOrder(order);
		treeModel.setFamily(family);
		treeModel.setGenus(genus);
		treeModel.setAcceptedspecies(species);
		List<SpeciesTreeModel> trees = speciesExplorerService.getChildren(treeModel);
		response.setSuccess(true);
		response.setTaxonomies(trees);
		return response;
	}
	private String getParameter(HttpServletRequest request, String key){
		String value = request.getParameter(key);
		if(StringUtils.isBlank(value)){
			value = null;
		}
		return value;
	}
}
