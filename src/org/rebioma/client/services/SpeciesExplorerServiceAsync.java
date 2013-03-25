package org.rebioma.client.services;

import java.util.List;

import org.rebioma.client.bean.SpeciesTreeModel;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SpeciesExplorerServiceAsync {

	void getChildren(SpeciesTreeModel parent,
			AsyncCallback<List<SpeciesTreeModel>> callback);

	void loadCsv(AsyncCallback<Void> callback);
}
