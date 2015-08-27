/**
 * 
 */
package org.rebioma.server.bean;

/**
 * @author Mika
 *
 */
public class StatisticYearRange {
	
	private Integer inf;
	private Integer sup;
	
	public StatisticYearRange(){
		
	}
	
	public StatisticYearRange(Integer from, Integer to){
		this.inf = from;
		this.sup = to;
	}

	public Integer getInf() {
		return inf;
	}

	public void setInf(Integer inf) {
		this.inf = inf;
	}

	public Integer getSup() {
		return sup;
	}

	public void setSup(Integer sup) {
		this.sup = sup;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + inf;
		result = prime * result + sup;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatisticYearRange other = (StatisticYearRange) obj;
		if (inf != other.inf)
			return false;
		if (sup != other.sup)
			return false;
		return true;
	}
	
	
	
	
}
