package org.rebioma.client.services;

import java.util.List;

import org.rebioma.client.bean.SpeciesStatisticModel;
import org.rebioma.client.bean.SpeciesTreeModel;
import org.rebioma.client.bean.SpeciesTreeModelInfoItem;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SpeciesExplorerServiceAsync {

	void getChildren(SpeciesTreeModel parent,
			AsyncCallback<List<SpeciesTreeModel>> callback);

	void loadCsv(AsyncCallback<Void> callback);

	void getStatistics(SpeciesTreeModel model,
			AsyncCallback<List<SpeciesStatisticModel>> callback);

	void getInfomations(SpeciesTreeModel source, AsyncCallback<List<SpeciesTreeModelInfoItem>> callback);
}
