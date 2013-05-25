package org.rebioma.client.gxt.treegrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rebioma.client.bean.SpeciesStatisticModel;
import org.rebioma.client.bean.SpeciesStatisticModel.SpeciesStatisticModelProperties;
import org.rebioma.client.bean.SpeciesTreeModel;
import org.rebioma.client.services.SpeciesExplorerService;
import org.rebioma.client.services.SpeciesExplorerServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.dom.Mask;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class SpeciesStatistiqueDialog extends Dialog {

	private final SpeciesExplorerServiceAsync speciesExplorerService = GWT
			.create(SpeciesExplorerService.class);

	private final Grid<SpeciesStatisticModel> grid;
	
	private final RpcProxy<SpeciesTreeModel, List<SpeciesStatisticModel>> proxy;
	
	private final Map<SpeciesTreeModel, List<SpeciesStatisticModel>> clientCache = new HashMap<SpeciesTreeModel, List<SpeciesStatisticModel>>();
	
	public SpeciesStatistiqueDialog() {
		super();
		SpeciesStatisticModelProperties props = GWT
				.create(SpeciesStatisticModelProperties.class);

		proxy = new RpcProxy<SpeciesTreeModel, List<SpeciesStatisticModel>>() {
			@Override
			public void load(SpeciesTreeModel source,
					AsyncCallback<List<SpeciesStatisticModel>> callback) {
				speciesExplorerService.getStatistics(source, callback);
			}
		};
		ListStore<SpeciesStatisticModel> store = new ListStore<SpeciesStatisticModel>(
				props.key());

		ColumnConfig<SpeciesStatisticModel, String> kindOfDataCC = new ColumnConfig<SpeciesStatisticModel, String>(
				props.kindOfData(), 300, "Kind of Data");
		ColumnConfig<SpeciesStatisticModel, Integer> nbRecordCC = new ColumnConfig<SpeciesStatisticModel, Integer>(
				props.nbRecords(), 200, "Number of records");
		/*ColumnConfig<SpeciesStatisticModel, String> observationCC = new ColumnConfig<SpeciesStatisticModel, String>(
				props.observations(), 500, "Observations");*/
		List<ColumnConfig<SpeciesStatisticModel, ?>> l = new ArrayList<ColumnConfig<SpeciesStatisticModel, ?>>();
		l.add(kindOfDataCC);
		l.add(nbRecordCC);
		//l.add(observationCC);
		ColumnModel<SpeciesStatisticModel> cm = new ColumnModel<SpeciesStatisticModel>(l);
		grid = new Grid<SpeciesStatisticModel>(store, cm);
		grid.getView().setAutoExpandColumn(kindOfDataCC);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.setBorders(false);
		grid.setAllowTextSelection(false);
		grid.setLoadMask(true);
//		grid.setLoader(loader);
		grid.setColumnReordering(true);
		grid.setStateful(true);
		grid.setStateId("gridExample");
		setBodyBorder(false);
		setWidth(550);
		setHeight(250);
		setHideOnButtonClick(true);
		setModal(true);
		add(grid);
		// initWidget(complex);
	}

	public void showStatistic(final SpeciesTreeModel model) {
		if(model != null){
			String headingText = getHeadingText(model);
			setHeadingText(headingText);
			this.show();
			if(clientCache.containsKey(model)){
				List<SpeciesStatisticModel> cacheDatas = clientCache.get(model);
				grid.getStore().replaceAll(cacheDatas);
			}else{
				 Scheduler.get().scheduleDeferred(new ScheduledCommand(){
					@Override
					public void execute() {
						grid.getStore().clear();
						Mask.mask(grid.getElement(), "Loading...");
					}
				 });
				proxy.load(model, new AsyncCallback<List<SpeciesStatisticModel>>() {
					@Override
					public void onFailure(Throwable caught) {
						hide();
						Window.alert(caught.getLocalizedMessage());
					}

					@Override
					public void onSuccess(List<SpeciesStatisticModel> result) {
						grid.setLoadMask(false);
						grid.getStore().replaceAll(result);
						clientCache.put(model, result);
						Mask.unmask(grid.getElement());
					}
				});
			}
			
		}
	}
	
	private String getHeadingText(SpeciesTreeModel model){
		return model.getLevel() + " " + model.getLabel();
	}

}
