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

import com.google.gwt.maps.client.maptypes.ImageMapType;
import com.google.gwt.maps.client.maptypes.ImageMapTypeOptions;

/**
 * An abstract class that represents ASC tile layers.
 */
public abstract class AscTileLayer {
	
	protected final ImageMapTypeOptions imageMapTypeOptions;

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
  
  private int mapIndex;

  protected String baseUrl;

  protected ImageMapType overlay;

  public AscTileLayer() {
    super();
    imageMapTypeOptions = ImageMapTypeOptions.newInstance();
  }

  public ImageMapType asOverlay() {
    if (overlay == null) {
      overlay = ImageMapType.newInstance(imageMapTypeOptions);
    }
    return overlay;
  }

  public abstract TileLayerLegend getLegend();
  
  public int getMapIndex(){
	  return mapIndex;
  }
  /**
   * On memorise l'index de l'imageMapType quand il est ajouté au map
   * @param idx
   */
  public void setMapIndex(int idx){
	  this.mapIndex = idx;
  }
  
  public ImageMapTypeOptions getImageMapTypeOptions(){
	  return imageMapTypeOptions;
  }

}
