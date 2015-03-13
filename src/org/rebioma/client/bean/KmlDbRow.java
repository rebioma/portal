package org.rebioma.client.bean;

import java.io.Serializable;

public class KmlDbRow implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -810979290579930027L;
	private int gid;
	private String name;
	private String gisAsKmlResult;
	private String group;

	public int getGid() {
		return gid;
	}

	public void setGid(int gid) {
		this.gid = gid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGisAsKmlResult() {
		return gisAsKmlResult;
	}

	public void setGisAsKmlResult(String gisAsKmlResult) {
		this.gisAsKmlResult = gisAsKmlResult;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

}
