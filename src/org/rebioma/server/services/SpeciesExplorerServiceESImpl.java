/**
 * 
 */
package org.rebioma.server.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.rebioma.client.bean.SpeciesStatisticModel;
import org.rebioma.client.bean.SpeciesTreeModel;
import org.rebioma.client.bean.SpeciesTreeModelInfoItem;
import org.rebioma.client.bean.Taxonomy;
import org.rebioma.client.services.SpeciesExplorerService;
import org.rebioma.server.elasticsearch.search.Indexation;
import org.rebioma.server.elasticsearch.search.TaxonomyMapping;

/**
 * 
 * @author Mikajy
 * 
 * Une implementation de SpeciesExplorerService qui utilise Elasticsearch comme bdd.
 *
 */
public class SpeciesExplorerServiceESImpl implements SpeciesExplorerService {
	
	private Node esNode;
	
	public static void main(String[] args){
		Node node = NodeBuilder.nodeBuilder().clusterName(Indexation.REBIOMA_ES_CLUSTER_NAME).node();
		SpeciesExplorerService service = new SpeciesExplorerServiceESImpl(node);
		service.getChildren(null);
	}

	public SpeciesExplorerServiceESImpl(Node esNode) {
		super();
		this.esNode = esNode;
	}

	/* (non-Javadoc)
	 * @see org.rebioma.client.services.SpeciesExplorerService#getChildren(org.rebioma.client.bean.SpeciesTreeModel)
	 */
	@Override
	public List<SpeciesTreeModel> getChildren(SpeciesTreeModel parent) {
		List<SpeciesTreeModel> listToReturn = new ArrayList<SpeciesTreeModel>();
		String taxonomyFieldConcerne="kingdom", occurrenceFieldConcerne="acceptedkingdom", level = LEVELS.get("KINGDOM");;
		FilterAggregationBuilder publicAggs = AggregationBuilders.filter("public").filter(FilterBuilders.termFilter("public_", true));
		FilterAggregationBuilder privateAggs = AggregationBuilders.filter("private").filter(FilterBuilders.orFilter(
				FilterBuilders.termFilter("public_", false),
				FilterBuilders.missingFilter("public_").existence(true).nullValue(true)));
		if(parent==null || parent.getKingdom()==null || parent.getKingdom().toString().isEmpty()) {
			taxonomyFieldConcerne="kingdom";
			occurrenceFieldConcerne ="acceptedkingdom";
			level=LEVELS.get("KINGDOM");
		}
		Set<FilterBuilder> occFilterBuilders = new HashSet<FilterBuilder>();
		Set<FilterBuilder> taFilterBuilders = new HashSet<FilterBuilder>();
		if(parent!=null && StringUtils.isNotBlank(parent.getKingdom())) {
			taxonomyFieldConcerne="phylum";
			occurrenceFieldConcerne ="acceptedphylum";
			level=LEVELS.get("PHYLUM");
			occFilterBuilders.add(FilterBuilders.termFilter("acceptedkingdom", parent.getKingdom()));
			taFilterBuilders.add(FilterBuilders.termFilter("kingdom", parent.getKingdom()));
		}
		if(parent!=null && StringUtils.isNotBlank(parent.getPhylum())) {
			taxonomyFieldConcerne="class_";
			occurrenceFieldConcerne ="acceptedclass";
			level=LEVELS.get("CLASS");
			occFilterBuilders.add(FilterBuilders.termFilter("acceptedphylum", parent.getPhylum()));
			taFilterBuilders.add(FilterBuilders.termFilter("phylum", parent.getPhylum()));
			//colonneSource=" getInfosClass(t.class) ";
		}
		if(parent!=null && StringUtils.isNotBlank(parent.getClass_())) {
			taxonomyFieldConcerne="order";
			occurrenceFieldConcerne ="acceptedorder";
			level=LEVELS.get("ORDER");
			occFilterBuilders.add(FilterBuilders.termFilter("acceptedclass", parent.getClass_()));
			taFilterBuilders.add(FilterBuilders.termFilter("class_", parent.getClass_()));
		}
		if(parent!=null && StringUtils.isNotBlank(parent.getOrder())) {
			taxonomyFieldConcerne="family";
			occurrenceFieldConcerne ="acceptedfamily";
			level=LEVELS.get("FAMILY");
			occFilterBuilders.add(FilterBuilders.termFilter("acceptedorder", parent.getOrder()));
			taFilterBuilders.add(FilterBuilders.termFilter("order", parent.getOrder()));
		}
		if(parent!=null && StringUtils.isNotBlank(parent.getFamily())) {
			taxonomyFieldConcerne="genus";
			occurrenceFieldConcerne ="acceptedgenus";
			level=LEVELS.get("GENUS");
			occFilterBuilders.add(FilterBuilders.termFilter("acceptedfamily", parent.getFamily()));
			taFilterBuilders.add(FilterBuilders.termFilter("family", parent.getFamily()));
		}
		if(parent!=null && StringUtils.isNotBlank(parent.getGenus())) {
			taxonomyFieldConcerne="species";
			occurrenceFieldConcerne ="acceptedspecies";
			level=LEVELS.get("ACCEPTEDSPECIES");
			occFilterBuilders.add(FilterBuilders.termFilter("acceptedgenus", parent.getGenus()));
			taFilterBuilders.add(FilterBuilders.termFilter("genus", parent.getGenus()));
			
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
				.setSearchType(SearchType.COUNT)
				.setQuery(taxonomyQueryBuilder)
				.addAggregation(
						AggregationBuilders.terms("children").field(taxonomyFieldConcerne).size(0)
						);
		SearchResponse taxonomySearchResponse = taxonomyRequestBuilder.execute().actionGet();
		SearchHits taSearchHits = taxonomySearchResponse.getHits();
		long totalHits = taSearchHits.getTotalHits();
		System.out.println(totalHits);
//		List<Taxonomy> taxonomies = new ArrayList<Taxonomy>();
		for(SearchHit hit: taSearchHits.hits()){
			Taxonomy ta = TaxonomyMapping.asTaxonomy(hit.getSource());
//			taxonomies.add(ta);
			SpeciesTreeModel model = new SpeciesTreeModel(ta, level);
			listToReturn.add(model);
		}
		
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
						.subAggregation(publicAggs)
						.subAggregation(privateAggs)
				);
		SearchResponse occurrenceSearchResponse = occurrenceRequestBuilder.execute().actionGet();
		
		return listToReturn;
	}

	/* (non-Javadoc)
	 * @see org.rebioma.client.services.SpeciesExplorerService#getStatistics(org.rebioma.client.bean.SpeciesTreeModel)
	 */
	@Override
	public List<SpeciesStatisticModel> getStatistics(SpeciesTreeModel model) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rebioma.client.services.SpeciesExplorerService#loadCsv()
	 */
	@Override
	public void loadCsv() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rebioma.client.services.SpeciesExplorerService#getInfomations(org.rebioma.client.bean.SpeciesTreeModel)
	 */
	@Override
	public List<SpeciesTreeModelInfoItem> getInfomations(SpeciesTreeModel source) {
		// TODO Auto-generated method stub
		return null;
	}

}
