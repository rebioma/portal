/**
 * 
 */
package org.rebioma.server.elasticsearch.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.InternalFilter;
import org.elasticsearch.search.aggregations.bucket.range.RangeBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.InternalTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.InternalCardinality;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.rebioma.client.OccurrenceQuery.ResultFilter;
import org.rebioma.client.bean.SpeciesTreeModel;
import org.rebioma.client.bean.User;
import org.rebioma.client.services.SpeciesExplorerService;
import org.rebioma.client.services.StatisticsService;
import org.rebioma.server.bean.StatisticYearRange;
import org.rebioma.server.services.OccurrenceDbImpl;
import org.rebioma.server.util.HibernateUtil;

/**
 * @author Mikajy
 *
 */
public class OccurrenceSearch {
	
	private Node esNode;
	
	private static OccurrenceSearch instance;
	
	private OccurrenceSearch(){
		esNode = NodeBuilder.nodeBuilder().client(true)
				.settings(ImmutableSettings.settingsBuilder().put("http.enabled", false)).clusterName(Indexation.REBIOMA_ES_CLUSTER_NAME).node();
	}
	
	public void end(){
		if(esNode != null && !esNode.isClosed()){
			esNode.stop();
			esNode.close();
		}
	}
	
	public static OccurrenceSearch getInstance(){
		if(instance == null){
			instance = new OccurrenceSearch();
		}
		return instance;
	}
	
	/* (non-Javadoc)
	 * @see org.rebioma.client.services.SpeciesExplorerService#getChildren(org.rebioma.client.bean.SpeciesTreeModel)
	 */
	public List<SpeciesTreeModel> getSpeciesTreeModels(SpeciesTreeModel parent) {
		List<SpeciesTreeModel> listToReturn = new ArrayList<SpeciesTreeModel>();
		String taxonomyFieldConcerne="kingdom", occurrenceFieldConcerne="acceptedkingdom.lower", level = SpeciesExplorerService.LEVELS.get("KINGDOM");
		FilterAggregationBuilder publicAggs = AggregationBuilders.filter("public").filter(FilterBuilders.termFilter("public_", true));
		FilterAggregationBuilder privateAggs = AggregationBuilders.filter("private").filter(FilterBuilders.orFilter(
				FilterBuilders.termFilter("public_", false),
				FilterBuilders.missingFilter("public_").existence(true).nullValue(true)));
		if(parent==null || parent.getKingdom()==null || parent.getKingdom().toString().isEmpty()) {
			taxonomyFieldConcerne="kingdom";
			occurrenceFieldConcerne ="acceptedkingdom.lower";
			level=SpeciesExplorerService.LEVELS.get("KINGDOM");
		}
		Set<FilterBuilder> occFilterBuilders = new HashSet<FilterBuilder>();
		Set<FilterBuilder> taFilterBuilders = new HashSet<FilterBuilder>();
		Set<TermsBuilder> parentAggs = new HashSet<TermsBuilder>();
		if(parent!=null && StringUtils.isNotBlank(parent.getKingdom())) {
			taxonomyFieldConcerne="phylum";
			occurrenceFieldConcerne ="acceptedphylum.lower";
			level=SpeciesExplorerService.LEVELS.get("PHYLUM");
			occFilterBuilders.add(FilterBuilders.termFilter("acceptedkingdom", parent.getKingdom()));
			taFilterBuilders.add(FilterBuilders.termFilter("kingdom", parent.getKingdom()));
			parentAggs.add(AggregationBuilders.terms("kingdom").field("kingdom").size(0));
		}
		if(parent!=null && StringUtils.isNotBlank(parent.getPhylum())) {
			taxonomyFieldConcerne="class_";
			occurrenceFieldConcerne ="acceptedclass.lower";
			level=SpeciesExplorerService.LEVELS.get("CLASS");
			occFilterBuilders.add(FilterBuilders.termFilter("acceptedphylum", parent.getPhylum()));
			taFilterBuilders.add(FilterBuilders.termFilter("phylum", parent.getPhylum()));
			parentAggs.clear();
			parentAggs.add(AggregationBuilders.terms("kingdom").field("kingdom").size(0));
			parentAggs.add(AggregationBuilders.terms("phylum").field("phylum").size(0));
			//colonneSource=" getInfosClass(t.class) ";
		}
		if(parent!=null && StringUtils.isNotBlank(parent.getClass_())) {
			taxonomyFieldConcerne="order";
			occurrenceFieldConcerne ="acceptedorder.lower";
			level=SpeciesExplorerService.LEVELS.get("ORDER");
			occFilterBuilders.add(FilterBuilders.termFilter("acceptedclass", parent.getClass_()));
			taFilterBuilders.add(FilterBuilders.termFilter("class_", parent.getClass_()));
			parentAggs.clear();
			parentAggs.add(AggregationBuilders.terms("kingdom").field("kingdom").size(0));
			parentAggs.add(AggregationBuilders.terms("phylum").field("phylum").size(0));
			parentAggs.add(AggregationBuilders.terms("class").field("class_").size(0));
		}
		if(parent!=null && StringUtils.isNotBlank(parent.getOrder())) {
			taxonomyFieldConcerne="family";
			occurrenceFieldConcerne ="acceptedfamily.lower";
			level=SpeciesExplorerService.LEVELS.get("FAMILY");
			occFilterBuilders.add(FilterBuilders.termFilter("acceptedorder", parent.getOrder()));
			taFilterBuilders.add(FilterBuilders.termFilter("order", parent.getOrder()));
			parentAggs.clear();
			parentAggs.add(AggregationBuilders.terms("kingdom").field("kingdom").size(0));
			parentAggs.add(AggregationBuilders.terms("phylum").field("phylum").size(0));
			parentAggs.add(AggregationBuilders.terms("class").field("class_").size(0));
			parentAggs.add(AggregationBuilders.terms("order").field("order").size(0));
		}
		if(parent!=null && StringUtils.isNotBlank(parent.getFamily())) {
			taxonomyFieldConcerne="genus";
			occurrenceFieldConcerne ="acceptedgenus.lower";
			level=SpeciesExplorerService.LEVELS.get("GENUS");
			occFilterBuilders.add(FilterBuilders.termFilter("acceptedfamily", parent.getFamily()));
			taFilterBuilders.add(FilterBuilders.termFilter("family", parent.getFamily()));
			parentAggs.clear();
			parentAggs.add(AggregationBuilders.terms("kingdom").field("kingdom").size(0));
			parentAggs.add(AggregationBuilders.terms("phylum").field("phylum").size(0));
			parentAggs.add(AggregationBuilders.terms("class").field("class_").size(0));
			parentAggs.add(AggregationBuilders.terms("order").field("order").size(0));
			parentAggs.add(AggregationBuilders.terms("family").field("family").size(0));
		}
		if(parent!=null && StringUtils.isNotBlank(parent.getGenus())) {
			taxonomyFieldConcerne="acceptedspecies";
			occurrenceFieldConcerne ="acceptedspecies.lower";
			level=SpeciesExplorerService.LEVELS.get("ACCEPTEDSPECIES");
			occFilterBuilders.add(FilterBuilders.termFilter("acceptedgenus", parent.getGenus()));
			taFilterBuilders.add(FilterBuilders.termFilter("genus", parent.getGenus()));
			parentAggs.clear();
			parentAggs.add(AggregationBuilders.terms("kingdom").field("kingdom").size(0));
			parentAggs.add(AggregationBuilders.terms("phylum").field("phylum").size(0));
			parentAggs.add(AggregationBuilders.terms("class").field("class_").size(0));
			parentAggs.add(AggregationBuilders.terms("order").field("order").size(0));
			parentAggs.add(AggregationBuilders.terms("family").field("family").size(0));
			parentAggs.add(AggregationBuilders.terms("genus").field("genus").size(0));
		}
		QueryBuilder occurrenceQueryBuilder, taxonomyQueryBuilder;
		if(occFilterBuilders.isEmpty()){
			occurrenceQueryBuilder = QueryBuilders.matchAllQuery();
			taxonomyQueryBuilder = QueryBuilders.matchAllQuery();
		}else if(occFilterBuilders.size() == 1){
			occurrenceQueryBuilder = QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), occFilterBuilders.iterator().next());
			taxonomyQueryBuilder = QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), taFilterBuilders.iterator().next());
		}else{
			occurrenceQueryBuilder = QueryBuilders.filteredQuery(
						QueryBuilders.matchAllQuery(), 
						FilterBuilders.andFilter(occFilterBuilders.toArray(new FilterBuilder[0])));
			taxonomyQueryBuilder = QueryBuilders.filteredQuery(
					QueryBuilders.matchAllQuery(), 
					FilterBuilders.andFilter(taFilterBuilders.toArray(new FilterBuilder[0])));
		}

		SearchRequestBuilder taxonomyRequestBuilder = esNode.client().prepareSearch(Indexation.REBIOMA_ES_INDEX_NAME)
				.setTypes(Indexation.REBIOMA_ES_TAXONOMY_TYPE_NAME)
				.setQuery(taxonomyQueryBuilder)
				.setSearchType(SearchType.COUNT)
				.addAggregation(
						AggregationBuilders.terms("children").field(taxonomyFieldConcerne).size(0)
							.subAggregation(AggregationBuilders.cardinality("species_count").field("acceptedspecies.hash").precisionThreshold(40000))
						);
		for(TermsBuilder t: parentAggs){
			taxonomyRequestBuilder.addAggregation(t);
		}
		SearchResponse taxonomySearchResponse = taxonomyRequestBuilder.execute().actionGet();
		Map<String, Aggregation> aggsMap = taxonomySearchResponse.getAggregations().asMap();
		SpeciesTreeModel modelParent = new SpeciesTreeModel();
		if(aggsMap.containsKey("kingdom")){
			InternalTerms aggTerms = (InternalTerms)aggsMap.get("kingdom");
			String key = aggTerms.getBuckets().iterator().next().getKey();
			modelParent.setKingdom(key);
		}
		if(aggsMap.containsKey("phylum")){
			InternalTerms aggTerms = (InternalTerms)aggsMap.get("phylum");
			String key = aggTerms.getBuckets().iterator().next().getKey();
			modelParent.setPhylum(key);
		}
		if(aggsMap.containsKey("class")){
			InternalTerms aggTerms = (InternalTerms)aggsMap.get("class");
			String key = aggTerms.getBuckets().iterator().next().getKey();
			modelParent.setClass_(key);
		}
		if(aggsMap.containsKey("order")){
			InternalTerms aggTerms = (InternalTerms)aggsMap.get("order");
			String key = aggTerms.getBuckets().iterator().next().getKey();
			modelParent.setOrder(key);
		}
		if(aggsMap.containsKey("family")){
			InternalTerms aggTerms = (InternalTerms)aggsMap.get("family");
			String key = aggTerms.getBuckets().iterator().next().getKey();
			modelParent.setFamily(key);
		}
		if(aggsMap.containsKey("genus")){
			InternalTerms aggTerms = (InternalTerms)aggsMap.get("genus");
			String key = aggTerms.getBuckets().iterator().next().getKey();
			modelParent.setGenus(key);
		}
		
		InternalTerms aggregation = taxonomySearchResponse.getAggregations().get("children");
//		String name1 = aggregation.getName();
		List<Terms.Bucket> buckets = aggregation.getBuckets();
		Map<String, Integer> mapTemps = new HashMap<String, Integer>();
		for(Terms.Bucket bucket: buckets){
			String key = bucket.getKey();
			Long docCount = bucket.getDocCount();
			SpeciesTreeModel model = new SpeciesTreeModel();
			model.setNbTaxon(docCount.intValue());
			model.setLabel(key);
			model.setLevel(level);
			model.setId(level + "_" + key);
			if(SpeciesTreeModel.KINGDOM.equals(level)){
				model.setKingdom(key);
			}else if(SpeciesTreeModel.PHYLUM.equals(level)){
				model.setPhylum(key);
			}else if(SpeciesTreeModel.CLASS_.equals(level)){
				model.setClass_(key);
			}else if(SpeciesTreeModel.ORDER.equals(level)){
				model.setOrder(key);
			}else if(SpeciesTreeModel.FAMILY.equals(level)){
				model.setFamily(key);
			}else if(SpeciesTreeModel.GENUS.equals(level)){
				model.setGenus(key);
			}else if(SpeciesTreeModel.ACCEPTEDSPECIES.equals(level) || SpeciesTreeModel.SPECIES.equals(level)){
				model.setAcceptedspecies(key);
			}
			if(StringUtils.isNotBlank(modelParent.getKingdom())){
				model.setKingdom(modelParent.getKingdom());
			}
			if(StringUtils.isNotBlank(modelParent.getPhylum())){
				model.setPhylum(modelParent.getPhylum());
			}
			if(StringUtils.isNotBlank(modelParent.getClass_())){
				model.setClass_(modelParent.getClass_());
			}
			if(StringUtils.isNotBlank(modelParent.getOrder())){
				model.setOrder(modelParent.getOrder());
			}
			if(StringUtils.isNotBlank(modelParent.getFamily())){
				model.setFamily(modelParent.getFamily());
			}
			if(StringUtils.isNotBlank(modelParent.getGenus())){
				model.setGenus(modelParent.getGenus());
			}
			Aggregations internalAggs = bucket.getAggregations();
			InternalCardinality internalCardinality = (InternalCardinality)internalAggs.iterator().next();
			Long speciesCount = internalCardinality.getValue();
			model.setNbSpeciesTaxon(speciesCount.intValue());
			listToReturn.add(model);
			mapTemps.put(key.trim().toLowerCase(), listToReturn.size() - 1);
			
		}
		
//		List<Taxonomy> taxonomies = new ArrayList<Taxonomy>();
//		for(SearchHit hit: taSearchHits.hits()){
//			Taxonomy ta = TaxonomyMapping.asTaxonomy(hit.getSource());
////			taxonomies.add(ta);
//			SpeciesTreeModel model = new SpeciesTreeModel(ta, level);
//			listToReturn.add(model);
//		}
		
//		InternalTerms aggregation = taxonomySearchResponse.getAggregations().get("children");
////		String name1 = aggregation.getName();
//		List<Terms.Bucket> buckets = aggregation.getBuckets();
//		for(Terms.Bucket bucket: buckets){
//			String key = bucket.getKey();
//			StatisticModel model = new StatisticModel();
//			model.setTitle(key);
//			Aggregations aggs = bucket.getAggregations();
//			List<Aggregation> aggList = aggs.asList();
//			for(Aggregation iAgg: aggList){
//				InternalFilter filter = (InternalFilter)iAgg;
//				Long docCount = filter.getDocCount();
//				String name2 = filter.getName();
//				if("private".equalsIgnoreCase(name2)) model.setNbPrivateData(docCount.intValue());
//				if("reliable".equalsIgnoreCase(name2)) model.setNbReliable(docCount.intValue());
//				if("public".equalsIgnoreCase(name2)) model.setNbPublicData(docCount.intValue());
//				if("questionable".equalsIgnoreCase(name2)) model.setNbQuestionable(docCount.intValue());
//				if("awaitingreview".equalsIgnoreCase(name2)) model.setNbQuestionable(docCount.intValue());
//				if("invalidated".equalsIgnoreCase(name2)) model.setNbInvalidated(docCount.intValue());
//			}
//			statisticsModels.add(model);
//		}

		SearchRequestBuilder occurrenceRequestBuilder = esNode.client().prepareSearch(Indexation.REBIOMA_ES_INDEX_NAME)
				.setTypes(Indexation.REBIOMA_ES_OCCURRENCE_TYPE_NAME)
				.setQuery(occurrenceQueryBuilder)
				.setSearchType(SearchType.COUNT)
				.addAggregation(
						AggregationBuilders.terms("children").field(occurrenceFieldConcerne).size(0)
						//https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-metrics-cardinality-aggregation.html
						.subAggregation(AggregationBuilders.cardinality("species_count").field("acceptedspecies.hash").precisionThreshold(40000))
						.subAggregation(publicAggs)
						.subAggregation(privateAggs)
				);
		SearchResponse occurrenceSearchResponse = occurrenceRequestBuilder.execute().actionGet();
		InternalTerms occAggregation = occurrenceSearchResponse.getAggregations().get("children");
//		String name1 = aggregation.getName();
		List<Terms.Bucket> occBuckets = occAggregation.getBuckets();
		int index;
		for(Terms.Bucket bucket: occBuckets){
			String key = bucket.getKey();
//			Long docCount = bucket.getDocCount();
			Aggregations aggs = bucket.getAggregations();
			if(mapTemps.containsKey(key.trim().toLowerCase())){
				index = mapTemps.get(key.trim().toLowerCase());
				for(Aggregation iAgg: aggs){
					if("species_count".equalsIgnoreCase(iAgg.getName())){
						InternalCardinality internalCardinality = (InternalCardinality)iAgg;
						Long speciesCount = internalCardinality.getValue();
						listToReturn.get(index).setNbSpeciesOccurrence(speciesCount);
					}else{
						InternalFilter filter = (InternalFilter)iAgg;
						Long docCount = filter.getDocCount();
						String name2 = filter.getName();
						if("private".equalsIgnoreCase(name2)){
							listToReturn.get(index).setNbPrivateOccurence(docCount.intValue());
						}
						if("public".equalsIgnoreCase(name2)){
							listToReturn.get(index).setNbPublicOccurence(docCount.intValue());
						}
					}
					
				}
			}
		}
		return listToReturn;
	}
	
	public void checkStatus(){
//		esNode.client().admin().indices();
	}
	
	public SearchResponse doSearch(QueryBuilder queryBuilder, int from, int size){
		SearchRequestBuilder requestBuilder = esNode.client().prepareSearch(Indexation.REBIOMA_ES_INDEX_NAME)
				.setTypes(Indexation.REBIOMA_ES_OCCURRENCE_TYPE_NAME);//.setFrom(offset).setSize(pageSize);
		requestBuilder.setFrom(from).setSize(size);
		SearchResponse searchResponse = requestBuilder.setQuery(queryBuilder)
				.execute().actionGet();
		return searchResponse;
	}
	
	public SearchResponse doSearch(String text, int from, int size){
		MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(text)
					.field("full_identity", 4).field("biologic_classification", 2).field("autre_nom")
					.type(MultiMatchQueryBuilder.Type.CROSS_FIELDS);
		QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(/*"*" + */text + "*");
		QueryBuilder disMaxQuery = QueryBuilders.disMaxQuery()
					.add(multiMatchQueryBuilder)
					.add(queryStringQueryBuilder);
		return doSearch(disMaxQuery, from, size);
	}
	
	public SearchResponse doOccurrenceStatistic(String statisticType){
		
		FilterAggregationBuilder publicAggs = AggregationBuilders.filter("public").filter(FilterBuilders.termFilter("public_", true));
		FilterAggregationBuilder privateAggs = AggregationBuilders.filter("private").filter(FilterBuilders.orFilter(
				FilterBuilders.termFilter("public_", false),
				FilterBuilders.missingFilter("public_").existence(true).nullValue(true)));
		FilterAggregationBuilder reliableAggs = AggregationBuilders.filter("reliable").filter(FilterBuilders.andFilter(
				FilterBuilders.termFilter("validated", true),
				FilterBuilders.termFilter("reviewed", true)));
		FilterAggregationBuilder validatedAggs = AggregationBuilders.filter("validated").filter(FilterBuilders.termFilter("validated", true));
		FilterAggregationBuilder invalidatedAggs = AggregationBuilders.filter("invalidated").filter(FilterBuilders.orFilter(
				FilterBuilders.termFilter("validated", false),
				FilterBuilders.missingFilter("validated").existence(true).nullValue(true)));
		FilterAggregationBuilder reviewedAggs = AggregationBuilders.filter("reviewed").filter(FilterBuilders.termFilter("reviewed", true));
		FilterAggregationBuilder questionableAggs = AggregationBuilders.filter("questionable").filter(FilterBuilders.andFilter(
				FilterBuilders.termFilter("validated", true),
				FilterBuilders.termFilter("reviewed", false)));
		FilterAggregationBuilder awaitingreviewAggs = AggregationBuilders.filter("awaitingreview").filter(FilterBuilders.andFilter(
				FilterBuilders.termFilter("validated", true),
				FilterBuilders.missingFilter("reviewed").existence(true).nullValue(true)));
		String field;
//		QueryBuilders.matchAllQuery().setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		SearchRequestBuilder requestBuilder = esNode.client().prepareSearch(Indexation.REBIOMA_ES_INDEX_NAME)
				.setTypes(Indexation.REBIOMA_ES_OCCURRENCE_TYPE_NAME)
				.setQuery(QueryBuilders.matchAllQuery()).setSearchType(SearchType.COUNT);
		TermsBuilder termsBuilder;
		switch (statisticType) {
		case StatisticsService.TYPE_DATA_MANAGER:
			field = "data_manager";
			termsBuilder = AggregationBuilders.terms(statisticType).field(field).size(0);
			termsBuilder.subAggregation(publicAggs);
			termsBuilder.subAggregation(privateAggs);
			termsBuilder.subAggregation(reliableAggs);
			termsBuilder.subAggregation(validatedAggs);
			termsBuilder.subAggregation(invalidatedAggs);
			termsBuilder.subAggregation(reviewedAggs);
			termsBuilder.subAggregation(questionableAggs);
			termsBuilder.subAggregation(awaitingreviewAggs);
			requestBuilder.addAggregation(termsBuilder);
			
		break;
		case StatisticsService.TYPE_COLLECTION_CODE:
			field = "collectioncode";
			termsBuilder = AggregationBuilders.terms(statisticType).field(field).size(0);
			termsBuilder.subAggregation(publicAggs);
			termsBuilder.subAggregation(privateAggs);
			termsBuilder.subAggregation(reliableAggs);
			termsBuilder.subAggregation(validatedAggs);
			termsBuilder.subAggregation(invalidatedAggs);
			termsBuilder.subAggregation(reviewedAggs);
			termsBuilder.subAggregation(questionableAggs);
			termsBuilder.subAggregation(awaitingreviewAggs);
			requestBuilder.addAggregation(termsBuilder);
		break;
		case StatisticsService.TYPE_DATA_PROVIDER_INSTITUTION:
			field = "institutioncode";
			termsBuilder = AggregationBuilders.terms(statisticType).field(field).size(0);
			termsBuilder.subAggregation(publicAggs);
			termsBuilder.subAggregation(privateAggs);
			termsBuilder.subAggregation(reliableAggs);
			termsBuilder.subAggregation(validatedAggs);
			termsBuilder.subAggregation(invalidatedAggs);
			termsBuilder.subAggregation(reviewedAggs);
			termsBuilder.subAggregation(questionableAggs);
			termsBuilder.subAggregation(awaitingreviewAggs);
			requestBuilder.addAggregation(termsBuilder);
		break;
		case StatisticsService.TYPE_YEAR_COLLECTED:
			field = "year";
			List<StatisticYearRange> statisticYearRange = getStatisticYearRanges();
			RangeBuilder rangeBuilder = AggregationBuilders.range(statisticType).field(field);
			for(StatisticYearRange range: statisticYearRange){
				if(range.getInf() != null && range.getSup() != null){
					rangeBuilder.addRange(range.getInf(), range.getSup());
				}else if(range.getInf() == null && range.getSup() != null){
					rangeBuilder.addUnboundedTo(range.getSup());
				}else if(range.getInf() != null && range.getSup() == null){
					rangeBuilder.addUnboundedFrom(range.getInf());
				}
			}
			rangeBuilder.subAggregation(publicAggs);
			rangeBuilder.subAggregation(privateAggs);
			rangeBuilder.subAggregation(reliableAggs);
			rangeBuilder.subAggregation(validatedAggs);
			rangeBuilder.subAggregation(invalidatedAggs);
			rangeBuilder.subAggregation(reviewedAggs);
			rangeBuilder.subAggregation(questionableAggs);
			rangeBuilder.subAggregation(awaitingreviewAggs);
			requestBuilder.addAggregation(rangeBuilder);
		break;
		default:
			throw new IllegalArgumentException("Le type de statistique [" + statisticType + "] est incorrect");
		}
		SearchResponse searchResponse = requestBuilder.execute().actionGet();
		return searchResponse;
	}
	
	public Map<String, Set<String>> getFieldValues(String query){
		Map<String, Set<String>> fieldValues = new HashMap<String, Set<String>>();
		for(int i=0; i< 40; i++){
			String key = "key " + i;
			Set<String> values = new HashSet<String>();
			for(int v=0; v< 10; v++){
				values.add("value " + v);
			}
			fieldValues.put(key, values);
		}
		return fieldValues;
	}
	
	 public Set<String> addCreterionByFilters(Criteria criteria, User user,
		      Set<OccurrenceDbImpl.OccurrenceFilter> searchFilters, ResultFilter resultFilter, int tryCount) {
		    Set<String> queryFilters = new HashSet<String>();
		    
		    return null;
	 }
	 
		
		public List<StatisticYearRange> getStatisticYearRanges(){
			String sql = "SELECT  cast(year as int)/10 * 10 as inf,  case 	when cast(max(year)as int)/10 = cast(extract(year from current_date)as int)/10 	then date_part('year', current_date) || ''" + 	
								"else cast(year as int)/10 * 10 + 9 || '' end as sup FROM occurrence GROUP BY   cast(year as int)/10 order by cast(year as int)/10 * 10 asc";
			SQLQuery sqlQuery = HibernateUtil.getSessionFactory().openStatelessSession().createSQLQuery(sql);
			sqlQuery.addScalar("inf", Hibernate.INTEGER);
			sqlQuery.addScalar("sup", Hibernate.INTEGER);
			sqlQuery.setResultTransformer(Transformers
					.aliasToBean(StatisticYearRange.class));
			List<StatisticYearRange> yearRanges = sqlQuery.list();
			return yearRanges;
		}
	 
}
