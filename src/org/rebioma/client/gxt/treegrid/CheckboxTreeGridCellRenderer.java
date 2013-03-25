package org.rebioma.client.gxt.treegrid;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.Joint;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class CheckboxTreeGridCellRenderer<M extends ModelData> implements GridCellRenderer<M> {
	private boolean checkable;
	private boolean checked;
	
	public CheckboxTreeGridCellRenderer(boolean checkable) {
		this.checkable = checkable;
	}
	
	public CheckboxTreeGridCellRenderer() {}

	  @SuppressWarnings({"unchecked", "rawtypes"})
	  public Object render(M model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<M> store,
	      Grid<M> grid) {
	    config.css = "x-treegrid-column";

	    assert grid instanceof CheckboxTreeGrid : "CheckboxTreeGridCellRenderer can only be used in a CheckboxTreeGrid";

	    CheckboxTreeGrid tree = (CheckboxTreeGrid) grid;
	    TreeStore ts = tree.getTreeStore();

	    int level = ts.getDepth(model);

	    String id = getId(tree, model, property, rowIndex, colIndex);
	    String text = getText(tree, model, property, rowIndex, colIndex);
	    AbstractImagePrototype icon = calculateIconStyle(tree, model, property, rowIndex, colIndex);
	    Joint j = calcualteJoint(tree, model, property, rowIndex, colIndex);

	    return tree.getTreeView().getTemplate(model, id, text, icon, this.checkable, this.checked, j, level - 1);
	  }

	  protected Joint calcualteJoint(CheckboxTreeGrid<M> grid, M model, String property, int rowIndex, int colIndex) {
	    return grid.calcualteJoint(model);
	  }

	  protected AbstractImagePrototype calculateIconStyle(CheckboxTreeGrid<M> grid, M model, String property, int rowIndex,
	      int colIndex) {
	    return grid.calculateIconStyle(model);
	  }

	  protected String getId(CheckboxTreeGrid<M> grid, M model, String property, int rowIndex, int colIndex) {
	    return grid.findNode(model).id;
	  }

	  protected String getText(CheckboxTreeGrid<M> grid, M model, String property, int rowIndex, int colIndex) {
	    return String.valueOf(model.get(property));
	  }

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}	  
	}