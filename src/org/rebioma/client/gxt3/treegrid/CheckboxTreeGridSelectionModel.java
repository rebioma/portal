package org.rebioma.client.gxt3.treegrid;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;

public class CheckboxTreeGridSelectionModel<M> extends GridSelectionModel<M> {

	  protected CheckboxTreeGrid<M> tree;
	  protected TreeStore<M> treeStore;

	  @Override
	  public void bind(Store<M> store) {
	    super.bind(store);
	    if (store instanceof TreeStore<?>) {
	      treeStore = (TreeStore<M>) store;
	    } else {
	      treeStore = null;
	    }
	  }

	  @Override
	  public void bindGrid(Grid<M> grid) {
	    super.bindGrid(grid);
	    if (grid instanceof CheckboxTreeGrid<?>) {
	      tree = (CheckboxTreeGrid<M>) grid;
	      treeStore = tree.getTreeStore();
	    } else {
	      tree = null;
	    }
	  }

	  @Override
	  protected void onKeyLeft(NativeEvent ce) {
	    if (Element.is(ce.getEventTarget())
	        && !grid.getView().isSelectableTarget(Element.as(ce.getEventTarget()))) {
	      return;
	    }
	    super.onKeyLeft(ce);
	    ce.preventDefault();
	    boolean leaf = tree.isLeaf(getLastFocused());
	    if (!leaf && tree.isExpanded(getLastFocused())) {
	      tree.setExpanded(getLastFocused(), false);
	    } else if (!leaf) {
	      M parent = treeStore.getParent(getLastFocused());
	      if (parent != null) {
	        select(parent, false);
	      }
	    } else if (leaf) {
	      M parent = treeStore.getParent(getLastFocused());
	      if (parent != null) {
	        select(parent, false);
	      }
	    }
	  }

	  @Override
	  protected void onKeyRight(NativeEvent ce) {
	    if (Element.is(ce.getEventTarget())
	        && !grid.getView().isSelectableTarget(Element.as(ce.getEventTarget()))) {
	      return;
	    }
	    super.onKeyRight(ce);
	    ce.preventDefault();
	    if (!tree.isLeaf(getLastFocused()) && !tree.isExpanded(getLastFocused())) {
	      tree.setExpanded(getLastFocused(), true);
	    }
	  }
}
