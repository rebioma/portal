/**
 * 
 */
package org.rebioma.client.bean.api;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.rebioma.client.bean.StatisticModel;

/**
 * @author Mikajy
 *
 */
@XmlRootElement(name="response")
@XmlAccessorType (XmlAccessType.FIELD)
public class APIStatisticResponse {
	@XmlElement
	private boolean success = true;
	@XmlElement
	private String message;
	@XmlElement
	private long tookInMillis;//temps de recherche es en milliseconde
	
	@XmlTransient
	private String statisticType;
	
	@XmlElement(name="statistics")
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
	@XmlTransient
	public String getStatisticType() {
		return statisticType;
	}
	@XmlTransient
	public void setStatisticType(String statisticType) {
		this.statisticType = statisticType;
	}

	public List<StatisticModel> getStatistics() {
		return statistics;
	}

	public void setStatistics(List<StatisticModel> statistics) {
		this.statistics = statistics;
	}

	public long getTookInMillis() {
		return tookInMillis;
	}

	public void setTookInMillis(long tookInMillis) {
		this.tookInMillis = tookInMillis;
	}
	
	
}
