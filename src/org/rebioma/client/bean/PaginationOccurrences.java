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
@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class PaginationOccurrences {
	

	@XmlElement
	private int pageNum;
	
	@XmlElement
	private int pageSize;
	
	@XmlElement
	private int nbTotal;
	
	@XmlElement(name="occurrences")
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
	
	

}
