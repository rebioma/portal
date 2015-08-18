/**
 * 
 */
package org.rebioma.server.elasticsearch.batch;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.hibernate.Query;
import org.hibernate.Session;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.server.elasticsearch.search.OccurrenceIndexation;
import org.rebioma.server.elasticsearch.search.OccurrenceMapping;
import org.rebioma.server.util.HibernateUtil;

/**
 * @author Mika
 *
 */
public class IndexationBatch {
	
	public IndexationBatch() {
		super();
	}
	
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException{
		OccurrenceIndexation indexation = null;
		boolean ok = false;
		try{
			indexation = new OccurrenceIndexation();
			indexation.createIndex();
			Session session = HibernateUtil.getSessionFactory().openSession();
			Query query = session.createQuery("from Occurrence");
			query.setMaxResults(1000);
			List<Occurrence> occurrences = query.list();
			for(Occurrence o: occurrences){
				XContentBuilder source = OccurrenceMapping.asXcontentBuilder(o);
				indexation.indexer(source, o.getId() + "");
			}
			ok = true;
		}catch(Exception e){
			ok = false;
			e.printStackTrace();
		}finally{
			if(indexation != null){
				indexation.end();
			}
			if(ok){
				System.out.println("Vita Tsara Tompoko !");
			}else{
				System.err.println("Vita fa misy Erreur Tompoko !");
			}
			
		}
		
	}
	
}
