/**
 * 
 */
package org.rebioma.client.bean;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Mika
 *
 */
@XmlRootElement(name="root")
@XmlAccessorType (XmlAccessType.FIELD)
public class PaginationOccurrences {
	
	@XmlElement
	private boolean success;
	
	@XmlElement
	private String message;

	@XmlElement
	private int pageNum;
	
	@XmlElement
	private int pageSize;
	
	@XmlElement
	private int nbTotal;

	@XmlElement
	private long tookInMillis;//temps de recherche es en milliseconde
	
	public long getTookInMillis() {
		return tookInMillis;
	}

	public void setTookInMillis(long tookInMillis) {
		this.tookInMillis = tookInMillis;
	}

	@XmlElement(name="occurrence")
	private List<Occurrence> occurrences;
	
	public PaginationOccurrences(){
		
	}
	
	public PaginationOccurrences(int pageNum, int pageSize, int nbTotal,
			List<Occurrence> items) {
		super();
		this.pageNum = pageNum;
		this.pageSize = pageSize;
		this.nbTotal = nbTotal;
		this.occurrences = items;
	}
	
	public int getNbTotal() {
		return nbTotal;
	}
	public void setNbTotal(int nbTotal) {
		this.nbTotal = nbTotal;
	}
	public int getPageNum() {
		return pageNum;
	}
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<Occurrence> getOccurrences() {
		return occurrences;
	}

	public void setOccurrences(List<Occurrence> occurrences) {
		this.occurrences = occurrences;
	}

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
	
	

}
