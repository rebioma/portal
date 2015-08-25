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
@XmlRootElement(name="results")
@XmlAccessorType (XmlAccessType.FIELD)
public class PaginationResponse<M> {
	
	public PaginationResponse() {
		super();
	}
	private int pageNum;
	
	private int pageSize;
	
	private int nbTotal;
	
	@XmlElement(name="items")
	private List<M> items;
	
	public PaginationResponse(int pageNum, int pageSize, int nbTotal,
			List<M> items) {
		super();
		this.pageNum = pageNum;
		this.pageSize = pageSize;
		this.nbTotal = nbTotal;
		this.items = items;
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
	public List<M> getItems() {
		return items;
	}
	public void setItems(List<M> items) {
		this.items = items;
	}
	
}
