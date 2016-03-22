package org.rebioma.client.services;

public enum StatisticType {
	TYPE_DATA_MANAGER(1, "data_manager"), TYPE_DATA_PROVIDER_INSTITUTION(2,
			"institution"), TYPE_COLLECTION_CODE(3, "collection_code"), TYPE_YEAR_COLLECTED(
			4, "year_collected");

	private int i;

	private String s;

	/**
	 * 
	 * @param i
	 *            - integer representation of the statistic type
	 * @param s
	 *            - string representation of the statistic type
	 */
	StatisticType(int i, String s) {
		this.i = i;
		this.s = s;
	}

	public int asInt() {
		return i;
	}

	public String asString() {
		return s;
	}
	
	public static StatisticType asEnum(int i){
		for(StatisticType s: values()){
			if(s.i == i){
				return s;
			}
		}
		return null;
	}
	
	public static StatisticType asEnum(String s){
		for(StatisticType stat: values()){
			if(stat.s.equalsIgnoreCase(s)){
				return stat;
			}
		}
		return null;
	}
}