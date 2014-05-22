package org.rebioma.client.maps;

import java.util.List;

import org.rebioma.client.bean.ShapeFileInfo;

public interface ShapeSelectionHandler {
	/**
	 * 
	 * @param selectedItems
	 */
	void onShapeSelect(List<ShapeFileInfo> selectedItems);
}
