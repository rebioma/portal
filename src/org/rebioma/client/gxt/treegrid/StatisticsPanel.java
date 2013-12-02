package org.rebioma.client.gxt.treegrid;

import java.util.LinkedList;
import java.util.List;

import org.rebioma.client.bean.StatisticModel;
import org.rebioma.client.bean.StatisticModel.StatisticsModelProperties;
import org.rebioma.client.services.StatisticsService;
import org.rebioma.client.services.StatisticsServiceAsync;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.CellClickEvent;
import com.sencha.gxt.widget.core.client.event.CellClickEvent.CellClickHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

public class StatisticsPanel  extends Widget{
	private final StatisticsServiceAsync statisticsService = GWT
	.create(StatisticsService.class);
	
	//private ContentPanel root;
	public static final int NUM_PAGE = 25;
	private int intStatisticType = 1;
	private String BY_OWNER = "Numbers of occurrences per data manager (owner)";
	private String BY_INSTITUTION = "Numbers of occurrences  per data provider institution";
	private String BY_COLLECTION = "Numbers of occurrences per collection code";
	private String BY_YEAR = "Numbers of occurrences per year collected";
	String title = "";
	Label lTitle;
	VerticalLayoutContainer cp ;
	RpcProxy<PagingLoadConfig, PagingLoadResult<StatisticModel>> proxy;
	private PagingToolBar toolBar;
	private  PagingLoader<PagingLoadConfig, PagingLoadResult<StatisticModel>> loader;
	 
	SimpleSafeHtmlCell formatNbr = new SimpleSafeHtmlCell<Integer>(new AbstractSafeHtmlRenderer<Integer>() {
      @Override
      public SafeHtml render(Integer object) {
        return SafeHtmlUtils.fromString(NumberFormat.getDecimalFormat().format(object));
      }
    });
	
	interface ExampleTemplate extends XTemplates {
	    @XTemplate("<b>{name}</b><br />{value}")
	    SafeHtml render(String name, String value);
	}
	
	public StatisticsPanel(Label lTitle) {
		super();
		this.lTitle = lTitle;
	}

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
		 IdentityValueProvider<StatisticModel> identity = new IdentityValueProvider<StatisticModel>();
		  final CheckBoxSelectionModel<StatisticModel> selectionModel = new CheckBoxSelectionModel<StatisticModel>(identity);
		
		ccs.add(selectionModel.getColumn());
		ColumnConfig cTitle = new ColumnConfig<StatisticModel, String>(
				statisticsModelProperties.title(), 150, "");
		final ExampleTemplate template = GWT.create(ExampleTemplate.class);
		cTitle.setCell(new AbstractCell<String>() {
			 
	        @Override
	        public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
	          sb.append(template.render(value.split(" - ")[0], value.split(" - ").length>1?value.replace(value.split(" - ")[0] + " - ", ""):""));
	        }

		});
		
		ColumnConfig cNbPrivateData = new ColumnConfig<StatisticModel, Integer>(
				statisticsModelProperties.nbPrivateData(), 80, "Private data");
		cNbPrivateData.setAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		cNbPrivateData.setCell(formatNbr);
		
		ColumnConfig cNbPublicData = new ColumnConfig<StatisticModel, Integer>(
				statisticsModelProperties.nbPublicData(), 80,"Public data");
		cNbPublicData.setAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		cNbPublicData.setCell(formatNbr);
		
		ColumnConfig cNbReliable = new ColumnConfig<StatisticModel, Integer>(
				statisticsModelProperties.nbReliable(), 80, "Reliable");
		cNbReliable.setAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		cNbReliable.setCell(formatNbr);
		
		ColumnConfig cNbAwaiting = new ColumnConfig<StatisticModel, Integer>(
			statisticsModelProperties.nbAwaiting(), 100, "Awaiting review");
		cNbAwaiting.setAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		cNbAwaiting.setCell(formatNbr);
		
		ColumnConfig cNbQuestionable = new ColumnConfig<StatisticModel, Integer>(
				statisticsModelProperties.nbQuestionable(), 80, "Questionable");
		cNbQuestionable.setAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		cNbQuestionable.setCell(formatNbr);
		
		ColumnConfig cNbInvalidated = new ColumnConfig<StatisticModel, Integer>(
			statisticsModelProperties.nbInvalidated(), 80, "Invalidated");
		cNbInvalidated.setAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		cNbInvalidated.setCell(formatNbr);
	
		ColumnConfig cNbTotal = new ColumnConfig<StatisticModel, Integer>(
				statisticsModelProperties.nbTotal(), 80, "All");
		cNbTotal.setAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		cNbTotal.setCell(formatNbr);

		ccs.add(cTitle);
		ccs.add(cNbPrivateData);	
		ccs.add(cNbPublicData);
		ccs.add(cNbReliable);
		ccs.add(cNbAwaiting);
		ccs.add(cNbQuestionable);		
		ccs.add(cNbInvalidated);
		ccs.add(cNbTotal);
		
		ColumnModel<StatisticModel> cm = new ColumnModel<StatisticModel>(
				ccs);
		
		
		
	  	ListStore<StatisticModel> store = new ListStore<StatisticModel>(new ModelKeyProvider<StatisticModel>() {
	  		@Override
	        public String getKey(StatisticModel item) {
	          return "" + item.getIdKey();
	        }
	  	});
	  	
	  	ToolBar toolBarHaut = new ToolBar();
	  	LabelToolItem statLabel = new LabelToolItem("Statistics: ");
	  	toolBarHaut.add(statLabel);
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
	      TextButton details  = new TextButton("Show details");
	      toolBarHaut.add(details);
	  	
  			loader = new PagingLoader<PagingLoadConfig, PagingLoadResult<StatisticModel>>(proxy);
	  	    loader.setRemoteSort(true);
	  	    loader.addLoadHandler(new LoadResultListStoreBinding<PagingLoadConfig, StatisticModel, PagingLoadResult<StatisticModel>>(store));
	  	    
	  	  toolBar = new PagingToolBar(NUM_PAGE);
	  	  toolBar.getElement().getStyle().setProperty("borderBottom", "none");
	      toolBar.bind(loader);
	       
	    
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
	     
	        grid.setSelectionModel(selectionModel);
	        grid.getSelectionModel().setSelectionMode(SelectionMode.SIMPLE);
	        cp = new VerticalLayoutContainer();//FramedPanel();
//	        cp.setCollapsible(true);
//	        cp.setHeadingText(BY_OWNER);
	        lTitle.setText(BY_OWNER);
//	        cp.setWidth("100%");
//	        cp.setHeight(500);
	        cp.addStyleName("margin-0");
	        
	        
	        BorderLayoutContainer panel = new BorderLayoutContainer();
	        BorderLayoutData centerLayoutData = new BorderLayoutData();
	        centerLayoutData.setMargins(new Margins(5, 0, 0, 0));
	        VerticalLayoutContainer con = new VerticalLayoutContainer();
	        con.setBorders(true);
	        con.add(grid, new VerticalLayoutData(1, 1));
	        con.add(toolBar);
	        panel.setCenterWidget(con, centerLayoutData);
	        
//	        ContentPanel eastPanel = new ContentPanel();
//	        eastPanel.setHeadingText("Company Details");
//	        eastPanel.setHeaderVisible(false);
//	        eastPanel.addStyleName("white-bg");
//	        VBoxLayoutContainer vbox = new VBoxLayoutContainer();
//	        vbox.setVBoxLayoutAlign(VBoxLayoutAlign.CENTER);
//	        vbox.add(radarChart);
//	        detail.setStyleName("searchLabel");
//	        HorizontalPanel hp = new HorizontalPanel();
//	        hp.add(detail);
//	        vbox.add(hp);
//	        eastPanel.add(vbox, new VerticalLayoutData(1, 1, new Margins(5, 0, 0, 0)));
	        
//	        BorderLayoutData eastLayoutData = new BorderLayoutData(400);
//	        eastLayoutData.setMargins(new Margins(5, 5, 5, 0));
//	        panel.setEastWidget(eastPanel, eastLayoutData);
	        
	        cp.setBorders(false);
	        cp.add(toolBarHaut);
	        cp.add(panel, new VerticalLayoutData(1, 1));
//	        cp.add(toolBar, new VerticalLayoutData(1, -1));
//	        cp.setWidget(cp);
	      /*grid.setColumnReordering(true);
	      grid.setStateful(true);
	      grid.setStateId("gridExample");*/
	 
	      /*GridStateHandler<StatisticModel> state = new GridStateHandler<StatisticModel>(grid);
	      state.loadState();*/
	      
	      grid.addCellClickHandler(new CellClickHandler() {
			
			@Override
			public void onCellClick(CellClickEvent event) {
				int idx = event.getRowIndex();
				grid.getSelectionModel().select(idx, false);
				
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
//				cp.setHeadingText(title);
				lTitle.setText(title);
				loader.load(0,NUM_PAGE);
			}
		});
	      //getAnotherStatistics
	      details.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				StatisticModel model = grid.getSelectionModel().getSelectedItem();
				
				StatisticsDialog dialog = new StatisticsDialog(model);
				dialog.show();
				
				
				
				
				
			}
		});
	      
	     
		return cp;
		
	}
	
	protected void changeStatistics(){
		
	}
	
	public void forceLayout() {
		toolBar.forceLayout();
	}

}
