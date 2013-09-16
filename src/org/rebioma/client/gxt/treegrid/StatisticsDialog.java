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
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;

public class StatisticsDialog extends Dialog {

	private final StatisticsServiceAsync statService = GWT
			.create(StatisticsService.class);

	private final Grid<StatisticModel> grid;
	
	private final RpcProxy<PagingLoadConfig, PagingLoadResult<StatisticModel>> proxy;
	
	 private  PagingLoader<PagingLoadConfig, PagingLoadResult<StatisticModel>> loader;
	
	private final Map<StatisticModel, List<StatisticModel>> clientCache = new HashMap<StatisticModel, List<StatisticModel>>();
	
	StatisticModel source;
	public StatisticsDialog(StatisticModel source) {
		super();
		this.source = source;
		
		StatisticsModelProperties props = GWT
				.create(StatisticsModelProperties.class);
		proxy = new RpcProxy<PagingLoadConfig, PagingLoadResult<StatisticModel>>() {
		      @Override
		      public void load(PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<StatisticModel>> callback) {
		    	  statService.getStatisticDetails(StatisticsDialog.this.source,loadConfig,callback);
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
		
		loader = new PagingLoader<PagingLoadConfig, PagingLoadResult<StatisticModel>>(proxy);
  	    loader.setRemoteSort(true);
  	    loader.addLoadHandler(new LoadResultListStoreBinding<PagingLoadConfig, StatisticModel, PagingLoadResult<StatisticModel>>(store));
  	  final PagingToolBar toolBar = new PagingToolBar(30);
      toolBar.getElement().getStyle().setProperty("borderBottom", "none");
      toolBar.bind(loader);
		ColumnModel<StatisticModel> cm = new ColumnModel<StatisticModel>(l);
		grid = new Grid<StatisticModel>(store, cm){
	          @Override
	          protected void onAfterFirstAttach() {
	            super.onAfterFirstAttach();
	            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
	              @Override
	              public void execute() {
	                loader.load();
	              }
	            });
	          }
	        };
		grid.getView().setAutoExpandColumn(titleCC);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.setBorders(false);
		grid.setAllowTextSelection(false);
		grid.setLoadMask(true);
		grid.setLoader(loader);
		grid.setColumnReordering(true);
		grid.setStateful(true);
		grid.setStateId("gridExample");
		
        
		
        
		
		VerticalLayoutContainer con = new VerticalLayoutContainer();
		 con.add(grid, new VerticalLayoutData(1, 1));
	        con.add(toolBar, new VerticalLayoutData(1, -1));
		setBodyBorder(false);
		con.setWidth(700);
		con.setHeight(500);
		setHideOnButtonClick(true);
		setModal(true);
		add(con);
		// initWidget(complex);
	}

	
	/*private String getHeadingText(SpeciesTreeModel model){
		return model.getLevel() + " " + model.getLabel();
	}*/

}
