package org.rebioma.client.bean;

import java.util.Date;

@SuppressWarnings("serial")
public class LastComment implements java.io.Serializable{
	
	private int oid;
	private Date date;
	public int getOid() {
		return oid;
	}
	public void setOid(int oid) {
		this.oid = oid;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public LastComment(int oid, Date date) {
		super();
		this.oid = oid;
		this.date = date;
	}
	
	public LastComment() {
		super();
	}

}