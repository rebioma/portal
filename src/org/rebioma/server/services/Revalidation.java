package org.rebioma.server.services;

import java.util.List;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.RevalidationResult;

public interface Revalidation {
/**
 * A service to fetch all records of occurrence from the databases
 */
	public java.util.List<Occurrence> fetchAllOccurrences()throws Exception;
	
	
	/**
	 * Revalidates  all of the occurrences and return a map with the possibles cases as keys 
	 */
	
	public RevalidationResult revalidate(String sessionId) throws Exception;
	
    public List<Occurrence> fetchInvalidOccurrences() throws Exception ;
	
	public List<Occurrence> fetchValidOccurrences() throws Exception ;
}
