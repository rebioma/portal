package org.rebioma.client.gxt.treegrid;

import org.rebioma.client.gxt.treegrid.CheckboxTreeGrid.TreeNode;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

@SuppressWarnings({"unchecked","rawtypes"})
public class CheckboxTreeGridEvent<M extends ModelData> extends GridEvent<M> {
	  private CheckboxTreeGrid<M> treeGrid;
	  private TreeNode treeNode;
	  private M item;
	  private boolean checked;
	  private TreeStore<M> store;

	  public CheckboxTreeGridEvent(CheckboxTreeGrid<M> grid, M item) {
		    this(grid);
		    setItem(item);
		    setModel(item);
		  }
	  
	  /**
	   * Creates a new tree grid event.
	   * 
	   * @param grid the source tree grid
	   */
	  public CheckboxTreeGridEvent(CheckboxTreeGrid<M> grid) {
	    super(grid);
	    this.treeGrid = (CheckboxTreeGrid) grid;
	  }

	  /**
	   * Creates a new tree grid event.
	   * 
	   * @param grid the tree grid
	   * @param event the event
	   */
	  public CheckboxTreeGridEvent(CheckboxTreeGrid<M> grid, Event event) {
	    super(grid, event);
	    this.treeGrid = (CheckboxTreeGrid) grid;
	  }
	  
	  public CheckboxTreeGridEvent(CheckboxTreeGrid<M> grid, Event event, M item) {
		    this(grid, event);		    
		    setItem(item);
		    setModel(item);		    
	}
	  
	  /**
	   * Returns the source tree grid.
	   * 
	   * @return the tree grid
	   */
	  public CheckboxTreeGrid<M> getTreeGrid() {
	    return treeGrid;
	  }

	  /**
	   * Returns the source tree node.
	   * 
	   * @return the tree node
	   */
	  public TreeNode getTreeNode() {
		    if (treeNode == null) {
		    	treeNode = treeGrid.findNode((Element) event.getEventTarget().cast());
		      }
		      return treeNode;
	  }

	  /**
	   * Sets the source tree grid.
	   * 
	   * @param treeGrid the tree grid
	   */
	  public void setTreeGrid(CheckboxTreeGrid<M> treeGrid) {
	    this.treeGrid = treeGrid;
	  }

	  /**
	   * Sets the source tree node.
	   * 
	   * @param treeNode the source tree node
	   */
	  public void setTreeNode(TreeNode treeNode) {
	    this.treeNode = treeNode;
	  }

	  public M getItem() {
		    if (item == null) {
		      if (getTreeNode() != null) {
		        item = (M) getTreeNode().getModel();
		      }
		    }
		    return item;
	}
	  
	  public void setItem(M item) {
		    this.item = item;
		  }

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public TreeStore<M> getStore() {
		return store;
	}

	public void setStore(TreeStore<M> store) {
		this.store = store;
	}		
}
