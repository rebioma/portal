/**
 * 
 */
package org.rebioma.server.services;

import java.util.List;

import org.rebioma.client.bean.ListStatisticAPIModel;
import org.rebioma.client.bean.StatisticModel;
import org.rebioma.client.services.StatisticType;

/**
 * @author Mikajy
 *
 */
public interface StatisticsDbProxy {
	/**
	 * 
	 * @param type
	 * @return
	 */
	public ListStatisticAPIModel getStatisticsByTypeEnum(StatisticType type);
	
	/**
	 * 
	 * @param statisticsType
	 * @return
	 */
	public List<StatisticModel> getStatisticsByType(int statisticsType);
}
