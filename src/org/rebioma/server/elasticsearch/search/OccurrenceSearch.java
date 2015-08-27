/**
 * 
 */
package org.rebioma.server.elasticsearch.search;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.RangeBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.rebioma.client.OccurrenceQuery.ResultFilter;
import org.rebioma.client.bean.User;
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
		esNode = NodeBuilder.nodeBuilder().clusterName(OccurrenceIndexation.REBIOMA_ES_CLUSTER_NAME).node();
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
	
	public SearchResponse doSearch(QueryBuilder queryBuilder, int from, int size){
		SearchRequestBuilder requestBuilder = esNode.client().prepareSearch(OccurrenceIndexation.REBIOMA_ES_INDEX_NAME)
				.setTypes(OccurrenceIndexation.REBIOMA_ES_OCCURRENCE_TYPE_NAME);//.setFrom(offset).setSize(pageSize);
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
		SearchRequestBuilder requestBuilder = esNode.client().prepareSearch(OccurrenceIndexation.REBIOMA_ES_INDEX_NAME)
				.setTypes(OccurrenceIndexation.REBIOMA_ES_OCCURRENCE_TYPE_NAME)
				.setQuery(QueryBuilders.matchAllQuery()).setSearchType(SearchType.COUNT);
		TermsBuilder termsBuilder;
		switch (statisticType) {
		case StatisticsService.TYPE_DATA_MANAGER:
			field = "data_manager";
			termsBuilder = AggregationBuilders.terms(statisticType).field(field);
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
			termsBuilder = AggregationBuilders.terms(statisticType).field(field);
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
			field = "institution";
			termsBuilder = AggregationBuilders.terms(statisticType).field(field);
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
