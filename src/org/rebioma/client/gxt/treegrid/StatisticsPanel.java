package org.rebioma.client.gxt.treegrid;

import java.util.LinkedList;
import java.util.List;

import org.rebioma.client.bean.StatisticModel;
import org.rebioma.client.bean.StatisticModel.StatisticsModelProperties;
import org.rebioma.client.services.StatisticsService;
import org.rebioma.client.services.StatisticsServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.CellClickEvent;
import com.sencha.gxt.widget.core.client.event.CellClickEvent.CellClickHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

public class StatisticsPanel  extends Widget{
	private final StatisticsServiceAsync statisticsService = GWT
	.create(StatisticsService.class);
	
	//private ContentPanel root;
	public static final int NUM_PAGE = 9;
	private int intStatisticType = 1;
	private String BY_OWNER = "Numbers of occurrences per data manager (owner)";
	private String BY_INSTITUTION = "Numbers of occurrences  per data provider institution";
	private String BY_COLLECTION = "Numbers of occurrences per collection code";
	private String BY_YEAR = "Numbers of occurrences per year";
	String title = "";
	 FramedPanel cp ;
	 RpcProxy<PagingLoadConfig, PagingLoadResult<StatisticModel>> proxy;
	 private  PagingLoader<PagingLoadConfig, PagingLoadResult<StatisticModel>> loader;
	 
	 
	public Widget statisticsPanel(String gridTitle) {
		
		proxy = new RpcProxy<PagingLoadConfig, PagingLoadResult<StatisticModel>>() {
		      @Override
		      public void load(PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<StatisticModel>> callback) {
		    	  statisticsService.getStatisticsByType(intStatisticType,loadConfig, callback);
		      }
		    };
		// Generate the key provider and value provider for the Data class
		StatisticsModelProperties statisticsModelProperties = GWT.create(StatisticsModelProperties.class);
		
		
		List<ColumnConfig<StatisticModel, ?>> ccs = new LinkedList<ColumnConfig<StatisticModel, ?>>();
		ccs.add(new ColumnConfig<StatisticModel, String>(
				statisticsModelProperties.title(), 150, ""));
		ccs.add(new ColumnConfig<StatisticModel, Integer>(
				statisticsModelProperties.nbPrivateData(), 80, "Private data"));
		ccs.add(new ColumnConfig<StatisticModel, Integer>(
				statisticsModelProperties.nbPublicData(), 80,"Public data"));
		ccs.add(new ColumnConfig<StatisticModel, Integer>(
				statisticsModelProperties.nbReliable(), 80, "Reliable"));
		ccs.add(new ColumnConfig<StatisticModel, Integer>(
				statisticsModelProperties.nbAwaiting(), 100, "Awaiting review"));
		ccs.add(new ColumnConfig<StatisticModel, Integer>(
				statisticsModelProperties.nbQuestionable(), 80, "Questionable"));
		ccs.add(new ColumnConfig<StatisticModel, Integer>(
				statisticsModelProperties.nbInvalidated(), 80, "Invalidated"));
		ccs.add(new ColumnConfig<StatisticModel, Integer>(
				statisticsModelProperties.nbTotal(), 80, "All"));
		
		ColumnModel<StatisticModel> cm = new ColumnModel<StatisticModel>(
				ccs);
		
		
		
	  	ListStore<StatisticModel> store = new ListStore<StatisticModel>(new ModelKeyProvider<StatisticModel>() {
	        @Override
	        public String getKey(StatisticModel item) {
	          return "" + item.getIdKey();
	        }
	      });
	  	
	  	 ToolBar toolBarHaut = new ToolBar();
	  	toolBarHaut.add(new LabelToolItem("Statistics: "));
	    SimpleComboBox<String> type = new SimpleComboBox<String>(new StringLabelProvider<String>());
	      type.setTriggerAction(TriggerAction.ALL);
	      type.setEditable(false);
	      type.setWidth(300);
	      type.add(BY_OWNER);
	      type.add(BY_INSTITUTION);
	      type.add(BY_COLLECTION);
	      type.add(BY_YEAR);
	      type.setValue(BY_OWNER);
	      
	      TextButton save  = new TextButton("Go");
	      toolBarHaut.add(type);
	      toolBarHaut.add(save);
	  	
	  	loader = new PagingLoader<PagingLoadConfig, PagingLoadResult<StatisticModel>>(proxy);
	  	    loader.setRemoteSort(true);
	  	    loader.addLoadHandler(new LoadResultListStoreBinding<PagingLoadConfig, StatisticModel, PagingLoadResult<StatisticModel>>(store));
	  	    
	  	  final PagingToolBar toolBar = new PagingToolBar(10);
	      toolBar.getElement().getStyle().setProperty("borderBottom", "none");
	      toolBar.bind(loader);
	       
	      IdentityValueProvider<StatisticModel> identity = new IdentityValueProvider<StatisticModel>();
	    
	  	  /*ListStore<StatisticModel> store = new ListStore<StatisticModel>(statisticsModelProperties.key());
		      store.addAll(StatisticModel.getstats());*/
	     /* root = new ContentPanel();
	      root.setHeadingText(gridTitle);
	     // root.getHeader().setIcon();
	     // root.setPixelSize(600, 300);
	      root.setWidth("100%");
	      root.addStyleName("margin-10");*/
	      
	      final Grid<StatisticModel> grid = new Grid<StatisticModel>(store, cm){
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
	        
	        grid.getView().setForceFit(true);
	        grid.setLoadMask(true);
	        grid.setLoader(loader);
	     
	        
	       cp = new FramedPanel();
	        cp.setCollapsible(true);
	        cp.setHeadingText(BY_OWNER);
	        cp.setWidth("90%");
	        cp.setHeight(500);
	        cp.addStyleName("margin-10");
	        
	        VerticalLayoutContainer con = new VerticalLayoutContainer();
	        
	        con.setBorders(true);
	        con.add(toolBarHaut);
	        con.add(grid, new VerticalLayoutData(1, 1));
	        con.add(toolBar, new VerticalLayoutData(1, -1));
	        cp.setWidget(con);
	 
	      /*grid.setColumnReordering(true);
	      grid.setStateful(true);
	      grid.setStateId("gridExample");*/
	 
	      /*GridStateHandler<StatisticModel> state = new GridStateHandler<StatisticModel>(grid);
	      state.loadState();*/
	      
	      grid.addCellClickHandler(new CellClickHandler() {
			
			@Override
			public void onCellClick(CellClickEvent event) {
				grid.setSelectionModel(new GridSelectionModel<StatisticModel>());
				
			}
		});
	      type.addSelectionHandler(new SelectionHandler<String>() {
				
				@Override
				public void onSelection(SelectionEvent<String> arg0) {
					if(arg0.getSelectedItem().equals(BY_OWNER)){
						intStatisticType = 1;
						title = BY_OWNER;
					}else if(arg0.getSelectedItem().equals(BY_INSTITUTION)){
						intStatisticType = 2;
						title = BY_INSTITUTION;
					}else if(arg0.getSelectedItem().equals(BY_COLLECTION)){
						intStatisticType = 3;
						title = BY_COLLECTION;
					}else if(arg0.getSelectedItem().equals(BY_YEAR)){
						intStatisticType = 4;
						title = BY_YEAR;
					}
					
				}
			});
	      
	      
	      //getAnotherStatistics
	      save.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				cp.setHeadingText(title);
				loader.load(0,10);
				
				
				
			}
		});
	      
	     
		return cp;
		
	}
	
	protected void changeStatistics(){
		
	}
	

}
