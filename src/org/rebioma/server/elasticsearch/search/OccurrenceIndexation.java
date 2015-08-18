package org.rebioma.server.elasticsearch.search;

import java.io.IOException;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

/**
 * 
 * @author Mikajy
 *
 */
public class OccurrenceIndexation {
	
	public static final String REBIOMA_ES_CLUSTER_NAME = "elasticsearch_rebioma-dev";
	public static final String REBIOMA_ES_INDEX_NAME = "rebioma-dev";
	public static final String REBIOMA_ES_OCCURRENCE_TYPE_NAME = "occurrence";
	
	private Node esNode;
	
	public OccurrenceIndexation() {
		super();
		esNode = NodeBuilder.nodeBuilder().clusterName(REBIOMA_ES_CLUSTER_NAME).node();
	}
	
	public void createIndex() throws IOException {
		IndicesExistsResponse existResponse = esNode.client().admin().indices().prepareExists(REBIOMA_ES_INDEX_NAME).execute().actionGet();
		boolean isExists = existResponse.isExists(); 
		if(isExists){
			esNode.client().admin().indices().prepareDelete(REBIOMA_ES_INDEX_NAME).execute().actionGet();
		}
		esNode.client().admin().indices().prepareCreate(REBIOMA_ES_INDEX_NAME)
			.setSettings(IndexSetting.getSettingsAsString())
			.addMapping(REBIOMA_ES_OCCURRENCE_TYPE_NAME, OccurrenceMapping.getMappingString())
			.execute().actionGet();
	}
	
	public void indexer(XContentBuilder source, String id){
		esNode.client().prepareIndex(REBIOMA_ES_INDEX_NAME, REBIOMA_ES_OCCURRENCE_TYPE_NAME, id).setSource(source).execute().actionGet();
	}
	
	public void end(){
		if(esNode != null){
			esNode.client().close();
			esNode.close();
		}
	}
}
