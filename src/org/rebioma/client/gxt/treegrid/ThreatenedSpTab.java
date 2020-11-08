package org.rebioma.client.gxt.treegrid;

import java.util.ArrayList;
import java.util.List;

import org.rebioma.client.ApplicationView;
import org.rebioma.client.Resources;
import org.rebioma.client.bean.Iucn;
import org.rebioma.client.bean.ThreatenedSpProperties;
import org.rebioma.client.bean.ThreatenedSpeciesModel;
import org.rebioma.client.gxt.treegrid.MailTabPanel.MailingResources;
import org.rebioma.client.services.TaxonomyService;
import org.rebioma.client.services.TaxonomyServiceAsync;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

public class ThreatenedSpTab {

	private BorderLayoutContainer blc;
	private ComboBox<Iucn> combo;
	private TextButton iucnStatus;
	private MailingResources ressources = GWT.create(MailingResources.class);
	private ToolBar toolBar;
	private PagingToolBar pagingToolBar;
	final TaxonomyServiceAsync service = GWT.create(TaxonomyService.class);

	public Widget getWidget() {

		RpcProxy<PagingLoadConfig, PagingLoadResult<ThreatenedSpeciesModel>> proxy = new RpcProxy<PagingLoadConfig, PagingLoadResult<ThreatenedSpeciesModel>>() {
			@Override
			public void load(PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<ThreatenedSpeciesModel>> callback) {
				service.threatenedSpecies(loadConfig, combo.getValue(), callback);
			}
		};

		ThreatenedSpProperties props = GWT.create(ThreatenedSpProperties.class);

		ListStore<ThreatenedSpeciesModel> store = new ListStore<ThreatenedSpeciesModel>(new ModelKeyProvider<ThreatenedSpeciesModel>() {
			@Override
			public String getKey(ThreatenedSpeciesModel item) {
				return "" + item.getId();
			}
		});

		final PagingLoader<PagingLoadConfig, PagingLoadResult<ThreatenedSpeciesModel>> loader = new PagingLoader<PagingLoadConfig, PagingLoadResult<ThreatenedSpeciesModel>>(
				proxy);
		loader.setRemoteSort(true);
		loader.addLoadHandler(new LoadResultListStoreBinding<PagingLoadConfig, ThreatenedSpeciesModel, PagingLoadResult<ThreatenedSpeciesModel>>(store));

		///////
		RpcProxy<PagingLoadConfig, PagingLoadResult<Iucn>> proxy2 = new RpcProxy<PagingLoadConfig, PagingLoadResult<Iucn>>() {
			@Override
			public void load(PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<Iucn>> callback) {
				service.getIucnStatus(loadConfig, callback);
			}
		};

		ListStore<Iucn> store2 = new ListStore<Iucn>(new ModelKeyProvider<Iucn>() {
			@Override
			public String getKey(Iucn item) {
				return "" + item.getId();
			}
		});

		final PagingLoader<PagingLoadConfig, PagingLoadResult<Iucn>> loader2 = new PagingLoader<PagingLoadConfig, PagingLoadResult<Iucn>>(
				proxy2);
		loader2.addLoadHandler(new LoadResultListStoreBinding<PagingLoadConfig, Iucn, PagingLoadResult<Iucn>>(store2));

		iucnStatus = new TextButton("IUCN status");
		iucnStatus.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				iucnStatus.setText("Loading");
				iucnStatus.setIcon(ressources.loading());
				iucnStatus.setEnabled(false);
			}
		});

		final String moduleBaseUrl = GWT.getHostPageBaseURL();

		final IucnProperties iucnProps = GWT.create(IucnProperties.class);

		loader2.load();
		combo = new ComboBox<Iucn>(store2, iucnProps.name(),
				new AbstractSafeHtmlRenderer<Iucn>() {
			final ComboBoxTemplates comboBoxTemplates = GWT.create(ComboBoxTemplates.class);

			public SafeHtml render(Iucn item) {
				SafeUri imageUri = UriUtils.fromString(moduleBaseUrl + "images/s_iucn/" + item.getName().replace('/', '_').toUpperCase() + ".png");
				return comboBoxTemplates.iucn(imageUri, item.getName());

			}
		});
		combo.setEmptyText("Select a status...");
		combo.setWidth(150);
		combo.setTypeAhead(true);
		combo.setTriggerAction(TriggerAction.ALL);

		combo.addSelectionHandler(new SelectionHandler<Iucn>() {
			@Override
			public void onSelection(SelectionEvent<Iucn> event) {
				combo.setValue(event.getSelectedItem());
				loader.load();
			}
		});

		toolBar = new ToolBar();
		toolBar.addStyleName("style");
		toolBar.add(iucnStatus);
		toolBar.add(combo);
		pagingToolBar = new PagingToolBar(50);
		pagingToolBar.getElement().getStyle().setProperty("borderBottom", "none");
		pagingToolBar.bind(loader);

		ColumnConfig<ThreatenedSpeciesModel, String> scientificNColumn = new ColumnConfig<ThreatenedSpeciesModel, String>(props.acceptedSpecies(), 150, "Scientific name");
		ColumnConfig<ThreatenedSpeciesModel, String> kingdomeColumn = new ColumnConfig<ThreatenedSpeciesModel, String>(props.kingdom(), 150, "Kingdom");
		ColumnConfig<ThreatenedSpeciesModel, String> phylumColumn = new ColumnConfig<ThreatenedSpeciesModel, String>(props.phylum(), 150, "Phylum");
		ColumnConfig<ThreatenedSpeciesModel, String> classColumn = new ColumnConfig<ThreatenedSpeciesModel, String>(props.class_(), 150, "Class");
		ColumnConfig<ThreatenedSpeciesModel, String> orderColumn = new ColumnConfig<ThreatenedSpeciesModel, String>(props.order(), 150, "Order");
		ColumnConfig<ThreatenedSpeciesModel, String> familyColumn = new ColumnConfig<ThreatenedSpeciesModel, String>(props.family(), 150, "Family");
		ColumnConfig<ThreatenedSpeciesModel, String> genusColumn = new ColumnConfig<ThreatenedSpeciesModel, String>(props.genus(), 150, "Genus");
		ColumnConfig<ThreatenedSpeciesModel, String> specificEColumn = new ColumnConfig<ThreatenedSpeciesModel, String>(props.specificEpithet(), 150, "Specific epithet");


		scientificNColumn.setCell(new AbstractCell<String>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,
					String value, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<span style='font-style: italic;'>" + value + "</span>");

			}
		});

		List<ColumnConfig<ThreatenedSpeciesModel, ?>> columns = new ArrayList<ColumnConfig<ThreatenedSpeciesModel, ?>>();
		columns.add(scientificNColumn);
		columns.add(kingdomeColumn);
		columns.add(phylumColumn);
		columns.add(classColumn);
		columns.add(orderColumn);
		columns.add(familyColumn);
		columns.add(genusColumn);
		columns.add(specificEColumn);

		ColumnModel<ThreatenedSpeciesModel> cm = new ColumnModel<ThreatenedSpeciesModel>(columns);

		Grid<ThreatenedSpeciesModel> grid = new Grid<ThreatenedSpeciesModel>(store, cm) {
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
		grid.getView().setAutoExpandColumn(scientificNColumn);
		grid.getView().setStripeRows(true);

		blc = new BorderLayoutContainer();
		blc.setBorders(true);
		//	    cp.setCollapsible(true);
		//	    cp.setHeadingText("Paging Grid Example");
		//	    blc.setHeaderVisible(false);
		blc.setWidth("100%");
		blc.setHeight("400px");
		//	    blc.addStyleName("margin-10");

		VerticalLayoutContainer con = new VerticalLayoutContainer();

		con.setBorders(true);
		con.add(toolBar);
		con.add(grid, new VerticalLayoutData(1, 1));
		con.add(pagingToolBar);

		MarginData centerData = new MarginData();
		centerData.setMargins(new Margins(5, 5, 5, 5));
		FramedPanel frame = new FramedPanel();
		frame.setHeadingText(ApplicationView.getConstants().ThreatenedSpeciesList());
		frame.getHeader().setIcon(Resources.INSTANCE.iucn());
		frame.add(con);
		blc.setCenterWidget(frame, centerData);

		return blc;

	}

	public void forceLayout() {
		toolBar.forceLayout();
		pagingToolBar.forceLayout();
	}

	interface ComboBoxTemplates extends XTemplates {

		@XTemplate("<img width=\"11\" height=\"11\" src=\"{imageUri}\">&nbsp;&nbsp;{name}")
		SafeHtml iucn(SafeUri imageUri, String name);

	}

	interface IucnProperties extends PropertyAccess<Iucn> {
		ModelKeyProvider<Iucn> id();

		LabelProvider<Iucn> name();
	}

}
