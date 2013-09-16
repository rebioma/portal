package org.rebioma.client.services;

import java.util.List;

import org.rebioma.client.bean.StatisticModel;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;

public interface StatisticsServiceAsync {

	

	void getStatisticDetails(StatisticModel statisticModel,
			AsyncCallback<List<StatisticModel>> callback);

	void getStatisticsByType(int statisticsType, PagingLoadConfig config,
			AsyncCallback<PagingLoadResult<StatisticModel>> callback);

	void getStatisticsByType(int statisticsType,
			AsyncCallback<List<StatisticModel>> callback);

	void getStatisticDetails(StatisticModel statisticModel,
			PagingLoadConfig config,
			AsyncCallback<PagingLoadResult<StatisticModel>> callback);

	void getStatisticDetails(int statisticsType, String libelle,
			AsyncCallback<List<StatisticModel>> callback);

}
