package org.rebioma.client.gxt.treegrid;
 
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.rebioma.client.bean.OccurrenceCommentModel;
import org.rebioma.client.bean.OccurrenceCommentProperties;
import org.rebioma.client.gxt.treegrid.MailTabPanel.MailingResources;
import org.rebioma.client.services.MailingService;
import org.rebioma.client.services.MailingServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.util.Margins;
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
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;
import com.sencha.gxt.widget.core.client.toolbar.SeparatorToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
 
public class MailingTab {
 
	private BorderLayoutContainer blc;
	private DateField startDate;
	private DateField endDate;
	private SimpleComboBox<String> box;
	private TextButton loadConfig;
	private TextButton search = new TextButton("Search");
	private TextButton sendButton = new TextButton("Send mail");
	private MailingResources ressources = GWT.create(MailingResources.class);
	private MailingServiceAsync mailingService = GWT.create(MailingService.class);
	private long frequency[] = {(long)7*1000*60*60*24, (long)14*1000*60*60*24, (long)30*1000*60*60*24};  
	private List<OccurrenceCommentModel> listOccurrenceCommentModel;
	private CheckBoxSelectionModel<OccurrenceCommentModel> sm;
	public Widget getWidget() {
	
		final MailingServiceAsync service = GWT.create(MailingService.class);
	  
		RpcProxy<PagingLoadConfig, PagingLoadResult<OccurrenceCommentModel>> proxy = new RpcProxy<PagingLoadConfig, PagingLoadResult<OccurrenceCommentModel>>() {
			@Override
			public void load(PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<OccurrenceCommentModel>> callback) {
				service.getOccurrenceComments(loadConfig, box.getValue(), startDate.getValue(), endDate.getValue(), callback);
			}
		};
	 
		OccurrenceCommentProperties props = GWT.create(OccurrenceCommentProperties.class);
	 
		ListStore<OccurrenceCommentModel> store = new ListStore<OccurrenceCommentModel>(new ModelKeyProvider<OccurrenceCommentModel>() {
			@Override
			public String getKey(OccurrenceCommentModel item) {
				return "" + item.getUId();
			}
		});
	 
		final PagingLoader<PagingLoadConfig, PagingLoadResult<OccurrenceCommentModel>> loader = new PagingLoader<PagingLoadConfig, PagingLoadResult<OccurrenceCommentModel>>(
				proxy);
		loader.setRemoteSort(true);
		loader.addLoadHandler(new LoadResultListStoreBinding<PagingLoadConfig, OccurrenceCommentModel, PagingLoadResult<OccurrenceCommentModel>>(store));
		
		loadConfig = new TextButton("Load config");
		loadConfig.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				loadConfig.setText("Loading");
				loadConfig.setIcon(ressources.loading());
				loadConfig.setEnabled(false);
				load();
			}
		});
		
		search.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				loader.load();
			}
		});
		
		sendButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				sendButton.setText("Sending...");
				sendButton.setIcon(ressources.loading());
				sendButton.setEnabled(false);
				sendSelected();
			}
		});
		
		startDate = new DateField();
		startDate.setAllowBlank(false);
		
		endDate = new DateField();
		endDate.setAllowBlank(false);
		
		box = new SimpleComboBox<String>(new StringLabelProvider<String>());
		box.setTriggerAction(TriggerAction.ALL);
		box.setEditable(false);
		box.setWidth(100);
		box.add("TRB");
		box.add("Data owner");
		box.setValue("Data owner");
		box.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
			}
		});
	    
		ToolBar toolBar = new ToolBar();
		toolBar.addStyleName("style");
	    toolBar.add(loadConfig);
	    toolBar.add(new SeparatorToolItem());
	    toolBar.add(new LabelToolItem("Start date: "));
	    toolBar.add(startDate);
	    toolBar.add(new SeparatorToolItem());
	    toolBar.add(new LabelToolItem(" End date: "));
	    toolBar.add(endDate);
	    toolBar.add(new SeparatorToolItem());
	    toolBar.add(new LabelToolItem(" Mail to: "));
	    toolBar.add(box);
	    toolBar.add(new SeparatorToolItem());
	    toolBar.add(search);
	    toolBar.add(new FillToolItem());
	    toolBar.add(sendButton);
	    sendButton.setEnabled(false);

	    final PagingToolBar pagingToolBar = new PagingToolBar(50);
	    pagingToolBar.getElement().getStyle().setProperty("borderBottom", "none");
	    pagingToolBar.bind(loader);
	     
	    IdentityValueProvider<OccurrenceCommentModel> identity = new IdentityValueProvider<OccurrenceCommentModel>();
	    sm = new CheckBoxSelectionModel<OccurrenceCommentModel>(identity) /*{
	    	@Override
	    	protected void onRefresh(RefreshEvent event) {
	    		// this code selects all rows when paging if the header checkbox is selected
	    		if (isSelectAllChecked()) {
	    			selectAll();
	    		}
	    		super.onRefresh(event);
	    	}
	    	
	    	
	    }*/;
	    
	    sm.setSelectionMode(SelectionMode.MULTI);
	    sm.deselectAll();
		sm.addSelectionChangedHandler(new SelectionChangedHandler<OccurrenceCommentModel>() {

			@Override
			public void onSelectionChanged(
					SelectionChangedEvent<OccurrenceCommentModel> event) {
				if (event.getSelection().size() > 0) {
					sendButton.setEnabled(true);
					listOccurrenceCommentModel = event.getSelection();
				} else sendButton.setEnabled(false);
			}
		});
	    
	    ColumnConfig<OccurrenceCommentModel, String> emailColumn = new ColumnConfig<OccurrenceCommentModel, String>(props.email(), 150, "Email");
	    ColumnConfig<OccurrenceCommentModel, String> firstNameColumn = new ColumnConfig<OccurrenceCommentModel, String>(props.firstName(), 150, "First name");
	    ColumnConfig<OccurrenceCommentModel, String> lastNameColumn = new ColumnConfig<OccurrenceCommentModel, String>(props.lastName(), 150, "Last name");
	    ColumnConfig<OccurrenceCommentModel, String> commentDetailColumn = new ColumnConfig<OccurrenceCommentModel, String>(props.commentDetail(), 150, "Comment detail");
//	    commentDetailColumn.setCell(new DateCell(DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT)));
	 
	    List<ColumnConfig<OccurrenceCommentModel, ?>> l = new ArrayList<ColumnConfig<OccurrenceCommentModel, ?>>();
	    l.add(sm.getColumn());
	    l.add(emailColumn);
	    l.add(firstNameColumn);
	    l.add(lastNameColumn);
	    l.add(commentDetailColumn);
	 
	    ColumnModel<OccurrenceCommentModel> cm = new ColumnModel<OccurrenceCommentModel>(l);
	 
	    Grid<OccurrenceCommentModel> grid = new Grid<OccurrenceCommentModel>(store, cm) {
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
	    grid.setSelectionModel(sm);
	    grid.getView().setForceFit(true);
	    grid.setLoadMask(true);
	    grid.setLoader(loader);
	    
	    blc = new BorderLayoutContainer();
	    blc.setBorders(true);
//	    cp.setCollapsible(true);
//	    cp.setHeadingText("Paging Grid Example");
//	    blc.setHeaderVisible(false);
	    blc.setWidth("100%");
	    blc.setHeight("400px");
	    blc.addStyleName("margin-10");
	 
	    VerticalLayoutContainer con = new VerticalLayoutContainer();
	    con.setBorders(true);
	    con.add(toolBar, new VerticalLayoutData(1, -1));
	    con.add(grid, new VerticalLayoutData(1, 1));
	    con.add(pagingToolBar, new VerticalLayoutData(1, -1));
	    
	    BorderLayoutData eastData = new BorderLayoutData(350);
	    eastData.setMargins(new Margins(5, 5, 5, 5));
	    eastData.setCollapsible(true);
	    eastData.setSplit(true);
	    
	    MarginData centerData = new MarginData();
	    centerData.setMargins(new Margins(5, 0, 5, 5));
	    FramedPanel frame = new FramedPanel();
	    frame.add(con);
	    blc.setCenterWidget(frame, centerData);
	    blc.setEastWidget(new MailTabPanel().getWidget(), eastData);
	    
//	    blc.setWidget(con,);
	 
	    return blc;

	}
	
	public void load(){
		mailingService.getMailingStat(new AsyncCallback<String[]>() {
			
			@Override
			public void onSuccess(String[] result) {
				Date date = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss").parse(result[2]);
				long diff = frequency[Integer.valueOf(result[1])];
				startDate.setValue(new Date(date.getTime() - diff));
//				date.getDate() 
				endDate.setValue(date);
				loadConfig.setIcon(null);
				loadConfig.setEnabled(true);
				loadConfig.setText("Load config");
				sm.deselectAll();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				com.google.gwt.user.client.Window.alert(caught.getMessage());
				loadConfig.setIcon(null);
				loadConfig.setEnabled(true);
				loadConfig.setText("Load config");
				sm.deselectAll();
			}
		});
	}
	
	public void sendSelected(){
		mailingService.sendSelected(box.getValue(), startDate.getValue(), endDate.getValue(), listOccurrenceCommentModel, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				Info.display("Information", "Failed");
				sendButton.setText("Send mail");
				sendButton.setIcon(null);
				sendButton.setEnabled(true);
			}

			@Override
			public void onSuccess(Boolean result) {
				Info.display("Information", "Done");
				sendButton.setText("Send mail");
				sendButton.setIcon(null);
				sendButton.setEnabled(true);
			}
		});
	}
	
}
