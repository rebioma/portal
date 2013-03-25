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
import com.google.gwt.maps.client.control.ControlAnchor;
import com.google.gwt.maps.client.control.ControlPosition;
import com.google.gwt.maps.client.control.Control.CustomControl;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class TileLayerSelector extends CustomControl {

  public interface TileLayerCallback {
    public void onLayerCleared(LayerInfo layerInfo);

    public void onLayerSelected(LayerInfo layerInfo);
  }

  protected static final String SELECT = "Select Layer...";

  protected static final String CLEAR = "--- Clear Layers ---";

  private static final String LOADING = "Loading...";

  protected final ListBox lb = new ListBox();

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
    super(new ControlPosition(ControlAnchor.TOP_RIGHT, 7, 60));
    this.callback = callback;
  }

  public void clearSelection() {
    if (lb.getItemCount() <= 0 || lb.getItemText(0).equals(LOADING)) {
      return;
    }
    if (selectedLayer != null) {
      map.removeOverlay(selectedLayer.asOverlay());
    }
    if (layerLegend != null) {
      map.removeControl(layerLegend);
    }
    lb.setSelectedIndex(selectionNames.get(SELECT));
    selectedLayer = null;
    layerLegend = null;
  }

  public Widget getControlWidget() {
    return lb;
  }

  public int getSelectionIndex(String selectionName) {
    return selectionNames.get(selectionName);
  }

  @Override
  public boolean isSelectable() {
    return true;
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
          if (!lb.getItemText(0).equals(LOADING)) {
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
      map.removeOverlay(selectedLayer.asOverlay());
    }
    if (layerLegend != null) {
      map.removeControl(layerLegend);
    }
    if (layerSelected.equals(SELECT) || layerSelected.equals(CLEAR)) {
      lb.setSelectedIndex(selectionNames.get(SELECT));
      selectedLayer = null;
      layerLegend = null;
      callback.onLayerCleared(layerInfos.get(selectedLayer));
    } else {
      lb.setSelectedIndex(selectionNames.get(layerSelected));
      layerInfo = layerInfos.get(layerSelected);
      selectedLayer = layerInfo.getInstance();
      map.addOverlay(selectedLayer.asOverlay());
      layerLegend = layerInfos.get(layerSelected).getInstance().getLegend();
      if (layerLegend != null) {
        map.addControl(layerLegend);
      }
      callback.onLayerSelected(layerInfo);
    }
    return layerInfo;
  }

  protected String currentSelection() {
    return lb.getItemText(lb.getSelectedIndex());
  }

  @Override
  protected Widget initialize(final MapWidget map) {
    this.map = map;
    lb.addItem(LOADING);
    loadLayers(new AsyncCallback<List<LayerInfo>>() {
      public void onFailure(Throwable caught) {
        GWT.log(this.getClass().getName(), caught);
      }

      public void onSuccess(List<LayerInfo> result) {
        lb.addChangeHandler(changeHandler);
        lb.clear();
        int index = 0;
        lb.addItem(SELECT);
        selectionNames.put(SELECT, index++);
        lb.addItem(CLEAR);
        selectionNames.put(CLEAR, index++);
        String name;
        for (LayerInfo layerInfo : result) {
          name = layerInfo.getName();
          lb.addItem(name);
          selectionNames.put(name, index++);
          selectionIndices.put(selectionNames.get(name), name);
          layerInfos.put(name, layerInfo);
        }
      }
    });
    return lb;
  }

  protected abstract void loadLayers(
      AsyncCallback<List<LayerInfo>> asyncCallback);

}
