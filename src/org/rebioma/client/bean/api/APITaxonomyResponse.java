/**
 * 
 */
package org.rebioma.client.bean.api;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.rebioma.client.bean.SpeciesTreeModel;

/**
 * @author Mika
 *
 */
@XmlRootElement(name="response")
@XmlAccessorType(XmlAccessType.FIELD)
public class APITaxonomyResponse {
	private boolean success;
	private long tookInMillis;
	
	private String message;

	
	@XmlElement(name="taxonomy")
	private List<SpeciesTreeModel> taxonomies;
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<SpeciesTreeModel> getTaxonomies() {
		return taxonomies;
	}
	public void setTaxonomies(List<SpeciesTreeModel> taxonomies) {
		this.taxonomies = taxonomies;
	}
	public long getTookInMillis() {
		return tookInMillis;
	}
	public void setTookInMillis(long tookInMillis) {
		this.tookInMillis = tookInMillis;
	}
	
	
}
