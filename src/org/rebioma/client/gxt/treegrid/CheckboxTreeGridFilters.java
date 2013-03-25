package org.rebioma.client.gxt.treegrid;

import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.grid.filters.AbstractGridFilters;

public class CheckboxTreeGridFilters extends AbstractGridFilters {
	  public CheckboxTreeGridFilters() {
	    // TreeGridFilters only support local filtering, see TreeGrid.setExpanded
	    setLocal(true);
	  }

	  @Override
	  public void init(Component component) {
	    assert component instanceof CheckboxTreeGrid<?> : "CheckboxTreeGridFilters can only be used with a CheckboxTreeGrid.";
	    super.init(component);
	  }

	  @Override
	  protected Loader<?> getLoader(Store<ModelData> store) {
	    // we do not support remote filtering on TreeGridFilter
	    return null;
	  }

	  @Override
	  protected Store<ModelData> getStore() {
	    return ((CheckboxTreeGrid<ModelData>) grid).getTreeStore();
	  }

	}