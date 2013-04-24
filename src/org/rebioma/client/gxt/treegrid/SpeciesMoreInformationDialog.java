package org.rebioma.client.gxt.treegrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.rebioma.client.bean.SpeciesStatisticModel;
import org.rebioma.client.bean.SpeciesTreeModel;
import org.rebioma.client.bean.SpeciesTreeModelInfoItem;
import org.rebioma.client.bean.SpeciesTreeModelInfoItem.SpeciesTreeModelInfosProperties;
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

public class SpeciesMoreInformationDialog extends Dialog {
	
	private final SpeciesExplorerServiceAsync speciesExplorerService = GWT
	.create(SpeciesExplorerService.class);
	
	private final RpcProxy<SpeciesTreeModel, List<SpeciesTreeModelInfoItem>> proxy;
	
	private final Grid<SpeciesTreeModelInfoItem> grid;
	
	public SpeciesMoreInformationDialog() {
		super();
		SpeciesTreeModelInfosProperties props = GWT.create(SpeciesTreeModelInfosProperties.class);
		proxy = new RpcProxy<SpeciesTreeModel, List<SpeciesTreeModelInfoItem>>() {
			@Override
			public void load(SpeciesTreeModel source,
					AsyncCallback<List<SpeciesTreeModelInfoItem>> callback) {
				speciesExplorerService.getInfomations(source, callback);
			}
		};
		
		ListStore<SpeciesTreeModelInfoItem> store = new ListStore<SpeciesTreeModelInfoItem>(props.key());

		ColumnConfig<SpeciesTreeModelInfoItem, String> labelCC = new ColumnConfig<SpeciesTreeModelInfoItem, String>(
				props.label(), 200, "Label");
		ColumnConfig<SpeciesTreeModelInfoItem, String> valueCC = new ColumnConfig<SpeciesTreeModelInfoItem, String>(
				props.value(), 500, "Value");
		List<ColumnConfig<SpeciesTreeModelInfoItem, ?>> l = new ArrayList<ColumnConfig<SpeciesTreeModelInfoItem, ?>>();
		l.add(labelCC);
		l.add(valueCC);
		ColumnModel<SpeciesTreeModelInfoItem> cm = new ColumnModel<SpeciesTreeModelInfoItem>(l);
		grid = new Grid<SpeciesTreeModelInfoItem>(store, cm);
		grid.getView().setAutoExpandColumn(labelCC);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.setBorders(false);
		grid.setAllowTextSelection(true);
		grid.setLoadMask(true);
//		grid.setLoader(loader);
		grid.setColumnReordering(false);
		setBodyBorder(true);
		setWidth(700);
		setHeight(300);
		setHideOnButtonClick(true);
		setModal(true);
		add(grid);
	}
	
	public void showInformations(final SpeciesTreeModel model){
		if(model != null){
			String headingText = getHeadingText(model);
			setHeadingText(headingText);
			this.show();
			if(model.getInfos() != null && !model.getInfos().isEmpty()){
				grid.getStore().replaceAll(model.getInfos());
			}else{
				 Scheduler.get().scheduleDeferred(new ScheduledCommand(){
					@Override
					public void execute() {
						grid.getStore().clear();
						Mask.mask(grid.getElement(), "Loading...");
					}
				 });
				proxy.load(model, new AsyncCallback<List<SpeciesTreeModelInfoItem>>() {
					@Override
					public void onFailure(Throwable caught) {
						hide();
						Window.alert(caught.getLocalizedMessage());
					}

					@Override
					public void onSuccess(List<SpeciesTreeModelInfoItem> result) {
						grid.setLoadMask(false);
						grid.getStore().replaceAll(result);
						model.setInfos(result);
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
