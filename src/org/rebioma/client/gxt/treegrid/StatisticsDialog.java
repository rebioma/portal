package org.rebioma.client.gxt.treegrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rebioma.client.bean.StatisticModel;
import org.rebioma.client.bean.StatisticModel.StatisticsModelProperties;
import org.rebioma.client.services.StatisticsService;
import org.rebioma.client.services.StatisticsServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.dom.Mask;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class StatisticsDialog extends Dialog {

	private final StatisticsServiceAsync statService = GWT
			.create(StatisticsService.class);

	private final Grid<StatisticModel> grid;
	
	private final RpcProxy<StatisticModel, List<StatisticModel>> proxy;
	
	private final Map<StatisticModel, List<StatisticModel>> clientCache = new HashMap<StatisticModel, List<StatisticModel>>();
	
	public StatisticsDialog() {
		super();
		StatisticsModelProperties props = GWT
				.create(StatisticsModelProperties.class);

		proxy = new RpcProxy<StatisticModel, List<StatisticModel>>() {
			@Override
			public void load(StatisticModel source,
					AsyncCallback<List<StatisticModel>> callback) {
				statService.getStatisticDetails(source, callback);
			}
		};
		ListStore<StatisticModel> store = new ListStore<StatisticModel>(
				props.key());

		ColumnConfig<StatisticModel, String> titleCC = new ColumnConfig<StatisticModel, String>(
				props.title(), 150, "");
		ColumnConfig<StatisticModel, Integer> privateCC = new ColumnConfig<StatisticModel, Integer>(
				props.nbPrivateData(), 80, "Private data");
		ColumnConfig<StatisticModel, Integer> publicCC = new ColumnConfig<StatisticModel, Integer>(
				props.nbPublicData(), 80, "Public data");
		ColumnConfig<StatisticModel, Integer> reliableCC = new ColumnConfig<StatisticModel, Integer>(
				props.nbReliable(), 80, "Reliable");
		ColumnConfig<StatisticModel, Integer> awaitingCC = new ColumnConfig<StatisticModel, Integer>(
				props.nbAwaiting(), 100, "Awaiting review");
		ColumnConfig<StatisticModel, Integer> questionableCC = new ColumnConfig<StatisticModel, Integer>(
				props.nbQuestionable(), 80, "Questionable");
		ColumnConfig<StatisticModel, Integer> invalidatedCC = new ColumnConfig<StatisticModel, Integer>(
				props.nbInvalidated(), 80, "Invalidated");
		ColumnConfig<StatisticModel, Integer> allCC = new ColumnConfig<StatisticModel, Integer>(
				props.nbTotal(), 80, "All");
		
		List<ColumnConfig<StatisticModel, ?>> l = new ArrayList<ColumnConfig<StatisticModel, ?>>();
		l.add(titleCC);
		l.add(privateCC);
		l.add(publicCC);
		l.add(reliableCC);
		l.add(awaitingCC);
		l.add(questionableCC);
		l.add(invalidatedCC);
		l.add(allCC);
		ColumnModel<StatisticModel> cm = new ColumnModel<StatisticModel>(l);
		grid = new Grid<StatisticModel>(store, cm);
		grid.getView().setAutoExpandColumn(titleCC);
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
		setWidth(730);
		setHeight(300);
		setHideOnButtonClick(true);
		setModal(true);
		add(grid);
		// initWidget(complex);
	}

	public void showStatistic(final StatisticModel model) {
		if(model != null){
			/*String headingText = getHeadingText(model);
			setHeadingText(headingText);*/
			this.show();
			if(clientCache.containsKey(model)){
				List<StatisticModel> cacheDatas = clientCache.get(model);
				grid.getStore().replaceAll(cacheDatas);
			}else{
				 Scheduler.get().scheduleDeferred(new ScheduledCommand(){
					@Override
					public void execute() {
						grid.getStore().clear();
						Mask.mask(grid.getElement(), "Loading...");
					}
				 });
				proxy.load(model, new AsyncCallback<List<StatisticModel>>() {
					@Override
					public void onFailure(Throwable caught) {
						hide();
						Window.alert(caught.getLocalizedMessage());
					}

					@Override
					public void onSuccess(List<StatisticModel> result) {
						grid.setLoadMask(false);
						grid.getStore().replaceAll(result);
						clientCache.put(model, result);
						Mask.unmask(grid.getElement());
					}
				});
			}
			
		}
	}
	
	/*private String getHeadingText(SpeciesTreeModel model){
		return model.getLevel() + " " + model.getLabel();
	}*/

}
