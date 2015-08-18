/**
 * 
 */
package org.rebioma.server.elasticsearch.search;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

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
	
	public SearchResponse doSearch(String text, int from, int size){
		MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(text)
					.field("full_identity", 4).field("biologic_classification", 2).field("autre_nom")
					.type(MultiMatchQueryBuilder.Type.CROSS_FIELDS);
		QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(/*"*" + */text + "*");
		QueryBuilder disMaxQuery = QueryBuilders.disMaxQuery()
					.add(multiMatchQueryBuilder)
					.add(queryStringQueryBuilder);
		SearchRequestBuilder requestBuilder = esNode.client().prepareSearch(OccurrenceIndexation.REBIOMA_ES_INDEX_NAME)
				.setTypes(OccurrenceIndexation.REBIOMA_ES_OCCURRENCE_TYPE_NAME);//.setFrom(offset).setSize(pageSize);
		requestBuilder.setFrom(from).setSize(size);
		SearchResponse searchResponse = requestBuilder.setQuery(disMaxQuery)
				.execute().actionGet();
		return searchResponse;
	}
}
