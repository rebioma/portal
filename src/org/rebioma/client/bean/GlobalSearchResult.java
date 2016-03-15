/**
 * 
 */
package org.rebioma.client.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikajy
 *
 */
public class GlobalSearchResult {
	
	private String totalCount;
	
	private List<GlobalSearchResultModel> topics = new ArrayList<GlobalSearchResultModel>();
	
	public GlobalSearchResult(String totalCount, List<GlobalSearchResultModel> topics){
		this.totalCount = totalCount;
		this.topics = topics;
	}

	public String getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}

	public List<GlobalSearchResultModel> getTopics() {
		return topics;
	}

	public void setTopics(List<GlobalSearchResultModel> topics) {
		this.topics = topics;
	}
	
	
}
