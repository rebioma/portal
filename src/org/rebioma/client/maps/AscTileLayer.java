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

import com.google.gwt.maps.client.CopyrightCollection;
import com.google.gwt.maps.client.TileLayer;
import com.google.gwt.maps.client.overlay.TileLayerOverlay;

/**
 * An abstract class that represents ASC tile layers.
 */
public abstract class AscTileLayer extends TileLayer {

  /**
   * Allows {@link AscTileLayer} objects to be lazy loaded.
   */
  public abstract static class LayerInfo {
    private AscTileLayer instance;

    public AscTileLayer getInstance() {
      if (instance == null) {
        instance = get();
      }
      return instance;
    }

    public abstract String getName();

    protected abstract AscTileLayer get();
  }

  protected String baseUrl;

  protected TileLayerOverlay overlay;

  public AscTileLayer() {
    super(new CopyrightCollection(), 0, 20);
  }

  public TileLayerOverlay asOverlay() {
    if (overlay == null) {
      overlay = new TileLayerOverlay(this);
    }
    return overlay;
  }

  public abstract TileLayerLegend getLegend();

  @Override
  public boolean isPng() {
    return true;
  }
}
