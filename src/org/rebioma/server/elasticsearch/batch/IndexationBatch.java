/**
 * 
 */
package org.rebioma.server.elasticsearch.batch;

import java.io.IOException;
import java.sql.SQLException;

import org.rebioma.server.elasticsearch.search.OccurrenceIndexation;

/**
 * @author Mika
 *
 */
public class IndexationBatch {
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException{
		OccurrenceIndexation indexation = null;
		boolean ok = false;
		try{
			indexation = new OccurrenceIndexation();
			indexation.indexAllOccurrence();
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
