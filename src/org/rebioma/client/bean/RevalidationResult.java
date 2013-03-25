package org.rebioma.client.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
/**
 * Classe pour vehiculer les informations de la revalidation à l'utilisateur.
 * 
 * @author mikajy
 *
 */
public class RevalidationResult implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<Integer,Integer> resultMap=new HashMap<Integer,Integer>();
	private String errorMessage;
	
	public boolean hasError(){
		return errorMessage != null && !errorMessage.isEmpty();
	}
	public Map<Integer, Integer> getResultMap() {
		return resultMap;
	}
	public void setResultMap(Map<Integer, Integer> resultMap) {
		this.resultMap = resultMap;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
