/**
 * 
 */
package org.rebioma.client.gxt.treegrid;

import java.util.Arrays;
import java.util.List;

import org.rebioma.client.bean.SpeciesTreeModel;
import org.rebioma.client.services.SpeciesExplorerService;
import org.rebioma.client.services.SpeciesExplorerServiceAsync;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.CheckChangedEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Mikajy
 *
 */
public class SpeciesExplorerPanel extends LayoutContainer{
	private static final String LABEL_COL_HEADER = "Explorer";
	private static final String LEVEL_COL_HEADER = "Level";
	private static final String PRIVATE_OCCURENCE_COL_HEADER = "Private Occurences";
	private static final String PUBLIC_OCCURENCE_COL_HEADER = "Public Occurences";
	
	private final SpeciesExplorerServiceAsync speciesExplorerService = GWT
				.create(SpeciesExplorerService.class);
	
	private CheckboxTreeGrid<SpeciesTreeModel> treeGrid;
	
	private List<SpeciesTreeModel> checkedSelection;
	
	public SpeciesExplorerPanel(){
		this(new FitLayout(), null);
	}

	public SpeciesExplorerPanel(Layout layout){
		this(layout, null);
	}
	public SpeciesExplorerPanel(GridSelectionModel<SpeciesTreeModel> sm){
		this(new FitLayout(), sm);
	}
	public SpeciesExplorerPanel(Layout layout, GridSelectionModel<SpeciesTreeModel> sm){
		ColumnConfig explorer = new ColumnConfig(SpeciesTreeModel.LABEL, LABEL_COL_HEADER, 350);
		ColumnConfig level = new ColumnConfig(SpeciesTreeModel.LEVEL, LEVEL_COL_HEADER, 100);
		ColumnConfig privateOcc = new ColumnConfig(SpeciesTreeModel.PRIVATE_OCCURENCE, PRIVATE_OCCURENCE_COL_HEADER, 100);
		ColumnConfig publicOcc = new ColumnConfig(SpeciesTreeModel.PUBLIC_OCCURENCE, PUBLIC_OCCURENCE_COL_HEADER, 100);
		explorer.setRenderer(new CheckboxTreeGridCellRenderer<SpeciesTreeModel>(true));
		
		ColumnModel cm = new ColumnModel(Arrays.asList(explorer, level, privateOcc, publicOcc));
		RpcProxy<List<SpeciesTreeModel>> proxy = getProxy();
		BaseTreeLoader<SpeciesTreeModel> loader = getLoader(proxy);
		loader.addLoadListener(new LoadListener(){
			public void loaderBeforeLoad(LoadEvent le){
				//mask(GXT.MESSAGES.loadMask_msg());
			}
			
			public void loaderLoad(LoadEvent le) {
				unmask();
			 }
		});
		TreeStore<SpeciesTreeModel> store = getStore(loader);
		treeGrid = new CheckboxTreeGrid<SpeciesTreeModel>(store, cm);
		treeGrid.setLoadMask(true);
		if(sm != null){
			treeGrid.setSelectionModel(sm);
		}
		treeGrid.getView().setForceFit(true);
		mask(GXT.MESSAGES.loadMask_msg());
		//treeGrid.setSelectionModel(sm);
		treeGrid.addListener(Events.CheckChanged, new Listener<CheckChangedEvent<SpeciesTreeModel>>() {
			@Override
			public void handleEvent(CheckChangedEvent<SpeciesTreeModel> be) {
				checkedSelection = be.getCheckedSelection();
			}
			
		});
		this.setBorders(true);
		this.setLayout(layout);
		//this.add(treeGrid);
	}
	
	public List<SpeciesTreeModel> getCheckedSelection(){
		return checkedSelection;
	}
	
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		this.add(treeGrid);
	}

	private TreeStore<SpeciesTreeModel> getStore(BaseTreeLoader<SpeciesTreeModel> loader){
		//le store
		TreeStore<SpeciesTreeModel> store = new TreeStore<SpeciesTreeModel>(loader);
		return store;
	}
	
	private BaseTreeLoader<SpeciesTreeModel> getLoader(RpcProxy<List<SpeciesTreeModel>> proxy){
		BaseTreeLoader<SpeciesTreeModel> loader = new BaseTreeLoader<SpeciesTreeModel>(proxy){
			@Override
			public boolean hasChildren(SpeciesTreeModel parent) {
				if(parent == null){
					return false;
				}
				return !"species".equalsIgnoreCase(parent.getLevel());
			}
		};
		return loader;
	}
	
	private RpcProxy<List<SpeciesTreeModel>> getProxy(){
		RpcProxy<List<SpeciesTreeModel>> proxy = new RpcProxy<List<SpeciesTreeModel>>() {
			@Override
			protected void load(Object loadConfig,
					AsyncCallback<List<SpeciesTreeModel>> callback) {
				// C'est le loader qui fera tout seul l'appel asynchrone pour
				// le chargement des enfants
				speciesExplorerService.getChildren((SpeciesTreeModel) loadConfig,
						callback);
			}
		};
		return proxy;
	}
	
	public CheckboxTreeGrid<SpeciesTreeModel> getTreeGrid(){
		return this.treeGrid;
	}
}
