package org.rebioma.client.services;

import java.util.List;

import org.rebioma.client.bean.StatisticModel;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;

@RemoteServiceRelativePath("statisticsService")
public interface StatisticsService extends RemoteService{
	/**
	 * 1=...
	 * @return
	 */
	
	//statisticsType=0 ==>Nombres d’occurrences par utilisateurs gestionnaires de données
	//statisticsType=0 ==>Nombres d’occurrences par institutions fournisseurs de données
	//statisticsType=0 ==>Nombres d’occurrences par collection de données
	//statisticsType=0 ==>Nombres d’occurrences par année de collection
	List<StatisticModel> getStatisticsByType(int statisticsType);
	List<StatisticModel> getStatisticDetails(StatisticModel statisticModel);
	List<StatisticModel> getStatisticDetails(int statisticsType,String libelle);
	PagingLoadResult<StatisticModel> getStatisticsByType(int statisticsType, PagingLoadConfig config);
	PagingLoadResult<StatisticModel> getStatisticDetails(StatisticModel statisticModel, PagingLoadConfig config);

}
