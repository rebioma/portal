package org.rebioma.server.elasticsearch.search;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.StatelessSession;
import org.hibernate.criterion.Restrictions;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.User;
import org.rebioma.server.util.HibernateUtil;

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
	
	BulkProcessor bulkProcessor;
	
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
		bulkProcessor = BulkProcessor.builder(esNode.client(), new BulkProcessor.Listener() {
			@Override
			public void beforeBulk(long executionId, BulkRequest request) {
				System.out.println("Going to execute new bulk composed of "+request.numberOfActions()+" actions");
			}
			@Override
			public void afterBulk(long executionId, BulkRequest request, Throwable ex) {
				System.err.println("Error executing bulk " + ex.getMessage());
				ex.printStackTrace();
			}
			
			@Override
			public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
				System.out.println("Executed bulk composed of "+request.numberOfActions()+" actions");
			}
		})
		.setBulkActions(1000) 
        .setBulkSize(new ByteSizeValue(2, ByteSizeUnit.MB)) 
        .setFlushInterval(TimeValue.timeValueSeconds(1)) 
        .setConcurrentRequests(2) 
        .build();
	}
	
	/**
	 * https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/bulk.html
	 * 
	 * @throws IOException
	 */
	public void indexAllOccurrence() throws IOException{
		createIndex();
		
		StatelessSession statelessSession = HibernateUtil.getSessionFactory().openStatelessSession();
		Query countQuery = statelessSession.createQuery("select count(id) from Occurrence");
		long rowCount = (Long)countQuery.uniqueResult();
		System.out.println(rowCount + " occurrences à indexer ...");
		int maxResult = 5000;
		Map<Integer, User> userMap = new HashMap<Integer, User>();
		try{
			Occurrence o;
			for(int i=0; i< rowCount; i+= maxResult){
	//			Transaction tx = statelessSession.beginTransaction();
				System.out.println("********************************");
				System.out.println("********************************");
				System.out.println("from " + i + " to " + maxResult);
				System.out.println("********************************");
				System.out.println("********************************");
				ScrollableResults scrollableResults = statelessSession.createQuery("from Occurrence order by id desc")
						.setFirstResult(i)
						.setMaxResults(maxResult)
						.setCacheable(false)
						.scroll(ScrollMode.FORWARD_ONLY);
					while(scrollableResults.next()){
						o = (Occurrence)scrollableResults.get(0);
						if(!userMap.containsKey(o.getOwner())){
							User ownerUser = (User)statelessSession.createCriteria(User.class).add(Restrictions.eq("id", o.getOwner())).uniqueResult();
							userMap.put(o.getOwner(), ownerUser);
						}
						XContentBuilder source = OccurrenceMapping.asXcontentBuilder(o, userMap.get(o.getOwner()));
						bulkProcessor.add(new IndexRequest(REBIOMA_ES_INDEX_NAME, REBIOMA_ES_OCCURRENCE_TYPE_NAME, Integer.toString(o.getId())).source(source));
						o = null;
					}
					bulkProcessor.flush();
				
			}
		}finally{
			bulkProcessor.flush();
//			bulkProcessor.awaitClose(10, TimeUnit.MINUTES);
			bulkProcessor.close();
		}
	}
	
	/**
	 * https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/bulk.html
	 * 
	 * @param occurrences
	 * @throws IOException
	 */
	public void indexer(List<Occurrence> occurrences) throws IOException {
		try{
			StatelessSession statelessSession = HibernateUtil.getSessionFactory().openStatelessSession();
			for(Occurrence o: occurrences){
				User ownerUser = (User)statelessSession.createCriteria(User.class).add(Restrictions.eq("id", o.getOwner())).uniqueResult();
				XContentBuilder source = OccurrenceMapping.asXcontentBuilder(o, ownerUser);
				bulkProcessor.add(new IndexRequest(REBIOMA_ES_INDEX_NAME, REBIOMA_ES_OCCURRENCE_TYPE_NAME, Integer.toString(o.getId())).source(source));
			}
		}finally{
			bulkProcessor.flush();
			bulkProcessor.close();
		}
	}
	/**
	 * Insert or update an occurrence document
	 * 
	 * https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-update-api-upsert.html
	 * 
	 * @param o
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void indexer(Occurrence o) throws IOException, InterruptedException, ExecutionException{
		StatelessSession statelessSession = HibernateUtil.getSessionFactory().openStatelessSession();
		User ownerUser = (User)statelessSession.createCriteria(User.class).add(Restrictions.eq("id", o.getOwner())).uniqueResult();
		XContentBuilder source = OccurrenceMapping.asXcontentBuilder(o, ownerUser);
		XContentBuilder occurrenceSource = OccurrenceMapping.asXcontentBuilder(o, ownerUser);
		IndexRequest indexRequest = new IndexRequest(REBIOMA_ES_INDEX_NAME, REBIOMA_ES_OCCURRENCE_TYPE_NAME, Integer.toString(o.getId()))
			.source(occurrenceSource);
		UpdateRequest updateRequest = new UpdateRequest(REBIOMA_ES_INDEX_NAME, REBIOMA_ES_OCCURRENCE_TYPE_NAME, Integer.toString(o.getId()))
        	.doc(source).upsert(indexRequest);//upsert
		esNode.client().update(updateRequest).get();
	}
	
	public void end(){
		if(esNode != null){
			esNode.client().close();
			esNode.close();
		}
	}
}
