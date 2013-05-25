/**
 * 
 */
package org.rebioma.client.gxt.treegrid;

import java.util.LinkedList;
import java.util.List;

import org.rebioma.client.bean.SpeciesTreeModel;
import org.rebioma.client.bean.SpeciesTreeModel.SpeciesTreeModelProperties;
import org.rebioma.client.gxt3.treegrid.CheckBoxTreeGridListener;
import org.rebioma.client.gxt3.treegrid.CheckboxTreeGrid;
import org.rebioma.client.services.SpeciesExplorerService;
import org.rebioma.client.services.SpeciesExplorerServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.ChildTreeStoreBinding;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeNode;

/**
 * @author Mikajy
 * 
 */
public class SpeciesExplorerPanel {

	private final SpeciesExplorerServiceAsync speciesExplorerService = GWT
			.create(SpeciesExplorerService.class);
	//
	private CheckboxTreeGrid<SpeciesTreeModel> treeGrid;
	//
	public SpeciesExplorerPanel() {
		// Generate the key provider and value provider for the Data class
		SpeciesTreeModelProperties speciesTreeModelProperties = GWT
				.create(SpeciesTreeModelProperties.class);
		// Create the configurations for each column in the tree grid
		List<ColumnConfig<SpeciesTreeModel, ?>> ccs = new LinkedList<ColumnConfig<SpeciesTreeModel, ?>>();
		ccs.add(new ColumnConfig<SpeciesTreeModel, String>(
				speciesTreeModelProperties.label(), 200, "Explorer"));
		ccs.add(new ColumnConfig<SpeciesTreeModel, String>(
				speciesTreeModelProperties.level(), 200, "Level"));
		ccs.add(new ColumnConfig<SpeciesTreeModel, Integer>(
				speciesTreeModelProperties.nbPrivateOccurence(), 200,
				"Private Occurrences"));
		ccs.add(new ColumnConfig<SpeciesTreeModel, Integer>(
				speciesTreeModelProperties.nbPublicOccurence(), 200,
				"Public Occurrences"));
		ColumnModel<SpeciesTreeModel> cm = new ColumnModel<SpeciesTreeModel>(
				ccs);

		RpcProxy<SpeciesTreeModel, List<SpeciesTreeModel>> proxy = new RpcProxy<SpeciesTreeModel, List<SpeciesTreeModel>>() {

			@Override
			public void load(SpeciesTreeModel loadConfig,
					AsyncCallback<List<SpeciesTreeModel>> callback) {
				speciesExplorerService.getChildren(loadConfig, callback);
			}
		};

		final TreeLoader<SpeciesTreeModel> loader = new TreeLoader<SpeciesTreeModel>(
				proxy) {
			@Override
			public boolean hasChildren(SpeciesTreeModel parent) {
				//seul les species n'ont pas d'enfant
				return !SpeciesTreeModel.ACCEPTEDSPECIES.equalsIgnoreCase(parent.getLevel()) 
							&& !SpeciesTreeModel.SPECIES.equalsIgnoreCase(parent.getLevel());
			}
		};
		// Create the store that the contains the data to display in the tree
		// grid
		TreeStore<SpeciesTreeModel> s = new TreeStore<SpeciesTreeModel>(
				speciesTreeModelProperties.key());

		loader.addLoadHandler(new ChildTreeStoreBinding<SpeciesTreeModel>(s));
		
		// Create the tree grid using the store, column model and column config
		// for the tree column
		treeGrid = new CheckboxTreeGrid<SpeciesTreeModel>(s, cm, ccs.get(0));
		treeGrid.setTreeLoader(loader);
		// treeGrid.getView().setTrackMouseOver(false);
		treeGrid.getView().setForceFit(true);
		// treeGrid.setWidth(300);
		treeGrid.setHeight(400);

	}
	
	public void addCheckBoxGridListener(CheckBoxTreeGridListener<SpeciesTreeModel> listener){
		treeGrid.addCheckBoxTreeGridListener(listener);
	}

	public List<SpeciesTreeModel> getCheckedSelection() {
		return getTreeGrid().getCheckedSelection();
	}

	public CheckboxTreeGrid<SpeciesTreeModel> getTreeGrid() {
		return this.treeGrid;
	}
}
