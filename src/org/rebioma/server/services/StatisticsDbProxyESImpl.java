/**
 * 
 */
package org.rebioma.server.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.filter.InternalFilter;
import org.elasticsearch.search.aggregations.bucket.range.InternalRange;
import org.elasticsearch.search.aggregations.bucket.terms.InternalTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.rebioma.client.bean.ListStatisticAPIModel;
import org.rebioma.client.bean.StatisticModel;
import org.rebioma.client.services.StatisticType;
import org.rebioma.server.elasticsearch.search.OccurrenceSearch;

/**
 * @author Mikajy
 *
 */
public class StatisticsDbProxyESImpl implements StatisticsDbProxy {

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.StatisticsDbProxy#getStatisticsByTypeEnum(org.rebioma.client.services.StatisticType)
	 */
	@Override
	public ListStatisticAPIModel getStatisticsByTypeEnum(StatisticType statisticType) {
		if(statisticType == null){
			throw new IllegalArgumentException("Le type de statistique [null] n'est pas géré par l'application.");
		}
		List<StatisticModel> statisticsModels = new ArrayList<StatisticModel>();
		SearchResponse searchResponse = OccurrenceSearch.getInstance().doOccurrenceStatistic(statisticType);
//		SearchHits searchHits = searchResponse.getHits();
		Aggregations aggregations = searchResponse.getAggregations();
		String type = statisticType.asString();
		if(StatisticType.TYPE_YEAR_COLLECTED.equals(statisticType)){
			InternalRange<InternalRange.Bucket> rangeAgg = aggregations.get(type);
			Collection<InternalRange.Bucket> buckets = rangeAgg.getBuckets();
			for(InternalRange.Bucket bucket: buckets){
				StatisticModel model = new StatisticModel();
				model.setStatisticType(statisticType.asInt());
				Double from = (Double)bucket.getFrom();
				Double to = (Double)bucket.getTo();
				String key = from.intValue()  + " ~ " + to.intValue();
				model.setTitle(key);
				Aggregations aggs = bucket.getAggregations();
				List<Aggregation> aggList = aggs.asList();
				for(Aggregation iAgg: aggList){
					InternalFilter filter = (InternalFilter)iAgg;
					Long docCount = filter.getDocCount();
					String name2 = filter.getName();
					if("private".equalsIgnoreCase(name2)) model.setNbPrivateData(docCount.intValue());
					if("reliable".equalsIgnoreCase(name2)) model.setNbReliable(docCount.intValue());
					if("public".equalsIgnoreCase(name2)) model.setNbPublicData(docCount.intValue());
					if("questionable".equalsIgnoreCase(name2)) model.setNbQuestionable(docCount.intValue());
					if("awaitingreview".equalsIgnoreCase(name2)) model.setNbQuestionable(docCount.intValue());
					if("invalidated".equalsIgnoreCase(name2)) model.setNbInvalidated(docCount.intValue());
				}
				statisticsModels.add(model);
			}
		}else{
			InternalTerms aggregation = aggregations.get(type);
//			String name1 = aggregation.getName();
			List<Terms.Bucket> buckets = aggregation.getBuckets();
			for(Terms.Bucket bucket: buckets){
				String key = bucket.getKey();
				StatisticModel model = new StatisticModel();
				model.setStatisticType(statisticType.asInt());
				model.setTitle(key);
				Aggregations aggs = bucket.getAggregations();
				List<Aggregation> aggList = aggs.asList();
				for(Aggregation iAgg: aggList){
					InternalFilter filter = (InternalFilter)iAgg;
					Long docCount = filter.getDocCount();
					String name2 = filter.getName();
					if("private".equalsIgnoreCase(name2)) model.setNbPrivateData(docCount.intValue());
					if("reliable".equalsIgnoreCase(name2)) model.setNbReliable(docCount.intValue());
					if("public".equalsIgnoreCase(name2)) model.setNbPublicData(docCount.intValue());
					if("questionable".equalsIgnoreCase(name2)) model.setNbQuestionable(docCount.intValue());
					if("awaitingreview".equalsIgnoreCase(name2)) model.setNbQuestionable(docCount.intValue());
					if("invalidated".equalsIgnoreCase(name2)) model.setNbInvalidated(docCount.intValue());
				}
				statisticsModels.add(model);
			}
		}
		ListStatisticAPIModel response = new ListStatisticAPIModel();
		response.setSuccess(true);
		response.setStatistics(statisticsModels);
		long took = searchResponse.getTookInMillis();
		response.setTookInMillis(took);
		return response;
	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.StatisticsDbProxy#getStatisticsByType(int)
	 */
	@Override
	public List<StatisticModel> getStatisticsByType(int statisticsType) {
		StatisticType typeEnum = StatisticType.asEnum(statisticsType);
		List<StatisticModel> ret = new ArrayList<StatisticModel>();
		ListStatisticAPIModel listStatisticApiModel = getStatisticsByTypeEnum(typeEnum);
		ret = listStatisticApiModel.getStatistics();
		return ret;
	}

}
