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

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.ChildTreeStoreBinding;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.StoreFilterField;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

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
	private String style = "style='padding-right:15px'";
	
	NumberFormat format = NumberFormat.getDecimalFormat();
	
	private TextButton findButton;
	private TextButton resetButton;
	private TextButton filterButton;
	private ToolBar toolBarHaut;
	private TextArea criteriArea;
	
	public TextButton getFindButton() {
		return findButton;
	}

	public TextButton getResetButton() {
		return resetButton;
	}

	public TextButton getFilterButton() {
		return filterButton;
	}

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
		ColumnConfig<SpeciesTreeModel, Integer> priO = new ColumnConfig<SpeciesTreeModel, Integer>(
				speciesTreeModelProperties.nbPrivateOccurence(), 200,
				"Private Occurrences");
		priO.setAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		priO.setCell(new AbstractCell<Integer>() {
	        @Override
	        public void render(com.google.gwt.cell.client.Cell.Context context, Integer value, SafeHtmlBuilder sb) {
	        	String v = format.format(value);// + space;
	        	sb.appendHtmlConstant("<span " + style + ">" + v + "</span>");
	        }

		});
		ccs.add(priO);
		ColumnConfig<SpeciesTreeModel, Integer> pubO = new ColumnConfig<SpeciesTreeModel, Integer>(
				speciesTreeModelProperties.nbPublicOccurence(), 200,
				"Public Occurrences");
		pubO.setAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		pubO.setCell(new AbstractCell<Integer>() {
	        @Override
	        public void render(com.google.gwt.cell.client.Cell.Context context, Integer value, SafeHtmlBuilder sb) {
	        	String v = format.format(value);// + space;
	        	sb.appendHtmlConstant("<span " + style + ">" + v + "</span>");
	        }

		});
		ccs.add(pubO);
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
		
		StoreFilterField<SpeciesTreeModel> criteria = new StoreFilterField<SpeciesTreeModel>() {

			@Override
			protected boolean doSelect(Store<SpeciesTreeModel> store,
					SpeciesTreeModel parent, SpeciesTreeModel item,
					String filter) {
				
				 /*if (item instanceof SpeciesTreeModel) {
			          return false;
			        }
			        
			 */
					//SpeciesTreeModel treeModel = treeGrid.findNode(getElement().)
			        String name = item.getKingdom();
			        String class_ = item.getClass_();
			        if(class_!=null)
			        	class_ = class_.toLowerCase();
			        else
			        	class_ = "";
			        name = name.toLowerCase();
			        
			        if (name.startsWith(filter.toLowerCase())) {
			          return true;
			        }
			        return false;
				
			}
			
		};
		criteria.bind(s);
		toolBarHaut = new ToolBar();
		findButton = new TextButton("Find");
		resetButton = new TextButton("Reset");
		filterButton = new TextButton("Filter");
		
		findButton.addStyleName("text");
		resetButton.addStyleName("text");
		filterButton.addStyleName("text");
		
		toolBarHaut.add(findButton);
		toolBarHaut.add(resetButton);
		toolBarHaut.add(filterButton);
		toolBarHaut.add(criteria);
		// Create the tree grid using the store, column model and column config
		// for the tree column
		treeGrid = new CheckboxTreeGrid<SpeciesTreeModel>(s, cm, ccs.get(0));
		treeGrid.setTreeLoader(loader);
//		loader.
		// treeGrid.getView().setTrackMouseOver(false);
		treeGrid.getView().setForceFit(true);
		// treeGrid.setWidth(300);
//		treeGrid.setHeight(400);
		
		resetButton.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				
				
			}
		});
        
		findButton.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
				
				
			}
		});

        filterButton.addSelectHandler(new SelectHandler() {
	
	@Override
	public void onSelect(SelectEvent event) {
		
		
	}
});

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

	public void setToolBarHaut(ToolBar toolBarHaut) {
		this.toolBarHaut = toolBarHaut;
	}

	public ToolBar getToolBarHaut() {
		return toolBarHaut;
	}

	public void setCriteriArea(TextArea criteriArea) {
		this.criteriArea = criteriArea;
	}

	public TextArea getCriteriArea() {
		return criteriArea;
	}
}
