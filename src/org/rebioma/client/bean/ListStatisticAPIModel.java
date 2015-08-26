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
@XmlRootElement(name="response")
@XmlAccessorType (XmlAccessType.FIELD)
public class ListStatisticAPIModel {
	
	@XmlElement
	private boolean success = true;
	
	@XmlElement
	private String message;
	
	@XmlElement
	private long tookInMillis;//temps de recherche es en milliseconde
	
	@XmlElement(name="statistic")
	private List<StatisticModel> statistics;

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

	public long getTookInMillis() {
		return tookInMillis;
	}

	public void setTookInMillis(long tookInMillis) {
		this.tookInMillis = tookInMillis;
	}

	public List<StatisticModel> getStatistics() {
		return statistics;
	}

	public void setStatistics(List<StatisticModel> statistics) {
		this.statistics = statistics;
	}
	
	
	
}
