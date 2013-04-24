/*
 * Copyright 2008 University of California at Berkeley
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.rebioma.client.maps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rebioma.client.maps.AscTileLayer.LayerInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.controls.ControlPosition;
import com.google.gwt.maps.client.events.tiles.TilesLoadedMapEvent;
import com.google.gwt.maps.client.events.tiles.TilesLoadedMapHandler;
import com.google.gwt.maps.client.maptypes.ImageMapType;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ListBox;

public abstract class TileLayerSelector extends ListBox {

  public interface TileLayerCallback {
    public void onLayerCleared(LayerInfo layerInfo);

    public void onLayerSelected(LayerInfo layerInfo);
  }

  protected static final String SELECT = "Select Layer...";

  protected static final String CLEAR = "--- Clear Layers ---";

  private static final String LOADING = "Loading...";

  protected Map<String, LayerInfo> layerInfos = new HashMap<String, LayerInfo>();

  private final ChangeHandler changeHandler = new ChangeHandler() {
    public void onChange(ChangeEvent event) {
      selectLayer(currentSelection(), callback);
    }
  };

  protected TileLayerLegend layerLegend;

  private final TileLayerCallback callback;

  protected AscTileLayer selectedLayer;

  private MapWidget map;

  private final Map<String, Integer> selectionNames = new HashMap<String, Integer>();

  protected Map<Integer, String> selectionIndices = new HashMap<Integer, String>();

  public TileLayerSelector(TileLayerCallback callback) {
    super();
    this.callback = callback;
  }

  public void clearSelection() {
    if (getItemCount() <= 0 || getItemText(0).equals(LOADING)) {
      return;
    }
    if (selectedLayer != null) {
    	map.getOverlayMapTypes().removeAt(map.getOverlayMapTypes().getLength() - 1);
    }
    if (layerLegend != null) {
    	layerLegend.removeFromParent();
    }
    setSelectedIndex(selectionNames.get(SELECT));
    selectedLayer = null;
    layerLegend = null;
  }

  public int getSelectionIndex(String selectionName) {
    return selectionNames.get(selectionName);
  }

  public void removeLayer(String layerName, TileLayerCallback callback) {

  }

  /**
   * The timer is needed when a page is reloaded since it takes time for the
   * layers to return from the server.
   * 
   * @param selectionIndex
   * @return
   */
  public LayerInfo selectLayer(final int selectionIndex) {
    if (selectionIndices.isEmpty()) {
      Timer t = new Timer() {
        @Override
        public void run() {
          if (!selectionIndices.isEmpty()) {
            selectLayer(selectionIndices.get(selectionIndex));
            cancel();
          }
        }
      };
      t.scheduleRepeating(500);
    }
    return selectLayer(selectionIndices.get(selectionIndex));
  }

  public LayerInfo selectLayer(String layer) {
    return selectLayer(layer, new TileLayerCallback() {
      public void onLayerCleared(LayerInfo layerInfo) {
        // NOP.
      }

      public void onLayerSelected(LayerInfo layerInfo) {
        // NOP.
      }
    });
  }

  public LayerInfo selectLayer(final String layerSelected,
      final TileLayerCallback callback) {
    if (layerSelected == null) {
      return null;
    }
    if (layerSelected.equals(LOADING)) {
      Timer t = new Timer() {
        @Override
        public void run() {
          if (!getItemText(0).equals(LOADING)) {
            cancel();
            selectLayer(layerSelected, callback);
          }
        }
      };
      t.scheduleRepeating(500);
      return null;
    }
    LayerInfo layerInfo = null;
    if (selectedLayer != null) {
    	map.getOverlayMapTypes().removeAt(selectedLayer.getMapIndex());
    }
    if (layerLegend != null) {
    	layerLegend.removeFromParent();
    }
    if (layerSelected.equals(SELECT) || layerSelected.equals(CLEAR)) {
      setSelectedIndex(selectionNames.get(SELECT));
      selectedLayer = null;
      layerLegend = null;
      callback.onLayerCleared(layerInfos.get(selectedLayer));
    } else {
      setSelectedIndex(selectionNames.get(layerSelected));
      layerInfo = layerInfos.get(layerSelected);
      selectedLayer = layerInfo.getInstance();
      ImageMapType overlay = selectedLayer.asOverlay();
      map.getOverlayMapTypes().push(overlay);
      selectedLayer.setMapIndex(map.getOverlayMapTypes().getLength() - 1);
      layerLegend = layerInfos.get(layerSelected).getInstance().getLegend();
      if (layerLegend != null) {
    	  map.setControls(ControlPosition.RIGHT_BOTTOM, layerLegend);
      }
      callback.onLayerSelected(layerInfo);
    }
    return layerInfo;
  }

  protected String currentSelection() {
    return getItemText(getSelectedIndex());
  }
  
  /**
   * Ajouter le control au "map" à la position "position"
   * @param map
   * @param position
   * @return
   */
  public void setMap(final MapWidget map /*, com.google.gwt.maps.client.controls.ControlPosition position*/) {
    this.map = map;
//    map.setControls(position, this);
    addItem(LOADING);
    loadLayers(new AsyncCallback<List<LayerInfo>>() {
      public void onFailure(Throwable caught) {
        GWT.log(this.getClass().getName(), caught);
      }

      public void onSuccess(List<LayerInfo> result) {
        addChangeHandler(changeHandler);
        clear();
        int index = 0;
        addItem(SELECT);
        selectionNames.put(SELECT, index++);
        addItem(CLEAR);
        selectionNames.put(CLEAR, index++);
        String name;
        for (LayerInfo layerInfo : result) {
          name = layerInfo.getName();
          addItem(name);
          selectionNames.put(name, index++);
          selectionIndices.put(selectionNames.get(name), name);
          layerInfos.put(name, layerInfo);
        }
      }
    });
  }

  protected abstract void loadLayers(
      AsyncCallback<List<LayerInfo>> asyncCallback);

}
